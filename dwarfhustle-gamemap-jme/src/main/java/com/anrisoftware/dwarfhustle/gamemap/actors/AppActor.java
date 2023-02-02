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
package com.anrisoftware.dwarfhustle.gamemap.actors;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static java.time.Duration.ofSeconds;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.console.actor.ConsoleActor;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppCommand;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppErrorMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DbResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DbResponseMessage.DbErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.OrientDbActor;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.StartEmbeddedServerMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.StartEmbeddedServerMessage.StartEmbeddedServerSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.AbstractLoadObjectMessage.LoadObjectErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.AbstractLoadObjectMessage.LoadObjectSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.AbstractObjectsReplyMessage.ObjectsResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.CreateSchemasMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.GameObjectSchema;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.ObjectsDbActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.KnowledgeBaseActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.KnowledgeBaseMessage.GetMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.KnowledgeBaseMessage.GetReplyMessage;
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

	/**
	 * Factory to create {@link AppActor}.
	 *
	 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
	 */
	public interface AppActorFactory {
		AppActor create(ActorContext<Message> context, StashBuffer<Message> stash);
	}

	/**
	 * Creates the {@link AppActor}.
	 */
	public static Behavior<Message> create(Injector injector, AppCommand command) {
		return Behaviors.withStash(100, stash -> Behaviors.setup((context) -> {
			context.pipeToSelf(createActors(injector, command, context), (result, cause) -> {
				if (cause == null) {
					return result;
				} else {
					return new SetupErrorMessage(cause);
				}
			});
			return injector.getInstance(AppActorFactory.class).create(context, stash).start();
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
	private ActorSystemProvider actor;

	private ActorRef<Message> objects;

	@Inject
	private List<GameObjectSchema> schemas;

	private ActorRef<DbResponseMessage> dbResponseAdapter;

	private ActorRef<ObjectsResponseMessage> objectsResponseAdapter;

	private Optional<CreateSchemasMessage> createSchemasMessage = Optional.empty();

	private URL dbConfig = AppActor.class.getResource("/orientdb-config.xml");

	/**
	 * Stash behavior. Returns a behavior for the messages:
	 *
	 * <ul>
	 * <li>{@link InitialStateMessage}
	 * <li>{@link SetupErrorMessage}
	 * <li>{@link Message}
	 * </ul>
	 */
	public Behavior<Message> start() {
		this.dbResponseAdapter = context.messageAdapter(DbResponseMessage.class, WrappedDbResponse::new);
		this.objectsResponseAdapter = context.messageAdapter(ObjectsResponseMessage.class, WrappedObjectsResponse::new);
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
	 * <li>{@link GetReplyMessage}
	 * <li>{@link GetMessage}
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
		// objects.tell(new LoadWorldMapMessage(objectsResponseAdapter));
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
		if (response instanceof DbErrorMessage) {
			var rm = (DbErrorMessage) response;
			log.error("Db error", rm);
			return Behaviors.stopped();
		} else if (response instanceof StartEmbeddedServerSuccessMessage) {
			log.debug("Started embedded server");
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
		var om = createSchemasMessage.get();
		if (response instanceof LoadObjectErrorMessage) {
			var rm = (LoadObjectErrorMessage) response;
			log.error("Objects error", rm);
			om.replyTo.tell(rm);
			return Behaviors.stopped();
		} else if (response instanceof LoadObjectSuccessMessage) {
			om.replyTo.tell(response);
		}
		return Behaviors.same();
	}

	/**
	 * Returns a behavior for the messages:
	 *
	 * <ul>
	 * <li>{@link LoadWorldMessage}
	 * <li>{@link WrappedDbResponse}
	 * <li>{@link WrappedObjectsResponse}
	 * </ul>
	 */
	private BehaviorBuilder<Message> getInitialBehavior() {
		return Behaviors.receive(Message.class)//
				.onMessage(LoadWorldMessage.class, this::onLoadWorld)//
				.onMessage(WrappedDbResponse.class, this::onWrappedDbResponse)//
				.onMessage(WrappedObjectsResponse.class, this::onWrappedObjectsResponse)//
		;
	}
}
