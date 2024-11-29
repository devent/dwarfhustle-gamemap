package com.anrisoftware.dwarfhustle.gamemap.jme.objectsmodel;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static com.anrisoftware.dwarfhustle.model.db.cache.MapObject.getMapObject;
import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage.askKnowledgeObjects;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import org.eclipse.collections.api.factory.primitive.IntLists;
import org.eclipse.collections.api.list.primitive.IntList;
import org.lable.oss.uniqueid.IDGenerator;

import com.anrisoftware.dwarfhustle.gamemap.jme.app.MaterialAssetsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.ModelsAssetsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.objectsrender.ObjectsRenderActor;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppPausedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.DeleteObjectMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.DeleteObjectMessage.DeleteObjectSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.InsertObjectMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.InsertObjectMessage.InsertObjectSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.StartTerrainForGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.objects.ObjectsActor;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.IdsObjectsProvider.IdsObjects;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeTree;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeTreeSapling;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeVegetation;
import com.anrisoftware.dwarfhustle.model.api.vegetations.Tree;
import com.anrisoftware.dwarfhustle.model.api.vegetations.TreeSapling;
import com.anrisoftware.dwarfhustle.model.api.vegetations.Vegetation;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.evrete.AskKnowledge;
import com.anrisoftware.dwarfhustle.model.knowledge.evrete.VegetationKnowledge;
import com.anrisoftware.dwarfhustle.model.knowledge.evrete.VegetationLoadKnowledges;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

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
 * Updates the objects on the map.
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class ObjectsModelActor {

    private static final String UPDATE_OBJECTS_MESSAGE_TIMER_KEY = "ObjectsModelActor-UpdateObjectsMessage-Timer";

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            ObjectsModelActor.class.getSimpleName());

    public static final String NAME = ObjectsModelActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {
        public final ObjectsGetter materials;
        public final ObjectsGetter models;
        public final ObjectsGetter cg;
        public final ObjectsSetter cs;
        public final ObjectsGetter og;
        public final ObjectsSetter os;
        public final KnowledgeGetter kg;
        public final ObjectsGetter mg;
        public final ObjectsSetter ms;
        public final ActorRef<Message> oa;
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
    private static class WrappedInsertObjectResponse extends Message {
        public final InsertObjectMessage.InsertObjectSuccessMessage res;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedDeleteObjectResponse extends Message {
        public final DeleteObjectMessage.DeleteObjectSuccessMessage res;
    }

    /**
     * Factory to create {@link ObjectsModelActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface ObjectsModelActorFactory {
        ObjectsModelActor create(ActorContext<Message> context, StashBuffer<Message> stash,
                TimerScheduler<Message> timer);
    }

    /**
     * Creates the {@link ObjectsRenderActor}.
     */
    private static Behavior<Message> create(Injector injector, ActorSystemProvider actor) {
        return Behaviors.withStash(100, stash -> Behaviors.withTimers(timer -> Behaviors.setup(context -> {
            context.pipeToSelf(CompletableFuture.supplyAsync(() -> returnInitialState(injector, actor)),
                    (result, cause) -> {
                        if (cause == null) {
                            return result;
                        } else {
                            return new SetupErrorMessage(cause);
                        }
                    });
            return injector.getInstance(ObjectsModelActorFactory.class).create(context, stash, timer).start(injector);
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
        try {
            var ma = actor.getObjectGetterAsyncNow(MaterialAssetsCacheActor.ID);
            var mo = actor.getObjectGetterAsyncNow(ModelsAssetsCacheActor.ID);
            var kg = actor.getKnowledgeGetterAsyncNow(PowerLoomKnowledgeActor.ID);
            var og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            var os = actor.getObjectSetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            var cg = actor.getObjectGetterAsyncNow(MapChunksJcsCacheActor.ID);
            var cs = actor.getObjectSetterAsyncNow(MapChunksJcsCacheActor.ID);
            var mg = actor.getObjectGetterAsyncNow(MapObjectsJcsCacheActor.ID);
            var ms = actor.getObjectSetterAsyncNow(MapObjectsJcsCacheActor.ID);
            var oa = actor.getActorAsyncNow(ObjectsActor.ID);
            return new InitialStateMessage(ma, mo, cg, cs, og, os, kg, mg, ms, oa);
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
    private GameSettingsProvider gs;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    @IdsObjects
    private IDGenerator ids;

    private Optional<StartTerrainForGameMapMessage> previousStartTerrainForGameMapMessage = Optional.empty();

    private ForkJoinPool pool;

    private AskKnowledge askKnowledge;

    private boolean pauseOnFocusLost;

    private InitialStateMessage is;

    private ActorRef<DeleteObjectSuccessMessage> objectsDeleteAdapter;

    private ActorRef<InsertObjectSuccessMessage> objectsInsertAdapter;

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
        this.pool = new ForkJoinPool(4);
        this.askKnowledge = (timeout, type) -> askKnowledgeObjects(actor.getActorSystem(), timeout, type);
        this.objectsInsertAdapter = context.messageAdapter(InsertObjectMessage.InsertObjectSuccessMessage.class,
                WrappedInsertObjectResponse::new);
        this.objectsDeleteAdapter = context.messageAdapter(DeleteObjectMessage.DeleteObjectSuccessMessage.class,
                WrappedDeleteObjectResponse::new);
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
        this.previousStartTerrainForGameMapMessage = Optional.of(m);
        Duration interval = gs.get().gameTickDuration.get();
        interval = Duration.ofMillis(1000);
        timer.startTimerAtFixedRate(UPDATE_OBJECTS_MESSAGE_TIMER_KEY, new UpdateTerrainMessage(m.gm), interval);
        this.pauseOnFocusLost = false;
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link UpdateTerrainMessage} message.
     */
    private Behavior<Message> onUpdateModel(UpdateTerrainMessage m) {
        long oldtime = System.currentTimeMillis();
        try {
            onUpdateModel0(m);
        } catch (Exception e) {
            log.error("", e);
        }
        // log.trace("updateModel done in {}", System.currentTimeMillis() - oldtime);
        return Behaviors.same();
    }

    @RequiredArgsConstructor
    private class ObjectsUpdateAction extends RecursiveAction {

        private static final long serialVersionUID = 1L;

        private final int maxSize = 8;

        private final GameMap gm;

        private final IntList indices;

        private final int start;

        private final int end;

        @Override
        protected void compute() {
            if (end - start > maxSize) {
                var tasks = ForkJoinTask.invokeAll(createSubtasks());
                for (var action : tasks) {
                    action.join();
                }
            } else {
                processing();
            }
        }

        private Collection<RecursiveAction> createSubtasks() {
            List<RecursiveAction> dividedTasks = new ArrayList<>();
            final int exx = start / 2 + end / 2;
            dividedTasks.add(create(start, exx));
            dividedTasks.add(create(exx, end));
            return dividedTasks;
        }

        private RecursiveAction create(int start, int end) {
            return new ObjectsUpdateAction(gm, indices, start, end);
        }

        protected void processing() {
            // System.out.printf("processing %d/%d/%d-%d/%d/%d\n", sx, sy, sz, ex, ey, ez);
            // // TODO
            for (int i = start; i < end; i++) {
                var mo = getMapObject(is.mg, indices.get(i));
                mo.getOids().forEachKeyValue(this::updateObject);
            }
        }

        private void updateObject(long id, int type) {
            final GameMapObject object = is.og.get(GameMapObject.OBJECT_TYPE, id);
            final var oid = object.getOid();
            final var k = is.kg.get(oid);
            for (var kv : k.objects) {
                if (kv.kid == object.kid) {
                    if (object.getObjectType() == TreeSapling.OBJECT_TYPE) {
                        runTreeSapling(kv, (Vegetation) object);
                    } else if (object.getObjectType() == Tree.OBJECT_TYPE) {
                        runTree(kv, (Vegetation) object);
                    }
                }
            }
        }

        @SneakyThrows
        private void runTreeSapling(KnowledgeObject kv, Vegetation v) {
            v.setGrowth(v.getGrowth() + 0.001f);
            is.os.set(v.getObjectType(), v);
            if (v.getGrowth() > 1.0f) {
                final var kvv = (KnowledgeTreeSapling) kv.getAsType();
                final String growsInto = kvv.getGrowsInto();
                final var kgrowv = is.kg.get(KnowledgeTree.TYPE.hashCode()).objects
                        .detect((it) -> it.name.equalsIgnoreCase(growsInto));
                is.oa.tell(new DeleteObjectMessage<>(objectsDeleteAdapter, gm, v.pos, (id, type) -> id == v.getId()));
                is.oa.tell(new InsertObjectMessage<>(objectsInsertAdapter, gm, kgrowv, v.pos, (og) -> {
                    final var growv = (Tree) og;
                    growv.setHidden(true);
                    growv.setGrowth(2.0f);
                }));
            }
        }

        @SneakyThrows
        private void runTree(KnowledgeObject kv, Vegetation v) {
            v.setGrowth(v.getGrowth() + 0.001f);
            is.os.set(v.getObjectType(), v);
            if (v.getGrowth() > 1.0f) {
                v.setGrowth(0.0f);
                final var loaded = new VegetationLoadKnowledges((KnowledgeVegetation) kv);
                loaded.loadKnowledges(askKnowledge);
                final var vkn = new VegetationKnowledge();
                final var k = vkn.createKnowledgeService();
                vkn.setLoadedKnowledges(loaded);
                final var knowledge = vkn.createRulesKnowledgeFromSource(k, "PineRules.java");
                vkn.run(askKnowledge, knowledge, v, (KnowledgeVegetation) kv, is.cg, is.cs, gm);
                is.os.set(v.getObjectType(), v);
            }
        }
    }

    private void onUpdateModel0(UpdateTerrainMessage m) {
        IntList indices = m.gm.filledBlocks.injectIntoKeyValue(IntLists.mutable.withInitialCapacity(100),
                (list, index, value) -> {
                    value.getAndUpdate((c) -> {
                        if (c > 0) {
                            list.add(index);
                        }
                        return c;
                    });
                    return list;
                });
        pool.invoke(new ObjectsUpdateAction(m.gm, indices, 0, indices.size()));
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onAppPaused(AppPausedMessage m) {
        log.debug("onAppPaused {}", m);
        if (pauseOnFocusLost) {
            if (m.paused) {
                timer.cancel(UPDATE_OBJECTS_MESSAGE_TIMER_KEY);
            } else {
                previousStartTerrainForGameMapMessage.ifPresent(this::onStartTerrainForGameMap);
            }
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
        pool.shutdown();
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
                .onMessage(WrappedInsertObjectResponse.class, (m) -> {
                    return Behaviors.same();
                })//
                .onMessage(WrappedDeleteObjectResponse.class, (m) -> {
                    return Behaviors.same();
                })//
        ;
    }
}
