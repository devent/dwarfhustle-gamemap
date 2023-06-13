package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.eclipse.collections.api.RichIterable;
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
import com.jme3.material.Material;
import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Format;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.VertexBuffer.Usage;
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

    private MutableLongObjectMap<VertexBuffer> combinedPos;

    private MutableLongObjectMap<VertexBuffer> combinedIndex;

    private MutableLongObjectMap<VertexBuffer> combinedNormal;

    private MutableLongObjectMap<VertexBuffer> combinedTex;

    private MutableLongObjectMap<Geometry> blockNodes;

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
        combinedPos = LongObjectMaps.mutable.empty();
        combinedIndex = LongObjectMaps.mutable.empty();
        combinedNormal = LongObjectMaps.mutable.empty();
        combinedTex = LongObjectMaps.mutable.empty();
        blockNodes = LongObjectMaps.mutable.empty();
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
        return Behaviors.same();
    }

    private void updateModel(UpdateModelMessage m, GameObject o) {
        MutableLongObjectMap<Multimap<Long, MapBlock>> chunksBlocks = LongObjectMaps.mutable.empty();
        collectChunks(chunksBlocks, m.gm, (MapChunk) o);
        var transform = new Transform();
        for (var chunks : chunksBlocks.keyValuesView()) {
            for (var blocks : chunks.getTwo().keyMultiValuePairsView()) {
                long material = blocks.getOne();
                createLazy(combinedPos, material, Type.Position, 3, Format.Float,
                        () -> BufferUtils.createFloatBuffer(3 * 10000));
                createLazy(combinedIndex, material, Type.Index, 3, Format.UnsignedShort,
                        () -> BufferUtils.createShortBuffer(3 * 10000));
                createLazy(combinedNormal, material, Type.Normal, 3, Format.Float,
                        () -> BufferUtils.createFloatBuffer(3 * 10000));
                createLazy(combinedTex, material, Type.TexCoord, 3, Format.Float,
                        () -> BufferUtils.createFloatBuffer(2 * 10000));
                fillBuffers(transform, blocks);
                var mesh = new Mesh();
                mesh.setBuffer(combinedPos.get(material));
                mesh.setBuffer(combinedIndex.get(material));
                mesh.setBuffer(combinedNormal.get(material));
                mesh.setBuffer(combinedTex.get(material));
                mesh.setMode(Mode.Triangles);
                mesh.updateBound();
                mesh.updateCounts();
                var geo = new Geometry("block-mesh", mesh);
                geo.setMaterial(new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md"));
                var tex = materialsg.get(TextureCacheObject.class, TextureCacheObject.OBJECT_TYPE, material);
                geo.getMaterial().setTexture("ColorMap", tex.tex);
                geo.getMaterial().setColor("Color", tex.baseColor);
                geo.getMaterial().getAdditionalRenderState().setWireframe(true);
                blockNodes.put(material, geo);
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
        for (var geo : blockNodes.values()) {
            is.terrainState.addBlockMesh(geo);
        }
    }

    private void createLazy(MutableLongObjectMap<VertexBuffer> map, long material, Type type, int components,
            Format format, Supplier<Buffer> buffer) {
        if (map.containsKey(material)) {
            var v = map.get(material);
            v.getData().clear();
        } else {
            var v = new VertexBuffer(type);
            var b = buffer.get();
            v.setupData(Usage.Static, components, format, b);
            map.put(material, v);
        }
    }

    private void fillBuffers(Transform transform, Pair<Long, RichIterable<MapBlock>> blocks) {
        var vpos = combinedPos.get(blocks.getOne());
        var vindex = combinedIndex.get(blocks.getOne());
        var vnormal = combinedNormal.get(blocks.getOne());
        var vtex = combinedTex.get(blocks.getOne());
        for (MapBlock mb : blocks.getTwo()) {
            var model = modelsg.get(ModelCacheObject.class, ModelCacheObject.OBJECT_TYPE, mb.getObject());
            var mesh = ((Geometry) ((Node) model.model).getChild(0)).getMesh();
            var pos = (FloatBuffer) mesh.getBuffer(Type.Position).clone().getData();
            transformPosCopy(mb, pos, (FloatBuffer) vpos.getData(), transform);
            var index = mesh.getShortBuffer(Type.Index);
            index.position(0);
            ((ShortBuffer) vindex.getData()).put(index);
            var normal = mesh.getFloatBuffer(Type.Normal);
            normal.position(0);
            ((FloatBuffer) vnormal.getData()).put(normal);
            var tex = mesh.getFloatBuffer(Type.TexCoord);
            tex.position(0);
            ((FloatBuffer) vtex.getData()).put(tex);
        }
        vpos.getData().limit(vpos.getData().position());
        vindex.getData().limit(vindex.getData().position());
        vnormal.getData().limit(vnormal.getData().position());
        vtex.getData().limit(vtex.getData().position());
    }

    private void transformPosCopy(MapBlock mb, FloatBuffer pos, FloatBuffer cpos, Transform t) {
        pos.position(0);
        var temp = TempVars.get();
        t.setTranslation(mb.getPos().x, mb.getPos().y, mb.getPos().z);
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

    private void putChunkSortBlocks(MutableLongObjectMap<Multimap<Long, MapBlock>> chunksBlocks, MapChunk chunk,
            int z) {
        MutableMultimap<Long, MapBlock> blocks = Multimaps.mutable.list.empty();
        for (var pair : chunk.getBlocks().keyValuesView()) {
            var mb = pair.getTwo();
            if (isBlockVisible(mb, z)) {
                blocks.put(mb.getMaterial(), mb);
            }
        }
        chunksBlocks.put(chunk.getId(), blocks);
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
