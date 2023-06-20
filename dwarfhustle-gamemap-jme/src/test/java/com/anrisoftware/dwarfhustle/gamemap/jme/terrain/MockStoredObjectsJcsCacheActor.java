package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Inject;

import org.apache.commons.jcs3.JCS;
import org.apache.commons.jcs3.access.CacheAccess;
import org.apache.commons.jcs3.access.exception.CacheException;
import org.eclipse.collections.api.map.primitive.LongObjectMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;

import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.db.cache.AbstractJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.google.inject.Injector;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.extern.slf4j.Slf4j;

/**
 * Returns statically available {@link GameObject}(s).
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class MockStoredObjectsJcsCacheActor extends AbstractJcsCacheActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            StoredObjectsJcsCacheActor.class.getSimpleName());

    public static final String NAME = StoredObjectsJcsCacheActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    /**
     * Factory to create {@link MockStoredObjectsJcsCacheActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface MockStoredObjectsJcsCacheActorFactory extends AbstractJcsCacheActorFactory {

        @Override
        MockStoredObjectsJcsCacheActor create(ActorContext<Message> context, StashBuffer<Message> stash,
                ObjectsGetter og);
    }

    public static Behavior<Message> create(Injector injector, AbstractJcsCacheActorFactory actorFactory,
            CompletionStage<ObjectsGetter> og, CompletionStage<CacheAccess<Object, GameObject>> initCacheAsync) {
        return AbstractJcsCacheActor.create(injector, actorFactory, og, initCacheAsync);
    }

    /**
     * Creates the {@link MockStoredObjectsJcsCacheActor}.
     *
     * @param injector the {@link Injector} injector.
     * @param timeout  the {@link Duration} timeout.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
            CompletionStage<ObjectsGetter> og) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        var actorFactory = injector.getInstance(MockStoredObjectsJcsCacheActorFactory.class);
        var initCache = createInitCacheAsync();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, actorFactory, og, initCache));
    }

    public static CompletableFuture<CacheAccess<Object, GameObject>> createInitCacheAsync() {
        CompletableFuture<CacheAccess<Object, GameObject>> initCache = CompletableFuture.supplyAsync(() -> {
            try {
                return JCS.getInstance("objects");
            } catch (CacheException e) {
                throw new RuntimeException(e);
            }
        });
        return initCache;
    }

    @Inject
    private LongObjectMap<GameObject> backend;

    @Override
    protected Behavior<Message> initialStage(InitialStateMessage m) {
        log.debug("initialStage {}", m);
        return super.initialStage(m);
    }

    @Override
    protected int getId() {
        return ID;
    }

    @Override
    protected void storeValueDb(Object key, GameObject go) {
        var b = (MutableLongObjectMap<GameObject>) this.backend;
        b.put((long) key, go);
    }

    @Override
    protected void storeValueDb(Class<?> keyType, Function<GameObject, Object> key, GameObject go) {
        var b = (MutableLongObjectMap<GameObject>) this.backend;
        b.put(go.getId(), go);
    }

    @Override
    protected void retrieveValueFromDb(CacheGetMessage<?> m, Consumer<GameObject> consumer) {
        retrieveGameObject(m.typeClass, m.type, (long) m.key, consumer);
    }

    private void retrieveGameObject(Class<? extends GameObject> typeClass, String type, long id,
            Consumer<GameObject> consumer) {
        consumer.accept(og.get(typeClass, type, id));
    }

    @Override
    protected <T extends GameObject> T getValueFromDb(Class<T> typeClass, String type, Object key) {
        return og.get(typeClass, type, key);
    }

    @Override
    protected BehaviorBuilder<Message> getInitialBehavior() {
        return super.getInitialBehavior()//
        ;
    }

}
