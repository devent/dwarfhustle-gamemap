/*
 * dwarfhustle-gamemap-tester-gui-javafx - Game map.
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
package org.dwarfhustle.gamemap.tester.gui.javafx.actor;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameMap.getGameMap;
import static com.anrisoftware.dwarfhustle.model.api.objects.WorldMap.getWorldMap;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletionStage;

import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

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
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * @see SetTimeSetMessage
 * @see StopTimeSetMessage
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TimeSetActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, TimeSetActor.class.getSimpleName());

    public static final String NAME = TimeSetActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {
        public final ObjectsGetter og;
        public final ObjectsSetter os;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class SetupErrorMessage extends Message {
        public final Throwable cause;
    }

    /**
     * Sets the time message.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @RequiredArgsConstructor
    public static class SetTimeSetMessage extends Message {

        public final long gm;

        public final LocalDateTime time;
    }

    /**
     * Stops time set message.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class StopTimeSetMessage extends Message {
    }

    /**
     * Factory to create {@link TimeSetActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface TimeSetActorFactory {
        TimeSetActor create(ActorContext<Message> context, StashBuffer<Message> stash);
    }

    /**
     * Creates the {@link TimeSetActor}.
     */
    private static Behavior<Message> create(Injector injector, ActorSystemProvider actor) {
        return Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            context.pipeToSelf(supplyAsync(() -> setupActor(injector, actor)), (result, cause) -> {
                if (cause == null) {
                    return result;
                } else {
                    return new SetupErrorMessage(cause);
                }
            });
            return injector.getInstance(TimeSetActorFactory.class).create(context, stash).start(injector);
        }));
    }

    /**
     * Creates the {@link TimeSetActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        final var actor = injector.getInstance(ActorSystemProvider.class);
        return createNamedActor(actor.getActorSystem(), timeout, ID, KEY, NAME, create(injector, actor));
    }

    private static Message setupActor(Injector injector, ActorSystemProvider actor) {
        try {
            final var og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            final var os = actor.getObjectSetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            return new InitialStateMessage(og, os);
        } catch (final Exception ex) {
            return new SetupErrorMessage(ex);
        }
    }

    @Inject
    @Assisted
    private ActorContext<Message> context;

    @Inject
    @Assisted
    private StashBuffer<Message> buffer;

    private InitialStateMessage is;

    /**
     * Stash behavior. Returns a behavior for the messages:
     *
     * <ul>
     * <li>{@link InitialStateMessage}
     * <li>{@link SetupErrorMessage}
     * <li>{@link Message}
     * </ul>
     */
    @SneakyThrows
    public Behavior<Message> start(Injector injector) {
        return Behaviors.receive(Message.class)//
                .onMessage(InitialStateMessage.class, this::onInitialState)//
                .onMessage(SetupErrorMessage.class, this::onSetupError)//
                .onMessage(Message.class, this::stashOtherCommand)//
                .build();
    }

    private Behavior<Message> stashOtherCommand(Message m) {
        log.trace("stashOtherCommand: {}", m);
        try {
            buffer.stash(m);
        } catch (final StashOverflowException e) {
            log.error("stashOtherCommand", e);
        }
        return Behaviors.same();
    }

    private Behavior<Message> onSetupError(SetupErrorMessage m) {
        log.trace("onSetupError: {}", m);
        return Behaviors.stopped();
    }

    /**
     * Returns a behavior for the messages from {@link #getInitialBehavior()}
     */
    private Behavior<Message> onInitialState(InitialStateMessage m) {
        log.trace("onInitialState");
        this.is = m;
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     *
     */
    private Behavior<Message> onSetTimeSet(SetTimeSetMessage m) {
        log.trace("onSetTimeSet");
        val gm = getGameMap(is.og, m.gm);
        val wm = getWorldMap(is.og, gm.getWorld());
        wm.setTime(m.time);
        is.os.set(WorldMap.OBJECT_TYPE, wm);
        return Behaviors.same();
    }

    /**
    *
    */
    private Behavior<Message> onStopTimeSet(StopTimeSetMessage m) {
        log.trace("onStopTimeSet");
        return Behaviors.stopped();
    }

    /**
     * <ul>
     * <li>{@link SetTimeSetMessage}
     * <li>{@link StopTimeSetMessage}
     * </ul>
     */
    private BehaviorBuilder<Message> getInitialBehavior() {
        return Behaviors.receive(Message.class)//
                .onMessage(SetTimeSetMessage.class, this::onSetTimeSet)//
                .onMessage(StopTimeSetMessage.class, this::onStopTimeSet)//
        ;
    }
}
