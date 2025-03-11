/*
 * dwarfhustle-gamemap-gui-javafx-utils - Game map.
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
package com.anrisoftware.dwarfhustle.gui.javafx.actor;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameSpeedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameSpeedPauseMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameSpeedFastTriggeredMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameSpeedNormalTriggeredMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameSpeedPauseTriggeredMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameSpeedTogglePauseTriggeredMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * @see GameSpeedFastTriggeredMessage
 * @see GameSpeedNormalTriggeredMessage
 * @see GameSpeedPauseTriggeredMessage
 * @see GameSpeedTogglePauseTriggeredMessage
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class GameTimeSpeedActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            GameTimeSpeedActor.class.getSimpleName());

    public static final String NAME = GameTimeSpeedActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    /**
     * Factory to create {@link GameTimeSpeedActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface GameTimeSpeedActorFactory {
        GameTimeSpeedActor create(ActorContext<Message> context);
    }

    /**
     * Creates the {@link GameTimeSpeedActor}.
     */
    public static Behavior<Message> create(Injector injector, ActorSystemProvider actor) {
        return Behaviors.setup(
                context -> injector.getInstance(GameTimeSpeedActorFactory.class).create(context).start(injector));
    }

    /**
     * Creates the {@link GameTimeSpeedActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        val actor = injector.getInstance(ActorSystemProvider.class);
        val system = actor.getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, actor));
    }

    @Inject
    @Assisted
    private ActorContext<Message> context;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    private ActorSystemProvider actor;

    /**
     *
     */
    public Behavior<Message> start(Injector injector) {
        return getInitialBehavior()//
                .build();
    }

    /**
     * @see GameSpeedPauseTriggeredMessage
     */
    private Behavior<Message> onGameSpeedPauseTriggered(GameSpeedPauseTriggeredMessage m) {
        log.trace("onGameSpeedPauseTriggered {}", m);
        gs.get().gameTickPaused.set(true);
        actor.tell(new SetGameSpeedPauseMessage(true));
        return Behaviors.same();
    }

    /**
     * @see GameSpeedNormalTriggeredMessage
     */
    private Behavior<Message> onGameSpeedNormalTriggered(GameSpeedNormalTriggeredMessage m) {
        log.trace("onGameSpeedNormalTriggered {}", m);
        gs.get().gameTickPaused.set(false);
        gs.get().gameTickDuration.set(gs.get().gameTickNormalDuration.get());
        actor.tell(new SetGameSpeedMessage(true));
        return Behaviors.same();
    }

    /**
     * @see GameSpeedFastTriggeredMessage
     */
    private Behavior<Message> onGameSpeedFastTriggered(GameSpeedFastTriggeredMessage m) {
        log.trace("onGameSpeedFastTriggered {}", m);
        gs.get().gameTickPaused.set(false);
        gs.get().gameTickDuration.set(gs.get().gameTickFastDuration.get());
        actor.tell(new SetGameSpeedMessage(false));
        return Behaviors.same();
    }

    /**
     * @see GameSpeedTogglePauseTriggeredMessage
     */
    private Behavior<Message> onGameSpeedTogglePauseTriggered(GameSpeedTogglePauseTriggeredMessage m) {
        log.trace("onGameSpeedTogglePauseTriggered {}", m);
        if (gs.get().gameTickPaused.get()) {
            gs.get().gameTickPaused.set(false);
            actor.tell(new SetGameSpeedPauseMessage(false));
        } else {
            gs.get().gameTickPaused.set(true);
            actor.tell(new SetGameSpeedPauseMessage(true));
        }
        return Behaviors.same();
    }

    /**
     * {@see ShutdownMessage}
     */
    private BehaviorBuilder<Message> getInitialBehavior() {
        return Behaviors.receive(Message.class)//
                .onMessage(GameSpeedPauseTriggeredMessage.class, this::onGameSpeedPauseTriggered)//
                .onMessage(GameSpeedNormalTriggeredMessage.class, this::onGameSpeedNormalTriggered)//
                .onMessage(GameSpeedFastTriggeredMessage.class, this::onGameSpeedFastTriggered)//
                .onMessage(GameSpeedTogglePauseTriggeredMessage.class, this::onGameSpeedTogglePauseTriggered)//
        ;
    }

}
