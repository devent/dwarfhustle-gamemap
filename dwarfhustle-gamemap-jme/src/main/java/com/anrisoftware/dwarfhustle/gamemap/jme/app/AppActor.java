/*
 * dwarfhustle-gamemap-jme - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.jme.app;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static java.time.Duration.ofSeconds;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.anrisoftware.dwarfhustle.gamemap.console.actor.ConsoleActor;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetLayersTerrainMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetTimeWorldMessage;
import com.anrisoftware.dwarfhustle.gamemap.jme.lights.SunActor;
import com.anrisoftware.dwarfhustle.gamemap.model.cache.AppCachesConfig;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppCommand;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AssetsResponseMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadWorldMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapBlockLoadedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetWorldMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.materials.Gas;
import com.anrisoftware.dwarfhustle.model.api.materials.IgneousExtrusive;
import com.anrisoftware.dwarfhustle.model.api.materials.IgneousIntrusive;
import com.anrisoftware.dwarfhustle.model.api.materials.Metamorphic;
import com.anrisoftware.dwarfhustle.model.api.materials.Sedimentary;
import com.anrisoftware.dwarfhustle.model.api.materials.Soil;
import com.anrisoftware.dwarfhustle.model.api.materials.SpecialStoneLayer;
import com.anrisoftware.dwarfhustle.model.api.objects.GameChunkPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage.CacheErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.ConnectDbEmbeddedMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.ConnectDbRemoteMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.ConnectDbSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DbMessage.DbErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DbMessage.DbResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.LoadObjectMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.LoadObjectMessage.LoadObjectSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.LoadObjectsMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.OrientDbActor;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.StartEmbeddedServerMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.StartEmbeddedServerMessage.StartEmbeddedServerSuccessMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeResponseMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeResponseMessage.KnowledgeResponseErrorMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeResponseMessage.KnowledgeResponseSuccessMessage;
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
import jakarta.inject.Inject;
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
    @ToString
    private static class InitialStateMessage extends Message {
    }

    @RequiredArgsConstructor
    @ToString
    private static class SetupErrorMessage extends Message {
        public final Throwable cause;
    }

    @RequiredArgsConstructor
    @ToString
    private static class LoadKnowledgeMessage extends Message {
    }

    @RequiredArgsConstructor
    @ToString
    private static class WrappedDbResponse extends Message {
        private final DbResponseMessage<?> response;
    }

    @RequiredArgsConstructor
    @ToString
    private static class WrappedCacheResponse extends Message {
        private final CacheResponseMessage<?> response;
    }

    @RequiredArgsConstructor
    @ToString
    private static class WrappedKnowledgeResponse extends Message {
        private final KnowledgeResponseMessage response;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedAssetsResponse extends Message {
        private final AssetsResponseMessage<?> response;
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
        injector.getInstance(AppCachesConfig.class).create(command.getGamedir());
        createDb(injector, command);
        createPowerLoom(injector);
        createConsole(injector);
        createObjectsCache(injector);
        createAssetsActor(injector);
        return new InitialStateMessage();
    }

    private static void createObjectsCache(Injector injector) {
        var actor = injector.getInstance(ActorSystemProvider.class);
        var task = StoredObjectsJcsCacheActor.create(injector, Duration.ofSeconds(30),
                actor.getObjectsAsync(OrientDbActor.ID));
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("ObjectsJcsCacheActor.create", ex);
            } else {
                log.debug("ObjectsJcsCacheActor created");
            }
        });
    }

    private static void createAssetsActor(Injector injector) {
        var task = MaterialAssetsJcsCacheActor.create(injector, Duration.ofSeconds(30));
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("AssetsActor.create", ex);
            } else {
                log.debug("AssetsActor created");
            }
        });
    }

    private static void createDb(Injector injector, AppCommand command) {
        var actor = injector.getInstance(ActorSystemProvider.class);
        OrientDbActor.create(injector, ofSeconds(1)).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("OrientDbActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("OrientDbActor created");
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
        PowerLoomKnowledgeActor.create(injector, ofSeconds(1), actor.getActorAsync(StoredObjectsJcsCacheActor.ID))
                .whenComplete((ret, ex) -> {
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
        KnowledgeJcsCacheActor.create(injector, ofSeconds(10), actor.getObjectsAsync(PowerLoomKnowledgeActor.ID))
                .whenComplete((ret, ex) -> {
                    if (ex != null) {
                        log.error("KnowledgeJcsCacheActor.create", ex);
                        actor.tell(new AppErrorMessage(ex));
                    } else {
                        log.debug("KnowledgeJcsCacheActor created");
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
    private Application app;

    @Inject
    private Engine engine;

    @SuppressWarnings("rawtypes")
    private ActorRef<DbResponseMessage> dbResponseAdapter;

    @SuppressWarnings("rawtypes")
    private ActorRef<CacheResponseMessage> cacheResponseAdapter;

    private ActorRef<KnowledgeResponseMessage> knowledgeResponseAdapter;

    private URL dbConfig = AppActor.class.getResource("/orientdb-config.xml");

    private Injector injector;

    private int currentMapBlocksLoaded;

    private int currentMapBlocksCount;

    private MapChunk currentMapRootBlock;

    @SuppressWarnings("rawtypes")
    private ActorRef<AssetsResponseMessage> assetsResponseAdapter;

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
        this.cacheResponseAdapter = context.messageAdapter(CacheResponseMessage.class, WrappedCacheResponse::new);
        this.knowledgeResponseAdapter = context.messageAdapter(KnowledgeResponseMessage.class,
                WrappedKnowledgeResponse::new);
        this.assetsResponseAdapter = context.messageAdapter(AssetsResponseMessage.class, WrappedAssetsResponse::new);
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
        actor.tell(new LoadTexturesMessage<>(assetsResponseAdapter));
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
        actor.tell(new CachePutMessage<>(cacheResponseAdapter, m.wm.getId(), m.wm));
        ogs.get().currentWorld.set(m.wm);
        actor.tell(new LoadObjectMessage<>(dbResponseAdapter, GameMap.OBJECT_TYPE, db -> {
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
    private Behavior<Message> onSetLayersTerrain(SetLayersTerrainMessage m) {
        log.debug("onSetLayersTerrain {}", m);
        ogs.get().visibleDepthLayers.set(m.layers);
        return Behaviors.same();
    }

    /**
     * Starts the loading of the map blocks by first retrieving the root map block
     * from the database. On success the method
     * {@link #onWrappedObjectsResponse(WrappedObjectsResponse)} with the message
     * {@link LoadObjectSuccessMessage} with a {@link MapChunk} object should be
     * called.
     */
    private Behavior<Message> onSetGameMap(SetGameMapMessage m) {
        log.debug("onSetGameMap {}", m);
        actor.tell(new CachePutMessage<>(cacheResponseAdapter, m.gm.getId(), m.gm));
        loadRootMapBlock(m);
        return Behaviors.same();
    }

    private void loadRootMapBlock(SetGameMapMessage m) {
        actor.tell(new LoadObjectMessage<>(dbResponseAdapter, MapChunk.OBJECT_TYPE, db -> {
            var w = m.gm.getWidth();
            var h = m.gm.getHeight();
            var d = m.gm.getDepth();
            var p = new GameChunkPos(m.gm.getMapid(), 0, 0, 0, w, h, d);
            var query = "SELECT * from ? where objecttype = ? and mapid = ? and sx = ? and sy = ? and sz = ? and ex = ? and ey = ? and ez = ? limit 1";
            return db.query(query, MapChunk.OBJECT_TYPE, MapChunk.OBJECT_TYPE, p.getMapid(), p.getX(), p.getY(),
                    p.getZ(), p.getEp().getX(), p.getEp().getY(), p.getEp().getZ());
        }));
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
     * Sends {@link KnowledgeGetMessage} to pre-cache knowledge objects. Should
     * return with {@link KnowledgeResponseSuccessMessage} or
     * {@link KnowledgeResponseErrorMessage} that is received on
     * {@link #onWrappedKnowledgeResponse(WrappedKnowledgeResponse)}.
     */
    private Behavior<Message> onLoadKnowledge(LoadKnowledgeMessage m) {
        log.debug("onLoadKnowledge {}", m);
        actor.tell(cKgM(Sedimentary.class, Sedimentary.TYPE));
        actor.tell(cKgM(IgneousIntrusive.class, IgneousIntrusive.TYPE));
        actor.tell(cKgM(IgneousExtrusive.class, IgneousExtrusive.TYPE));
        actor.tell(cKgM(Metamorphic.class, Metamorphic.TYPE));
        actor.tell(cKgM(SpecialStoneLayer.class, SpecialStoneLayer.TYPE));
        actor.tell(cKgM(Soil.class, Soil.TYPE));
        actor.tell(cKgM(Gas.class, Gas.TYPE));
        return Behaviors.same();
    }

    private KnowledgeGetMessage<KnowledgeResponseMessage> cKgM(Class<? extends GameObject> typeClass, String type) {
        return new KnowledgeGetMessage<>(knowledgeResponseAdapter, typeClass, type);
    }

    /**
     * Reacts to the {@link WrappedObjectsResponse} message from the objects actor.
     * <dl>
     * <dt>{@link LoadObjectErrorMessage}</dt>
     * <dd>Sends the {@link AppErrorMessage} and stops the actor.</dd>
     * <dt>{@link LoadObjectsErrorMessage}</dt>
     * <dd>Sends the {@link AppErrorMessage} and stops the actor.</dd>
     * <dt>{@link LoadObjectSuccessMessage}</dt>
     * <dd>Depending on the returned game object:
     * <dl>
     * </dl>
     * <dt>{@link WorldMap}</dt>
     * <dd>Caches the world map. Sends the {@link SetWorldMapMessage} message.</dd>
     * <dt>{@link GameMap}</dt>
     * <dd>Caches the game map. Sends the {@link SetGameMapMessage} message. Sends
     * the {@link LoadMapTilesMessage}.</dd>
     * <dt>{@link MapChunk}</dt>
     * <dd>Caches the map block. Retrieves all child map blocks from the root map
     * block.</dd>
     * </dl>
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
            actor.tell(new LoadObjectMessage<>(dbResponseAdapter, WorldMap.OBJECT_TYPE, db -> {
                var query = "SELECT * from ? limit 1";
                return db.query(query, WorldMap.OBJECT_TYPE);
            }));
        } else if (response instanceof LoadObjectSuccessMessage<?> rm) {
            if (rm.go instanceof WorldMap wm) {
                ogs.get().currentWorld.set(wm);
                actor.tell(new SetWorldMapMessage(wm));
            } else if (rm.go instanceof GameMap gm) {
                gm.setWorld(ogs.get().currentWorld.get().id);
                ogs.get().currentMap.set(gm);
                actor.tell(new SetGameMapMessage(gm));
            } else if (rm.go instanceof MapChunk mb) {
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

    private void retrieveChildMapBlocks(GameChunkPos p) {
        actor.tell(new LoadObjectsMessage<>(dbResponseAdapter, MapChunk.OBJECT_TYPE, go -> {
            var mb = (MapChunk) go;
            actor.tell(new CachePutMessage<>(cacheResponseAdapter, mb.getId(), mb));
            currentMapBlocksLoaded++;
            if (currentMapBlocksLoaded == currentMapBlocksCount) {
                mapBlockLoaded();
            }
        }, db -> createQuery(p, db)));
    }

    private void mapBlockLoaded() {
        app.enqueue(() -> {
        });
        createSunActor();
        actor.tell(new MapBlockLoadedMessage(currentMapRootBlock));
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

    private OResultSet createQuery(GameChunkPos p, ODatabaseDocument db) {
        var query = "SELECT * from ? where mapid = ? and sx >= ? and sy >= ? and sz >= ? and ex <= ? and ey <= ? and ez <= ?";
        return db.query(query, MapChunk.OBJECT_TYPE, p.getMapid(), p.getX(), p.getY(), p.getZ(), p.getEp().getX(),
                p.getEp().getY(), p.getEp().getZ());
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
     * Handles {@link WrappedKnowledgeResponse}. Returns a behavior for the messages
     * from {@link #getInitialBehavior()}.
     */
    private Behavior<Message> onWrappedKnowledgeResponse(WrappedKnowledgeResponse m) {
        log.debug("onWrappedKnowledgeBaseResponse {}", m);
        if (m.response instanceof KnowledgeResponseErrorMessage rm) {
            log.error("Error load materials", rm.error);
            actor.tell(new AppErrorMessage(rm.error));
            return Behaviors.stopped();
        } else if (m.response instanceof KnowledgeResponseSuccessMessage rm) {
        }
        return Behaviors.same();
    }

    /**
     * Handles {@link WrappedAssetsResponse}. Returns a behavior for the messages
     * from {@link #getInitialBehavior()}.
     */
    private Behavior<Message> onWrappedAssetsResponse(WrappedAssetsResponse m) {
        log.debug("onWrappedAssetsResponse {}", m);
        if (m.response instanceof LoadTexturesErrorMessage<?> rm) {
            log.error("Error load textures", rm.e);
            actor.tell(new AppErrorMessage(rm.e));
            return Behaviors.stopped();
        } else if (m.response instanceof LoadTexturesSuccessMessage<?> rm) {

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
     * <li>{@link LoadKnowledgeMessage}
     * <li>{@link WrappedDbResponse}
     * <li>{@link WrappedCacheResponse}
     * <li>{@link WrappedAssetsResponse}
     * </ul>
     */
    private BehaviorBuilder<Message> getInitialBehavior() {
        return Behaviors.receive(Message.class)//
                .onMessage(LoadWorldMessage.class, this::onLoadWorld)//
                .onMessage(SetWorldMapMessage.class, this::onSetWorldMap)//
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(SetLayersTerrainMessage.class, this::onSetLayersTerrain)//
                .onMessage(SetGameMapMessage.class, this::onSetGameMap)//
                .onMessage(SetTimeWorldMessage.class, this::onSetTimeWorld)//
                .onMessage(LoadKnowledgeMessage.class, this::onLoadKnowledge)//
                .onMessage(WrappedDbResponse.class, this::onWrappedDbResponse)//
                .onMessage(WrappedCacheResponse.class, this::onWrappedCacheResponse)//
                .onMessage(WrappedKnowledgeResponse.class, this::onWrappedKnowledgeResponse)//
                .onMessage(WrappedAssetsResponse.class, this::onWrappedAssetsResponse)//
        ;
    }
}
