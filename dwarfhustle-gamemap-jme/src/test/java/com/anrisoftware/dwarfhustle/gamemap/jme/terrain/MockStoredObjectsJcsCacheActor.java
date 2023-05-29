package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.apache.commons.jcs3.JCS;
import org.apache.commons.jcs3.access.CacheAccess;
import org.apache.commons.jcs3.access.exception.CacheException;

import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.StoredObject;
import com.anrisoftware.dwarfhustle.model.db.cache.AbstractJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutsMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DbMessage.DbResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.LoadObjectMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.SaveObjectMessage;
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
            MockStoredObjectsJcsCacheActor.class.getSimpleName());

    public static final String NAME = MockStoredObjectsJcsCacheActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    /**
     * Factory to create {@link MockStoredObjectsJcsCacheActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface MockStoredObjectsJcsCacheActorFactory extends AbstractJcsCacheActorFactory {

        @Override
        MockStoredObjectsJcsCacheActor create(ActorContext<Message> context, StashBuffer<Message> stash,
                ObjectsGetter og, Class<?> keyType);
    }

    public static Behavior<Message> create(Injector injector, AbstractJcsCacheActorFactory actorFactory,
            CompletionStage<ObjectsGetter> og, CompletionStage<CacheAccess<Object, GameObject>> initCacheAsync) {
        return AbstractJcsCacheActor.create(injector, actorFactory, og, Long.class, initCacheAsync);
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
    private ActorSystemProvider actor;

    @SuppressWarnings("rawtypes")
    private ActorRef<DbResponseMessage> dbResponseAdapter;

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
    protected void handleCacheMiss(@SuppressWarnings("rawtypes") CacheGetMessage m) {
        if (m.key instanceof Long id && StoredObject.class.isAssignableFrom(m.typeClass)) {
            super.handleCacheMiss(m);
        }
    }

    @Override
    protected void storeValueDb(CachePutMessage<?> m) {
        if (m.value instanceof StoredObject) {
            actor.tell(new SaveObjectMessage<>(dbResponseAdapter, m.value));
        }
    }

    @Override
    protected void storeValueDb(CachePutsMessage<?> m) {
        for (var go : m.value) {
            if (m.value instanceof StoredObject) {
                actor.tell(new SaveObjectMessage<>(dbResponseAdapter, go));
            }
        }
    }

    @Override
    protected void retrieveValueFromDb(CacheGetMessage<?> m, Consumer<GameObject> consumer) {
        if (m.key instanceof Long id && StoredObject.class.isAssignableFrom(m.typeClass)) {
            retrieveGameObject(m.type, id, consumer);
        }
    }

    private void retrieveGameObject(String type, long id, Consumer<GameObject> consumer) {
        actor.tell(new LoadObjectMessage<>(dbResponseAdapter, type, consumer, db -> {
            var query = "SELECT * from ? where objecttype = ? and objectid = ? limit 1";
            return db.query(query, type, type, id);
        }));
    }

    @Override
    protected <T extends GameObject> T getValueFromDb(Class<T> typeClass, String type, Object key) {
        return og.get(typeClass, type, key);
    }

    @Override
    public <T extends GameObject> T get(Class<T> typeClass, String type, Object key) throws ObjectsGetterException {
        if (key instanceof Long id && StoredObject.class.isAssignableFrom(typeClass)) {
            return super.get(typeClass, type, key);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    protected BehaviorBuilder<Message> getInitialBehavior() {
        return super.getInitialBehavior()//
        ;
    }

}
