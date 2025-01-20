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
package com.anrisoftware.dwarfhustle.gamemap.jme.lights;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameMap.getGameMap;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.anrisoftware.dwarfhustle.gamemap.jme.lights.SunTaskWorker.SunTaskWorkerFactory;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.GameTickMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetWorldMapMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.javadsl.StashOverflowException;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
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
public class SunActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, SunActor.class.getSimpleName());

    public static final String NAME = SunActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {
        public final LightAppState lightAppState;
        public final ObjectsGetter og;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class SetupErrorMessage extends Message {
        public final Throwable cause;
    }

    /**
     * Factory to create {@link SunActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface SunActorFactory {
        SunActor create(ActorContext<Message> context, StashBuffer<Message> stash);
    }

    /**
     * Creates the {@link SunActor}.
     */
    @SuppressWarnings("unchecked")
    public static Behavior<Message> create(Injector injector) {
        return Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            context.pipeToSelf(createState(injector, context), (result, cause) -> {
                if (cause == null) {
                    return (Message) result;
                } else {
                    return new SetupErrorMessage(cause);
                }
            });
            return (Behavior<Message>) injector.getInstance(SunActorFactory.class).create(context, stash)
                    .start(injector);
        }));
    }

    /**
     * Creates the {@link SunActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector));
    }

    private static CompletionStage<? extends Message> createState(Injector injector,
            ActorContext<? extends Message> context) {
        return CompletableFuture.supplyAsync(() -> attachState(injector));
    }

    private static Message attachState(Injector injector) {
        var app = injector.getInstance(Application.class);
        var lightAppState = injector.getInstance(LightAppState.class);
        var actor = injector.getInstance(ActorSystemProvider.class);
        try {
            var f = app.enqueue(() -> {
                app.getStateManager().attach(lightAppState);
                var og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
                return new InitialStateMessage(lightAppState, og);
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
    private SunTaskWorkerFactory sunTaskWorkerFactory;

    @Inject
    private Application app;

    private InitialStateMessage is;

    private Optional<SunTaskWorker> sunTaskWorker = Optional.empty();

    private long currentMap;

    private long currentWorld;

    /**
     * Stash behavior. Returns a behavior for the messages:
     *
     * <ul>
     * <li>{@link InitialStateMessage}
     * <li>{@link SetupErrorMessage}
     * <li>{@link Message}
     * </ul>
     */
    public Behavior<? extends Message> start(Injector injector) {
        return Behaviors.receive(Message.class)//
                .onMessage(InitialStateMessage.class, this::onInitialState)//
                .onMessage(SetupErrorMessage.class, this::onSetupError)//
                .onMessage(GameTickMessage.class, this::ignoreMessage)//
                .onMessage(Message.class, this::stashOtherCommand)//
                .build();
    }

    private Behavior<Message> ignoreMessage(Message m) {
        return Behaviors.same();
    }

    private Behavior<Message> stashOtherCommand(Message m) {
        log.trace("stashOtherCommand: {}", m);
        try {
            buffer.stash(m);
        } catch (StashOverflowException e) {
            log.warn("Stash message overflow");
        }
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
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     * Reacts to the {@link GameTickMessage} message.
     */
    private Behavior<Message> onGameTick(GameTickMessage m) {
        app.enqueue(this::runSunTaskWorker);
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onSetWorldMap(SetWorldMapMessage m) {
        this.currentWorld = m.wm;
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onSetGameMap(SetGameMapMessage m) {
        this.currentMap = m.gm;
        app.enqueue(this::createSunTaskWorker);
        return Behaviors.same();
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
                .onMessage(SetWorldMapMessage.class, this::onSetWorldMap)//
                .onMessage(SetGameMapMessage.class, this::onSetGameMap)//
                .onMessage(GameTickMessage.class, this::onGameTick)//
        ;
    }

    private void createSunTaskWorker() {
        var wm = WorldMap.getWorldMap(is.og, currentWorld);
        var gm = getGameMap(is.og, currentMap);
        this.sunTaskWorker = Optional.of(sunTaskWorkerFactory.create(wm, gm));
    }

    private void runSunTaskWorker() {
        sunTaskWorker.ifPresent(this::runSunTaskWorker0);
    }

    private void runSunTaskWorker0(SunTaskWorker w) {
        w.run();
    }

}
