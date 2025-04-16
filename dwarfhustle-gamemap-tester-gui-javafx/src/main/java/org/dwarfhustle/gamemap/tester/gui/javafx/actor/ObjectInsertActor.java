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
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.getChunk;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.findBlock;
import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage.askKnowledgeObjects;
import static java.time.Duration.ofMillis;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.CompletionStage;

import org.eclipse.collections.api.factory.primitive.IntLists;
import org.lable.oss.uniqueid.IDGenerator;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.ObjectTypeNameSetMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetMultiBlockSelectingModeMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.IdsObjectsProvider.IdsObjects;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject;
import com.anrisoftware.dwarfhustle.model.api.objects.NamedObject;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.api.objects.StringObject;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StringObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.anrisoftware.dwarfhustle.model.objects.InsertObjectMessage;
import com.anrisoftware.dwarfhustle.model.objects.InsertObjectMessage.InsertObjectSuccessMessage;
import com.anrisoftware.resources.texts.external.Texts;
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
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class ObjectInsertActor {

    private static final Duration KNOWLEDGE_GET_TIMEOUT = ofMillis(100);

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            ObjectInsertActor.class.getSimpleName());

    public static final String NAME = ObjectInsertActor.class.getSimpleName();

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
        public final ObjectsSetter ss;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class SetupErrorMessage extends Message {
        public final Throwable cause;
    }

    /**
     * Enables single-block selection mode and waits for the user to select the
     * object to insert.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class StartInsertObjectMessage extends Message {
    }

    /**
     * Stops single-block selection mode.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class StopInsertObjectMessage extends Message {
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedInsertObjectResponse extends Message {
        public final InsertObjectMessage.InsertObjectSuccessMessage res;
    }

    /**
     * Factory to create {@link ObjectInsertActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface ObjectInsertActorFactory {
        ObjectInsertActor create(ActorContext<Message> context, StashBuffer<Message> stash);
    }

    /**
     * Creates the {@link ObjectInsertActor}.
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
            return injector.getInstance(ObjectInsertActorFactory.class).create(context, stash).start(injector);
        }));
    }

    /**
     * Creates the {@link ObjectInsertActor}.
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
            final var ss = actor.getObjectSetterAsyncNow(StringObjectsJcsCacheActor.ID);
            return new InitialStateMessage(og, os, cg, cs, ko, mg, ms, ss);
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
    @Named("AppTexts")
    private Texts appTexts;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    @IdsObjects
    private IDGenerator ids;

    private InitialStateMessage is;

    private ActorRef<InsertObjectSuccessMessage> objectsInsertAdapter;

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
        this.objectsInsertAdapter = context.messageAdapter(InsertObjectMessage.InsertObjectSuccessMessage.class,
                WrappedInsertObjectResponse::new);
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     *
     */
    private Behavior<Message> onStartInsertObject(StartInsertObjectMessage m) {
        log.trace("onStartInsertObject");
        actor.tell(new SetMultiBlockSelectingModeMessage(true));
        return Behaviors.same();
    }

    /**
    *
    */
    private Behavior<Message> onStopInsertObject(StopInsertObjectMessage m) {
        log.trace("onStopInsertObject");
        actor.tell(new SetMultiBlockSelectingModeMessage(false));
        return Behaviors.stopped();
    }

    /**
     * Processing {@link ObjectTypeNameSetMessage}.
     */
    private Behavior<Message> onObjectTypeNameSet(ObjectTypeNameSetMessage m) {
        log.debug("onObjectTypeNameSet {}", m);
        val gm = getGameMap(is.og, gs.get().currentMap.get());
        val selected = IntLists.immutable.ofAll(gm.getSelectedBlocks());
        gm.clearSelectedBlocks();
        setGameMap(is.os, gm);
        val root = getChunk(is.cg, 0);
        askKnowledgeObjects(actor.getActorSystem(), KNOWLEDGE_GET_TIMEOUT, m.type).whenComplete((list, ex) -> {
            val ko = list.detect(k -> k.getName().equalsIgnoreCase(m.name));
            for (final var it = selected.intIterator(); it.hasNext();) {
                final int index = it.next();
                final int x = calcX(index, gm.getWidth(), 0), y = calcY(index, gm.getWidth(), 0),
                        z = calcZ(index, gm.getWidth(), gm.getHeight(), 0);
                val pos = new GameBlockPos(x, y, z);
                val mb = findBlock(root, pos, is.cg);
                actor.tell(new InsertObjectMessage<>(objectsInsertAdapter, gm.getId(), mb.getParent(), ko, pos, go -> {
                    if (go instanceof NamedObject no) {
                        createName(no, ko);
                    }
                }));
            }
        });
        return Behaviors.same();
    }

    @SneakyThrows
    private void createName(NamedObject go, KnowledgeObject ko) {
        val t = appTexts.getResource(String.format("object-name-%s", ko.getName()), Locale.ENGLISH);
        val name = t.getFormattedText(ko.getName(), go.getId() % 1000);
        val s = new StringObject(ids.generate(), name);
        is.ss.set(StringObject.OBJECT_TYPE, s);
        go.setName(s.getId());
    }

    /**
     * <ul>
     * <li>{@link StartInsertObjectMessage}
     * <li>{@link StopInsertObjectMessage}
     * <li>{@link ObjectTypeNameSetMessage}
     * <li>{@link WrappedInsertObjectResponse}
     * </ul>
     */
    private BehaviorBuilder<Message> getInitialBehavior() {
        return Behaviors.receive(Message.class)//
                .onMessage(StartInsertObjectMessage.class, this::onStartInsertObject)//
                .onMessage(StopInsertObjectMessage.class, this::onStopInsertObject)//
                .onMessage(ObjectTypeNameSetMessage.class, this::onObjectTypeNameSet)//
                .onMessage(WrappedInsertObjectResponse.class, m -> Behaviors.same())//
        ;
    }
}
