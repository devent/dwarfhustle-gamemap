/*
 * dwarfhustle-model-db - Manages the compile dependencies for the model.
 * Copyright © 2023 Erwin Müller (erwin.mueller@anrisoftware.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.anrisoftware.dwarfhustle.gamemap.jme.actors;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static java.time.Duration.ofSeconds;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.console.actor.ConsoleActor;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppCommand;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadMapTilesMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapBlockLoadedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetWorldMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.cache.AbstractCacheGetMessage.CacheGetMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.AbstractCacheGetMessage.CacheGetReplyMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.AbstractCacheGetMessage.CacheGetSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage.CacheErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.MapBlocksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.ConnectDbEmbeddedMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.ConnectDbSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DbResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DbResponseMessage.DbErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.OrientDbActor;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.StartEmbeddedServerMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.StartEmbeddedServerMessage.StartEmbeddedServerSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.AbstractLoadObjectMessage.LoadObjectErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.AbstractLoadObjectMessage.LoadObjectSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.AbstractObjectsReplyMessage.ObjectsResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.LoadGameObjectMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.ObjectsDbActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.KnowledgeBaseActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.PowerLoomKnowledgeActor;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Acts on the messages:
 * <ul>
 * <li>{@link LoadWorldMessage}</li>
 * </ul>
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class AppActor {

	public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, AppActor.class.getSimpleName());

	public static final String NAME = AppActor.class.getSimpleName();

	public static final int ID = KEY.hashCode();

	@RequiredArgsConstructor
	@ToString(callSuper = true)
	private static class InitialStateMessage extends Message {
	}

	@RequiredArgsConstructor
	@ToString(callSuper = true)
	private static class SetupErrorMessage extends Message {

		public final Throwable cause;
	}

	@RequiredArgsConstructor
	@ToString(callSuper = true)
	private static class WrappedDbResponse extends Message {
		private final DbResponseMessage response;
	}

	@RequiredArgsConstructor
	@ToString(callSuper = true)
	private static class WrappedObjectsResponse extends Message {
		private final ObjectsResponseMessage response;
	}

	@RequiredArgsConstructor
	@ToString(callSuper = true)
	private static class WrappedCacheResponse extends Message {
		private final CacheResponseMessage response;
	}

	/**
	 * Factory to create {@link AppActor}.
	 *
	 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
	 */
	public interface AppActorFactory {
		AppActor create(ActorContext<Message> context, StashBuffer<Message> stash, AppCommand command);
	}

	/**
	 * Creates the {@link AppActor}.
	 */
	public static Behavior<Message> create(Injector injector, AppCommand command) {
		return Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
			context.pipeToSelf(createActors(injector, command, context), (result, cause) -> {
				if (cause == null) {
					return result;
				} else {
					return new SetupErrorMessage(cause);
				}
			});
			return injector.getInstance(AppActorFactory.class).create(context, stash, command).start(injector);
		}));
	}

	/**
	 * Creates the {@link AppActor}.
	 */
	public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout, AppCommand command) {
		var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
		return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, command));
	}

	private static CompletionStage<Message> createActors(Injector injector, AppCommand command,
			ActorContext<Message> context) {
		return CompletableFuture.supplyAsync(() -> createActors0(injector, command), context.getExecutionContext());
	}

	private static Message createActors0(Injector injector, AppCommand command) {
		createDb(injector, command);
		createPowerLoom(injector);
		createConsole(injector);
		return new InitialStateMessage();
	}

	private static void createDb(Injector injector, AppCommand command) {
		var actor = injector.getInstance(ActorSystemProvider.class);
		OrientDbActor.create(injector, ofSeconds(1)).whenComplete((ret, ex) -> {
			if (ex != null) {
				log.error("OrientDbActor.create", ex);
				actor.tell(new AppErrorMessage(ex));
			} else {
				log.debug("OrientDbActor created");
				createObjects(injector, ret, command);
			}
		});
	}

	private static void createObjects(Injector injector, ActorRef<Message> db, AppCommand command) {
		var actor = injector.getInstance(ActorSystemProvider.class);
		ObjectsDbActor.create(injector, ofSeconds(1), db).whenComplete((ret, ex) -> {
			if (ex != null) {
				log.error("ObjectsDbActor.create", ex);
				actor.tell(new AppErrorMessage(ex));
			} else {
				log.debug("ObjectsDbActor created");
				if (command.isSkipLoad()) {
					log.warn("Skip load is set, not loading world.");
					return;
				}
				actor.tell(new LoadWorldMessage(command.getGamedir()));
			}
		});
	}

	private static void createPowerLoom(Injector injector) {
		var actor = injector.getInstance(ActorSystemProvider.class);
		PowerLoomKnowledgeActor.create(injector, ofSeconds(1)).whenComplete((ret, ex) -> {
			if (ex != null) {
				log.error("PowerLoomKnowledgeActor.create", ex);
				actor.tell(new AppErrorMessage(ex));
			} else {
				log.debug("PowerLoomKnowledgeActor created");
				createKnowledge(injector, ret);
			}
		});
	}

	private static void createKnowledge(Injector injector, ActorRef<Message> powerLoom) {
		var actor = injector.getInstance(ActorSystemProvider.class);
		KnowledgeBaseActor.create(injector, ofSeconds(1), powerLoom).whenComplete((ret, ex) -> {
			if (ex != null) {
				log.error("KnowledgeBaseActor.create", ex);
				actor.tell(new AppErrorMessage(ex));
			} else {
				log.debug("KnowledgeBaseActor created");
			}
		});
	}

	private static void createConsole(Injector injector) {
		var actor = injector.getInstance(ActorSystemProvider.class);
		ConsoleActor.create(injector, ofSeconds(1)).whenComplete((ret, ex) -> {
			if (ex != null) {
				log.error("ConsoleActor.create", ex);
				actor.tell(new AppErrorMessage(ex));
			} else {
				log.debug("ConsoleActor created");
			}
		});
	}

	@Inject
	@Assisted
	private ActorContext<Message> context;

	@Inject
	@Assisted
	private StashBuffer<Message> buffer;

	@Inject
	@Assisted
	private AppCommand command;

	@Inject
	private ActorSystemProvider actor;

	@Inject
	private GameSettingsProvider ogs;

	private ActorRef<DbResponseMessage> dbResponseAdapter;

	private ActorRef<ObjectsResponseMessage> objectsResponseAdapter;

	private ActorRef<CacheResponseMessage> cacheResponseAdapter;

	private URL dbConfig = AppActor.class.getResource("/orientdb-config.xml");

	private Injector injector;

	private ActorRef<Message> mapBlocks;

	/**
	 * Stash behavior. Returns a behavior for the messages:
	 *
	 * <ul>
	 * <li>{@link InitialStateMessage}
	 * <li>{@link SetupErrorMessage}
	 * <li>{@link Message}
	 * </ul>
	 */
	public Behavior<Message> start(Injector injector) {
		this.injector = injector;
		this.dbResponseAdapter = context.messageAdapter(DbResponseMessage.class, WrappedDbResponse::new);
		this.objectsResponseAdapter = context.messageAdapter(ObjectsResponseMessage.class, WrappedObjectsResponse::new);
		this.cacheResponseAdapter = context.messageAdapter(CacheResponseMessage.class, WrappedCacheResponse::new);
		return Behaviors.receive(Message.class)//
				.onMessage(InitialStateMessage.class, this::onInitialState)//
				.onMessage(SetupErrorMessage.class, this::onSetupError)//
				.onMessage(Message.class, this::stashOtherCommand)//
				.build();
	}

	private Behavior<Message> stashOtherCommand(Message m) {
		log.debug("stashOtherCommand: {}", m);
		buffer.stash(m);
		return Behaviors.same();
	}

	private Behavior<Message> onSetupError(SetupErrorMessage m) {
		log.debug("onSetupError: {}", m);
		return Behaviors.stopped();
	}

	/**
	 * Returns a behavior for the messages:
	 *
	 * <ul>
	 * <li>{@link CacheGetReplyMessage}
	 * <li>{@link CacheGetMessage}
	 * </ul>
	 */
	private Behavior<Message> onInitialState(InitialStateMessage m) {
		log.debug("onInitialState");
		return buffer.unstashAll(getInitialBehavior()//
				.build());
	}

	/**
	 * Returns a behavior for the messages from {@link #getInitialBehavior()}
	 */
	private Behavior<Message> onLoadWorld(LoadWorldMessage m) {
		log.debug("onLoadWorld {}", m);
		actor.tell(new StartEmbeddedServerMessage(dbResponseAdapter, m.dir.getAbsolutePath(), dbConfig));
		return Behaviors.same();
	}

	/**
	 * <ul>
	 * <li>
	 * </ul>
	 */
	private Behavior<Message> onSetWorldMap(SetWorldMapMessage m) {
		log.debug("onSetWorldMap {}", m);
		ogs.get().currentWorld.set(m.wm);
		actor.tell(new LoadGameObjectMessage(objectsResponseAdapter, GameMap.OBJECT_TYPE, db -> {
			var query = "SELECT * from ? where objecttype = ? and mapid = ?";
			return db.query(query, GameMap.OBJECT_TYPE, GameMap.OBJECT_TYPE, m.wm.getCurrentMapid());
		}));
		return Behaviors.same();
	}

	/**
	 * <ul>
	 * <li>
	 * </ul>
	 */
	private Behavior<Message> onLoadMapTiles(LoadMapTilesMessage m) {
		log.debug("onLoadMapTiles {}", m);
		Map<String, Object> params = new HashMap<>();
		params.put("parent_dir", command.getGamedir());
		params.put("mapid", m.gm.getMapid());
		params.put("width", m.gm.getWidth());
		params.put("height", m.gm.getHeight());
		params.put("depth", m.gm.getDepth());
		params.put("block_size", m.gm.getBlockSize());
		var task = MapBlocksJcsCacheActor.create(injector, Duration.ofSeconds(1000), params);
		task.whenComplete((ret, ex) -> {
			if (ex != null) {
				log.error("MapBlocksJcsCacheActor.create", ex);
			}
			mapBlocks = ret;
			var w = m.gm.getWidth();
			var h = m.gm.getHeight();
			var d = m.gm.getDepth();
			var pos = new GameBlockPos(m.gm.getMapid(), 0, 0, 0, 0 + w, 0 + h, 0 + d);
			mapBlocks.tell(new CacheGetReplyMessage(cacheResponseAdapter, MapBlock.OBJECT_TYPE, pos));
		});
		return Behaviors.same();
	}

	/**
	 * <ul>
	 * <li>
	 * </ul>
	 */
	private Behavior<Message> onWrappedDbResponse(WrappedDbResponse m) {
		log.debug("onWrappedDbResponse {}", m);
		var response = m.response;
		if (response instanceof DbErrorMessage rm) {
			log.error("Db error", rm);
			actor.tell(new AppErrorMessage(rm.error));
			return Behaviors.stopped();
		} else if (response instanceof StartEmbeddedServerSuccessMessage) {
			log.debug("Started embedded server");
			var rm = (StartEmbeddedServerSuccessMessage) response;
			actor.tell(new ConnectDbEmbeddedMessage(dbResponseAdapter, rm.server, "test", "root", "admin"));
		} else if (response instanceof ConnectDbSuccessMessage) {
			log.debug("Connected to embedded server");
			actor.tell(new LoadGameObjectMessage(objectsResponseAdapter, WorldMap.OBJECT_TYPE, db -> {
				var query = "SELECT * from ? limit 1";
				return db.query(query, WorldMap.OBJECT_TYPE);
			}));
		}
		return Behaviors.same();
	}

	/**
	 * <ul>
	 * <li>
	 * </ul>
	 */
	private Behavior<Message> onWrappedObjectsResponse(WrappedObjectsResponse m) {
		log.debug("onWrappedObjectsResponse {}", m);
		var response = m.response;
		if (response instanceof LoadObjectErrorMessage rm) {
			log.error("Objects error", rm);
			actor.tell(new AppErrorMessage(rm.error));
			return Behaviors.stopped();
		} else if (response instanceof LoadObjectSuccessMessage rm) {
			if (rm.go instanceof WorldMap wm) {
				actor.tell(new SetWorldMapMessage(wm));
			} else if (rm.go instanceof GameMap gm) {
				gm.setWorld(ogs.get().currentWorld.get());
				ogs.get().currentMap.set(gm);
				actor.tell(new SetGameMapMessage(gm));
				actor.tell(new LoadMapTilesMessage(gm));
			}
		}
		return Behaviors.same();
	}

	/**
	 * <ul>
	 * <li>
	 * </ul>
	 */
	private Behavior<Message> onWrappedCacheResponse(WrappedCacheResponse m) {
		log.debug("onWrappedCacheResponse {}", m);
		var response = m.response;
		if (response instanceof CacheErrorMessage rm) {
			log.error("Cache error", rm);
			actor.tell(new AppErrorMessage(rm.error));
			return Behaviors.stopped();
		} else if (response instanceof CacheGetSuccessMessage rm) {
			if (rm.go instanceof MapBlock mb) {
				log.debug("MapBlock loaded {}", mb);
				actor.tell(new MapBlockLoadedMessage(mb));
			}
		}
		return Behaviors.same();
	}

	/**
	 * <ul>
	 * <li>
	 * </ul>
	 */
	private Behavior<Message> onShutdown(ShutdownMessage m) {
		log.debug("onShutdown {}", m);
		return Behaviors.stopped();
	}

	/**
	 * Returns a behavior for the messages:
	 *
	 * <ul>
	 * <li>{@link LoadMapTilesMessage}
	 * <li>{@link LoadWorldMessage}
	 * <li>{@link SetWorldMapMessage}
	 * <li>{@link ShutdownMessage}
	 * <li>{@link WrappedDbResponse}
	 * <li>{@link WrappedObjectsResponse}
	 * <li>{@link WrappedCacheResponse}
	 * </ul>
	 */
	private BehaviorBuilder<Message> getInitialBehavior() {
		return Behaviors.receive(Message.class)//
				.onMessage(LoadMapTilesMessage.class, this::onLoadMapTiles)//
				.onMessage(LoadWorldMessage.class, this::onLoadWorld)//
				.onMessage(SetWorldMapMessage.class, this::onSetWorldMap)//
				.onMessage(ShutdownMessage.class, this::onShutdown)//
				.onMessage(WrappedDbResponse.class, this::onWrappedDbResponse)//
				.onMessage(WrappedObjectsResponse.class, this::onWrappedObjectsResponse)//
				.onMessage(WrappedCacheResponse.class, this::onWrappedCacheResponse)//
		;
	}
}
