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
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcZ;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.chunkIndex2MapIndex;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameMap.getGameMap;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.getChunk;
import static com.anrisoftware.dwarfhustle.model.db.cache.MapObject.getMapObject;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.eclipse.collections.api.factory.primitive.IntSets;
import org.eclipse.collections.api.factory.primitive.LongObjectMaps;
import org.eclipse.collections.api.factory.primitive.LongSets;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
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
import lombok.val;
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
        public final long gm;
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
        final var actor = injector.getInstance(ActorSystemProvider.class);
        return createNamedActor(actor.getActorSystem(), timeout, ID, KEY, NAME, create(injector, actor));
    }

    private static Message returnInitialState(Injector injector, ActorSystemProvider actor) {
        final var app = injector.getInstance(Application.class);
        try {
            final var ma = actor.getObjectGetterAsyncNow(MaterialAssetsCacheActor.ID);
            final var mo = actor.getObjectGetterAsyncNow(ModelsAssetsCacheActor.ID);
            final var og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            final var os = actor.getObjectSetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            final var cg = actor.getObjectGetterAsyncNow(MapChunksJcsCacheActor.ID);
            final var mg = actor.getObjectGetterAsyncNow(MapObjectsJcsCacheActor.ID);
            final var terrainState = injector.getInstance(ObjectsState.class);
            app.enqueue(() -> {
                terrainState.setup(ma, mo);
                app.getStateManager().attach(terrainState);
            });
            return new InitialStateMessage(terrainState, ma, mo, og, os, cg, mg);
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
     * [Object-ID := Entity]]
     */
    private MutableLongObjectMap<Entity> objectEntities;

    /**
     * Block-Index
     */
    private MutableIntSet blocksIndices;

    private final MutableLongSet removeids = LongSets.mutable.withInitialCapacity(100);

    private final MutableListMultimap<Integer, Long> chunkids = Multimaps.mutable.list.empty();

    private final MutableLongSet alldbids = LongSets.mutable.withInitialCapacity(100);

    private CollectChunksUpdate collectChunksUpdate;

    private int visibleLayers;

    private int depthLayers;

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
        this.depthLayers = gs.get().visibleDepthLayers.get();
        gs.get().visibleDepthLayers.addListener((observable, oldValue, newValue) -> depthLayers = newValue.intValue());
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
        is = m;
        collectChunksUpdate = collectChunksUpdateFactory.create();
        visibleLayers = gs.get().visibleDepthLayers.get();
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
        } catch (final Exception e) {
            log.error("", e);
        }
        return Behaviors.same();
    }

    private void onUpdateModel0(UpdateTerrainMessage m) {
        // long oldtime = System.currentTimeMillis();
        final var gm = getGameMap(is.og, m.gm);
        is.objectsState.setGameMap(gm);
        final var root = getChunk(is.chunks, 0);
        final var cz = gm.getCursorZ();
        blocksIndices.clear();
        collectChunksUpdate.collectChunks(root, cz, cz, depthLayers, gm, this::isBlockVisible, is.chunks,
                this::putMapBlock);
        updateObjectsBlocks(gm);
        // long finishtime = System.currentTimeMillis();
        // log.trace("updateModel done in {}", finishtime - oldtime);
    }

    private boolean isBlockVisible(GameMap gm, MapChunk chunk, int i, int x, int y, int z) {
        final int off = MapBlockBuffer.calcOff(chunk, x, y, z);
        final int p = MapBlockBuffer.getProp(chunk.getBlocks(), off);
        if (PropertiesSet.get(p, MapBlock.EMPTY_POS)) {
            return true;
        }
        if (PropertiesSet.get(p, MapBlock.LIQUID_POS)) {
            return true;
        }
        if (PropertiesSet.get(p, MapBlock.FILLED_POS)) {
            return true;
        }
        return false;
    }

    private void putMapBlock(GameMap gm, MapChunk chunk, int index) {
        final int thatIndex = chunkIndex2MapIndex(index, chunk, gm);
        blocksIndices.add(thatIndex);
    }

    @SneakyThrows
    private void updateObjectsBlocks(GameMap gm) {
        /*
         * check entities from map if in db, if not, remove from map
         *
         * check entities from map if in visible blocks, if not, set to hidden
         *
         * if new object, create entity, put in (x,y,z)->entity map
         */
        final long oldtime = System.currentTimeMillis();
        final int cursorZ = gm.cursor.z;
        chunkids.clear();
        alldbids.clear();
        removeids.clear();
        try (val lock = gm.acquireLockMapObjects()) {
            val filledBlocks = gm.getFilledBlocksIndices();
            for (final var it = filledBlocks.intIterator(); it.hasNext();) {
                val thatIndex = it.next();
                if (!blocksIndices.contains(thatIndex)) {
                    continue;
                }
                final int z = calcZ(thatIndex, gm.getWidth(), gm.getHeight(), 0);
                if (cursorZ <= z && (cursorZ + visibleLayers - 1) > z) {
                    val mo = getMapObject(is.mg, gm, thatIndex);
                    mo.getOids().forEachKeyValue((id, type) -> {
                        final GameMapObject go = is.og.get(type, id);
                        if (go.isHaveModel()) {
                            chunkids.put(go.getObjectType(), go.getId());
                        }
                        alldbids.add(go.getId());
                    });
                }
            }
            checkOldEntries(objectEntities, alldbids, removeids);
            for (final var typeOid : chunkids.keyMultiValuePairsView()) {
                final int type = typeOid.getOne();
                for (final long oid : typeOid.getTwo()) {
                    final GameMapObject object = is.og.get(type, oid);
                    updateObjectEntity(objectEntities, object);
                }
            }
        }
        final long finishtime = System.currentTimeMillis();
        log.trace("onUpdateObjectsBlocks done in {} showing {} objects", finishtime - oldtime, objectEntities.size());
    }

    private void checkOldEntries(MutableLongObjectMap<Entity> indexOidsEntity, MutableLongSet alldbids,
            MutableLongSet removeids) {
        for (final var pair : indexOidsEntity.keyValuesView()) {
            final Entity entity = pair.getTwo();
            final long id = pair.getOne();
            if (!alldbids.contains(id)) {
                removeids.add(id);
            } else {
                app.enqueue(() -> {
                    entity.add(new ObjectMeshVisibleComponent(id, false));
                });
            }
        }
        removeids.forEach(id -> {
            final var e = indexOidsEntity.remove(id);
            if (e != null) {
                app.enqueue(() -> {
                    System.out.printf("[checkOldEntries-removeEntity] %s %s %s\n", indexOidsEntity, alldbids,
                            removeids); // TODO
                    engine.removeEntity(e);
                });
            }
        });
    }

    private void updateObjectEntity(MutableLongObjectMap<Entity> objectEntities, GameMapObject object) {
        final var old = objectEntities.get(object.getId());
        Entity e;
        if (old == null) {
            e = engine.createEntity();
            objectEntities.put(object.getId(), e);
        } else {
            e = old;
        }
        app.enqueue(() -> {
            e.add(new ObjectMeshVisibleComponent(object.getId(), true));
            e.add(new ObjectMeshComponent(object));
            e.add(new ObjectElevatedComponent(object.isElevated() ? 1f : 0f));
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
        previousStartTerrainForGameMapMessage = Optional.of(m);
        blocksIndices = IntSets.mutable.empty();
        app.enqueue(() -> {
        });
        timer.startTimerAtFixedRate(UPDATE_OBJECTS_MESSAGE_TIMER_KEY, new UpdateTerrainMessage(m.gm),
                gs.get().terrainUpdateDuration.get());
        // timer.startTimerAtFixedRate(UPDATE_OBJECTS_MESSAGE_TIMER_KEY, new
        // UpdateTerrainMessage(m.gm),
        // Duration.ofMillis(3000));
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
