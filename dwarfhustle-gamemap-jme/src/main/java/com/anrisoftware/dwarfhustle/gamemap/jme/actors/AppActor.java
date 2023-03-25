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
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.console.actor.ConsoleActor;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetLayersTerrainMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetTimeWorldMessage;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.GameTickSystem;
import com.anrisoftware.dwarfhustle.gamemap.jme.lights.SunActor;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppCommand;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadMapTilesMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadWorldMessage;
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
import com.anrisoftware.dwarfhustle.model.db.cache.AppCachesConfig;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage.CacheErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.ObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.ConnectDbEmbeddedMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.ConnectDbRemoteMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.ConnectDbSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DbResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DbResponseMessage.DbErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.OrientDbActor;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.StartEmbeddedServerMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.StartEmbeddedServerMessage.StartEmbeddedServerSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.LoadObjectMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.LoadObjectMessage.LoadObjectErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.LoadObjectMessage.LoadObjectSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.LoadObjectsMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.LoadObjectsMessage.LoadObjectsErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.ObjectsDbActor;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.ObjectsResponseMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeBaseActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.badlogic.ashley.core.Engine;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

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
        private final DbResponseMessage<?> response;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedObjectsResponse extends Message {
        private final ObjectsResponseMessage<?> response;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedCacheResponse extends Message {
        private final CacheResponseMessage<?> response;
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
                createKnowledgeCache(injector, ret);
            }
        });
    }

    private static void createKnowledgeCache(Injector injector, ActorRef<Message> powerLoom) {
        var actor = injector.getInstance(ActorSystemProvider.class);
        KnowledgeJcsCacheActor.create(injector, ofSeconds(1)).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("KnowledgeJcsCacheActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("KnowledgeJcsCacheActor created");
                createKnowledge(injector, powerLoom, ret);
            }
        });
    }

    private static void createKnowledge(Injector injector, ActorRef<Message> powerLoom, ActorRef<Message> cache) {
        var actor = injector.getInstance(ActorSystemProvider.class);
        KnowledgeBaseActor.create(injector, ofSeconds(1), powerLoom, cache).whenComplete((ret, ex) -> {
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

    @Inject
    private AppCachesConfig appCachesConfig;

    @Inject
    private Application app;

    @Inject
    private Engine engine;

    @SuppressWarnings("rawtypes")
    private ActorRef<DbResponseMessage> dbResponseAdapter;

    @SuppressWarnings("rawtypes")
    private ActorRef<ObjectsResponseMessage> objectsResponseAdapter;

    @SuppressWarnings("rawtypes")
    private ActorRef<CacheResponseMessage> cacheResponseAdapter;

    private URL dbConfig = AppActor.class.getResource("/orientdb-config.xml");

    private Injector injector;

    private GameTickSystem gameTickSystem;

    private int currentMapBlocksLoaded;

    private int currentMapBlocksCount;

    private MapBlock currentMapRootBlock;

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
        if (command.isUseRemoteServer()) {
            actor.tell(new ConnectDbRemoteMessage<>(dbResponseAdapter, command.getRemoteServer(),
                    command.getRemoteDatabase(), command.getRemoteUser(), command.getRemotePassword()));
        } else {
            actor.tell(new StartEmbeddedServerMessage<>(dbResponseAdapter, m.dir.getAbsolutePath(), dbConfig));
        }
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
        actor.tell(new LoadObjectMessage<>(objectsResponseAdapter, GameMap.OBJECT_TYPE, db -> {
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
        appCachesConfig.create(command.getGamedir(), m.gm);
        createObjectsCache(m);
        loadMapBlocks(m);
        return Behaviors.same();
    }

    private void createObjectsCache(LoadMapTilesMessage m) {
        var task = ObjectsJcsCacheActor.create(injector, Duration.ofSeconds(30));
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("ObjectsJcsCacheActor.create", ex);
            } else {
                log.debug("ObjectsJcsCacheActor created");
            }
        });
    }

    private void loadMapBlocks(LoadMapTilesMessage m) {
        actor.tell(new LoadObjectMessage<>(objectsResponseAdapter, MapBlock.OBJECT_TYPE, db -> {
            var w = m.gm.getWidth();
            var h = m.gm.getHeight();
            var d = m.gm.getDepth();
            var p = new GameBlockPos(m.gm.getMapid(), 0, 0, 0, w, h, d);
            var query = "SELECT * from ? where objecttype = ? and mapid = ? and sx = ? and sy = ? and sz = ? and ex = ? and ey = ? and ez = ? limit 1";
            return db.query(query, MapBlock.OBJECT_TYPE, MapBlock.OBJECT_TYPE, p.getMapid(), p.getX(), p.getY(),
                    p.getZ(), p.getEndPos().getX(), p.getEndPos().getY(), p.getEndPos().getZ());
        }));
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onSetLayersTerrain(SetLayersTerrainMessage m) {
        log.debug("onSetLayersTerrain {}", m);
        ogs.get().visibleDepthLayers.set(m.layers);
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onSetGameMap(SetGameMapMessage m) {
        log.debug("onSetGameMap {}", m);
        ogs.get().currentMap.set(m.gm);
        app.enqueue(() -> {
            this.gameTickSystem = injector.getInstance(GameTickSystem.class);
            engine.addSystem(gameTickSystem);
            createSunActor();
        });
        return Behaviors.same();
    }

    private void createSunActor() {
        SunActor.create(injector, ofSeconds(1)).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("SunActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("SunActor created");
            }
        });
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onSetTimeWorld(SetTimeWorldMessage m) {
        log.debug("onSetTimeWorld {}", m);
        ogs.get().currentWorld.get().setTime(LocalDateTime.from(ogs.get().currentWorld.get().getTime()).with(m.time));
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
        if (response instanceof DbErrorMessage<?> rm) {
            log.error("Db error", rm);
            actor.tell(new AppErrorMessage(rm.error));
            return Behaviors.stopped();
        } else if (response instanceof StartEmbeddedServerSuccessMessage) {
            log.debug("Started embedded server");
            var rm = (StartEmbeddedServerSuccessMessage<?>) response;
            actor.tell(new ConnectDbEmbeddedMessage<>(dbResponseAdapter, rm.server, "test", "root", "admin"));
        } else if (response instanceof ConnectDbSuccessMessage) {
            log.debug("Connected to server");
            actor.tell(new LoadObjectMessage<>(objectsResponseAdapter, WorldMap.OBJECT_TYPE, db -> {
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
        if (response instanceof LoadObjectErrorMessage<?> rm) {
            log.error("Load object error", rm.error);
            actor.tell(new AppErrorMessage(rm.error));
            return Behaviors.stopped();
        } else if (response instanceof LoadObjectsErrorMessage<?> rm) {
            log.error("Load objects error", rm.error);
            actor.tell(new AppErrorMessage(rm.error));
            return Behaviors.stopped();
        } else if (response instanceof LoadObjectSuccessMessage<?> rm) {
            if (rm.go instanceof WorldMap wm) {
                actor.tell(new CachePutMessage<>(cacheResponseAdapter, wm.getId(), wm));
                actor.tell(new SetWorldMapMessage(wm));
            } else if (rm.go instanceof GameMap gm) {
                gm.setWorld(ogs.get().currentWorld.get());
                actor.tell(new CachePutMessage<>(cacheResponseAdapter, gm.getId(), gm));
                actor.tell(new SetGameMapMessage(gm));
                actor.tell(new LoadMapTilesMessage(gm));
            } else if (rm.go instanceof MapBlock mb) {
                actor.tell(new CachePutMessage<>(cacheResponseAdapter, mb.getId(), mb));
                if (mb.isRoot()) {
                    currentMapRootBlock = mb;
                }
                if (!mb.getBlocks().isEmpty()) {
                    currentMapBlocksLoaded = 0;
                    currentMapBlocksCount = ogs.get().currentMap.get().getBlocksCount();
                    retrieveChildMapBlocks(mb.getPos());
                }
            }
        }
        return Behaviors.same();
    }

    private void retrieveChildMapBlocks(GameBlockPos p) {
        actor.tell(new LoadObjectsMessage<>(objectsResponseAdapter, MapBlock.OBJECT_TYPE, go -> {
            var mb = (MapBlock) go;
            actor.tell(new CachePutMessage<>(cacheResponseAdapter, mb.getId(), mb));
            currentMapBlocksLoaded++;
            if (currentMapBlocksLoaded == currentMapBlocksCount) {
                actor.tell(new MapBlockLoadedMessage(currentMapRootBlock));
            }
        }, db -> createQuery(p, db)));
    }

    private OResultSet createQuery(GameBlockPos p, ODatabaseDocument db) {
        var query = "SELECT * from ? where mapid = ? and sx >= ? and sy >= ? and sz >= ? and ex <= ? and ey <= ? and ez <= ?";
        return db.query(query, MapBlock.OBJECT_TYPE, p.getMapid(), p.getX(), p.getY(), p.getZ(), p.getEndPos().getX(),
                p.getEndPos().getY(), p.getEndPos().getZ());
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onWrappedCacheResponse(WrappedCacheResponse m) {
        // log.debug("onWrappedCacheResponse {}", m);
        var response = m.response;
        if (response instanceof CacheErrorMessage<?> rm) {
            log.error("Cache error", rm);
            actor.tell(new AppErrorMessage(rm.error));
            return Behaviors.stopped();
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
        app.enqueue(() -> {
            engine.removeSystem(gameTickSystem);
        });
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
     * <li>{@link SetLayersTerrainMessage}
     * <li>{@link SetGameMapMessage}
     * <li>{@link SetTimeWorldMessage}
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
                .onMessage(SetLayersTerrainMessage.class, this::onSetLayersTerrain)//
                .onMessage(SetGameMapMessage.class, this::onSetGameMap)//
                .onMessage(SetTimeWorldMessage.class, this::onSetTimeWorld)//
                .onMessage(WrappedDbResponse.class, this::onWrappedDbResponse)//
                .onMessage(WrappedObjectsResponse.class, this::onWrappedObjectsResponse)//
                .onMessage(WrappedCacheResponse.class, this::onWrappedCacheResponse)//
        ;
    }
}
