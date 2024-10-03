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
package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import org.lable.oss.uniqueid.IDGenerator;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.IdsObjectsProvider.IdsObjects;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.MapObjectsStorage;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainTestKeysActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            TerrainTestKeysActor.class.getSimpleName());

    public static final String NAME = TerrainTestKeysActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {
        public final TerrainTestKeysState state;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class SetupErrorMessage extends Message {
        public final Throwable cause;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedCacheResponse extends Message {
        public final CacheResponseMessage<?> res;
    }

    /**
     * Factory to create {@link TerrainTestKeysActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface TerrainTestKeysActorFactory {
        TerrainTestKeysActor create(ActorContext<Message> context, StashBuffer<Message> stash,
                @Assisted("knowledgeActor") ActorRef<Message> knowledgeActor,
                @Assisted("objects-getter") ObjectsGetter og, @Assisted("objects-setter") ObjectsSetter os,
                @Assisted("chunks-getter") ObjectsGetter chunks, MapObjectsStorage mos);
    }

    /**
     * Creates the {@link TerrainTestKeysActor}.
     */
    private static Behavior<Message> create(Injector injector, CompletionStage<ActorRef<Message>> knowledgeActor,
            CompletionStage<ObjectsGetter> og, CompletionStage<ObjectsSetter> os, CompletionStage<ObjectsGetter> chunks,
            MapObjectsStorage mos) {
        return Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            var knowledgeActor0 = knowledgeActor.toCompletableFuture().get();
            var og0 = og.toCompletableFuture().get();
            var os0 = os.toCompletableFuture().get();
            var chunks0 = chunks.toCompletableFuture().get();
            context.pipeToSelf(createState(injector), (result, cause) -> {
                if (cause == null) {
                    return result;
                } else {
                    return new SetupErrorMessage(cause);
                }
            });
            return injector.getInstance(TerrainTestKeysActorFactory.class)
                    .create(context, stash, knowledgeActor0, og0, os0, chunks0, mos).start(injector);
        }));
    }

    /**
     * Creates the {@link TerrainTestKeysActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
            CompletionStage<ActorRef<Message>> knowledgeActor, CompletionStage<ObjectsGetter> og,
            CompletionStage<ObjectsSetter> os, CompletionStage<ObjectsGetter> chunks, MapObjectsStorage mos) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, knowledgeActor, og, os, chunks, mos));
    }

    private static CompletionStage<Message> createState(Injector injector) {
        return CompletableFuture.supplyAsync(() -> attachState(injector));
    }

    private static Message attachState(Injector injector) {
        var app = injector.getInstance(Application.class);
        try {
            var f = app.enqueue(() -> {
                var state = injector.getInstance(TerrainTestKeysState.class);
                app.getStateManager().attach(state);
                return new InitialStateMessage(state);
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
    @Assisted("chunks-getter")
    private ObjectsGetter chunks;

    @Inject
    @Assisted("objects-getter")
    private ObjectsGetter og;

    @Inject
    @Assisted("objects-setter")
    private ObjectsSetter os;

    @Inject
    @Assisted("knowledgeActor")
    private ActorRef<Message> knowledgeActor;

    @Inject
    @IdsObjects
    private IDGenerator ids;

    @Inject
    @Assisted
    private MapObjectsStorage moStorage;

    @Inject
    private GameSettingsProvider gs;

    private InitialStateMessage is;

    private GameMap gm;

    @SuppressWarnings("rawtypes")
    private ActorRef<CacheResponseMessage> cacheAdapter;

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
        this.cacheAdapter = context.messageAdapter(CacheResponseMessage.class, WrappedCacheResponse::new);
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
        is.state.setActor(context.getSelf());
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onShutdown(ShutdownMessage m) {
        log.debug("onShutdown {}", m);
        return Behaviors.stopped();
    }

    /**
     * Reacts to the {@link SetGameMapMessage} message.
     */
    private Behavior<Message> onSetGameMap(SetGameMapMessage m) {
        log.debug("onSetGameMap {}", m);
        this.gm = m.gm;
        is.state.setGameMap(m.gm);
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link AddObjectOnBlockMessage} message.
     */
    private Behavior<Message> onAddObjectOnBlock(AddObjectOnBlockMessage m) {
        log.debug("onAddObjectOnBlock {}", m);
        knowledgeActor.tell(new KnowledgeGetMessage<>(cacheAdapter, m.type, (ko) -> {
            insertObject(m.cursor, ko.objects.getFirst(), m.validBlock);
        }));
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link ShowObjectsOnBlockMessage} message.
     */
    private Behavior<Message> onShowObjectsOnBlock(ShowObjectsOnBlockMessage m) {
        log.debug("onShowObjectsOnBlock {}", m);
        MapChunk root = chunks.get(MapChunk.OBJECT_TYPE, 0);
        var mb = MapChunkBuffer.findBlock(root, m.cursor, chunks);
        if (mb != null) {
            final var pos = mb.getPos();
            moStorage.getObjects(pos.getX(), mb.getPos().getY(), mb.getPos().getZ(), (type, id) -> {
                var go = og.get(type, id);
                System.out.println(go); // TODO
            });
        }
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link ShowSelectedBlockMessage} message.
     */
    private Behavior<Message> onShowSelectedBlock(ShowSelectedBlockMessage m) {
        log.debug("onShowSelectedBlock {}", m);
        MapChunk root = chunks.get(MapChunk.OBJECT_TYPE, 0);
        var mb = MapChunkBuffer.findBlock(root, m.cursor, chunks);
        if (mb != null) {
            System.out.println(mb); // TODO
        }
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link ToggleUndiscoveredMessage} message.
     */
    private Behavior<Message> onToggleUndiscovered(ToggleUndiscoveredMessage m) {
        log.debug("onToggleUndiscovered {}", m);
        gs.get().hideUndiscovered.set(!gs.get().hideUndiscovered.get());
        System.out.println(gs.get().hideUndiscovered.get()); // TODO
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link WrappedCacheResponse} message.
     */
    private Behavior<Message> onWrappedCache(WrappedCacheResponse m) {
        log.debug("onWrappedCache {}", m);
        return Behaviors.same();
    }

    @SneakyThrows
    private void insertObject(GameBlockPos pos, KnowledgeObject ko, Function<MapBlock, Boolean> validBlock) {
        MapChunk root = chunks.get(MapChunk.OBJECT_TYPE, 0);
        var mb = MapChunkBuffer.findBlock(root, pos, chunks);
        if (mb != null) {
            if (!validBlock.apply(mb)) {
                System.out.printf("Block at %s is not valid for %s\n", pos, ko); // TODO
                return;
            }
            var o = (GameMapObject) ko.createObject(ids.generate());
            o.setMap(gm.getId());
            o.setPos(mb.getPos());
            os.set(o.getObjectType(), o);
            moStorage.putObject(o.getPos().getX(), o.getPos().getY(), o.getPos().getZ(), o.getObjectType(), o.getId());
        }
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
                .onMessage(AddObjectOnBlockMessage.class, this::onAddObjectOnBlock)//
                .onMessage(ShowObjectsOnBlockMessage.class, this::onShowObjectsOnBlock)//
                .onMessage(ShowSelectedBlockMessage.class, this::onShowSelectedBlock)//
                .onMessage(ToggleUndiscoveredMessage.class, this::onToggleUndiscovered)//
                .onMessage(WrappedCacheResponse.class, this::onWrappedCache)//
        ;
    }

}
