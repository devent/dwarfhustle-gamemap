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
import static com.anrisoftware.dwarfhustle.model.api.objects.MapBlockFlags.EMPTY;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.getChunk;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.setChunk;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.calcOff;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.isProp;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.setMaterial;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.findChunk;
import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage.askKnowledgeObjects;
import static java.time.Duration.ofMillis;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialSetTriggeredMessage;
import org.eclipse.collections.api.factory.primitive.IntLists;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetMultiBlockSelectingModeMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.storages.GameObjectKnowledge;
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
public class PaintTerrainActor {

    private static final Duration KNOWLEDGE_GET_TIMEOUT = ofMillis(100);

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            PaintTerrainActor.class.getSimpleName());

    public static final String NAME = PaintTerrainActor.class.getSimpleName();

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
     * Enables multi-block selection mode and waits for the user to paint the
     * terrain with material.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class StartPaintTerrainMessage extends Message {
    }

    /**
     * Stops the painting terrain with material.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class StopPaintTerrainMessage extends Message {
    }

    /**
     * Factory to create {@link PaintTerrainActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface PaintTerrainActorFactory {
        PaintTerrainActor create(ActorContext<Message> context, StashBuffer<Message> stash);
    }

    /**
     * Creates the {@link PaintTerrainActor}.
     *
     * @param actor
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
            return injector.getInstance(PaintTerrainActorFactory.class).create(context, stash).start(injector);
        }));
    }

    /**
     * Creates the {@link PaintTerrainActor}.
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

    @Inject
    @Named("knowledge-storages")
    private Map<String, GameObjectKnowledge> ks;

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
    private Behavior<Message> onStartPaintTerrain(StartPaintTerrainMessage m) {
        log.trace("onStartPaintTerrain");
        actor.tell(new SetMultiBlockSelectingModeMessage(true));
        return Behaviors.same();
    }

    /**
    *
    */
    private Behavior<Message> onStopPaintTerrain(StopPaintTerrainMessage m) {
        log.trace("onStopPaintTerrain");
        actor.tell(new SetMultiBlockSelectingModeMessage(false));
        return Behaviors.stopped();
    }

    /**
     * Processing {@link MaterialSetTriggeredMessage}.
     */
    private Behavior<Message> onMaterialSetTriggered(MaterialSetTriggeredMessage m) {
        log.debug("onMaterialSetTriggered {}", m);
        val gm = getGameMap(is.og, gs.get().currentMap.get());
        val selected = IntLists.immutable.ofAll(gm.getSelectedBlocks());
        gm.clearSelectedBlocks();
        setGameMap(is.os, gm);
        askKnowledgeObjects(actor.getActorSystem(), KNOWLEDGE_GET_TIMEOUT, m.type).whenComplete((list, ex) -> {
            val foundko = list.detect(ko -> ko.getName().equalsIgnoreCase(m.material));
            for (final var it = selected.intIterator(); it.hasNext();) {
                val index = it.next();
                final int w = gm.getWidth();
                final int x = calcX(index, w, 0), y = calcY(index, gm.getWidth(), 0);
                int z = calcZ(index, gm.getWidth(), gm.getHeight(), 0);
                val chunk = findChunk(getChunk(is.cg, 0), x, y, z, is.cg);
                if (isProp(chunk.getBlocks(), calcOff(chunk, x, y, z), EMPTY)) {
                    z += 1;
                }
                setMaterial(chunk.getBlocks(), calcOff(chunk, x, y, z), foundko.getKid());
                setChunk(is.cs, chunk);
            }
        });
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>{@link StartPaintTerrainMessage}
     * <li>{@link StopPaintTerrainMessage}
     * </ul>
     */
    private BehaviorBuilder<Message> getInitialBehavior() {
        return Behaviors.receive(Message.class)//
                .onMessage(StartPaintTerrainMessage.class, this::onStartPaintTerrain)//
                .onMessage(StopPaintTerrainMessage.class, this::onStopPaintTerrain)//
                .onMessage(MaterialSetTriggeredMessage.class, this::onMaterialSetTriggered)//
        ;
    }
}
