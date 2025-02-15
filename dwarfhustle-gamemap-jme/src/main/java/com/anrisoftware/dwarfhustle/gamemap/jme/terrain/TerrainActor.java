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
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcIndex;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcX;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcY;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcZ;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameMap.getGameMap;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameMap.setGameMap;
import static com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject.kid2Id;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapBlock.DISCOVERED_POS;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapBlock.EMPTY_POS;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapBlock.FILLED_POS;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapBlock.HAVE_NATURAL_LIGHT_POS;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapBlockFlags.EMPTY;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.getChunk;
import static com.anrisoftware.dwarfhustle.model.api.objects.PropertiesSet.isProp;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.calcOff;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.getMaterial;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.getObject;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.getProp;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.isProp;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.getNeighborUp;
import static com.anrisoftware.dwarfhustle.model.db.cache.MapObject.getMapObject;
import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage.askKnowledgeIdByName;
import static java.time.Duration.ofSeconds;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.IntLongMaps;
import org.eclipse.collections.api.factory.primitive.IntObjectMaps;
import org.eclipse.collections.api.factory.primitive.LongObjectMaps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.IntLongMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.tuple.primitive.LongIntPair;
import org.eclipse.collections.impl.factory.Multimaps;

import com.anrisoftware.dwarfhustle.gamemap.jme.app.MaterialAssetsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.ModelsAssetsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.model.CollectChunksUpdate;
import com.anrisoftware.dwarfhustle.gamemap.jme.model.CollectChunksUpdate.CollectChunksUpdateFactory;
import com.anrisoftware.dwarfhustle.gamemap.jme.terrain.BlockModelUpdate.BlockModelUpdateFactory;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppPausedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapCursorUpdateMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapTileUnderCursorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MouseEnteredGuiMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MouseExitedGuiMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetMultiBlockSelectingModeMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetSingleBlockSelectingFinishedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetSingleBlockSelectingModeMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.StartTerrainForGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.ModelCacheObject;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.map.Block;
import com.anrisoftware.dwarfhustle.model.api.map.BlockObject;
import com.anrisoftware.dwarfhustle.model.api.materials.Liquid;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.api.objects.PropertiesSet;
import com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObject;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.javadsl.StashOverflowException;
import akka.actor.typed.javadsl.TimerScheduler;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Retrieves {@link MapChunk} map chunks with {@link MapBlock} blocks, sorts the
 * blocks by material, removes any not visible blocks, combines the block to one
 * mesh, sets the texture coordinates according to the texture map.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainActor {

    private static final Duration KNOWLEDGE_GET_TIMEOUT = ofSeconds(60);

    private static final String UPDATE_TERRAIN_MESSAGE_TIMER_KEY = "UpdateModelMessage-Timer";

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, TerrainActor.class.getSimpleName());

    public static final String NAME = TerrainActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    static final int BLOCK_MATERIAL_WATER = "water".hashCode();

    static final int BLOCK_MATERIAL_MAGMA = "magma".hashCode();

    static final int BLOCK_MATERIAL_SELECTED = "selected".hashCode();

    static final int BLOCK_MATERIAL_FOCUSED = "focused".hashCode();

    static final int OBJECT_BLOCK_CEILING = "OBJECT_BLOCK_CEILING".hashCode();

    static final int OBJECT_BLOCK_FOCUS = "OBJECT_BLOCK_FOCUS".hashCode();

    static final int UNDISCOVERED_MATERIAL = "UNDISCOVERED_MATERIAL".hashCode();

    static final int UNKNOWN_MATERIAL = "UNKNOWN_MATERIAL".hashCode();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {
        public final TerrainState terrainState;
        public final TerrainCameraState cameraState;
        public final TerrainSelectBlockState selectBlockState;
        public final ChunksBoundingBoxState chunksBoundingBoxState;
        public final TerrainRollState terrainRollState;
        public final ObjectsGetter materials;
        public final ObjectsGetter models;
        public final IntLongMap knowledges;
        public final ObjectsGetter chunks;
        public final ObjectsGetter og;
        public final ObjectsSetter os;
        public final ObjectsGetter mg;
        public final ObjectsSetter ms;
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
     * Factory to create {@link TerrainActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface TerrainActorFactory {
        TerrainActor create(ActorContext<Message> context, StashBuffer<Message> stash, TimerScheduler<Message> timer);
    }

    /**
     * Creates the {@link TerrainActor}.
     *
     * @param actor
     */
    private static Behavior<Message> create(Injector injector, ActorSystemProvider actor) {
        return Behaviors.withTimers(timer -> Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            context.pipeToSelf(CompletableFuture.supplyAsync(() -> attachState(injector, actor)), (result, cause) -> {
                if (cause == null) {
                    return result;
                } else {
                    return new SetupErrorMessage(cause);
                }
            });
            return injector.getInstance(TerrainActorFactory.class).create(context, stash, timer).start(injector);
        })));
    }

    /**
     * Creates the {@link TerrainActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        final var actor = injector.getInstance(ActorSystemProvider.class);
        return createNamedActor(actor.getActorSystem(), timeout, ID, KEY, NAME, create(injector, actor));
    }

    private static Message attachState(Injector injector, ActorSystemProvider actor) {
        final var app = injector.getInstance(Application.class);
        try {
            final var terrainState = injector.getInstance(TerrainState.class);
            final var cameraState = injector.getInstance(TerrainCameraState.class);
            final var chunksBoundingBoxState = injector.getInstance(ChunksBoundingBoxState.class);
            final var terrainRollState = injector.getInstance(TerrainRollState.class);
            final var selectBlockState = injector.getInstance(TerrainSelectBlockState.class);
            app.enqueue(() -> {
                app.getStateManager().attach(terrainState);
                app.getStateManager().attach(cameraState);
                app.getStateManager().attach(chunksBoundingBoxState);
                app.getStateManager().attach(terrainRollState);
                app.getStateManager().attach(selectBlockState);
                chunksBoundingBoxState.setEnabled(false);
            });
            final var ma = actor.getObjectGetterAsyncNow(MaterialAssetsCacheActor.ID);
            final var mo = actor.getObjectGetterAsyncNow(ModelsAssetsCacheActor.ID);
            final var og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            final var os = actor.getObjectSetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            final var cg = actor.getObjectGetterAsyncNow(MapChunksJcsCacheActor.ID);
            final var ko = actor.getActorAsyncNow(PowerLoomKnowledgeActor.ID);
            final var mg = actor.getObjectGetterAsyncNow(MapObjectsJcsCacheActor.ID);
            final var ms = actor.getObjectSetterAsyncNow(MapObjectsJcsCacheActor.ID);
            final var s = injector.getInstance(ActorSystemProvider.class).getActorSystem();
            final var k = IntLongMaps.mutable.ofInitialCapacity(100);
            k.put(BLOCK_MATERIAL_WATER,
                    askKnowledgeIdByName(ko, KNOWLEDGE_GET_TIMEOUT, s.scheduler(), Liquid.TYPE, "water"));
            k.put(BLOCK_MATERIAL_MAGMA,
                    askKnowledgeIdByName(ko, KNOWLEDGE_GET_TIMEOUT, s.scheduler(), Liquid.TYPE, "magma"));
            k.put(OBJECT_BLOCK_CEILING,
                    askKnowledgeIdByName(ko, KNOWLEDGE_GET_TIMEOUT, s.scheduler(), BlockObject.TYPE, "block-ceiling"));
            k.put(OBJECT_BLOCK_FOCUS,
                    askKnowledgeIdByName(ko, KNOWLEDGE_GET_TIMEOUT, s.scheduler(), BlockObject.TYPE, "block-focus"));
            k.put(UNDISCOVERED_MATERIAL, 0xfffe);
            k.put(UNKNOWN_MATERIAL, 0xffff);
            k.put(BLOCK_MATERIAL_SELECTED, 0xfffd);
            k.put(BLOCK_MATERIAL_FOCUSED, 0xfffc);
            return new InitialStateMessage(terrainState, cameraState, selectBlockState, chunksBoundingBoxState,
                    terrainRollState, ma, mo, k.toImmutable(), cg, og, os, mg, ms);
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
    private ActorSystemProvider actor;

    @Inject
    private BlockModelUpdateFactory blockModelUpdateFactory;

    @Inject
    private Application app;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    private AssetManager assets;

    @Inject
    private CollectChunksUpdateFactory collectChunksUpdateFactory;

    private InitialStateMessage is;

    private MutableList<Geometry> blockNodes;

    private ImmutableList<Geometry> copyBlockNodes;

    private MutableList<Geometry> ceilingNodes;

    private ImmutableList<Geometry> copyCeilingNodes;

    private Optional<StartTerrainForGameMapMessage> previousStartTerrainForGameMapMessage = Optional.empty();

    private MutableList<Geometry> waterNodes;

    private ImmutableList<Geometry> copyWaterNodes;

    private MutableList<Geometry> magmaNodes;

    private ImmutableList<Geometry> copyMagmaNodes;

    private MutableIntObjectMap<MaterialKey> materialKeys;

    private MutableLongObjectMap<Multimap<MaterialKey, Integer>> materialBlocks;

    private MutableLongObjectMap<Multimap<MaterialKey, Integer>> materialCeilings;

    private BlockModelUpdate blockModelUpdate;

    private long undiscoveredMaterialId;

    private boolean hideUndiscovered;

    private long terrainUpdateDuration;

    private CollectChunksUpdate collectChunksUpdate;

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
        this.materialKeys = IntObjectMaps.mutable.empty();
        this.hideUndiscovered = gs.get().hideUndiscovered.get();
        this.terrainUpdateDuration = gs.get().terrainUpdateDuration.get().toMillis();
        gs.get().hideUndiscovered.addListener((observable, oldValue, newValue) -> hideUndiscovered = newValue);
        this.depthLayers = gs.get().visibleDepthLayers.get();
        gs.get().visibleDepthLayers.addListener((observable, oldValue, newValue) -> depthLayers = newValue.intValue());
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
        is = m;
        undiscoveredMaterialId = kid2Id(is.knowledges.get(UNDISCOVERED_MATERIAL));
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     * Reacts to the {@link StartTerrainForGameMapMessage} message.
     */
    private Behavior<Message> onStartTerrainForGameMap(StartTerrainForGameMapMessage m) {
        log.debug("onStartTerrainForGameMap {}", m);
        blockNodes = Lists.mutable.empty();
        ceilingNodes = Lists.mutable.empty();
        waterNodes = Lists.mutable.empty();
        magmaNodes = Lists.mutable.empty();
        previousStartTerrainForGameMapMessage = Optional.of(m);
        materialBlocks = LongObjectMaps.mutable.empty();
        materialCeilings = LongObjectMaps.mutable.empty();
        blockModelUpdate = blockModelUpdateFactory.create(is.materials);
        collectChunksUpdate = collectChunksUpdateFactory.create();
        app.enqueue(() -> {
            final var gm = getGameMap(is.og, m.gm);
            is.selectBlockState.setOnRetrieveChunk(id -> getChunk(is.chunks, id));
            is.selectBlockState.setOnRetrieveMap(() -> getGameMap(is.og, m.gm));
            is.cameraState.setTerrainBounds(
                    new BoundingBox(new Vector3f(), gm.getWidth(), gm.getHeight(), gs.get().visibleDepthLayers.get()));
            is.cameraState.updateCamera(gm.getCameraPos(), gm.getCameraRot(), gm.getCursorZ());
            is.cameraState.setOnRetrieveMap(() -> getGameMap(is.og, m.gm));
            is.cameraState.setOnSaveCamera((pos, rot) -> {
                final var gm0 = getGameMap(is.og, m.gm);
                gm0.setCameraPos(pos.x, pos.y, pos.z);
                gm0.setCameraRot(rot.getX(), rot.getY(), rot.getZ(), rot.getW());
                setGameMap(is.os, gm0);
            });
            is.cameraState.setOnSaveZ(cursorZ -> {
                final var gm0 = getGameMap(is.og, m.gm);
                gm0.setCursorZ(cursorZ);
                setGameMap(is.os, gm0);
                actor.tell(new MapCursorUpdateMessage(gm0.getCursor()));
            });
            is.selectBlockState.setOnSaveCursor(c -> {
                final val gm0 = getGameMap(is.og, m.gm);
                if (gm0.getCursor().equals(c.getX(), c.getY(), c.getZ())) {
                    return;
                }
                gm0.setCursor(c);
                setGameMap(is.os, gm0);
                actor.tell(new MapCursorUpdateMessage(c));
                actor.tell(new MapTileUnderCursorMessage(c));
            });
            is.selectBlockState.setOnSelectSet((s, e) -> {
                final var gm0 = getGameMap(is.og, m.gm);
                gm0.clearSelectedBlocks();
                for (int x = s.getX(); x <= e.getX(); x++) {
                    for (int y = e.getY(); y <= s.getY(); y++) {
                        final int index = calcIndex(gm0, x, y, s.getZ());
                        gm0.addSelectedBlock(index);
                    }
                }
                setGameMap(is.os, gm0);
                actor.tell(new SetSingleBlockSelectingFinishedMessage());
            });
            is.selectBlockState.initKeys();
        });
        gs.get().mouseEnteredGui.addListener((o, oldv, newv) -> {
            if (newv) {
                actor.tell(new MouseEnteredGuiMessage());
            } else {
                actor.tell(new MouseExitedGuiMessage());
            }
        });
        timer.startTimerAtFixedRate(UPDATE_TERRAIN_MESSAGE_TIMER_KEY, new UpdateTerrainMessage(m.gm),
                gs.get().terrainUpdateDuration.get());
        return Behaviors.same();
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
        // log.debug("onUpdateModel {}", m);
        final var oldtime = System.currentTimeMillis();
        final var root = getChunk(is.chunks, 0);
        final var gm = getGameMap(is.og, m.gm);
        final var cz = gm.getCursorZ();
        clearMaps();
        collectChunksUpdate.collectChunks(root, cz, cz, depthLayers, gm, this::isBlockVisible, is.chunks,
                this::putMapBlock);
        final var bnum = blockModelUpdate.updateModelBlocks(materialBlocks, gm, this::retrieveBlockMesh,
                (chunk, index, n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z) -> FastMath.approximateEquals(n0z, -1.0f),
                is.chunks, this::putBlockNodes);
        blockModelUpdate.updateModelBlocks(materialCeilings, gm, this::retrieveCeilingMesh, NormalsPredicate.FALSE,
                is.chunks, this::putCeilingNodes);
        renderMeshs(gm);
        final var finishtime = System.currentTimeMillis();
        log.trace("updateModel done in {} showing {} blocks", finishtime - oldtime, bnum);
    }

    private void clearMaps() {
        blockNodes.clear();
        ceilingNodes.clear();
        waterNodes.clear();
        magmaNodes.clear();
        for (final var pair : materialBlocks.keyValuesView()) {
            final var map = (MutableMultimap<MaterialKey, Integer>) pair.getTwo();
            map.clear();
        }
        for (final var pair : materialCeilings.keyValuesView()) {
            final var map = (MutableMultimap<MaterialKey, Integer>) pair.getTwo();
            map.clear();
        }
    }

    private boolean isBlockVisible(GameMap gm, MapChunk chunk, int i, int x, int y, int z) {
        final var off = MapBlockBuffer.calcOff(chunk, x, y, z);
        final var p = MapBlockBuffer.getProp(chunk.getBlocks(), off);
        if (PropertiesSet.get(p, MapBlock.EMPTY_POS)) {
            if (gm.isCursor(x, y, z)) {
                return true;
            }
            return false;
        }
        return true;
    }

    private void putMapBlock(GameMap gm, MapChunk chunk, int index) {
        final var blocks = lazyPutMaterialBlocks(chunk);
        final var ceilings = lazyPutMaterialCeilings(chunk);
        putMaterialKey(gm, chunk, index, blocks, ceilings);
    }

    private void putMaterialKey(GameMap gm, MapChunk chunk, int index, MutableMultimap<MaterialKey, Integer> blocks,
            MutableMultimap<MaterialKey, Integer> ceilings) {
        final int x = calcX(index, chunk), y = calcY(index, chunk), z = calcZ(index, chunk);
//        if (x == 12 && y == 10 && z == 10) {
        // System.out.println("TerrainActor.putMaterialKey()"); // TODO
        // }
        final int offset = calcOff(index);
        val bblocks = chunk.getBlocks();
        final int prop = getProp(bblocks, offset);
        val objects = new Long[4];
        findObjects(objects, gm, chunk, index, z - 1);
        long mid = kid2Id(getMaterial(bblocks, offset));
        final Long emission = null; // TODO emission
        boolean selected = false;
        boolean transparent = false;
        if (isProp(prop, EMPTY_POS)) {
            transparent = true;
        }
        if (isProp(prop, FILLED_POS)) {
            selected = gm.isSelectedBlock(x, y, z);
            if (!selected && z == gm.getCursorZ() + 1) {
                val up = getNeighborUp(index, chunk, gm.getWidth(), gm.getHeight(), gm.getDepth(), is.chunks);
                if (up.isValid()) {
                    val upchunk = up.getC();
                    if (isProp(upchunk.getBlocks(), up.getOff(), EMPTY.flag)) {
                        selected = gm.isSelectedBlock(x, y, z - 1);
                    }
                }
            }
        }
        if (hideUndiscovered && !isProp(prop, DISCOVERED_POS)) {
            mid = undiscoveredMaterialId;
        }
        MaterialKey key = null;
        try {
            key = lazyCreateKey(mid, objects, emission, selected, transparent);
            blocks.put(key, index);
        } catch (final NullPointerException e) {
            final var oid = kid2Id(getObject(bblocks, offset));
            System.out.printf("[MaterialKey NULL] %d %d/%d/%d - %s\n", oid, x, y, z, e); // TODO
        }
        final var cursorZ = gm.getCursorZ();
        if (z <= cursorZ && !(isProp(prop, HAVE_NATURAL_LIGHT_POS))) {
            val up = getNeighborUp(index, chunk, gm.getWidth(), gm.getHeight(), gm.getDepth(), is.chunks);
            var ceilingmid = 0L;
            if (hideUndiscovered && !(isProp(prop, DISCOVERED_POS))) {
                ceilingmid = undiscoveredMaterialId;
            } else {
                ceilingmid = getMaterial(up.c.getBlocks(), up.getOff());
            }
            ceilings.put(lazyCreateKey(ceilingmid, objects, emission, selected, false), index);
        }
    }

    private Long[] findObjects(Long[] objects, GameMap gm, MapChunk chunk, int index, int upZ) {
        if (upZ < 0) {
            return objects;
        }
        final var mapIndexUp = calcIndex(gm, calcX(index, chunk), calcY(index, chunk), upZ);
        final var count = gm.getFilledBlocks().get(mapIndexUp);
        if (count != null && count.get() > 0) {
            final var mo = getMapObject(is.mg, gm, mapIndexUp);
            var i = 0;
            for (final LongIntPair pair : mo.getOids().keyValuesView()) {
                final GameMapObject go = is.og.get(pair.getTwo(), pair.getOne());
                if (go.isHaveTex()) {
                    objects[i++] = kid2Id(go.getKid());
                }
                if (i == objects.length) {
                    break;
                }
            }
        }
        return objects;
    }

    private MutableMultimap<MaterialKey, Integer> lazyPutMaterialCeilings(MapChunk chunk) {
        final var cid = chunk.getId();
        var ceilings = (MutableMultimap<MaterialKey, Integer>) materialCeilings.get(cid);
        if (ceilings == null) {
            ceilings = Multimaps.mutable.list.empty();
            materialCeilings.put(cid, ceilings);
        }
        return ceilings;
    }

    private MutableMultimap<MaterialKey, Integer> lazyPutMaterialBlocks(MapChunk chunk) {
        final var cid = chunk.getId();
        var blocks = (MutableMultimap<MaterialKey, Integer>) materialBlocks.get(cid);
        if (blocks == null) {
            blocks = Multimaps.mutable.list.empty();
            materialBlocks.put(cid, blocks);
        }
        return blocks;
    }

    private MaterialKey lazyCreateKey(long mid, Long[] objects, Long emission, boolean selected, boolean transparent) {
        final var hash = MaterialKey.calcHash(mid, objects, emission, selected, transparent);
        var key = materialKeys.get(hash);
        if (key == null) {
            final var tex = getTexture(mid);
            TextureCacheObject emissionTex = null;
            if (emission != null) {
                emissionTex = getTexture(emission);
            }
            final var objectTexs = new TextureCacheObject[objects.length];
            for (var i = 0; i < objectTexs.length; i++) {
                if (objects[i] != null) {
                    objectTexs[i] = getTexture(objects[i]);
                }
            }
            key = new MaterialKey(assets, tex, objectTexs, objects, emissionTex, selected, transparent);
            materialKeys.put(hash, key);
        }
        return key;
    }

    private TextureCacheObject getTexture(long id) {
        return is.materials.get(TextureCacheObject.OBJECT_TYPE, id);
    }

    private Mesh retrieveBlockMesh(MapChunk chunk, int index) {
        final var oid = kid2Id(getObject(chunk.getBlocks(), index * MapBlockBuffer.SIZE));
        final ModelCacheObject model = is.models.get(ModelCacheObject.OBJECT_TYPE, oid);
        final var geo = ((Node) model.model).getChild(0);
        return ((Geometry) (geo)).getMesh();
    }

    private Mesh retrieveCeilingMesh(MapChunk chunk, int index) {
        final var oid = is.knowledges.get(OBJECT_BLOCK_CEILING);
        final ModelCacheObject model = is.models.get(ModelCacheObject.OBJECT_TYPE, oid);
        final var node = (Node) model.model;
        return ((Geometry) (node.getChild(0))).getMesh();
    }

    private void putBlockNodes(MaterialKey m, Geometry geo) {
        if (m.transparent) {
            geo.setShadowMode(ShadowMode.Off);
            geo.setQueueBucket(Bucket.Translucent);
        } else {
            geo.setShadowMode(ShadowMode.Receive);
        }
        if (m.isMaterial(is.knowledges.get(BLOCK_MATERIAL_WATER))) {
            waterNodes.add(geo);
        } else if (m.isMaterial(is.knowledges.get(BLOCK_MATERIAL_MAGMA))) {
            magmaNodes.add(geo);
        } else {
            blockNodes.add(geo);
        }
    }

    private void putCeilingNodes(MaterialKey material, Geometry geo) {
        geo.setShadowMode(ShadowMode.Off);
        ceilingNodes.add(geo);
    }

    @SneakyThrows
    private void renderMeshs(GameMap gm) {
        copyBlockNodes = Lists.immutable.ofAll(blockNodes);
        copyCeilingNodes = Lists.immutable.ofAll(ceilingNodes);
        copyWaterNodes = Lists.immutable.ofAll(waterNodes);
        copyMagmaNodes = Lists.immutable.ofAll(magmaNodes);
        var task = app.enqueue(() -> {
            renderMeshsOnRenderingThread(gm);
            return true;
        });
        try {
            task.get(terrainUpdateDuration, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            // log.warn("renderMeshs timeout");
            return;
        } catch (InterruptedException e) {
            log.error("renderMeshs interrupted", e);
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    private boolean renderMeshsOnRenderingThread(GameMap gm) {
        is.terrainState.setLightDir(gm.sunPos[0], gm.sunPos[1], gm.sunPos[2]);
        is.terrainState.clearBlockNodes();
        is.terrainState.clearCeilingNodes();
        is.terrainState.clearWaterNodes();
        for (final var geo : copyBlockNodes) {
            is.terrainState.addBlockMesh(geo);
        }
        for (final var geo : copyCeilingNodes) {
            is.terrainState.addCeilingMesh(geo);
        }
        for (final var geo : copyWaterNodes) {
            is.terrainState.addWaterMesh(geo);
            is.terrainState.setWaterPos(geo.getModelBound().getCenter().z);
        }
        for (final var geo : copyMagmaNodes) {
            is.terrainState.addMagmaMesh(geo);
            is.terrainState.setMagmaPos(geo.getModelBound().getCenter().z);
        }
        return true;
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onAppPaused(AppPausedMessage m) {
        log.trace("onAppPaused {}", m);
        if (m.paused) {
            timer.cancel(UPDATE_TERRAIN_MESSAGE_TIMER_KEY);
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
        log.trace("onShutdown {}", m);
        timer.cancelAll();
        return Behaviors.stopped();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    @SneakyThrows
    private Behavior<Message> onMapCursorUpdate(MapCursorUpdateMessage m) {
        log.trace("onMapCursorUpdate {}", m);
        final var gm = getGameMap(is.og, gs.get().currentMap.get());
        try (val lock = gm.acquireLockMapObjects()) {
            final Block block = is.og.get(Block.OBJECT_TYPE, gm.getCursorObject());
            final var mo = getMapObject(is.mg, gm, block.getPos());
            if (mo.removeObject(block.getId())) {
                is.ms.set(MapObject.OBJECT_TYPE, mo);
                if (mo.isEmpty()) {
                    gm.removeFilledBlock(mo.getCid(), mo.getIndex());
                    is.os.set(GameMap.OBJECT_TYPE, gm);
                }
            }
            block.setPos(m.cursor);
            is.os.set(Block.OBJECT_TYPE, block);
            final var monew = getMapObject(is.mg, gm, block.getPos());
            if (monew.addObject(Block.OBJECT_TYPE, block.getId())) {
                is.ms.set(MapObject.OBJECT_TYPE, monew);
                gm.addFilledBlock(monew.getCid(), monew.getIndex());
                is.os.set(GameMap.OBJECT_TYPE, gm);
            }
        }
        return Behaviors.same();
    }

    /**
     * @see SetMultiBlockSelectingModeMessage
     */
    @SneakyThrows
    private Behavior<Message> onSetMultiBlockSelectingMode(SetMultiBlockSelectingModeMessage m) {
        log.trace("onSetMultiBlockSelectingMode {}", m);
        is.selectBlockState.setMultiSelectEnabled(m.enabled);
        return Behaviors.same();
    }

    /**
     * @see SetSingleBlockSelectingModeMessage
     */
    @SneakyThrows
    private Behavior<Message> onSetSingleBlockSelectingMode(SetSingleBlockSelectingModeMessage m) {
        log.trace("onSetSingleBlockSelectingMode {}", m);
        is.selectBlockState.setSingleSelectEnabled(m.enabled);
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
                .onMessage(StartTerrainForGameMapMessage.class, this::onStartTerrainForGameMap)//
                .onMessage(UpdateTerrainMessage.class, this::onUpdateModel)//
                .onMessage(AppPausedMessage.class, this::onAppPaused)//
                .onMessage(MapCursorUpdateMessage.class, this::onMapCursorUpdate)//
                .onMessage(SetMultiBlockSelectingModeMessage.class, this::onSetMultiBlockSelectingMode)//
                .onMessage(SetSingleBlockSelectingModeMessage.class, this::onSetSingleBlockSelectingMode)//
        ;
    }
}
