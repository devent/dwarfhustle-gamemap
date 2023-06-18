package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.ModelCacheObject;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage.CacheGetSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage.CacheErrorMessage;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import com.jme3.util.TempVars;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.javadsl.TimerScheduler;
import akka.actor.typed.receptionist.ServiceKey;
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

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, TerrainActor.class.getSimpleName());

    public static final String NAME = TerrainActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {
        public final TerrainState terrainState;
        public final TerrainCameraState cameraState;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class SetupErrorMessage extends Message {
        public final Throwable cause;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class UpdateModelMessage extends Message {
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
        var terrainState = injector.getInstance(TerrainState.class);
        var cameraState = injector.getInstance(TerrainCameraState.class);
        try {
            var f = app.enqueue(() -> {
                app.getStateManager().attach(terrainState);
                app.getStateManager().attach(cameraState);
                return new InitialStateMessage(terrainState, cameraState);
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
    private Engine engine;

    @Inject
    private Application app;

    @Inject
    private AssetManager assets;

    private InitialStateMessage is;

    @SuppressWarnings("rawtypes")
    private ActorRef<CacheResponseMessage> cacheResponseAdapter;

    private Optional<Entity> cursorEntity = Optional.empty();

    private MutableList<Geometry> blockNodes;

    private BoundingBox terrainBounds;

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
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     * Reacts to the {@link SetGameMapMessage} message.
     */
    private Behavior<Message> onSetGameMap(SetGameMapMessage m) {
        log.debug("onSetGameMap {}", m);
        blockNodes = Lists.mutable.empty();
        terrainBounds = new BoundingBox();
        app.enqueue(() -> is.cameraState.updateCamera(m.gm));
        timer.startTimerAtFixedRate(new UpdateModelMessage(m.gm), Duration.ofMillis(3000));
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link UpdateModelMessage} message.
     */
    private Behavior<Message> onUpdateModel(UpdateModelMessage m) {
        log.debug("onUpdateModel {}", m);
        var root = objectsg.get(MapChunk.class, MapChunk.OBJECT_TYPE, m.gm.getRootid());
        updateModel(m, root);
        is.cameraState.setTerrainModel(terrainBounds);
        return Behaviors.same();
    }

    private void updateModel(UpdateModelMessage m, GameObject o) {
        MutableLongObjectMap<Multimap<Long, MapBlock>> chunksBlocks = LongObjectMaps.mutable.empty();
        collectChunks(chunksBlocks, m.gm, (MapChunk) o);
        int w = m.gm.getWidth(), h = m.gm.getHeight(), d = m.gm.getDepth();
        var transform = new Transform();
        blockNodes.clear();
        for (var chunks : chunksBlocks.keyValuesView()) {
            for (var blocks : chunks.getTwo().keyMultiValuePairsView()) {
                int spos = 0;
                int sindex = 0;
                for (MapBlock mb : blocks.getTwo()) {
                    var model = modelsg.get(ModelCacheObject.class, ModelCacheObject.OBJECT_TYPE, mb.getObject());
                    var mesh = ((Geometry) ((Node) model.model).getChild(0)).getMesh();
                    spos += mesh.getBuffer(Type.Position).getNumElements();
                    sindex += mesh.getBuffer(Type.Index).getNumElements();
                }
                final long material = blocks.getOne();
                final var cpos = BufferUtils.createFloatBuffer(3 * spos);
                final var cindex = BufferUtils.createShortBuffer(3 * sindex);
                final var cnormal = BufferUtils.createFloatBuffer(3 * spos);
                final var ctex = BufferUtils.createFloatBuffer(2 * spos);
                fillBuffers(transform, blocks, w, h, d, cpos, cindex, cnormal, ctex);
                var mesh = new Mesh();
                mesh.setBuffer(Type.Position, 3, cpos);
                mesh.setBuffer(Type.Index, 1, cindex);
                mesh.setBuffer(Type.Normal, 3, cnormal);
                mesh.setBuffer(Type.TexCoord, 2, ctex);
                mesh.setMode(Mode.Triangles);
                mesh.updateBound();
                var geo = new Geometry("block-mesh", mesh);
                geo.setMaterial(new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md"));
                var tex = materialsg.get(TextureCacheObject.class, TextureCacheObject.OBJECT_TYPE, material);
                geo.getMaterial().setTexture("ColorMap", tex.tex);
                geo.getMaterial().setColor("Color", tex.baseColor);
                geo.getMaterial().getAdditionalRenderState().setWireframe(true);
                geo.getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
                blockNodes.add(geo);
                terrainBounds.mergeLocal(mesh.getBound());
            }
        }
        renderMeshs();
        log.info("updateModel done");
    }

    private void renderMeshs() {
        app.enqueue(this::renderMeshs1);
    }

    private void renderMeshs1() {
        is.terrainState.clearBlockNodes();
        for (var geo : blockNodes) {
            is.terrainState.addBlockMesh(geo);
        }
    }

    private void fillBuffers(Transform transform, Pair<Long, RichIterable<MapBlock>> blocks, int w, int h, int d,
            FloatBuffer cpos, ShortBuffer cindex, FloatBuffer cnormal, FloatBuffer ctex) {
        for (MapBlock mb : blocks.getTwo()) {
            System.out.println(mb.getMaterial()); // TODO
            var temp = TempVars.get();
            try {
                var model = modelsg.get(ModelCacheObject.class, ModelCacheObject.OBJECT_TYPE, mb.getObject());
                var mesh = ((Geometry) ((Node) model.model).getChild(0)).getMesh();
                var bindex = mesh.getShortBuffer(Type.Index).rewind();
                var bnormal = mesh.getFloatBuffer(Type.Normal).rewind();
                var bpos = mesh.getFloatBuffer(Type.Position).rewind();
                var btex = mesh.getFloatBuffer(Type.TexCoord).rewind();
                int delta = cindex.position();
                for (int i = 0; i < bindex.limit() / 3; i++) {
                    short i0 = (short) (bindex.get() * 3);
                    short i1 = (short) (bindex.get() * 3);
                    short i2 = (short) (bindex.get() * 3);
                    var n0 = temp.vect1.set(bnormal.get(i0), bnormal.get(i0 + 1), bnormal.get(i0 + 2));
                    var n1 = temp.vect2.set(bnormal.get(i1), bnormal.get(i1 + 1), bnormal.get(i1 + 2));
                    var n2 = temp.vect3.set(bnormal.get(i2), bnormal.get(i2 + 1), bnormal.get(i2 + 2));
                    n0.addLocal(n1.addLocal(n2)).divideLocal(3f);
                    if (n0.z < 0.0f) {
                        continue;
                    }
                    cindex.put((short) (i0 + delta));
                    cindex.put((short) (i1 + delta));
                    cindex.put((short) (i2 + delta));
                    System.out.printf("%d normal: %s %s %s\n", i, n0, n1, n2); // TODO
                }
                copyNormal(mb, mesh, cnormal);
                copyTex(mb, mesh, ctex);
                copyPos(mb, mesh, cpos, transform, w, h, d);
            } finally {
                temp.release();
            }
        }
        cpos.flip();
        cindex.flip();
        cnormal.flip();
        ctex.flip();
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
     * Transforms the indices based on the current position of the vertices.
     */
    private void copyIndex(MapBlock mb, Mesh mesh, ShortBuffer cindex, int d) {
        var index = mesh.getShortBuffer(Type.Index).rewind();
        for (int i = 0; i < index.limit(); i += 3) {
            short x = (short) (index.get() + d);
            short y = (short) (index.get() + d);
            short z = (short) (index.get() + d);
            cindex.put(x);
            cindex.put(y);
            cindex.put(z);
        }
    }

    /**
     * Transforms the position values based on the block position.
     */
    private void copyPos(MapBlock mb, Mesh mesh, FloatBuffer cpos, Transform t, int w, int h, int d) {
        var pos = mesh.getFloatBuffer(Type.Position).rewind();
        var temp = TempVars.get();
        t.setTranslation(-w + 1f + 2.0f * mb.getPos().x, h - 1f - 2.0f * mb.getPos().y, 0);
        try {
            var inv = temp.vect1;
            var outv = temp.vect2;
            for (int i = 0; i < pos.limit(); i += 3) {
                inv.x = pos.get();
                inv.y = pos.get();
                inv.z = pos.get();
                t.transformVector(inv, outv);
                cpos.put(outv.x);
                cpos.put(outv.y);
                cpos.put(outv.z);
            }
        } finally {
            temp.release();
        }
    }

    private void collectChunks(MutableLongObjectMap<Multimap<Long, MapBlock>> chunksBlocks, GameMap gm, MapChunk root) {
        int x = 0;
        int y = 0;
        int z = gm.getCursorZ();
        var firstchunk = root.findMapChunk(x, y, z, id -> objectsg.get(MapChunk.class, MapChunk.OBJECT_TYPE, id));
        putChunkSortBlocks(chunksBlocks, firstchunk, z);
        long chunkid;
        var nextchunk = firstchunk;
        // nextchunk = firstchunk;
        while (true) {
            chunkid = nextchunk.getNeighborEast();
            if (chunkid == 0) {
                long nsid = firstchunk.getNeighborSouth();
                if (nsid == 0) {
                    break;
                }
                firstchunk = objectsg.get(MapChunk.class, MapChunk.OBJECT_TYPE, nsid);
                nextchunk = firstchunk;
                putChunkSortBlocks(chunksBlocks, nextchunk, z);
            } else {
                nextchunk = objectsg.get(MapChunk.class, MapChunk.OBJECT_TYPE, chunkid);
                putChunkSortBlocks(chunksBlocks, nextchunk, z);
            }
        }
        log.trace("collectChunks {}", chunksBlocks.size());
    }

    private void putChunkSortBlocks(MutableLongObjectMap<Multimap<Long, MapBlock>> chunks, MapChunk chunk, int z) {
        MutableMultimap<Long, MapBlock> blocks = Multimaps.mutable.list.empty();
        for (var pair : chunk.getBlocks().keyValuesView()) {
            var mb = pair.getTwo();
            if (isBlockVisible(mb, z)) {
                blocks.put(mb.getMaterial(), mb);
            }
        }
        chunks.put(chunk.getId(), blocks);
    }

    private boolean isBlockVisible(MapBlock mb, int z) {
        if (mb.getPos().z < z) {
            return false;
        }
        if (mb.isMined()) {
            return false;
        }
        if (mb.getPos().z > z) {
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
                .onMessage(UpdateModelMessage.class, this::onUpdateModel)//
        ;
    }
}
