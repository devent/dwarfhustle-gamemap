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
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcX;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcY;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcZ;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameMap.getGameMap;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameMap.setGameMap;
import static com.anrisoftware.dwarfhustle.model.objects.RetrieveObjectsMessage.askRetrieveObjects;
import static java.time.Duration.ofMillis;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.factory.Multimaps;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetSingleBlockSelectingFinishedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetSingleBlockSelectingModeMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.map.Block;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.anrisoftware.dwarfhustle.model.objects.DeleteBulkObjectsMessage;
import com.anrisoftware.dwarfhustle.model.objects.ObjectResponseMessage;
import com.anrisoftware.dwarfhustle.model.objects.RetrieveObjectsMessage.RetrieveObjectsSuccessMessage;
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
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class ObjectDeleteActor {

    private static final Duration KNOWLEDGE_GET_TIMEOUT = ofMillis(100);

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            ObjectDeleteActor.class.getSimpleName());

    public static final String NAME = ObjectDeleteActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {
        public final ObjectsGetter og;
        public final ObjectsSetter os;
        public final ObjectsGetter cg;
        public final ObjectsSetter cs;
        public final KnowledgeGetter ko;
        public final ObjectsGetter mg;
        public final ObjectsSetter ms;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class SetupErrorMessage extends Message {
        public final Throwable cause;
    }

    /**
     * Enables single-block selection mode and waits for the user to select the
     * object to delete.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class StartDeleteObjectMessage extends Message {
    }

    /**
     * Stops single-block selection mode.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class StopDeleteObjectMessage extends Message {
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedObjectResponseMessage extends Message {
        public final ObjectResponseMessage res;
    }

    /**
     * Factory to create {@link ObjectDeleteActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface ObjectDeleteActorFactory {
        ObjectDeleteActor create(ActorContext<Message> context, StashBuffer<Message> stash);
    }

    /**
     * Creates the {@link ObjectDeleteActor}.
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
            return injector.getInstance(ObjectDeleteActorFactory.class).create(context, stash).start(injector);
        }));
    }

    /**
     * Creates the {@link ObjectDeleteActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        final var actor = injector.getInstance(ActorSystemProvider.class);
        return createNamedActor(actor.getActorSystem(), timeout, ID, KEY, NAME, create(injector, actor));
    }

    private static Message setupActor(Injector injector, ActorSystemProvider actor) {
        try {
            final var og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            final var os = actor.getObjectSetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            final var cg = actor.getObjectGetterAsyncNow(MapChunksJcsCacheActor.ID);
            final var cs = actor.getObjectSetterAsyncNow(MapChunksJcsCacheActor.ID);
            final var ko = actor.getKnowledgeGetterAsyncNow(PowerLoomKnowledgeActor.ID);
            final var mg = actor.getObjectGetterAsyncNow(MapObjectsJcsCacheActor.ID);
            final var ms = actor.getObjectSetterAsyncNow(MapObjectsJcsCacheActor.ID);
            return new InitialStateMessage(og, os, cg, cs, ko, mg, ms);
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

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameSettingsProvider gs;

    private InitialStateMessage is;

    private ActorRef<ObjectResponseMessage> objectsDeleteAdapter;

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
        this.objectsDeleteAdapter = context.messageAdapter(ObjectResponseMessage.class,
                WrappedObjectResponseMessage::new);
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     *
     */
    private Behavior<Message> onStartDeleteObject(StartDeleteObjectMessage m) {
        log.trace("onStartDeleteObject");
        actor.tell(new SetSingleBlockSelectingModeMessage(true));
        return Behaviors.same();
    }

    /**
    *
    */
    private Behavior<Message> onStopDeleteObject(StopDeleteObjectMessage m) {
        log.trace("onStopDeleteObject");
        actor.tell(new SetSingleBlockSelectingModeMessage(false));
        return Behaviors.stopped();
    }

    /**
     * Processing {@link SetSingleBlockSelectingFinishedMessage}.
     */
    private Behavior<Message> onSetSingleBlockSelectingFinished(SetSingleBlockSelectingFinishedMessage m) {
        log.debug("onSetSingleBlockSelectingFinished {}", m);
        val gm = getGameMap(is.og, gs.get().currentMap.get());
        val selected = IntLists.immutable.ofAll(gm.getSelectedBlocks());
        final int index = selected.getFirst();
        gm.clearSelectedBlocks();
        setGameMap(is.os, gm);
        final int x = calcX(index, gm.getWidth(), 0), y = calcY(index, gm.getWidth(), 0),
                z = calcZ(index, gm.getWidth(), gm.getHeight(), 0);
        val pos = new GameBlockPos(x, y, z);
        val ret = askRetrieveObjects(actor.getActorSystem(), gm.getId(), pos, Duration.ofMillis(100));
        ret.whenComplete((res, ex) -> {
            if (res instanceof RetrieveObjectsSuccessMessage resm) {
                MutableMultimap<Integer, Long> ids = Multimaps.mutable.list.empty();
                for (val go : resm.objects) {
                    if (go.getObjectType() != Block.OBJECT_TYPE) {
                        ids.put(go.getObjectType(), go.getId());
                    }
                }
                for (val key : ids.keysView()) {
                    actor.tell(new DeleteBulkObjectsMessage<>(objectsDeleteAdapter, gm.getId(), key, ids.get(key)));
                }
            }
        });
        return Behaviors.same();
    }

    /**
     * Processing {@link WrappedObjectResponseMessage}.
     */
    private Behavior<Message> onWrappedObjectResponse(WrappedObjectResponseMessage m) {
        log.debug("onWrappedObjectResponse {}", m);
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>{@link StartDeleteObjectMessage}
     * <li>{@link StopDeleteObjectMessage}
     * <li>{@link SetSingleBlockSelectingFinishedMessage}
     * <li>{@link WrappedObjectResponseMessage}
     * </ul>
     */
    private BehaviorBuilder<Message> getInitialBehavior() {
        return Behaviors.receive(Message.class)//
                .onMessage(StartDeleteObjectMessage.class, this::onStartDeleteObject)//
                .onMessage(StopDeleteObjectMessage.class, this::onStopDeleteObject)//
                .onMessage(SetSingleBlockSelectingFinishedMessage.class, this::onSetSingleBlockSelectingFinished)//
                .onMessage(WrappedObjectResponseMessage.class, this::onWrappedObjectResponse)//
        ;
    }
}
