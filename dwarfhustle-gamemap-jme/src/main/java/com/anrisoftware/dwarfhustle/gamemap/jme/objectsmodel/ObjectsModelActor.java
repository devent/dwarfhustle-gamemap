package com.anrisoftware.dwarfhustle.gamemap.jme.objectsmodel;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage.askKnowledgeObjects;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;

import com.anrisoftware.dwarfhustle.gamemap.jme.objectsrender.ObjectsRenderActor;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppPausedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.StartTerrainForGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject;
import com.anrisoftware.dwarfhustle.model.api.objects.MapObjectsStorage;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeTree;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeTreeSapling;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeVegetation;
import com.anrisoftware.dwarfhustle.model.api.vegetations.Vegetation;
import com.anrisoftware.dwarfhustle.model.knowledge.evrete.AskKnowledge;
import com.anrisoftware.dwarfhustle.model.knowledge.evrete.VegetationKnowledge;
import com.anrisoftware.dwarfhustle.model.trees.VegetationLoadKnowledges;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.TimerScheduler;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
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
    private static class UpdateTerrainMessage extends Message {
        public final GameMap gm;
    }

    /**
     * Factory to create {@link ObjectsModelActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface ObjectsModelActorFactory {
        ObjectsModelActor create(ActorContext<Message> context, TimerScheduler<Message> timer,
                MapObjectsStorage mapObjects, @Assisted("materials") ObjectsGetter materials,
                @Assisted("models") ObjectsGetter models, @Assisted("objects-getter") ObjectsGetter og,
                @Assisted("objects-setter") ObjectsSetter os, @Assisted("chunks-getter") ObjectsGetter cg,
                @Assisted("chunks-setter") ObjectsSetter cs, @Assisted("knowledge-getter") KnowledgeGetter kg);
    }

    /**
     * Creates the {@link ObjectsRenderActor}.
     */
    private static Behavior<Message> create(Injector injector, MapObjectsStorage mapObjects,
            CompletionStage<ObjectsGetter> materials, CompletionStage<ObjectsGetter> models,
            CompletionStage<ObjectsGetter> og, CompletionStage<ObjectsSetter> os, CompletionStage<ObjectsGetter> cg,
            CompletionStage<ObjectsSetter> cs, CompletionStage<KnowledgeGetter> kg) {
        return Behaviors.withTimers(timer -> Behaviors.setup(context -> {
            var ma = materials.toCompletableFuture().get(15, SECONDS);
            var mo = models.toCompletableFuture().get(15, SECONDS);
            var og0 = og.toCompletableFuture().get(15, SECONDS);
            var os0 = os.toCompletableFuture().get(15, SECONDS);
            var cg0 = cg.toCompletableFuture().get(15, SECONDS);
            var cs0 = cs.toCompletableFuture().get(15, SECONDS);
            var kg0 = kg.toCompletableFuture().get(15, SECONDS);
            return injector.getInstance(ObjectsModelActorFactory.class)
                    .create(context, timer, mapObjects, ma, mo, og0, os0, cg0, cs0, kg0).start(injector);
        }));
    }

    /**
     * Creates the {@link ObjectsRenderActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
            MapObjectsStorage mapObjects, CompletionStage<ObjectsGetter> materials,
            CompletionStage<ObjectsGetter> models, CompletionStage<ObjectsGetter> og, CompletionStage<ObjectsSetter> os,
            CompletionStage<ObjectsGetter> cg, CompletionStage<ObjectsSetter> cs, CompletionStage<KnowledgeGetter> kg) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME,
                create(injector, mapObjects, materials, models, og, os, cg, cs, kg));
    }

    @Inject
    @Assisted
    private ActorContext<Message> context;

    @Inject
    @Assisted
    private TimerScheduler<Message> timer;

    @Inject
    @Assisted
    private MapObjectsStorage mapObjects;

    @Inject
    @Assisted("materials")
    private ObjectsGetter materials;

    @Inject
    @Assisted("models")
    private ObjectsGetter models;

    @Inject
    @Assisted("chunks-getter")
    private ObjectsGetter cg;

    @Inject
    @Assisted("chunks-setter")
    private ObjectsSetter cs;

    @Inject
    @Assisted("objects-getter")
    private ObjectsGetter og;

    @Inject
    @Assisted("objects-setter")
    private ObjectsSetter os;

    @Inject
    @Assisted("knowledge-getter")
    private KnowledgeGetter kg;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    private ActorSystemProvider actor;

    private Optional<StartTerrainForGameMapMessage> previousStartTerrainForGameMapMessage = Optional.empty();

    private ForkJoinPool pool;

    private AskKnowledge askKnowledge;

    private boolean pauseOnFocusLost;

    /**
     * Returns a behavior for the messages from {@link #getInitialBehavior()}
     */
    private Behavior<Message> start(Injector injector) {
        log.debug("onInitialState");
        this.pool = new ForkJoinPool(4);
        this.askKnowledge = (timeout, type) -> askKnowledgeObjects(actor.getActorSystem(), timeout, type);
        return getInitialBehavior()//
                .build();
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

        private final int maxSize = 32;

        private final GameMap gm;

        private final int sx;

        private final int sy;

        private final int sz;

        private final int ex;

        private final int ey;

        private final int ez;

        private final List<GameMapObject> removeObjects;

        @Override
        protected void compute() {
            if (ex - sx > maxSize || ey - sy > maxSize || ez - sz > maxSize) {
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
            final int exx = sx / 2 + ex / 2;
            final int eyy = sy / 2 + ey / 2;
            final int ezz = sz / 2 + ez / 2;
            dividedTasks.add(create(sx, sy, sz, exx, eyy, ezz));
            dividedTasks.add(create(exx, eyy, ezz, gm.width, gm.height, gm.depth));
            return dividedTasks;
        }

        private RecursiveAction create(int sx, int sy, int sz, int ex, int ey, int ez) {
            return new ObjectsUpdateAction(gm, sx, sy, sz, ex, ey, ez, removeObjects);
        }

        protected void processing() {
            mapObjects.getObjectsRange(sx, sy, sz, ex, ey, ez, this::updateModels);
        }

        private void updateModels(int type, long id, int x, int y, int z) {
            var object = (GameMapObject) og.get(type, id);
            var oid = object.getOid();
            var k = kg.get(oid);
            for (var kv : k.objects) {
                if (kv.kid == object.kid) {
                    runVegetationKnowledges(kv, (Vegetation) object);
                    return;
                }
            }
        }

        private void runVegetationKnowledges(KnowledgeObject kv, Vegetation v) {
            v.setGrowth(v.getGrowth() + 0.5f);
            os.set(v.getObjectType(), v);
            if (v.getGrowth() > 1.0f) {
                var kvv = (KnowledgeTreeSapling) kv;
                final String growsInto = kvv.getGrowsInto();
                final var kgrowv = kg.get(KnowledgeTree.TYPE.hashCode()).objects
                        .detect((it) -> it.name.equalsIgnoreCase(growsInto));
                var loaded = new VegetationLoadKnowledges((KnowledgeVegetation) kgrowv);
                loaded.loadKnowledges(askKnowledge);
                var vkn = new VegetationKnowledge();
                var k = vkn.createKnowledgeService();
                vkn.setLoadedKnowledges(loaded);
                var knowledge = vkn.createRulesKnowledgeFromSource(k, "PineRules.java");
                vkn.run(askKnowledge, knowledge, v, (KnowledgeVegetation) kv, cg, cs, gm);
                os.set(v.getObjectType(), v);
                removeObjects.add(v);
            }
            System.out.println(v); // TODO
        }
    }

    private final MutableList<GameMapObject> removeObjects = Lists.mutable.withInitialCapacity(100);

    private void onUpdateModel0(UpdateTerrainMessage m) {
        removeObjects.clear();
        var removeObjectsList = removeObjects.asSynchronized();
        pool.invoke(new ObjectsUpdateAction(m.gm, 0, 0, 0, m.gm.width, m.gm.height, m.gm.depth, removeObjectsList));
        for (GameMapObject o : removeObjects) {
            mapObjects.removeObject(o.getPos().getX(), o.getPos().getY(), o.getPos().getZ(), o.getObjectType(),
                    o.getId());
        }
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
        ;
    }
}
