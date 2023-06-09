package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.map.primitive.LongObjectMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.ModelCacheObject;
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

    private InitialStateMessage is;

    @SuppressWarnings("rawtypes")
    private ActorRef<CacheResponseMessage> cacheResponseAdapter;

    private Optional<Entity> cursorEntity = Optional.empty();

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
        combineModels(chunksBlocks);
        // log.info("chunks {}", chunks);
    }

    private void combineModels(LongObjectMap<Multimap<Long, MapBlock>> chunksBlocks) {
        for (Multimap<Long, MapBlock> chunks : chunksBlocks) {
            for (Pair<Long, RichIterable<MapBlock>> blocks : chunks.keyMultiValuePairsView()) {
                for (MapBlock mb : blocks.getTwo()) {
                    var model = modelsg.get(ModelCacheObject.class, ModelCacheObject.OBJECT_TYPE, mb.getObject());
                }

            }

        }
    }

    private void collectChunks(MutableLongObjectMap<Multimap<Long, MapBlock>> chunksBlocks, GameMap gm, MapChunk root) {
        int x = 0;
        int y = 0;
        int z = gm.getCursorZ();
        var firstchunk = root.findMapChunk(x, y, z, id -> objectsg.get(MapChunk.class, MapChunk.OBJECT_TYPE, id));
        putChunkSortBlocks(chunksBlocks, firstchunk);
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
                putChunkSortBlocks(chunksBlocks, nextchunk);
            } else {
                nextchunk = objectsg.get(MapChunk.class, MapChunk.OBJECT_TYPE, chunkid);
                putChunkSortBlocks(chunksBlocks, nextchunk);
            }
        }
        log.trace("collectChunks {}", chunksBlocks.size());
    }

    private void putChunkSortBlocks(MutableLongObjectMap<Multimap<Long, MapBlock>> chunksBlocks, MapChunk chunk) {
        MutableMultimap<Long, MapBlock> blocks = Multimaps.mutable.list.empty();
        for (var pair : chunk.getBlocks().keyValuesView()) {
            var mb = pair.getTwo();
            if (isBlockVisible(mb)) {
                blocks.put(mb.getMaterial(), mb);
            }
        }
        chunksBlocks.put(chunk.getId(), blocks);
    }

    private boolean isBlockVisible(MapBlock mb) {
        if (mb.isMined()) {
            return false;
        }
        if (mb.isSolid()) {
            long nt;
            if ((nt = mb.getNeighborTop()) != 0) {
                var bt = objectsg.get(MapBlock.class, MapBlock.OBJECT_TYPE, nt);
                if (bt.isSolid()) {
                    return false;
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
