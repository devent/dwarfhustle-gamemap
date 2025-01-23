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
import static com.anrisoftware.dwarfhustle.model.api.objects.GameMap.getGameMap;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.getChunk;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.findBlock;
import static com.anrisoftware.dwarfhustle.model.db.cache.MapObject.getMapObject;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import org.lable.oss.uniqueid.IDGenerator;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetMaterialBlockMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.IdsObjectsProvider.IdsObjects;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.api.vegetations.Vegetation;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.anrisoftware.dwarfhustle.model.objects.DeleteObjectMessage;
import com.anrisoftware.dwarfhustle.model.objects.InsertObjectMessage;
import com.anrisoftware.dwarfhustle.model.objects.ObjectsActor;
import com.anrisoftware.dwarfhustle.model.objects.DeleteObjectMessage.DeleteObjectSuccessMessage;
import com.anrisoftware.dwarfhustle.model.objects.InsertObjectMessage.InsertObjectSuccessMessage;
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
        public final ActorRef<Message> knowledgeActor;
        public final ObjectsGetter og;
        public final ObjectsSetter os;
        public final ObjectsGetter chunks;
        public final ObjectsGetter mg;
        public final ObjectsSetter ms;
        public final ActorRef<Message> oa;
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

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedInsertObjectResponse extends Message {
        public final InsertObjectMessage.InsertObjectSuccessMessage res;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedDeleteObjectResponse extends Message {
        public final DeleteObjectMessage.DeleteObjectSuccessMessage res;
    }

    /**
     * Factory to create {@link TerrainTestKeysActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface TerrainTestKeysActorFactory {
        TerrainTestKeysActor create(ActorContext<Message> context, StashBuffer<Message> stash);
    }

    /**
     * Creates the {@link TerrainTestKeysActor}.
     */
    private static Behavior<Message> create(Injector injector, ActorSystemProvider actor) {
        return Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            context.pipeToSelf(CompletableFuture.supplyAsync(() -> returnInitialState(injector, actor)),
                    (result, cause) -> {
                        if (cause == null) {
                            return result;
                        } else {
                            return new SetupErrorMessage(cause);
                        }
                    });
            return injector.getInstance(TerrainTestKeysActorFactory.class).create(context, stash).start(injector);
        }));
    }

    /**
     * Creates the {@link TerrainTestKeysActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        var actor = injector.getInstance(ActorSystemProvider.class);
        return createNamedActor(actor.getActorSystem(), timeout, ID, KEY, NAME, create(injector, actor));
    }

    private static Message returnInitialState(Injector injector, ActorSystemProvider actor) {
        var app = injector.getInstance(Application.class);
        try {
            var state = injector.getInstance(TerrainTestKeysState.class);
            app.enqueue(() -> {
                app.getStateManager().attach(state);
            });
            var knowledgeActor = actor.getActorAsyncNow(PowerLoomKnowledgeActor.ID);
            var og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            var os = actor.getObjectSetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            var chunks = actor.getObjectGetterAsyncNow(MapChunksJcsCacheActor.ID);
            var mg = actor.getObjectGetterAsyncNow(MapObjectsJcsCacheActor.ID);
            var ms = actor.getObjectSetterAsyncNow(MapObjectsJcsCacheActor.ID);
            var oa = actor.getActorAsyncNow(ObjectsActor.ID);
            return new InitialStateMessage(state, knowledgeActor, og, os, chunks, mg, ms, oa);
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
    @IdsObjects
    private IDGenerator ids;

    @Inject
    private GameSettingsProvider gs;

    private InitialStateMessage is;

    private long currentMap;

    @SuppressWarnings("rawtypes")
    private ActorRef<CacheResponseMessage> cacheAdapter;

    private ActorRef<InsertObjectSuccessMessage> objectsInsertAdapter;

    private ActorRef<DeleteObjectSuccessMessage> objectsDeleteAdapter;

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
        this.objectsInsertAdapter = context.messageAdapter(InsertObjectMessage.InsertObjectSuccessMessage.class,
                WrappedInsertObjectResponse::new);
        this.objectsDeleteAdapter = context.messageAdapter(DeleteObjectMessage.DeleteObjectSuccessMessage.class,
                WrappedDeleteObjectResponse::new);
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
        is.state.setChunks(is.chunks);
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
        this.currentMap = m.gm;
        var gm = getGameMap(is.og, m.gm);
        is.state.setGameMap(gm);
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link AddObjectOnBlockMessage} message.
     */
    private Behavior<Message> onAddObjectOnBlock(AddObjectOnBlockMessage m) {
        log.debug("onAddObjectOnBlock {}", m);
        is.knowledgeActor.tell(new KnowledgeGetMessage<>(cacheAdapter, m.type, (ko) -> {
            insertObject(m.cursor, ko.objects.detectOptional((it) -> it.getName().equalsIgnoreCase(m.name)).get(),
                    m.validBlock);
        }));
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link DeleteVegetationOnBlockMessage} message.
     */
    private Behavior<Message> onDeleteVegetationOnBlock(DeleteVegetationOnBlockMessage m) {
        log.debug("onDeleteVegetationOnBlock {}", m);
        for (String type : m.types) {
            is.knowledgeActor.tell(new KnowledgeGetMessage<>(cacheAdapter, type, (ko) -> {
                deleteObject(m.cursor, ko.objects.getFirst());
            }));
        }
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link ShowObjectsOnBlockMessage} message.
     */
    private Behavior<Message> onShowObjectsOnBlock(ShowObjectsOnBlockMessage m) {
        log.debug("onShowObjectsOnBlock {}", m);
        var gm = getGameMap(is.og, this.currentMap);
        final var mo = getMapObject(is.mg, gm, m.cursor.getX(), m.cursor.getY(), m.cursor.getZ());
        mo.getOids().forEachKeyValue((id, type) -> {
            final GameMapObject go = is.og.get(type, id);
            System.out.println(go); // TODO
        });
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link ShowSelectedBlockMessage} message.
     */
    private Behavior<Message> onShowSelectedBlock(ShowSelectedBlockMessage m) {
        log.debug("onShowSelectedBlock {}", m);
        MapChunk root = is.chunks.get(MapChunk.OBJECT_TYPE, 0);
        var mb = findBlock(root, m.cursor, is.chunks);
        System.out.println(mb); // TODO
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
     * Reacts to the {@link VegetationAddGrowMessage} message.
     */
    private Behavior<Message> onVegetationAddGrow(VegetationAddGrowMessage m) {
        log.debug("onVegetationAddGrow {}", m);
        final var gm = getGameMap(is.og, this.currentMap);
        final var mo = getMapObject(is.mg, gm, m.cursor.getX(), m.cursor.getY(), m.cursor.getZ());
        mo.getOids().forEachKeyValue((id, type) -> {
            final GameMapObject go = is.og.get(type, id);
            if (go instanceof Vegetation) {
                addVegetationGrow(go.getAsType());
            }
        });
        return Behaviors.same();
    }

    private void addVegetationGrow(Vegetation o) {
        o.setGrowth(2.0f);
        is.os.set(o.getObjectType(), o);
        System.out.printf("Set vegetation grow %s\n", o); // TODO
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
        var root = getChunk(is.chunks, 0);
        var mb = findBlock(root, pos, is.chunks);
        if (!validBlock.apply(mb)) {
            System.out.printf("Block at %s is not valid for %s\n", pos, ko); // TODO
            return;
        }
        is.oa.tell(new InsertObjectMessage<>(objectsInsertAdapter, currentMap, mb.parent, ko, pos));
    }

    @SneakyThrows
    private void deleteObject(GameBlockPos pos, KnowledgeObject ko) {
        // is.oa.tell(new DeleteObjectMessage<>(objectsDeleteAdapter, gm, pos, (id,
        // type) -> true));
    }

    /**
     * Reacts to the {@link SetMaterialBlockMessage} message.
     */
    private Behavior<Message> onSetMaterialBlock(SetMaterialBlockMessage m) {
        log.debug("onSetMaterialBlock {}", m);
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
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(SetGameMapMessage.class, this::onSetGameMap)//
                .onMessage(AddObjectOnBlockMessage.class, this::onAddObjectOnBlock)//
                .onMessage(ShowObjectsOnBlockMessage.class, this::onShowObjectsOnBlock)//
                .onMessage(ShowSelectedBlockMessage.class, this::onShowSelectedBlock)//
                .onMessage(ToggleUndiscoveredMessage.class, this::onToggleUndiscovered)//
                .onMessage(DeleteVegetationOnBlockMessage.class, this::onDeleteVegetationOnBlock)//
                .onMessage(VegetationAddGrowMessage.class, this::onVegetationAddGrow)//
                .onMessage(SetMaterialBlockMessage.class, this::onSetMaterialBlock)//
                .onMessage(WrappedCacheResponse.class, this::onWrappedCache)//
                .onMessage(WrappedInsertObjectResponse.class, (m) -> {
                    return Behaviors.same();
                })//
                .onMessage(WrappedDeleteObjectResponse.class, (m) -> {
                    return Behaviors.same();
                })//
        ;
    }

}
