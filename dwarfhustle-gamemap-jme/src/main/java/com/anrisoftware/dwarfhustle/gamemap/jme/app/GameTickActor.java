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

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.GameTickMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.TimerScheduler;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Starts and stops the periodically sending of the {@link GameTickMessage}.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class GameTickActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, GameTickActor.class.getSimpleName());

    public static final String NAME = GameTickActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    private static final String GAME_TICK_MESSAGE_TIMER_KEY = "GAME_TICK_MESSAGE_TIMER_KEY";

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class UpdateGameTickMessage extends Message {
        public final long tick;
    }

    /**
     * Factory to create {@link GameTickActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface GameTickActorFactory {
        GameTickActor create(ActorContext<Message> context, TimerScheduler<Message> timer);
    }

    /**
     * Creates the {@link GameTickActor}.
     */
    @SuppressWarnings("unchecked")
    public static Behavior<Message> create(Injector injector) {
        return Behaviors.withTimers(timer -> Behaviors.setup(context -> {
            return (Behavior<Message>) injector.getInstance(GameTickActorFactory.class).create(context, timer)
                    .start(injector);
        }));
    }

    /**
     * Creates the {@link GameTickActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector));
    }

    @Inject
    @Assisted
    private ActorContext<Message> context;

    @Inject
    @Assisted
    private TimerScheduler<Message> timer;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    private ActorSystemProvider a;

    private long tick = 0;

    /**
     * @return {@link Behavior} from {@link #getInitialBehavior()}
     */
    public Behavior<? extends Message> start(Injector injector) {
        timer.startTimerAtFixedRate(GAME_TICK_MESSAGE_TIMER_KEY, new UpdateGameTickMessage(tick++),
                gs.get().gameTickDuration.get());
        return getInitialBehavior() //
                .build();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onUpdateGameTick(UpdateGameTickMessage m) {
        a.tell(new GameTickMessage(m.tick));
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onShutdown(ShutdownMessage m) {
        timer.cancelAll();
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
                .onMessage(UpdateGameTickMessage.class, this::onUpdateGameTick)//
                .onMessage(ShutdownMessage.class, this::onShutdown)//
        ;
    }

}
