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
package com.anrisoftware.dwarfhustle.gamemap.jme.objectsrender;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcIndex;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcZ;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.getChunk;
import static com.anrisoftware.dwarfhustle.model.db.cache.MapObject.getMapObject;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.eclipse.collections.api.factory.primitive.IntObjectMaps;
import org.eclipse.collections.api.factory.primitive.LongObjectMaps;
import org.eclipse.collections.api.factory.primitive.LongSets;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.impl.factory.Multimaps;

import com.anrisoftware.dwarfhustle.gamemap.jme.app.MaterialAssetsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.ModelsAssetsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.model.CollectChunksUpdate;
import com.anrisoftware.dwarfhustle.gamemap.jme.model.CollectChunksUpdate.CollectChunksUpdateFactory;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppPausedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.StartTerrainForGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.api.objects.PropertiesSet;
import com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.javadsl.TimerScheduler;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Retrieves {@link MapChunk} map chunks with {@link MapBlock} blocks, shows
 * objects on the map.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class ObjectsRenderActor {

    private static final String UPDATE_OBJECTS_MESSAGE_TIMER_KEY = "ObjectsRenderActor-UpdateObjectsMessage-Timer";

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            ObjectsRenderActor.class.getSimpleName());

    public static final String NAME = ObjectsRenderActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {

        public final ObjectsState objectsState;

        public final ObjectsGetter ma;

        public final ObjectsGetter mo;

        public final ObjectsGetter og;

        public final ObjectsSetter os;

        public final ObjectsGetter chunks;

        public final ObjectsGetter mg;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class SetupErrorMessage extends Message {
        public final Throwable cause;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class UpdateTerrainMessage extends Message {
        public final GameMap gm;
    }

    /**
     * Factory to create {@link ObjectsRenderActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface ObjectsRenderActorFactory {
        ObjectsRenderActor create(ActorContext<Message> context, StashBuffer<Message> stash,
                TimerScheduler<Message> timer);
    }

    /**
     * Creates the {@link ObjectsRenderActor}.
     * 
     * @param actor
     * 
     * @param mapObjects
     */
    private static Behavior<Message> create(Injector injector, ActorSystemProvider actor) {
        return Behaviors.withTimers(timer -> Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            context.pipeToSelf(CompletableFuture.supplyAsync(() -> returnInitialState(injector, actor)),
                    (result, cause) -> {
                        if (cause == null) {
                            return result;
                        } else {
                            return new SetupErrorMessage(cause);
                        }
                    });
            return injector.getInstance(ObjectsRenderActorFactory.class).create(context, stash, timer).start(injector);
        })));
    }

    /**
     * Creates the {@link ObjectsRenderActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        var actor = injector.getInstance(ActorSystemProvider.class);
        return createNamedActor(actor.getActorSystem(), timeout, ID, KEY, NAME, create(injector, actor));
    }

    private static Message returnInitialState(Injector injector, ActorSystemProvider actor) {
        var app = injector.getInstance(Application.class);
        try {
            var ma = actor.getObjectGetterAsyncNow(MaterialAssetsCacheActor.ID);
            var mo = actor.getObjectGetterAsyncNow(ModelsAssetsCacheActor.ID);
            var og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            var os = actor.getObjectSetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            var cg = actor.getObjectGetterAsyncNow(MapChunksJcsCacheActor.ID);
            var mg = actor.getObjectGetterAsyncNow(MapObjectsJcsCacheActor.ID);
            var terrainState = injector.getInstance(ObjectsState.class);
            app.enqueue(() -> {
                terrainState.setup(ma, mo);
                app.getStateManager().attach(terrainState);
            });
            return new InitialStateMessage(terrainState, ma, mo, og, os, cg, mg);
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
    @Assisted
    private TimerScheduler<Message> timer;

    @Inject
    private Application app;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    private Engine engine;

    @Inject
    private CollectChunksUpdateFactory collectChunksUpdateFactory;

    private InitialStateMessage is;

    private Optional<StartTerrainForGameMapMessage> previousStartTerrainForGameMapMessage = Optional.empty();

    /**
     * Chunk-ID := [Block-Index := [Object-ID := Entity]]
     */
    private MutableLongObjectMap<MutableIntObjectMap<MutableLongObjectMap<Entity>>> objectEntities;

    /**
     * Chunk-ID := [Block-Index]
     */
    private MutableMultimap<Long, Integer> chunkBlocks;

    private CollectChunksUpdate collectChunksUpdate;

    private int visibleLayers;

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
        this.objectEntities = LongObjectMaps.mutable.ofInitialCapacity(100);
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
        log.error("onSetupError: {}", m);
        return Behaviors.stopped();
    }

    /**
     * Returns a behavior for the messages from {@link #getInitialBehavior()}
     */
    private Behavior<Message> onInitialState(InitialStateMessage m) {
        log.debug("onInitialState");
        this.is = m;
        this.collectChunksUpdate = collectChunksUpdateFactory.create();
        this.visibleLayers = gs.get().visibleDepthLayers.get();
        gs.get().visibleDepthLayers.addListener((observable, oldValue, newValue) -> {
            visibleLayers = newValue.intValue();
        });
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     * Reacts to the {@link UpdateTerrainMessage} message.
     */
    private Behavior<Message> onUpdateModel(UpdateTerrainMessage m) {
        try {
            onUpdateModel0(m);
        } catch (Exception e) {
            log.error("", e);
        }
        return Behaviors.same();
    }

    private void onUpdateModel0(UpdateTerrainMessage m) {
        // long oldtime = System.currentTimeMillis();
        is.objectsState.setGameMap(m.gm);
        var root = getChunk(is.chunks, 0);
        var cursorZ = m.gm.getCursorZ();
        int depthLayers = gs.get().visibleDepthLayers.get();
        chunkBlocks.clear();
        collectChunksUpdate.collectChunks(root, cursorZ, cursorZ, depthLayers, m.gm.depth, this::isBlockVisible,
                is.chunks, this::putMapBlock);
        updateObjectsBlocks(m.gm);
        // long finishtime = System.currentTimeMillis();
        // log.trace("updateModel done in {}", finishtime - oldtime);
    }

    private boolean isBlockVisible(MapChunk chunk, int i, int x, int y, int z) {
        int off = MapBlockBuffer.calcOff(chunk, x, y, z);
        int p = MapBlockBuffer.getProp(chunk.getBlocks(), off);
        if (PropertiesSet.get(p, MapBlock.EMPTY_POS)) {
            return true;
        }
        if (PropertiesSet.get(p, MapBlock.LIQUID_POS)) {
            return true;
        }
        return false;
    }

    private void putMapBlock(MapChunk chunk, int index) {
        final long cid = chunk.getId();
        chunkBlocks.put(cid, index);
    }

    private void updateObjectsBlocks(GameMap gm) {
        /*
         * check entities from map if in db, if not, remove from map
         * 
         * check entities from map if in visible blocks, if not, set to hidden
         * 
         * if new object, create entity, put in (x,y,z)->entity map
         */
        long oldtime = System.currentTimeMillis();
        final int w = gm.width, h = gm.height, d = gm.depth, cursorZ = gm.cursor.z;
        MutableLongSet alldbids = LongSets.mutable.withInitialCapacity(100);
        MutableLongObjectMap<MutableListMultimap<Integer, Long>> chunkidsTypeOids = LongObjectMaps.mutable
                .ofInitialCapacity(100);
        for (final var pairBlocks : this.chunkBlocks.keyMultiValuePairsView()) {
            final long cid = pairBlocks.getOne();
            final var chunkdbids = lazyCreateChunkidsTypeOidsEntry(chunkidsTypeOids, cid);
            gm.getFilledBlocks().forEachKeyValue((index, count) -> {
                count.getAndUpdate(c -> {
                    if (c > 0) {
                        final int z = calcZ(index, gm.getWidth(), gm.getHeight(), 0);
                        if (cursorZ <= z && (cursorZ + visibleLayers - 1) > z) {
                            final var mo = getMapObject(is.mg, index);
                            mo.getOids().forEachKeyValue((id, type) -> {
                                final GameMapObject go = is.og.get(type, id);
                                if (go.isVisible()) {
                                    chunkdbids.put(go.getObjectType(), go.getId());
                                }
                                alldbids.add(go.getId());
                            });
                        }
                    }
                    return c;
                });
            });
        }
        MutableLongSet removeids = LongSets.mutable.withInitialCapacity(100);
        for (var indexOidsEntity : this.objectEntities.values()) {
            if (indexOidsEntity.isEmpty()) {
                continue;
            }
            checkOldEntries(indexOidsEntity, alldbids, removeids, w, h);
        }
        for (var chunkidTypeOids : chunkidsTypeOids.keyValuesView()) {
            removeids.clear();
            long cid = chunkidTypeOids.getOne();
            var typeOids = chunkidTypeOids.getTwo();
            var indexOidsEntity = lazyCreateChunkEntities(cid);
            for (var typeOid : typeOids.keyMultiValuePairsView()) {
                final int type = typeOid.getOne();
                for (long oid : typeOid.getTwo()) {
                    GameMapObject object = is.og.get(type, oid);
                    updateObjectEntity(indexOidsEntity, object, w, h, d);
                }
            }
        }
        long finishtime = System.currentTimeMillis();
        log.trace("onUpdateObjectsBlocks done in {} showing {} objects", finishtime - oldtime, objectEntities.size());
    }

    private MutableListMultimap<Integer, Long> lazyCreateChunkidsTypeOidsEntry(
            MutableLongObjectMap<MutableListMultimap<Integer, Long>> chunksids, long cid) {
        MutableListMultimap<Integer, Long> chunkids = chunksids.get(cid);
        if (chunkids == null) {
            chunkids = Multimaps.mutable.list.empty();
            chunksids.put(cid, chunkids);
        }
        return chunkids;
    }

    private MutableIntObjectMap<MutableLongObjectMap<Entity>> lazyCreateChunkEntities(long cid) {
        MutableIntObjectMap<MutableLongObjectMap<Entity>> chunkEntities;
        chunkEntities = objectEntities.get(cid);
        if (chunkEntities == null) {
            chunkEntities = IntObjectMaps.mutable.ofInitialCapacity(100);
            objectEntities.put(cid, chunkEntities);
        }
        return chunkEntities;
    }

    private void checkOldEntries(MutableIntObjectMap<MutableLongObjectMap<Entity>> indexOidsEntity,
            MutableLongSet alldbids, MutableLongSet removeids, int w, int h) {
        for (var pair : indexOidsEntity.keyValuesView()) {
            MutableLongObjectMap<Entity> oidsEntityMap = pair.getTwo();
            for (var oidsEntity : oidsEntityMap.keyValuesView()) {
                final long id = oidsEntity.getOne();
                if (!alldbids.contains(id)) {
                    removeids.add(id);
                } else {
                    var e = oidsEntity.getTwo();
                    app.enqueue(() -> {
                        e.add(new ObjectMeshVisibleComponent(id, false));
                    });
                }
            }
            removeids.forEach((id) -> {
                var e = oidsEntityMap.remove(id);
                if (e != null) {
                    app.enqueue(() -> {
                        engine.removeEntity(e);
                    });
                }
            });
        }
    }

    private void updateObjectEntity(MutableIntObjectMap<MutableLongObjectMap<Entity>> chunksEntities,
            GameMapObject object, int w, int h, int d) {
        final var pos = object.getPos();
        final int index = calcIndex(w, h, d, 0, 0, 0, pos.getX(), pos.getY(), pos.getZ());
        var objectEntitiesEntry = chunksEntities.get(index);
        if (objectEntitiesEntry == null) {
            objectEntitiesEntry = LongObjectMaps.mutable.ofInitialCapacity(100);
            chunksEntities.put(index, objectEntitiesEntry);
        }
        var old = objectEntitiesEntry.get(object.getId());
        Entity e;
        if (old == null) {
            e = engine.createEntity();
            objectEntitiesEntry.put(object.getId(), e);
        } else {
            e = old;
        }
        app.enqueue(() -> {
            e.add(new ObjectMeshVisibleComponent(object.getId(), true));
            e.add(new ObjectMeshComponent(object));
            if (old == null) {
                engine.addEntity(e);
            }
        });
    }

    /**
     * Reacts to the {@link StartTerrainForGameMapMessage} message.
     */
    private Behavior<Message> onStartTerrainForGameMap(StartTerrainForGameMapMessage m) {
        log.debug("onStartTerrainForGameMap {}", m);
        this.previousStartTerrainForGameMapMessage = Optional.of(m);
        this.chunkBlocks = Multimaps.mutable.list.empty();
        app.enqueue(() -> {
        });
        timer.startTimerAtFixedRate(UPDATE_OBJECTS_MESSAGE_TIMER_KEY, new UpdateTerrainMessage(m.gm),
                gs.get().terrainUpdateDuration.get());
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onAppPaused(AppPausedMessage m) {
        log.debug("onAppPaused {}", m);
        if (m.paused) {
            timer.cancel(UPDATE_OBJECTS_MESSAGE_TIMER_KEY);
        } else {
            previousStartTerrainForGameMapMessage.ifPresent(this::onStartTerrainForGameMap);
        }
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onShutdown(ShutdownMessage m) {
        log.debug("onShutdown {}", m);
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
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(StartTerrainForGameMapMessage.class, this::onStartTerrainForGameMap)//
                .onMessage(UpdateTerrainMessage.class, this::onUpdateModel)//
                .onMessage(AppPausedMessage.class, this::onAppPaused)//
        ;
    }
}
