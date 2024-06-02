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
import static com.jme3.math.FastMath.approximateEquals;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.IntObjectMaps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Multimaps;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppPausedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.StartTerrainForGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.ModelCacheObject;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunksStore;
import com.anrisoftware.dwarfhustle.model.api.objects.MapCursor;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.Camera.FrustumIntersect;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

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
     * Factory to create {@link TerrainActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface TerrainActorFactory {
        TerrainActor create(ActorContext<Message> context, StashBuffer<Message> stash, TimerScheduler<Message> timer,
                @Assisted("materials") ObjectsGetter materials, @Assisted("models") ObjectsGetter models);
    }

    /**
     * Creates the {@link TerrainActor}.
     */
    public static Behavior<Message> create(Injector injector, CompletionStage<ObjectsGetter> materials,
            CompletionStage<ObjectsGetter> models) {
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
            return injector.getInstance(TerrainActorFactory.class).create(context, stash, timer, ma, mo)
                    .start(injector);
        })));
    }

    /**
     * Creates the {@link TerrainActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
            CompletionStage<ObjectsGetter> materials, CompletionStage<ObjectsGetter> models) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, materials, models));
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
    private ObjectsGetter materialsg;

    @Inject
    @Assisted("models")
    private ObjectsGetter modelsg;

    @Inject
    private Application app;

    @Inject
    private AssetManager assets;

    @Inject
    private Camera camera;

    @Inject
    private GameSettingsProvider gs;

    private InitialStateMessage is;

    private MutableList<Geometry> blockNodes;

    private ImmutableList<Geometry> copyBlockNodes;

    private Optional<StartTerrainForGameMapMessage> previousStartTerrainForGameMapMessage = Optional.empty();

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
        this.previousStartTerrainForGameMapMessage = Optional.of(m);
        app.enqueue(() -> {
            is.selectBlockState.setRetriever(m.store);
            is.selectBlockState.setGameMap(m.gm);
            is.cameraState.setTerrainBounds(
                    new BoundingBox(new Vector3f(), m.gm.width, m.gm.height, gs.get().visibleDepthLayers.get()));
            is.cameraState.updateCamera(m.gm);
            is.terrainRollState.setTerrainNode(is.terrainState.getNode());
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
        // log.debug("onUpdateModel {}", m);
        long oldtime = System.currentTimeMillis();
        var root = m.store.getChunk(0);
        MutableIntObjectMap<Multimap<Long, MapBlock>> chunksBlocks = IntObjectMaps.mutable.empty();
        int z = m.gm.getCursorZ();
        int depthLayers = gs.get().visibleDepthLayers.get();
        collectChunks(chunksBlocks, m.store::getChunk, root, z, z, depthLayers, m.gm.width, m.gm.height);
        int bnum = updateModel(m, root, chunksBlocks, m.store::getChunk);
        // updateModel(m, root, chunksBlocks);
        renderMeshs();
        long finishtime = System.currentTimeMillis();
        log.trace("updateModel done in {} showing {} blocks", finishtime - oldtime, bnum);
        return Behaviors.same();
    }

    private int updateModel(UpdateTerrainMessage m, MapChunk root,
            MutableIntObjectMap<Multimap<Long, MapBlock>> chunksBlocks, Function<Integer, MapChunk> retriever) {
        int w = m.gm.getWidth(), h = m.gm.getHeight(), d = m.gm.getDepth();
        var cursor = m.gm.cursor;
        blockNodes.clear();
        int bnum = 0;
        for (var chunks : chunksBlocks.keyValuesView()) {
            var chunk = retriever.apply(chunks.getOne());
            for (var blocks : chunks.getTwo().keyMultiValuePairsView()) {
                int spos = 0;
                int sindex = 0;
                for (MapBlock mb : blocks.getTwo()) {
                    var model = modelsg.get(ModelCacheObject.class, ModelCacheObject.OBJECT_TYPE, mb.getObjectId());
                    var mesh = ((Geometry) (model.model)).getMesh();
                    spos += mesh.getBuffer(Type.Position).getNumElements();
                    sindex += mesh.getBuffer(Type.Index).getNumElements();
                    bnum++;
                }
                final long material = blocks.getOne();
                final var cpos = BufferUtils.createFloatBuffer(3 * spos);
                final var cindex = BufferUtils.createShortBuffer(3 * sindex);
                final var cnormal = BufferUtils.createFloatBuffer(3 * spos);
                final var ctex = BufferUtils.createFloatBuffer(2 * spos);
                final var ccolor = BufferUtils.createFloatBuffer(3 * 4 * spos);
                fillBuffers(blocks, chunk, retriever, w, h, d, cursor, cpos, cindex, cnormal, ctex, ccolor);
                var mesh = new Mesh();
                mesh.setBuffer(Type.Position, 3, cpos);
                mesh.setBuffer(Type.Index, 1, cindex);
                mesh.setBuffer(Type.Normal, 3, cnormal);
                mesh.setBuffer(Type.TexCoord, 2, ctex);
                mesh.setBuffer(Type.Color, 4, ccolor);
                mesh.setMode(Mode.Triangles);
                mesh.updateBound();
                var geo = new Geometry("block-mesh", mesh);
                // geo.setMaterial(new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md"));
                var tex = materialsg.get(TextureCacheObject.class, TextureCacheObject.OBJECT_TYPE, material);
                setupPBRLighting(geo, tex);
                geo.getMaterial().getAdditionalRenderState().setWireframe(false);
                geo.getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
                geo.setShadowMode(ShadowMode.Receive);
                if (tex.transparent) {
                    geo.setQueueBucket(Bucket.Transparent);
                }
                blockNodes.add(geo);
            }
        }
        return bnum;
    }

    private void setupPBRLighting(Geometry geo, TextureCacheObject tex) {
        var m = new Material(assets, "Common/MatDefs/Light/PBRLighting.j3md");
        m.setTexture("BaseColorMap", tex.tex);
        m.setColor("BaseColor", tex.baseColor);
        m.setFloat("Metallic", tex.metallic);
        m.setFloat("Roughness", tex.roughness);
        m.setBoolean("UseVertexColor", true);
        m.getAdditionalRenderState().setBlendMode(tex.transparent ? BlendMode.Alpha : BlendMode.Off);
        geo.setMaterial(m);
    }

    @SuppressWarnings("unused")
    private void setupLighting(Geometry geo, TextureCacheObject tex) {
        geo.setMaterial(new Material(assets, "Common/MatDefs/Light/Lighting.j3md"));
        geo.getMaterial().setTexture("DiffuseMap", tex.tex);
        geo.getMaterial().setColor("Diffuse", tex.baseColor);
        geo.getMaterial().setBoolean("UseVertexColor", true);
    }

    @SneakyThrows
    private void renderMeshs() {
        copyBlockNodes = Lists.immutable.ofAll(blockNodes);
        var task = app.enqueue(this::renderMeshs1);
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

    private boolean renderMeshs1() {
        is.terrainState.clearBlockNodes();
        for (var geo : copyBlockNodes) {
            is.terrainState.addBlockMesh(geo);
        }
        return true;
    }

    private void fillBuffers(Pair<Long, RichIterable<MapBlock>> blocks, MapChunk chunk,
            Function<Integer, MapChunk> retriever, int w, int h, int d, MapCursor cursor, FloatBuffer cpos,
            ShortBuffer cindex, FloatBuffer cnormal, FloatBuffer ctex, FloatBuffer ccolor) {
        short in0, in1, in2, i0, i1, i2;
        float n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z;
        int delta;
        ModelCacheObject model;
        Mesh mesh;
        ShortBuffer bindex;
        FloatBuffer bnormal;
        for (MapBlock mb : blocks.getTwo()) {
            model = modelsg.get(ModelCacheObject.class, ModelCacheObject.OBJECT_TYPE, mb.getObjectId());
            mesh = ((Geometry) (model.model)).getMesh();
            bindex = mesh.getShortBuffer(Type.Index).rewind();
            bnormal = mesh.getFloatBuffer(Type.Normal).rewind();
            delta = cpos.position() / 3;
            for (int i = 0; i < bindex.limit() / 3; i++) {
                in0 = bindex.get();
                in1 = bindex.get();
                in2 = bindex.get();
                i0 = (short) (in0 * 3);
                i1 = (short) (in1 * 3);
                i2 = (short) (in2 * 3);
                n0x = bnormal.get(i0);
                n0y = bnormal.get(i0 + 1);
                n0z = bnormal.get(i0 + 2);
                n1x = bnormal.get(i1);
                n1y = bnormal.get(i1 + 1);
                n1z = bnormal.get(i1 + 2);
                n2x = bnormal.get(i2);
                n2y = bnormal.get(i2 + 1);
                n2z = bnormal.get(i2 + 2);
                n0x = (n0x + n1x + n2x) / 3f;
                n0y = (n0y + n1y + n2y) / 3f;
                n0z = (n0z + n1z + n2z) / 3f;
                if (approximateEquals(n0z, -1.0f)) {
                    continue;
                }
//                if (n0x < 0.0f && isSkipCheckNeighborWest(mb, chunk, retriever)) {
//                    continue;
//                }
//                if (n0x > 0.0f && isSkipCheckNeighborEast(mb, chunk, retriever)) {
//                    continue;
//                }
//                if (n0y < 0.0f && isSkipCheckNeighborNorth(mb, chunk, retriever)) {
//                    continue;
//                }
//                if (n0y > 0.0f && isSkipCheckNeighborSouth(mb, chunk, retriever)) {
//                    continue;
//                }
                cindex.put((short) (in0 + delta));
                cindex.put((short) (in1 + delta));
                cindex.put((short) (in2 + delta));
            }
            copyNormal(mb, mesh, cnormal);
            copyTex(mb, mesh, ctex);
            copyPosColor(mb, mesh, cpos, ccolor, w, h, d, cursor);
        }
        cpos.flip();
        cindex.flip();
        cnormal.flip();
        ctex.flip();
        ccolor.flip();
    }

    private boolean isSkipCheckNeighborNorth(MapBlock mb, MapChunk chunk, Function<Integer, MapChunk> retriever) {
        var nmb = mb.getNeighborNorth(chunk, retriever);
        return isSkipCheckNeighborEdge(nmb);
    }

    private boolean isSkipCheckNeighborSouth(MapBlock mb, MapChunk chunk, Function<Integer, MapChunk> retriever) {
        var nmb = mb.getNeighborSouth(chunk, retriever);
        return isSkipCheckNeighborEdge(nmb);
    }

    private boolean isSkipCheckNeighborEast(MapBlock mb, MapChunk chunk, Function<Integer, MapChunk> retriever) {
        var nmb = mb.getNeighborEast(chunk, retriever);
        return isSkipCheckNeighborEdge(nmb);
    }

    private boolean isSkipCheckNeighborWest(MapBlock mb, MapChunk chunk, Function<Integer, MapChunk> retriever) {
        var nmb = mb.getNeighborWest(chunk, retriever);
        return isSkipCheckNeighborEdge(nmb);
    }

    private boolean isSkipCheckNeighborEdge(MapBlock nmb) {
        if (nmb != null) {
            if (nmb.isFilled()) {
                return true;
            } else if (nmb.isRamp()) {
                return true;
            } else if (nmb.isLiquid()) {
                return true;
            } else if (nmb.isEmpty()) {
                return false;
            }
        }
        return false;
    }

    private void copyNormal(MapBlock mb, Mesh mesh, FloatBuffer cnormal) {
        var normal = mesh.getFloatBuffer(Type.Normal).rewind();
        cnormal.put(normal);
    }

    private void copyTex(MapBlock mb, Mesh mesh, FloatBuffer ctex) {
        var btex = mesh.getFloatBuffer(Type.TexCoord).rewind();
        var tex = materialsg.get(TextureCacheObject.class, TextureCacheObject.OBJECT_TYPE, mb.getMaterialId());
        for (int i = 0; i < btex.limit(); i += 2) {
            float tx = btex.get();
            float ty = btex.get();
            float x = tex.x + tx * tex.w;
            float y = tex.y + ty * tex.h;
            ctex.put(x);
            ctex.put(y);
        }
    }

    /**
     * Transforms the position values based on the block position. Sets the diffuse
     * color of the tile.
     */
    private void copyPosColor(MapBlock mb, Mesh mesh, FloatBuffer cpos, FloatBuffer ccolor, float w, float h, float d,
            MapCursor cursor) {
        var pos = mesh.getFloatBuffer(Type.Position).rewind();
        float x = mb.pos.x, y = mb.pos.y, z = mb.pos.z, vx, vy, vz;
        float c = mb.pos.isEqual(cursor.x, cursor.y, cursor.z) ? 2f : 1f;
        float tx = -w + 2f * x + 1f;
        float ty = h - 2f * y - 1;
        float tz = (cursor.z - z) * 2f;
        for (int i = 0; i < pos.limit(); i += 3) {
            vx = pos.get();
            vy = pos.get();
            vz = pos.get();
            vx += tx;
            vy += ty;
            vz += tz;
            cpos.put(vx);
            cpos.put(vy);
            cpos.put(vz);
            if (mb.pos.z - cursor.z > 0) {
                ccolor.put(1f / ((mb.pos.z - cursor.z) * 2f));
                ccolor.put(1f / ((mb.pos.z - cursor.z) * 2f));
                ccolor.put(1f / ((mb.pos.z - cursor.z) * 2f));
                ccolor.put(1f);
            } else {
                ccolor.put(c);
                ccolor.put(c);
                ccolor.put(c);
                ccolor.put(c);
            }
        }
    }

    private void collectChunks(MutableIntObjectMap<Multimap<Long, MapBlock>> chunksBlocks,
            Function<Integer, MapChunk> retriever, MapChunk root, int z, int currentZ, int visibleDepthLayers, float w,
            float h) {
        if (z < root.getPos().z) {
            return;
        }
        var firstchunk = root.findChunk(0, 0, z, retriever);
        putChunkSortBlocks(chunksBlocks, firstchunk, retriever, currentZ, visibleDepthLayers, w, h);
        int chunkid = 0;
        var nextchunk = firstchunk;
        // nextchunk = firstchunk;
        while (true) {
            chunkid = nextchunk.getNeighborEast();
            if (chunkid == 0) {
                chunkid = firstchunk.getNeighborSouth();
                if (chunkid == 0) {
                    int firstz = firstchunk.getPos().ep.z;
                    if (visibleDepthLayers - currentZ > firstz) {
                        collectChunks(chunksBlocks, retriever, root, firstz, currentZ, visibleDepthLayers, w, h);
                    }
                    break;
                }
                firstchunk = retriever.apply(chunkid);
                nextchunk = firstchunk;
                putChunkSortBlocks(chunksBlocks, nextchunk, retriever, currentZ, visibleDepthLayers, w, h);
            } else {
                nextchunk = retriever.apply(chunkid);
                putChunkSortBlocks(chunksBlocks, nextchunk, retriever, currentZ, visibleDepthLayers, w, h);
            }
        }
    }

    private void putChunkSortBlocks(MutableIntObjectMap<Multimap<Long, MapBlock>> chunks, MapChunk chunk,
            Function<Integer, MapChunk> retriever, int currentZ, int visibleDepthLayers, float w, float h) {
        var contains = getIntersectBb(w, h, chunk);
        if (contains == FrustumIntersect.Outside) {
            return;
        }
        MutableMultimap<Long, MapBlock> blocks = Multimaps.mutable.list.empty();
        for (var mb : chunk.getBlocks()) {
            if (mb.pos.z < currentZ + visibleDepthLayers && isBlockVisible(mb, currentZ, chunk, retriever)) {
                blocks.put(mb.getMaterialId(), mb);
            }
        }
        chunks.put(chunk.cid, blocks);
    }

    private FrustumIntersect getIntersectBb(float w, float h, MapChunk chunk) {
        var bb = createBb(w, h, chunk);
        app.enqueue(() -> {
            is.chunksBoundingBoxState.setChunk(chunk, bb);
        });
        return getIntersect(bb);
    }

    private FrustumIntersect getIntersect(BoundingBox bb) {
        int planeState = camera.getPlaneState();
        camera.setPlaneState(0);
        var contains = camera.contains(bb);
        camera.setPlaneState(planeState);
        return contains;
    }

    private BoundingBox createBb(float w, float h, MapChunk chunk) {
        var bb = new BoundingBox();
        bb.setXExtent(chunk.getCenterExtent().extentx);
        bb.setYExtent(chunk.getCenterExtent().extenty);
        bb.setZExtent(chunk.getCenterExtent().extentz);
        bb.setCenter(chunk.getCenterExtent().centerx, chunk.getCenterExtent().centery, chunk.getCenterExtent().centerz);
        return bb;
    }

    private boolean isBlockVisible(MapBlock mb, int z, MapChunk chunk, Function<Integer, MapChunk> retriever) {
        if (mb.pos.z < z) {
            return false;
        }
        if (mb.isEmpty()) {
            return false;
        }
        if (mb.pos.z > z) {
            if (mb.isFilled()) {
                MapBlock bt;
                if ((bt = mb.getNeighborUp(chunk, retriever)) != null) {
                    if (bt.isFilled()) {
                        return false;
                    } else if (bt.isLiquid()) {
                        return true;
                    } else if (bt.isEmpty()) {
                        return true;
                    }
                }
            }
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
