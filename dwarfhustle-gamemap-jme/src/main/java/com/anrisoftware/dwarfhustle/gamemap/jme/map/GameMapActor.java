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
package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetCameraPositionMessage;
import com.anrisoftware.dwarfhustle.gamemap.jme.terrain.TerrainCameraState;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapBlockLoadedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapCursorSetMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapCursorUpdateMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapTileUnderCursorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage.CacheGetSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage.CacheErrorMessage;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
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
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Acts on the messages:
 * <ul>
 * <li>{@link }</li>
 * </ul>
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class GameMapActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, GameMapActor.class.getSimpleName());

    public static final String NAME = GameMapActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {
        public final GameMapState gameMapState;
        public final TerrainCameraState cameraPanningState;
        public final MapCursorState mapCursorState;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class SetupErrorMessage extends Message {
        public final Throwable cause;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class UpdateModelMessage extends Message {
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedCacheResponse extends Message {
        private final CacheResponseMessage<?> response;
    }

    /**
     * Factory to create {@link GameMapActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface GameMapActorFactory {
        GameMapActor create(ActorContext<Message> context, StashBuffer<Message> stash, TimerScheduler<Message> timers);
    }

    /**
     * Creates the {@link GameMapActor}.
     */
    public static Behavior<Message> create(Injector injector) {
        return Behaviors.withTimers(timers -> Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            context.pipeToSelf(createState(injector, context), (result, cause) -> {
                if (cause == null) {
                    return result;
                } else {
                    return new SetupErrorMessage(cause);
                }
            });
            return injector.getInstance(GameMapActorFactory.class).create(context, stash, timers).start(injector);
        })));
    }

    /**
     * Creates the {@link GameMapActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector));
    }

    private static CompletionStage<Message> createState(Injector injector, ActorContext<Message> context) {
        return CompletableFuture.supplyAsync(() -> attachState(injector));
    }

    private static Message attachState(Injector injector) {
        var app = injector.getInstance(Application.class);
        var gameMapState = injector.getInstance(GameMapState.class);
        var cameraState = injector.getInstance(TerrainCameraState.class);
        var mapCursorState = injector.getInstance(MapCursorState.class);
        try {
            var f = app.enqueue(() -> {
                app.getStateManager().attach(gameMapState);
                app.getStateManager().attach(cameraState);
                app.getStateManager().attach(mapCursorState);
                return new InitialStateMessage(gameMapState, cameraState, mapCursorState);
            });
            return f.get();
        } catch (Exception ex) {
            return new SetupErrorMessage(ex);
        }
    }

    @Inject
    @Assisted
    private ActorContext<Message> context;

    @Inject
    @Assisted
    private StashBuffer<Message> buffer;

    @Inject
    @Assisted
    private TimerScheduler<Message> timers;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private Engine engine;

    @Inject
    private Application app;

    @Inject
    private GameSettingsProvider gs;

    private InitialStateMessage is;

    @SuppressWarnings("rawtypes")
    private ActorRef<CacheResponseMessage> cacheResponseAdapter;

    private Optional<Entity> cursorEntity = Optional.empty();

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
     * Returns a behavior for the messages from {@link #getInitialBehavior()}
     */
    private Behavior<Message> onInitialState(InitialStateMessage m) {
        log.debug("onInitialState");
        this.is = m;
        this.is.cameraPanningState.setSaveCamera(gm -> {
            actor.tell(new CachePutMessage<>(cacheResponseAdapter, gm.getId(), gm));
        });
        this.is.cameraPanningState.setSaveZ(gm -> {
            actor.tell(new CachePutMessage<>(cacheResponseAdapter, gm.getId(), gm));
            actor.tell(new MapCursorUpdateMessage(gm.getCursor()));
        });
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     * Reacts to the {@link SetGameMapMessage} message.
     */
    private Behavior<Message> onSetGameMap(SetGameMapMessage m) {
        log.debug("onSetGameMap {}", m);
        app.enqueue(() -> {
            is.cameraPanningState.updateCamera(m.gm);
        });
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link MapBlockLoadedMessage} message. Sends a
     * {@link AddMapChunkSceneMessage} message to add the {@link MapChunk} to the
     * scene.
     */
    private Behavior<Message> onMapBlockLoaded(MapBlockLoadedMessage m) {
        log.debug("onMapBlockLoaded {}", m);
        if (m.mb.isRoot()) {
            addMapBlockScene(m);
        }
        return Behaviors.same();
    }

    private void addMapBlockScene(MapBlockLoadedMessage m) {
        context.getSelf().tell(new AddMapChunkSceneMessage(gs.get().currentMap.get(), m.mb));
        app.enqueue(() -> {
            is.gameMapState.createMapBlockBox(gs.get().currentMap.get(), m.mb);
            is.cameraPanningState.setTerrainBounds(is.gameMapState.getModel());
            Entity cursor = engine.createEntity();
            this.cursorEntity = Optional.of(cursor);
            int z = gs.get().currentMap.get().getCursor().z;
            int y = gs.get().currentMap.get().getCursor().y;
            int x = gs.get().currentMap.get().getCursor().x;
            cursor.add(new MapCursorComponent(z, y, x));
            cursor.add(new MapTileSelectedComponent(z, y, x));
            engine.addEntity(cursor);
            timers.startTimerAtFixedRate(new UpdateModelMessage(), Duration.ofMillis(10));
        });
    }

    /**
     * Sets the camera position according to the {@link SetCameraPositionMessage}
     * message.
     */
    private Behavior<Message> onSetCameraPosition(SetCameraPositionMessage m) {
        log.debug("onSetCameraPosition {}", m);
        app.enqueue(() -> {
            is.cameraPanningState.setCameraPos(m.x, m.y, m.z);
        });
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link AddMapChunkSceneMessage} message. Creates the
     * {@link MapBlockComponent} for the render to construct map blocks and map
     * tiles of the game map.
     */
    private Behavior<Message> onAddMapBlockScene(AddMapChunkSceneMessage m) {
        // log.debug("onAddMapBlockScene {}", m);
        m.mb.getBlocks().forEachKey(pos -> {
            actor.tell(new CacheGetMessage<>(cacheResponseAdapter, MapChunk.class, MapChunk.OBJECT_TYPE, pos));
        });
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link MapCursorSetMessage} message.
     */
    private Behavior<Message> onMapCursorSet(MapCursorSetMessage m) {
        // log.debug("onMapCursorSet {}", m);
        var gm = gs.get().currentMap.get();
        gm.setCursor(gm.getCursorZ(), m.y, m.x);
        app.enqueue(() -> {
            cursorEntity.ifPresent(e -> {
                e.add(new MapCursorComponent(m.level, m.y, m.x));
            });
        });
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link MapTileUnderCursorMessage} message.
     */
    private Behavior<Message> onMapTileUnderCursor(MapTileUnderCursorMessage m) {
        // log.debug("onMapTileUnderCursor {}", m);
        app.enqueue(() -> {
            cursorEntity.ifPresent(e -> {
                e.add(new MapTileSelectedComponent(m.level, m.y, m.x));
            });
        });
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link UpdateModelMessage} message.
     */
    private Behavior<Message> onUpdateModel(UpdateModelMessage m) {
        // log.debug("onUpdateModel {}", m);
        is.gameMapState.getModel().update();
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onWrappedCacheResponse(WrappedCacheResponse m) {
        // log.debug("onWrappedCacheResponse {}", m);
        if (m.response instanceof CacheGetSuccessMessage<?> rm) {
            if (rm.go instanceof MapChunk mb) {
                context.getSelf().tell(new AddMapChunkSceneMessage(gs.get().currentMap.get(), mb));
            }
        } else if (m.response instanceof CacheErrorMessage<?> rm) {
            log.error("Cache error {}", m);
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
        timers.cancelAll();
        return Behaviors.stopped();
    }

    /**
     * Returns a behavior for the messages:
     *
     * <ul>
     * <li>{@link ShutdownMessage}
     * </ul>
     */
    private BehaviorBuilder<Message> getInitialBehavior() {
        return Behaviors.receive(Message.class)//
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(SetGameMapMessage.class, this::onSetGameMap)//
                .onMessage(MapBlockLoadedMessage.class, this::onMapBlockLoaded)//
                .onMessage(WrappedCacheResponse.class, this::onWrappedCacheResponse)//
                .onMessage(AddMapChunkSceneMessage.class, this::onAddMapBlockScene)//
                .onMessage(SetCameraPositionMessage.class, this::onSetCameraPosition)//
                .onMessage(MapCursorSetMessage.class, this::onMapCursorSet)//
                .onMessage(MapTileUnderCursorMessage.class, this::onMapTileUnderCursor)//
                .onMessage(UpdateModelMessage.class, this::onUpdateModel)//
        ;
    }
}
