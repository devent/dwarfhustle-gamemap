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
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.getNeighborUp;
import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage.askBlockMaterialId;
import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage.askObjectTypeId;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.IntLongMaps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.ImmutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.impl.factory.Multimaps;

import com.anrisoftware.dwarfhustle.gamemap.jme.terrain.BlockModelUpdate.BlockModelUpdateFactory;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppPausedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.StartTerrainForGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.ModelCacheObject;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.map.BlockObject;
import com.anrisoftware.dwarfhustle.model.api.materials.Liquid;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.db.store.MapChunksStore;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;

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
 * Retrieves {@link MapChunk} map chunks with {@link MapBlock} blocks, sorts the
 * blocks by material, removes any not visible blocks, combines the block to one
 * mesh, sets the texture coordinates according to the texture map.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainActor {

    private static final String UPDATE_TERRAIN_MESSAGE_TIMER_KEY = "UpdateModelMessage-Timer";

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, TerrainActor.class.getSimpleName());

    public static final String NAME = TerrainActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    static final int BLOCK_MATERIAL_WATER = "water".hashCode();

    static final int BLOCK_MATERIAL_MAGMA = "magma".hashCode();

    static final int OBJECT_BLOCK_CEILING = "OBJECT_BLOCK_CEILING".hashCode();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {
        public final TerrainState terrainState;
        public final TerrainCameraState cameraState;
        public final TerrainSelectBlockState selectBlockState;
        public final ChunksBoundingBoxState chunksBoundingBoxState;
        public final TerrainRollState terrainRollState;
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
        public final MapChunksStore store;
    }

    /**
     * Factory to create {@link TerrainTestKeysActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface TerrainActorFactory {
        TerrainActor create(ActorContext<Message> context, StashBuffer<Message> stash, TimerScheduler<Message> timer,
                @Assisted("materials") ObjectsGetter materials, @Assisted("models") ObjectsGetter models,
                @Assisted("knowledges") ImmutableIntLongMap knowledges);
    }

    /**
     * Creates the {@link TerrainTestKeysActor}.
     */
    public static Behavior<Message> create(Injector injector, CompletionStage<ObjectsGetter> materials,
            CompletionStage<ObjectsGetter> models, CompletionStage<ActorRef<Message>> knowledge) {
        return Behaviors.withTimers(timer -> Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            context.pipeToSelf(createState(injector, context), (result, cause) -> {
                if (cause == null) {
                    return result;
                } else {
                    return new SetupErrorMessage(cause);
                }
            });
            var ma = materials.toCompletableFuture().get(15, SECONDS);
            var mo = models.toCompletableFuture().get(15, SECONDS);
            var ko = knowledge.toCompletableFuture().get(15, SECONDS);
            var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
            MutableIntLongMap knowledges = IntLongMaps.mutable.ofInitialCapacity(100);
            knowledges.put(BLOCK_MATERIAL_WATER,
                    askBlockMaterialId(ko, ofSeconds(15), system.scheduler(), Liquid.TYPE, "water"));
            knowledges.put(BLOCK_MATERIAL_MAGMA,
                    askBlockMaterialId(ko, ofSeconds(15), system.scheduler(), Liquid.TYPE, "magma"));
            knowledges.put(OBJECT_BLOCK_CEILING,
                    askObjectTypeId(ko, ofSeconds(15), system.scheduler(), BlockObject.TYPE, "block-ceiling"));
            return injector.getInstance(TerrainActorFactory.class)
                    .create(context, stash, timer, ma, mo, knowledges.toImmutable()).start(injector);
        })));
    }

    /**
     * Creates the {@link TerrainTestKeysActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
            CompletionStage<ObjectsGetter> materials, CompletionStage<ObjectsGetter> models,
            CompletionStage<ActorRef<Message>> knowledge) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, materials, models, knowledge));
    }

    private static CompletionStage<Message> createState(Injector injector, ActorContext<Message> context) {
        return CompletableFuture.supplyAsync(() -> attachState(injector));
    }

    private static Message attachState(Injector injector) {
        var app = injector.getInstance(Application.class);
        try {
            var f = app.enqueue(() -> {
                var terrainState = injector.getInstance(TerrainState.class);
                var cameraState = injector.getInstance(TerrainCameraState.class);
                var chunksBoundingBoxState = injector.getInstance(ChunksBoundingBoxState.class);
                var terrainRollState = injector.getInstance(TerrainRollState.class);
                var selectBlockState = injector.getInstance(TerrainSelectBlockState.class);
                app.getStateManager().attach(terrainState);
                app.getStateManager().attach(cameraState);
                app.getStateManager().attach(chunksBoundingBoxState);
                app.getStateManager().attach(terrainRollState);
                app.getStateManager().attach(selectBlockState);
                chunksBoundingBoxState.setEnabled(false);
                return new InitialStateMessage(terrainState, cameraState, selectBlockState, chunksBoundingBoxState,
                        terrainRollState);
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
    @Assisted
    private TimerScheduler<Message> timer;

    @Inject
    @Assisted("materials")
    private ObjectsGetter materials;

    @Inject
    @Assisted("models")
    private ObjectsGetter models;

    @Inject
    @Assisted("knowledges")
    private ImmutableIntLongMap knowledges;

    @Inject
    private BlockModelUpdateFactory blockModelUpdateFactory;

    @Inject
    private Application app;

    @Inject
    private GameSettingsProvider gs;

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

    private GameMap gm;

    private MutableMultimap<Long, MapBlock> materialBlocks;

    private MutableMultimap<Long, MapBlock> materialCeilings;

    private Function<Integer, MapChunk> retriever;

    private BlockModelUpdate blockModelUpdate;

    private int CursorZ;

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
        this.materialBlocks = Multimaps.mutable.list.empty();
        this.materialCeilings = Multimaps.mutable.list.empty();
        this.retriever = m.store::getChunk;
        this.blockModelUpdate = blockModelUpdateFactory.create(materials);
        app.enqueue(() -> {
            is.selectBlockState.setRetriever(m.store);
            is.selectBlockState.setGameMap(m.gm);
            is.cameraState.setTerrainBounds(
                    new BoundingBox(new Vector3f(), m.gm.width, m.gm.height, gs.get().visibleDepthLayers.get()));
            is.cameraState.updateCamera(m.gm);
            is.terrainRollState.setTerrainNode(is.terrainState.getTerrainNode());
            is.terrainRollState.setWaterNode(is.terrainState.getWaterNode());
            is.terrainRollState.setBoundingNode(is.chunksBoundingBoxState.getNode());
        });
        timer.startTimerAtFixedRate(UPDATE_TERRAIN_MESSAGE_TIMER_KEY, new UpdateTerrainMessage(m.gm, m.store),
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
        long oldtime = System.currentTimeMillis();
        var root = m.store.getChunk(0);
        blockNodes.clear();
        ceilingNodes.clear();
        waterNodes.clear();
        magmaNodes.clear();
        materialBlocks.clear();
        materialCeilings.clear();
        this.CursorZ = m.gm.getCursorZ();
        int depthLayers = gs.get().visibleDepthLayers.get();
        blockModelUpdate.collectChunks(root, CursorZ, CursorZ, depthLayers, m.gm.depth, retriever, this::putMapBlock);
        int bnum = blockModelUpdate.updateModelBlocks(materialBlocks, m.gm, this::retrieveBlockMesh,
                (mb, n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z) -> {
                    return FastMath.approximateEquals(n0z, -1.0f);
                }, this::putBlockNodes);
        blockModelUpdate.updateModelBlocks(materialCeilings, m.gm, this::retrieveCeilingMesh, NormalsPredicate.FALSE,
                this::putCeilingNodes);
        renderMeshs(m.gm);
        long finishtime = System.currentTimeMillis();
        log.trace("updateModel done in {} showing {} blocks", finishtime - oldtime, bnum);
    }

    private void putMapBlock(MapChunk chunk, MapBlock mb) {
        this.materialBlocks.put(mb.getMaterialId(), mb);
        if (mb.pos.z <= CursorZ && !mb.isHaveNaturalLight()) {
            this.materialCeilings.put(getNeighborUp(mb, chunk, retriever).getMaterialId(), mb);
        }
    }

    private Mesh retrieveBlockMesh(MapBlock mb) {
        ModelCacheObject model = models.get(ModelCacheObject.OBJECT_TYPE, mb.getObjectId());
        return ((Geometry) (model.model)).getMesh();
    }

    private Mesh retrieveCeilingMesh(MapBlock mb) {
        ModelCacheObject model = models.get(ModelCacheObject.OBJECT_TYPE, knowledges.get(OBJECT_BLOCK_CEILING));
        return ((Geometry) (model.model)).getMesh();
    }

    private void putBlockNodes(Long material, Geometry geo) {
        geo.setShadowMode(ShadowMode.Receive);
        if (material.equals(knowledges.get(BLOCK_MATERIAL_WATER))) {
            waterNodes.add(geo);
        } else if (material.equals(knowledges.get(BLOCK_MATERIAL_MAGMA))) {
            magmaNodes.add(geo);
        } else {
            blockNodes.add(geo);
        }
    }

    private void putCeilingNodes(Long material, Geometry geo) {
        geo.setShadowMode(ShadowMode.Off);
        ceilingNodes.add(geo);
    }

    @SneakyThrows
    private void renderMeshs(GameMap gm) {
        this.gm = gm;
        copyBlockNodes = Lists.immutable.ofAll(blockNodes);
        copyCeilingNodes = Lists.immutable.ofAll(ceilingNodes);
        copyWaterNodes = Lists.immutable.ofAll(waterNodes);
        copyMagmaNodes = Lists.immutable.ofAll(magmaNodes);
        var task = app.enqueue(this::renderMeshsOnRenderingThread);
        try {
            task.get(gs.get().terrainUpdateDuration.get().toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            // log.warn("renderMeshs timeout");
            return;
        } catch (InterruptedException e) {
            log.error("renderMeshs interrupted", e);
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    private boolean renderMeshsOnRenderingThread() {
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
