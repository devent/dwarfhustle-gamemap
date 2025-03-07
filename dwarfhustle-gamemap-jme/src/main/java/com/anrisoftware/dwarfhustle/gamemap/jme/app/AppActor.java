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
import static java.util.concurrent.TimeUnit.SECONDS;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.anrisoftware.dwarfhustle.gamemap.console.actor.ConsoleActor;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetTimeWorldMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetVisibleDepthLayersMessage;
import com.anrisoftware.dwarfhustle.gamemap.jme.lights.SunActor;
import com.anrisoftware.dwarfhustle.gamemap.model.cache.AppCachesConfig;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppCommand;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AssetsResponseMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage.LoadModelsErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage.LoadModelsSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadWorldMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetWorldMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.materials.Gas;
import com.anrisoftware.dwarfhustle.model.api.materials.IgneousExtrusive;
import com.anrisoftware.dwarfhustle.model.api.materials.IgneousIntrusive;
import com.anrisoftware.dwarfhustle.model.api.materials.Liquid;
import com.anrisoftware.dwarfhustle.model.api.materials.Metamorphic;
import com.anrisoftware.dwarfhustle.model.api.materials.Sedimentary;
import com.anrisoftware.dwarfhustle.model.api.materials.Soil;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage.CacheErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeResponseMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeResponseMessage.KnowledgeResponseErrorMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeResponseMessage.KnowledgeResponseSuccessMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.javadsl.TimerScheduler;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

    private static final Duration ACTOR_TIMEOUT = Duration.ofSeconds(30);

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, AppActor.class.getSimpleName());

    public static final String NAME = AppActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    private static final String CHECK_GAMEMAP_CACHED_TIMER = "CHECK_GAMEMAP_CACHED_TIMER";

    @RequiredArgsConstructor
    @ToString
    private static class InitialStateMessage extends Message {
        public final ActorRef<Message> objectsActor;
        public final ActorRef<Message> dbActor;
    }

    @RequiredArgsConstructor
    @ToString
    private static class SetupErrorMessage extends Message {
        public final Throwable cause;
    }

    @RequiredArgsConstructor
    @ToString
    private static class CheckGameMapCachedMessage extends Message {
        private final ActorRef<Message> loadActor;
        private final GameMap gm;
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
    private static class WrappedAssetsCacheResponse extends Message {
        private final AssetsResponseMessage<?> response;
    }

    /**
     * Factory to create {@link AppActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface AppActorFactory {
        AppActor create(ActorContext<Message> context, StashBuffer<Message> stash, TimerScheduler<Message> timer,
                AppCommand command);
    }

    /**
     * Creates the {@link AppActor}.
     */
    public static Behavior<Message> create(Injector injector, AppCommand command) {
        return Behaviors.withTimers(timer -> Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            context.pipeToSelf(createActors(injector, command, context), (result, cause) -> {
                if (cause == null) {
                    return result;
                } else {
                    log.error("AppActor.create", cause);
                    return new SetupErrorMessage(cause);
                }
            });
            return injector.getInstance(AppActorFactory.class).create(context, stash, timer, command).start(injector);
        })));
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

    @SneakyThrows
    private static Message createActors0(Injector injector, AppCommand command) {
        injector.getInstance(AppCachesConfig.class).create(command.getGamedir());
        var actor = injector.getInstance(ActorSystemProvider.class);
        createDb(injector, actor, command);
        createPowerLoom(injector, actor);
        createConsole(injector, actor);
        createObjectsCache(injector, actor);
        createMaterialAssetsCacheActor(injector, actor);
        createModelsAssetsCacheActor(injector, actor);
        createSunActor(injector, actor);
        return new InitialStateMessage(
                actor.getActorAsync(StoredObjectsJcsCacheActor.ID).toCompletableFuture().get(30, SECONDS),
                actor.getActorAsync(OrientDbActor.ID).toCompletableFuture().get(30, SECONDS));
    }

    private static void createSunActor(Injector injector, ActorSystemProvider actor) {
        var task = SunActor.create(injector, ACTOR_TIMEOUT);
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("SunActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("SunActor created");
            }
        });
    }

    private static void createObjectsCache(Injector injector, ActorSystemProvider actor) {
        var task = StoredObjectsJcsCacheActor.create(injector, ACTOR_TIMEOUT,
                actor.getObjectGetterAsync(OrientDbActor.ID));
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("ObjectsJcsCacheActor.create", ex);
            } else {
                log.debug("ObjectsJcsCacheActor created");
            }
        });
    }

    private static void createMaterialAssetsCacheActor(Injector injector, ActorSystemProvider actor) {
        var task = MaterialAssetsCacheActor.create(injector, ACTOR_TIMEOUT);
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("MaterialAssetsCacheActor.create", ex);
            } else {
                log.debug("MaterialAssetsCacheActor created");
            }
        });
    }

    private static void createModelsAssetsCacheActor(Injector injector, ActorSystemProvider actor) {
        var task = ModelsAssetsCacheActor.create(injector, ACTOR_TIMEOUT);
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("ModelsAssetsCacheActor.create", ex);
            } else {
                log.debug("ModelsAssetsCacheActor created");
            }
        });
    }

    private static void createDb(Injector injector, ActorSystemProvider actor, AppCommand command) {
        OrientDbActor.create(injector, ACTOR_TIMEOUT).whenComplete((ret, ex) -> {
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

    private static void createPowerLoom(Injector injector, ActorSystemProvider actor) {
        PowerLoomKnowledgeActor.create(injector, ACTOR_TIMEOUT, actor.getActorAsync(KnowledgeJcsCacheActor.ID))
                .whenComplete((ret, ex) -> {
                    if (ex != null) {
                        log.error("PowerLoomKnowledgeActor.create", ex);
                        actor.tell(new AppErrorMessage(ex));
                    } else {
                        log.debug("PowerLoomKnowledgeActor created");
                        createKnowledgeCache(injector, actor, ret);
                    }
                });
    }

    private static void createKnowledgeCache(Injector injector, ActorSystemProvider actor,
            ActorRef<Message> powerLoom) {
        KnowledgeJcsCacheActor.create(injector, ACTOR_TIMEOUT, actor.getObjectGetterAsync(PowerLoomKnowledgeActor.ID))
                .whenComplete((ret, ex) -> {
                    if (ex != null) {
                        log.error("KnowledgeJcsCacheActor.create", ex);
                        actor.tell(new AppErrorMessage(ex));
                    } else {
                        log.debug("KnowledgeJcsCacheActor created");
                    }
                });
    }

    private static void createConsole(Injector injector, ActorSystemProvider actor) {
        ConsoleActor.create(injector, ACTOR_TIMEOUT).whenComplete((ret, ex) -> {
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
    private TimerScheduler<Message> timer;

    @Inject
    @Assisted
    private AppCommand command;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameSettingsProvider ogs;

    @Inject
    private Application app;

    @SuppressWarnings("rawtypes")
    private ActorRef<DbResponseMessage> dbAdapter;

    @SuppressWarnings("rawtypes")
    private ActorRef<CacheResponseMessage> objectsAdapter;

    private ActorRef<KnowledgeResponseMessage> knowledgeAdapter;

    private final URL dbConfig = AppActor.class.getResource("/orientdb-config.xml");

    private Injector injector;

    @SuppressWarnings("rawtypes")
    private ActorRef<AssetsResponseMessage> assetsCacheAdapter;

    private boolean knowledgeLoaded = false;

    private boolean texturesLoaded = false;

    private boolean modelsLoaded = false;

    private final boolean mapCacheLoaded = false;

    private ActorRef<Message> objectsActor;

    private ActorRef<Message> dbActor;

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
        this.dbAdapter = context.messageAdapter(DbResponseMessage.class, WrappedDbResponse::new);
        this.objectsAdapter = context.messageAdapter(CacheResponseMessage.class, WrappedCacheResponse::new);
        this.knowledgeAdapter = context.messageAdapter(KnowledgeResponseMessage.class, WrappedKnowledgeResponse::new);
        this.assetsCacheAdapter = context.messageAdapter(AssetsResponseMessage.class, WrappedAssetsCacheResponse::new);
        return Behaviors.receive(Message.class)//
                .onMessage(InitialStateMessage.class, this::onInitialState)//
                .onMessage(SetupErrorMessage.class, this::onSetupError)//
                .onMessage(Message.class, this::stashOtherCommand)//
                .build();
    }

    private Behavior<Message> stashOtherCommand(Message m) {
        log.trace("stashOtherCommand: {}", m);
        buffer.stash(m);
        return Behaviors.same();
    }

    private Behavior<Message> onSetupError(SetupErrorMessage m) {
        log.trace("onSetupError: {}", m);
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
        log.trace("onInitialState");
        this.objectsActor = m.objectsActor;
        this.dbActor = m.dbActor;
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     * Returns a behavior for the messages from {@link #getInitialBehavior()}
     */
    private Behavior<Message> onLoadWorld(LoadWorldMessage m) {
        log.trace("onLoadWorld {}", m);
        actor.tell(new LoadTexturesMessage<>(assetsCacheAdapter));
        actor.tell(new LoadModelsMessage<>(assetsCacheAdapter));
        loadKnowledge();
        if (command.isUseRemoteDb()) {
            dbActor.tell(new ConnectDbRemoteMessage<>(dbAdapter, command.getDbServer(), command.getDbName(),
                    command.getDbUser(), command.getDbPassword()));
        } else {
            dbActor.tell(new StartEmbeddedServerMessage<>(dbAdapter, m.dir.getAbsolutePath(), dbConfig));
        }
        return Behaviors.same();
    }

    private void loadKnowledge() {
        actor.tell(cKgM(Sedimentary.class, Sedimentary.TYPE));
        actor.tell(cKgM(IgneousIntrusive.class, IgneousIntrusive.TYPE));
        actor.tell(cKgM(IgneousExtrusive.class, IgneousExtrusive.TYPE));
        actor.tell(cKgM(Metamorphic.class, Metamorphic.TYPE));
        actor.tell(cKgM(Liquid.class, Liquid.TYPE));
        actor.tell(cKgM(Soil.class, Soil.TYPE));
        actor.tell(cKgM(Gas.class, Gas.TYPE));
    }

    private KnowledgeGetMessage<KnowledgeResponseMessage> cKgM(Class<? extends GameObject> typeClass, String type) {
        return new KnowledgeGetMessage<>(knowledgeAdapter, typeClass, type);
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onSetWorldMap(SetWorldMapMessage m) {
        log.trace("onSetWorldMap {}", m);
        objectsActor.tell(new CachePutMessage<>(objectsAdapter, m.wm.getId(), m.wm));
        ogs.get().currentWorld.set(m.wm);
        dbActor.tell(new LoadObjectMessage<>(dbAdapter, GameMap.OBJECT_TYPE, db -> {
            var query = "SELECT * from ? where objecttype = ? and objectid = ?";
            return db.query(query, GameMap.OBJECT_TYPE, GameMap.OBJECT_TYPE, m.wm.currentMap);
        }));
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onSetVisibleDepthLayers(SetVisibleDepthLayersMessage m) {
        log.trace("onSetVisibleDepthLayers {}", m);
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
        log.trace("onSetGameMap {}", m);
        objectsActor.tell(new CachePutMessage<>(objectsAdapter, m.gm.getId(), m.gm));
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onSetTimeWorld(SetTimeWorldMessage m) {
        log.trace("onSetTimeWorld {}", m);
        ogs.get().currentWorld.get().setTime(LocalDateTime.from(ogs.get().currentWorld.get().getTime()).with(m.time));
        return Behaviors.same();
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
        log.trace("onWrappedDbResponse {}", m);
        var response = m.response;
        if (response instanceof DbErrorMessage<?> rm) {
            log.error("Db error", rm);
            actor.tell(new AppErrorMessage(rm.error));
            return Behaviors.stopped();
        } else if (response instanceof StartEmbeddedServerSuccessMessage) {
            log.debug("Started embedded server");
            var rm = (StartEmbeddedServerSuccessMessage<?>) response;
            dbActor.tell(new ConnectDbEmbeddedMessage<>(dbAdapter, rm.server, command.getDbName(), command.getDbUser(),
                    command.getDbPassword()));
        } else if (response instanceof ConnectDbSuccessMessage) {
            log.debug("Connected to server");
            dbActor.tell(new LoadObjectMessage<>(dbAdapter, WorldMap.OBJECT_TYPE, db -> {
                var query = "SELECT * from ? limit 1";
                return db.query(query, WorldMap.OBJECT_TYPE);
            }));
        } else if (response instanceof LoadObjectSuccessMessage<?> rm) {
            log.debug("Loaded object successfully {}", rm.go);
            if (rm.go instanceof WorldMap wm) {
                ogs.get().currentWorld.set(wm);
                actor.tell(new SetWorldMapMessage(wm));
            } else if (rm.go instanceof GameMap gm) {
                ogs.get().currentMap.set(gm);
                actor.tell(new SetGameMapMessage(gm));
            }
        } else if (response instanceof LoadObjectNotFoundMessage rm) {
            log.error("Object not found", rm);
            actor.tell(new AppErrorMessage(new Exception("Object not found")));
            return Behaviors.stopped();
        }
        return Behaviors.same();
    }

    private void createGameTickActor() {
        GameTickActor.create(injector, ACTOR_TIMEOUT).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("GameTickActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("GameTickActor created");
            }
        });
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
        log.trace("onWrappedKnowledgeBaseResponse {}", m);
        if (m.response instanceof KnowledgeResponseErrorMessage rm) {
            log.error("Error load knowledge", rm.error);
            actor.tell(new AppErrorMessage(rm.error));
            return Behaviors.stopped();
        } else if (m.response instanceof KnowledgeResponseSuccessMessage rm) {
            this.knowledgeLoaded = true;
        }
        return Behaviors.same();
    }

    /**
     * Handles {@link WrappedAssetsCacheResponse}. Returns a behavior for the
     * messages from {@link #getInitialBehavior()}.
     */
    private Behavior<Message> onWrappedAssetsCacheResponse(WrappedAssetsCacheResponse m) {
        log.trace("onWrappedMaterialAssetsResponse {}", m);
        if (m.response instanceof LoadTexturesErrorMessage rm) {
            log.error("Error load textures", rm.e);
            actor.tell(new AppErrorMessage(rm.e));
            return Behaviors.stopped();
        } else if (m.response instanceof LoadModelsErrorMessage rm) {
            log.error("Error load models", rm.e);
            actor.tell(new AppErrorMessage(rm.e));
            return Behaviors.stopped();
        } else if (m.response instanceof LoadTexturesSuccessMessage rm) {
            this.texturesLoaded = true;
        } else if (m.response instanceof LoadModelsSuccessMessage rm) {
            this.modelsLoaded = true;
        }
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onShutdown(ShutdownMessage m) {
        log.trace("onShutdown {}", m);
        app.enqueue(() -> {
        });
        return Behaviors.stopped();
    }

    /**
     * Returns a behavior for the messages:
     *
     * <ul>
     * <li>{@link LoadWorldMessage}
     * <li>{@link SetWorldMapMessage}
     * <li>{@link ShutdownMessage}
     * <li>{@link SetVisibleDepthLayersMessage}
     * <li>{@link SetGameMapMessage}
     * <li>{@link SetTimeWorldMessage}
     * <li>{@link WrappedDbResponse}
     * <li>{@link WrappedCacheResponse}
     * <li>{@link WrappedAssetsCacheResponse}
     * </ul>
     */
    private BehaviorBuilder<Message> getInitialBehavior() {
        return Behaviors.receive(Message.class)//
                .onMessage(LoadWorldMessage.class, this::onLoadWorld)//
                .onMessage(SetWorldMapMessage.class, this::onSetWorldMap)//
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(SetVisibleDepthLayersMessage.class, this::onSetVisibleDepthLayers)//
                .onMessage(SetGameMapMessage.class, this::onSetGameMap)//
                .onMessage(SetTimeWorldMessage.class, this::onSetTimeWorld)//
                .onMessage(WrappedDbResponse.class, this::onWrappedDbResponse)//
                .onMessage(WrappedCacheResponse.class, this::onWrappedCacheResponse)//
                .onMessage(WrappedKnowledgeResponse.class, this::onWrappedKnowledgeResponse)//
                .onMessage(WrappedAssetsCacheResponse.class, this::onWrappedAssetsCacheResponse)//
        ;
    }
}
