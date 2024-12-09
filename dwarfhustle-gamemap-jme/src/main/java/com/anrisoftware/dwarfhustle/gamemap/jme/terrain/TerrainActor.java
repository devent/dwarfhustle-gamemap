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
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcX;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcY;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcZ;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameMap.getGameMap;
import static com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject.kid2Id;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapBlock.DISCOVERED_POS;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapBlock.EMPTY_POS;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapBlock.HAVE_NATURAL_LIGHT_POS;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.getChunk;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.getMaterial;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.getObject;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.haveProp;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.getNeighborUp;
import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage.askBlockMaterialId;
import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage.askObjectTypeId;
import static java.time.Duration.ofSeconds;

import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.IntLongMaps;
import org.eclipse.collections.api.factory.primitive.IntObjectMaps;
import org.eclipse.collections.api.factory.primitive.LongLongMaps;
import org.eclipse.collections.api.factory.primitive.LongObjectMaps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.IntLongMap;
import org.eclipse.collections.api.map.primitive.LongLongMap;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableLongLongMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.factory.Multimaps;

import com.anrisoftware.dwarfhustle.gamemap.jme.app.AssetsLoadMaterialTextures;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.MaterialAssetsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.ModelsAssetsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.model.CollectChunksUpdate;
import com.anrisoftware.dwarfhustle.gamemap.jme.model.CollectChunksUpdate.CollectChunksUpdateFactory;
import com.anrisoftware.dwarfhustle.gamemap.jme.terrain.BlockModelUpdate.BlockModelUpdateFactory;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppPausedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.StartTerrainForGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.ModelCacheObject;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.map.BlockObject;
import com.anrisoftware.dwarfhustle.model.api.materials.Liquid;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.api.objects.PropertiesSet;
import com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
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
import akka.actor.typed.javadsl.TimerScheduler;
import akka.actor.typed.receptionist.ServiceKey;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import jakarta.inject.Inject;
import javafx.beans.value.ChangeListener;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
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

    static final int OBJECT_BLOCK_CEILING = "OBJECT_BLOCK_CEILING".hashCode();

    static final int UNDISCOVERED_MATERIAL = "UNDISCOVERED_MATERIAL".hashCode();

    static final int UNKNOWN_MATERIAL = "UNKNOWN_MATERIAL".hashCode();

    static final long SELECTED_BLOCK_RAMP_TWO = 0xfff6;
    static final long SELECTED_BLOCK_RAMP_TRI = 0xfff7;
    static final long SELECTED_BLOCK_RAMP_SINGLE = 0xfff8;
    static final long SELECTED_BLOCK_RAMP_PERP = 0xfff9;
    static final long SELECTED_BLOCK_RAMP_EDGE_OUT = 0xfffa;
    static final long SELECTED_BLOCK_RAMP_EDGE_IN = 0xfffb;
    static final long SELECTED_BLOCK_RAMP_CORNER = 0xfffc;
    static final long SELECTED_BLOCK_NORMAL = 0xfffd;

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
        public final LongLongMap selectedMaterials;
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
        var actor = injector.getInstance(ActorSystemProvider.class);
        return createNamedActor(actor.getActorSystem(), timeout, ID, KEY, NAME, create(injector, actor));
    }

    private static Message attachState(Injector injector, ActorSystemProvider actor) {
        var app = injector.getInstance(Application.class);
        try {
            var terrainState = injector.getInstance(TerrainState.class);
            var cameraState = injector.getInstance(TerrainCameraState.class);
            var chunksBoundingBoxState = injector.getInstance(ChunksBoundingBoxState.class);
            var terrainRollState = injector.getInstance(TerrainRollState.class);
            var selectBlockState = injector.getInstance(TerrainSelectBlockState.class);
            app.enqueue(() -> {
                app.getStateManager().attach(terrainState);
                app.getStateManager().attach(cameraState);
                app.getStateManager().attach(chunksBoundingBoxState);
                app.getStateManager().attach(terrainRollState);
                app.getStateManager().attach(selectBlockState);
                chunksBoundingBoxState.setEnabled(false);
            });
            var ma = actor.getObjectGetterAsyncNow(MaterialAssetsCacheActor.ID);
            var mo = actor.getObjectGetterAsyncNow(ModelsAssetsCacheActor.ID);
            var og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            var os = actor.getObjectSetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            var cg = actor.getObjectGetterAsyncNow(MapChunksJcsCacheActor.ID);
            var ko = actor.getActorAsyncNow(PowerLoomKnowledgeActor.ID);
            var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
            MutableIntLongMap k = IntLongMaps.mutable.ofInitialCapacity(100);
            k.put(BLOCK_MATERIAL_WATER,
                    askBlockMaterialId(ko, KNOWLEDGE_GET_TIMEOUT, system.scheduler(), Liquid.TYPE, "water"));
            k.put(BLOCK_MATERIAL_MAGMA,
                    askBlockMaterialId(ko, KNOWLEDGE_GET_TIMEOUT, system.scheduler(), Liquid.TYPE, "magma"));
            k.put(OBJECT_BLOCK_CEILING,
                    askObjectTypeId(ko, KNOWLEDGE_GET_TIMEOUT, system.scheduler(), BlockObject.TYPE, "block-ceiling"));
            k.put(UNDISCOVERED_MATERIAL, 0xfffe);
            k.put(UNKNOWN_MATERIAL, 0xffff);
            var selectedMaterials = loadSelectedMaterials();
            return new InitialStateMessage(terrainState, cameraState, selectBlockState, chunksBoundingBoxState,
                    terrainRollState, ma, mo, k.toImmutable(), cg, og, os, selectedMaterials);
        } catch (Exception ex) {
            return new SetupErrorMessage(ex);
        }
    }

    @SneakyThrows
    private static LongLongMap loadSelectedMaterials() {
        MutableLongLongMap selectedMaterials = LongLongMaps.mutable.empty();
        var engine = new GroovyScriptEngine(
                new URL[] { AssetsLoadMaterialTextures.class.getResource("/BlockSelectedMaterials.groovy") });
        var binding = new Binding();
        @SuppressWarnings("unchecked")
        var map = (Map<Integer, Integer>) engine.run("BlockSelectedMaterials.groovy", binding);
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            Long key = kid2Id(entry.getKey());
            Long val = kid2Id(entry.getValue());
            selectedMaterials.put(key, val);
        }
        return selectedMaterials.toImmutable();
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
        gs.get().hideUndiscovered
                .addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> hideUndiscovered = newValue);
        this.depthLayers = gs.get().visibleDepthLayers.get();
        gs.get().visibleDepthLayers.addListener(
                (ChangeListener<Number>) (observable, oldValue, newValue) -> depthLayers = newValue.intValue());
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
        this.undiscoveredMaterialId = kid2Id(is.knowledges.get(UNDISCOVERED_MATERIAL));
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     * Reacts to the {@link StartTerrainForGameMapMessage} message.
     */
    private Behavior<Message> onStartTerrainForGameMap(StartTerrainForGameMapMessage m) {
        log.debug("onStartTerrainForGameMap {}", m);
        this.blockNodes = Lists.mutable.empty();
        this.ceilingNodes = Lists.mutable.empty();
        this.waterNodes = Lists.mutable.empty();
        this.magmaNodes = Lists.mutable.empty();
        this.previousStartTerrainForGameMapMessage = Optional.of(m);
        this.materialBlocks = LongObjectMaps.mutable.empty();
        this.materialCeilings = LongObjectMaps.mutable.empty();
        this.blockModelUpdate = blockModelUpdateFactory.create(is.materials);
        this.collectChunksUpdate = collectChunksUpdateFactory.create();
        app.enqueue(() -> {
            final var gm = getGameMap(is.og, m.gm);
            is.selectBlockState.setStorage(is.chunks);
            is.selectBlockState.setGameMap(gm);
            is.cameraState.setTerrainBounds(
                    new BoundingBox(new Vector3f(), gm.getWidth(), gm.getHeight(), gs.get().visibleDepthLayers.get()));
            is.cameraState.updateCamera(gm);
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
        } catch (Exception e) {
            log.error("", e);
        }
        return Behaviors.same();
    }

    private void onUpdateModel0(UpdateTerrainMessage m) {
        // log.debug("onUpdateModel {}", m);
        final long oldtime = System.currentTimeMillis();
        final var root = getChunk(is.chunks, 0);
        final var gm = getGameMap(is.og, m.gm);
        final var cz = gm.getCursorZ();
        clearMaps();
        collectChunksUpdate.collectChunks(root, cz, cz, depthLayers, gm, this::isBlockVisible, is.chunks,
                this::putMapBlock);
        int bnum = blockModelUpdate.updateModelBlocks(materialBlocks, gm, this::retrieveBlockMesh,
                (chunk, index, n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z) -> {
                    return FastMath.approximateEquals(n0z, -1.0f);
                }, is.chunks, this::putBlockNodes);
        blockModelUpdate.updateModelBlocks(materialCeilings, gm, this::retrieveCeilingMesh, NormalsPredicate.FALSE,
                is.chunks, this::putCeilingNodes);
        renderMeshs(gm);
        long finishtime = System.currentTimeMillis();
        log.trace("updateModel done in {} showing {} blocks", finishtime - oldtime, bnum);
    }

    private void clearMaps() {
        blockNodes.clear();
        ceilingNodes.clear();
        waterNodes.clear();
        magmaNodes.clear();
        for (var pair : this.materialBlocks.keyValuesView()) {
            var map = (MutableMultimap<MaterialKey, Integer>) pair.getTwo();
            map.clear();
        }
        for (var pair : this.materialCeilings.keyValuesView()) {
            var map = (MutableMultimap<MaterialKey, Integer>) pair.getTwo();
            map.clear();
        }
    }

    private boolean isBlockVisible(GameMap gm, MapChunk chunk, int i, int x, int y, int z) {
        int off = MapBlockBuffer.calcOff(chunk, x, y, z);
        int p = MapBlockBuffer.getProp(chunk.getBlocks(), off);
        if (PropertiesSet.get(p, MapBlock.EMPTY_POS)) {
            if (gm.isCursor(x, y, z)) {
                return true;
            }
            return false;
        }
        return true;
    }

    private void putMapBlock(GameMap gm, MapChunk chunk, int index) {
        final long cid = chunk.getId();
        var blocks = (MutableMultimap<MaterialKey, Integer>) materialBlocks.get(cid);
        if (blocks == null) {
            blocks = Multimaps.mutable.list.empty();
            materialBlocks.put(cid, blocks);
        }
        var ceilings = (MutableMultimap<MaterialKey, Integer>) materialCeilings.get(cid);
        if (ceilings == null) {
            ceilings = Multimaps.mutable.list.empty();
            materialCeilings.put(cid, ceilings);
        }
        int x = calcX(index, chunk), y = calcY(index, chunk), z = calcZ(index, chunk);
        long mid = kid2Id(getMaterial(chunk.getBlocks(), MapBlockBuffer.calcOff(index)));
        Long emission = null;
        boolean cursor = gm.isCursor(x, y, z);
        if (cursor) {
            long oid = kid2Id(getObject(chunk.getBlocks(), MapBlockBuffer.calcOff(index)));
            emission = is.selectedMaterials.get(oid);
        }
        boolean transparent = false;
        if (haveProp(chunk.getBlocks(), MapBlockBuffer.calcOff(index), EMPTY_POS)) {
            transparent = true;
        }
        if (hideUndiscovered && !(haveProp(chunk.getBlocks(), MapBlockBuffer.calcOff(index), DISCOVERED_POS))) {
            mid = undiscoveredMaterialId;
        }
        try {
            var key = lazyCreateKey(mid, emission, transparent);
            blocks.put(key, index);
        } catch (NullPointerException e) {
            long oid = kid2Id(getObject(chunk.getBlocks(), MapBlockBuffer.calcOff(index)));
            System.out.printf("%d %d/%d/%d - %s\n", oid, x, y, z, e); // TODO
        }
        final var cursorZ = gm.getCursorZ();
        if (z <= cursorZ && !(haveProp(chunk.getBlocks(), MapBlockBuffer.calcOff(index), HAVE_NATURAL_LIGHT_POS))) {
            var neighborUp = getNeighborUp(index, chunk, gm.width, gm.height, gm.depth, is.chunks);
            long ceilingmid = 0;
            if (hideUndiscovered && !(haveProp(chunk.getBlocks(), MapBlockBuffer.calcOff(index), DISCOVERED_POS))) {
                ceilingmid = undiscoveredMaterialId;
            } else {
                ceilingmid = getMaterial(neighborUp.c.getBlocks(), neighborUp.getOff());
            }
            ceilings.put(lazyCreateKey(ceilingmid, emission, false), index);
        }
    }

    private MaterialKey lazyCreateKey(long mid, Long emission, boolean transparent) {
        int hash = MaterialKey.calcHash(mid, emission, transparent);
        MaterialKey key = materialKeys.get(hash);
        if (key == null) {
            TextureCacheObject tex = getTexture(mid);
            TextureCacheObject emissionTex = null;
            if (emission != null) {
                emissionTex = getTexture(emission);
            }
            key = new MaterialKey(assets, tex, emissionTex, transparent);
            materialKeys.put(hash, key);
        }
        return key;
    }

    private TextureCacheObject getTexture(long id) {
        return is.materials.get(TextureCacheObject.OBJECT_TYPE, id);
    }

    private Mesh retrieveBlockMesh(MapChunk chunk, int index) {
        long oid = kid2Id(getObject(chunk.getBlocks(), index * MapBlockBuffer.SIZE));
        ModelCacheObject model = is.models.get(ModelCacheObject.OBJECT_TYPE, oid);
        var geo = ((Node) model.model).getChild(0);
        return ((Geometry) (geo)).getMesh();
    }

    private Mesh retrieveCeilingMesh(MapChunk chunk, int index) {
        long oid = is.knowledges.get(OBJECT_BLOCK_CEILING);
        ModelCacheObject model = is.models.get(ModelCacheObject.OBJECT_TYPE, oid);
        var node = (Node) model.model;
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
        for (var geo : copyBlockNodes) {
            is.terrainState.addBlockMesh(geo);
        }
        for (var geo : copyCeilingNodes) {
            is.terrainState.addCeilingMesh(geo);
        }
        for (var geo : copyWaterNodes) {
            is.terrainState.addWaterMesh(geo);
            is.terrainState.setWaterPos(geo.getModelBound().getCenter().z);
        }
        for (var geo : copyMagmaNodes) {
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
        log.debug("onAppPaused {}", m);
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
