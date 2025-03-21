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
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage.LoadModelsErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage.LoadModelsSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadWorldMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetWorldMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage.CacheErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeResponseMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeResponseMessage.KnowledgeResponseErrorMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeResponseMessage.KnowledgeResponseSuccessMessage;
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
                actor.getActorAsync(StoredObjectsJcsCacheActor.ID).toCompletableFuture().get(30, SECONDS), null);
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
    }

    private static void createPowerLoom(Injector injector, ActorSystemProvider actor) {
    }

    private static void createKnowledgeCache(Injector injector, ActorSystemProvider actor,
            ActorRef<Message> powerLoom) {
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
        return Behaviors.same();
    }

    private void loadKnowledge() {
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onSetWorldMap(SetWorldMapMessage m) {
        log.trace("onSetWorldMap {}", m);
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
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onSetTimeWorld(SetTimeWorldMessage m) {
        log.trace("onSetTimeWorld {}", m);
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
                .onMessage(WrappedCacheResponse.class, this::onWrappedCacheResponse)//
                .onMessage(WrappedKnowledgeResponse.class, this::onWrappedKnowledgeResponse)//
                .onMessage(WrappedAssetsCacheResponse.class, this::onWrappedAssetsCacheResponse)//
        ;
    }
}
