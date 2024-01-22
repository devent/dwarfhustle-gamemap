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
import static com.anrisoftware.dwarfhustle.model.api.objects.MapBlock.getMapBlock;
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

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.primitive.LongObjectMaps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Multimaps;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppPausedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.ModelCacheObject;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.MapCursor;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage.CacheGetSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage.CacheErrorMessage;
import com.badlogic.ashley.core.Entity;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.Camera.FrustumIntersect;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
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
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedCacheResponse extends Message {
        private final CacheResponseMessage<?> response;
    }

    /**
     * Factory to create {@link TerrainActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface TerrainActorFactory {
        TerrainActor create(ActorContext<Message> context, StashBuffer<Message> stash, TimerScheduler<Message> timer,
                @Assisted("objects") ObjectsGetter objects, @Assisted("materials") ObjectsGetter materials,
                @Assisted("models") ObjectsGetter models);
    }

    /**
     * Creates the {@link TerrainActor}.
     */
    public static Behavior<Message> create(Injector injector, CompletionStage<ObjectsGetter> objects,
            CompletionStage<ObjectsGetter> materials, CompletionStage<ObjectsGetter> models) {
        return Behaviors.withTimers(timer -> Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            context.pipeToSelf(createState(injector, context), (result, cause) -> {
                if (cause == null) {
                    return result;
                } else {
                    return new SetupErrorMessage(cause);
                }
            });
            var o = objects.toCompletableFuture().get(15, SECONDS);
            var ma = materials.toCompletableFuture().get(15, SECONDS);
            var mo = models.toCompletableFuture().get(15, SECONDS);
            return injector.getInstance(TerrainActorFactory.class).create(context, stash, timer, o, ma, mo)
                    .start(injector);
        })));
    }

    /**
     * Creates the {@link TerrainActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
            CompletionStage<ObjectsGetter> objects, CompletionStage<ObjectsGetter> materials,
            CompletionStage<ObjectsGetter> models) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, objects, materials, models));
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
                app.getStateManager().attach(terrainState);
                app.getStateManager().attach(cameraState);
                app.getStateManager().attach(chunksBoundingBoxState);
                app.getStateManager().attach(terrainRollState);
                chunksBoundingBoxState.setEnabled(false);
                return new InitialStateMessage(terrainState, cameraState, chunksBoundingBoxState, terrainRollState);
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
    @Assisted("objects")
    private ObjectsGetter objectsg;

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

    @SuppressWarnings("rawtypes")
    private ActorRef<CacheResponseMessage> cacheResponseAdapter;

    private Optional<Entity> cursorEntity = Optional.empty();

    private MutableList<Geometry> blockNodes;

    private ImmutableList<Geometry> copyBlockNodes;

    private Optional<SetGameMapMessage> previousSetGameMap = Optional.empty();

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
        this.cacheResponseAdapter = context.messageAdapter(CacheResponseMessage.class, WrappedCacheResponse::new);
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
        is.cameraState.setObjectsg(objectsg);
        is.cameraState.setTerrainNode(is.terrainState.getNode());
        is.terrainRollState.setTerrainNode(is.terrainState.getNode());
        is.terrainRollState.setBoundingNode(is.chunksBoundingBoxState.getNode());
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     * Reacts to the {@link SetGameMapMessage} message.
     */
    private Behavior<Message> onSetGameMap(SetGameMapMessage m) {
        log.debug("onSetGameMap {}", m);
        blockNodes = Lists.mutable.empty();
        app.enqueue(() -> {
            is.cameraState.setTerrainBounds(
                    new BoundingBox(new Vector3f(), m.gm.width, m.gm.height, gs.get().visibleDepthLayers.get()));
            is.cameraState.updateCamera(m.gm);
        });
        previousSetGameMap = Optional.of(m);
        timer.startTimerAtFixedRate(UPDATE_TERRAIN_MESSAGE_TIMER_KEY, new UpdateTerrainMessage(m.gm),
                gs.get().terrainUpdateDuration.get());
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link UpdateTerrainMessage} message.
     */
    private Behavior<Message> onUpdateModel(UpdateTerrainMessage m) {
        // log.debug("onUpdateModel {}", m);
        // long oldtime = System.currentTimeMillis();
        var root = objectsg.get(MapChunk.class, MapChunk.OBJECT_TYPE, m.gm.root);
        MutableLongObjectMap<Multimap<Long, MapBlock>> chunksBlocks = LongObjectMaps.mutable.empty();
        int z = m.gm.getCursorZ();
        collectChunks(chunksBlocks, root, z, z, m.gm.chunkSize, gs.get().visibleDepthLayers.get());
        // var bnum = updateModel(m, root, chunksBlocks);
        updateModel(m, root, chunksBlocks);
        renderMeshs();
        // long finishtime = System.currentTimeMillis();
        // log.trace("updateModel done in {} showing {} blocks", finishtime - oldtime,
        // bnum);
        return Behaviors.same();
    }

    private int updateModel(UpdateTerrainMessage m, GameObject o,
            MutableLongObjectMap<Multimap<Long, MapBlock>> chunksBlocks) {
        int w = m.gm.getWidth(), h = m.gm.getHeight(), d = m.gm.getDepth();
        var cursor = m.gm.cursor;
        blockNodes.clear();
        int bnum = 0;
        for (var chunks : chunksBlocks.keyValuesView()) {
            for (var blocks : chunks.getTwo().keyMultiValuePairsView()) {
                int spos = 0;
                int sindex = 0;
                for (MapBlock mb : blocks.getTwo()) {
                    var model = modelsg.get(ModelCacheObject.class, ModelCacheObject.OBJECT_TYPE, mb.getObject());
                    var mesh = ((Geometry) ((Node) model.model).getChild(0)).getMesh();
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
                fillBuffers(blocks, w, h, d, cursor, cpos, cindex, cnormal, ctex, ccolor);
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
                blockNodes.add(geo);
            }
        }
        return bnum;
    }

    private void setupPBRLighting(Geometry geo, TextureCacheObject tex) {
        geo.setMaterial(new Material(assets, "Common/MatDefs/Light/PBRLighting.j3md"));
        geo.getMaterial().setTexture("BaseColorMap", tex.tex);
        geo.getMaterial().setColor("BaseColor", tex.baseColor);
        geo.getMaterial().setFloat("Metallic", tex.metallic);
        geo.getMaterial().setFloat("Roughness", tex.roughness);
        geo.getMaterial().setBoolean("UseVertexColor", true);
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

    private void fillBuffers(Pair<Long, RichIterable<MapBlock>> blocks, int w, int h, int d, MapCursor cursor,
            FloatBuffer cpos, ShortBuffer cindex, FloatBuffer cnormal, FloatBuffer ctex, FloatBuffer ccolor) {
        short in0, in1, in2, i0, i1, i2;
        float n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z;
        int delta;
        ModelCacheObject model;
        Mesh mesh;
        ShortBuffer bindex;
        FloatBuffer bnormal;
        for (MapBlock mb : blocks.getTwo()) {
            model = modelsg.get(ModelCacheObject.class, ModelCacheObject.OBJECT_TYPE, mb.getObject());
            mesh = ((Geometry) ((Node) model.model).getChild(0)).getMesh();
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
                if (n0z < 0.0f) {
                    continue;
                }
                if (n0x < 0.0f && isSkipCheckNeighborWest(mb)) {
                    continue;
                }
                if (n0x > 0.0f && isSkipCheckNeighborEast(mb)) {
                    continue;
                }
                if (n0y < 0.0f && isSkipCheckNeighborSouth(mb)) {
                    continue;
                }
                if (n0y > 0.0f && isSkipCheckNeighborNorth(mb)) {
                    continue;
                }
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

    private boolean isSkipCheckNeighborNorth(MapBlock mb) {
        return mb.getNeighborNorth() != 0;
    }

    private boolean isSkipCheckNeighborSouth(MapBlock mb) {
        return mb.getNeighborSouth() != 0;
    }

    private boolean isSkipCheckNeighborEast(MapBlock mb) {
        return mb.getNeighborEast() != 0;
    }

    private boolean isSkipCheckNeighborWest(MapBlock mb) {
        return mb.getNeighborWest() != 0;
    }

    private void copyNormal(MapBlock mb, Mesh mesh, FloatBuffer cnormal) {
        var normal = mesh.getFloatBuffer(Type.Normal).rewind();
        cnormal.put(normal);
    }

    private void copyTex(MapBlock mb, Mesh mesh, FloatBuffer ctex) {
        var btex = mesh.getFloatBuffer(Type.TexCoord).rewind();
        var tex = materialsg.get(TextureCacheObject.class, TextureCacheObject.OBJECT_TYPE, mb.getMaterial());
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
        // System.out.println(mb); // TODO
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
            // System.out.printf("%f/%f/%f\n", vx, vy, vz); // TODO
            vx += tx;
            vy += ty;
            vz += tz;
            // System.out.printf("%f/%f/%f\n", vx, vy, vz); // TODO
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
        // System.out.println(); // TODO
    }

    private void collectChunks(MutableLongObjectMap<Multimap<Long, MapBlock>> chunksBlocks, MapChunk root, int z,
            int currentZ, int chuckSize, int visibleDepthLayers) {
        if (z == root.pos.ep.z) {
            return;
        }
        var firstchunk = root.findMapChunk(0, 0, z, id -> objectsg.get(MapChunk.class, MapChunk.OBJECT_TYPE, id));
        putChunkSortBlocks(chunksBlocks, firstchunk, currentZ, visibleDepthLayers);
        long chunkid = 0;
        var nextchunk = firstchunk;
        // nextchunk = firstchunk;
        while (true) {
            chunkid = nextchunk.getNeighborEast();
            if (chunkid == 0) {
                chunkid = firstchunk.getNeighborSouth();
                if (chunkid == 0) {
                    if (firstchunk.pos.ep.z < currentZ + visibleDepthLayers) {
                        collectChunks(chunksBlocks, root, firstchunk.pos.ep.z, currentZ, chuckSize, visibleDepthLayers);
                    }
                    break;
                }
                firstchunk = objectsg.get(MapChunk.class, MapChunk.OBJECT_TYPE, chunkid);
                nextchunk = firstchunk;
                putChunkSortBlocks(chunksBlocks, nextchunk, currentZ, visibleDepthLayers);
            } else {
                nextchunk = objectsg.get(MapChunk.class, MapChunk.OBJECT_TYPE, chunkid);
                putChunkSortBlocks(chunksBlocks, nextchunk, currentZ, visibleDepthLayers);
            }
        }
    }

    private void putChunkSortBlocks(MutableLongObjectMap<Multimap<Long, MapBlock>> chunks, MapChunk chunk, int currentZ,
            int visibleDepthLayers) {
        var contains = getIntersectBb(chunk);
        if (contains == FrustumIntersect.Outside) {
            return;
        }
        MutableMultimap<Long, MapBlock> blocks = Multimaps.mutable.list.empty();
        for (var pair : chunk.getBlocks().keyValuesView()) {
            var mbid = pair.getTwo();
            var mb = getMapBlock(objectsg, mbid);
            if (mb.pos.z < currentZ + visibleDepthLayers && isBlockVisible(mb, currentZ)) {
                blocks.put(mb.getMaterial(), mb);
            }
        }
        chunks.put(chunk.getId(), blocks);
    }

    private FrustumIntersect getIntersectBb(MapChunk chunk) {
        var bb = createBb(chunk);
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

    private BoundingBox createBb(MapChunk chunk) {
        var bb = new BoundingBox();
        bb.setXExtent(chunk.centerExtent.extentx);
        bb.setYExtent(chunk.centerExtent.extenty);
        bb.setZExtent(chunk.centerExtent.extentz);
        bb.setCenter(chunk.centerExtent.centerx, chunk.centerExtent.centery, chunk.centerExtent.centerz);
        return bb;
    }

    private boolean isBlockVisible(MapBlock mb, int z) {
        if (mb.pos.z < z) {
            return false;
        }
        if (mb.isMined()) {
            return false;
        }
        if (mb.pos.z > z) {
            if (mb.isSolid()) {
                long nt;
                if ((nt = mb.getNeighborTop()) != 0) {
                    var bt = objectsg.get(MapBlock.class, MapBlock.OBJECT_TYPE, nt);
                    if (bt.isSolid()) {
                        return false;
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
    private Behavior<Message> onWrappedCacheResponse(WrappedCacheResponse m) {
        if (m.response instanceof CacheGetSuccessMessage<?> rm) {
        } else if (m.response instanceof CacheErrorMessage<?> rm) {
            log.error("Cache error {}", m);
            return Behaviors.stopped();
        }
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
            timer.cancel(UPDATE_TERRAIN_MESSAGE_TIMER_KEY);
        } else {
            previousSetGameMap.ifPresent(this::onSetGameMap);
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
                .onMessage(SetGameMapMessage.class, this::onSetGameMap)//
                .onMessage(WrappedCacheResponse.class, this::onWrappedCacheResponse)//
                .onMessage(UpdateTerrainMessage.class, this::onUpdateModel)//
                .onMessage(AppPausedMessage.class, this::onAppPaused)//
        ;
    }
}
