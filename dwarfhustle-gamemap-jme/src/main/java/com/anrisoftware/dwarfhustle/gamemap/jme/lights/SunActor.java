/*
 * Dwarf Hustle Game Map - Game map.
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

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.jme.lights.SunTaskWorker.SunTaskWorkerFactory;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.GameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.GameTickMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
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
    private static class InitialStateMessage extends GameMapMessage {
        public final LightAppState lightAppState;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class SetupErrorMessage extends GameMapMessage {
        public final Throwable cause;
    }

    /**
     * Factory to create {@link SunActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface SunActorFactory {
        SunActor create(ActorContext<GameMapMessage> context, StashBuffer<GameMapMessage> stash);
    }

    /**
     * Creates the {@link SunActor}.
     */
    @SuppressWarnings("unchecked")
    public static Behavior<GameMapMessage> create(Injector injector) {
        return Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            context.pipeToSelf(createState(injector, context), (result, cause) -> {
                if (cause == null) {
                    return (GameMapMessage) result;
                } else {
                    return new SetupErrorMessage(cause);
                }
            });
            return (Behavior<GameMapMessage>) injector.getInstance(SunActorFactory.class).create(context, stash)
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

    private static GameMapMessage attachState(Injector injector) {
        var app = injector.getInstance(Application.class);
        var lightAppState = injector.getInstance(LightAppState.class);
        try {
            var f = app.enqueue(() -> {
                app.getStateManager().attach(lightAppState);
                return new InitialStateMessage(lightAppState);
            });
            return f.get();
        } catch (Exception ex) {
            return new SetupErrorMessage(ex);
        }
    }

    @Inject
    @Assisted
    private ActorContext<GameMapMessage> context;

    @Inject
    @Assisted
    private StashBuffer<GameMapMessage> buffer;

    @Inject
    private SunTaskWorkerFactory sunTaskWorkerFactory;

    @Inject
    private Application app;

    @Inject
    private GameSettingsProvider gs;

    private InitialStateMessage initialState;

    private Optional<SunTaskWorker> sunTaskWorker = Optional.empty();

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
        return Behaviors.receive(GameMapMessage.class)//
                .onMessage(InitialStateMessage.class, this::onInitialState)//
                .onMessage(SetupErrorMessage.class, this::onSetupError)//
                .onMessage(GameMapMessage.class, this::stashOtherCommand)//
                .build();
    }

    private Behavior<GameMapMessage> stashOtherCommand(GameMapMessage m) {
        log.debug("stashOtherCommand: {}", m);
        try {
            buffer.stash(m);
        } catch (StashOverflowException e) {
            log.warn("Stash message overflow");
        }
        return Behaviors.same();
    }

    private Behavior<GameMapMessage> onSetupError(SetupErrorMessage m) {
        log.debug("onSetupError: {}", m);
        return Behaviors.stopped();
    }

    /**
     * Returns a behavior for the messages from {@link #getInitialBehavior()}
     */
    private Behavior<GameMapMessage> onInitialState(InitialStateMessage m) {
        log.debug("onInitialState");
        this.initialState = m;
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     * Reacts to the {@link GameTickMessage} message.
     */
    private Behavior<GameMapMessage> onGameTick(GameTickMessage m) {
        app.enqueue(this::runSunTaskWorker);
        return Behaviors.same();
    }

    /**
     * Returns a behavior for the messages:
     *
     * <ul>
     * <li>{@link ShutdownMessage}
     * </ul>
     */
    private BehaviorBuilder<GameMapMessage> getInitialBehavior() {
        app.enqueue(this::createSunTaskWorker);
        return Behaviors.receive(GameMapMessage.class)//
                .onMessage(GameTickMessage.class, this::onGameTick)//
        ;
    }

    private void createSunTaskWorker() {
        this.sunTaskWorker = Optional
                .of(sunTaskWorkerFactory.create(gs.get().currentWorld.get(), gs.get().currentMap.get()));
    }

    private void runSunTaskWorker() {
        sunTaskWorker.ifPresent(this::runSunTaskWorker0);
    }

    private void runSunTaskWorker0(SunTaskWorker w) {
        w.run();
    }

}
