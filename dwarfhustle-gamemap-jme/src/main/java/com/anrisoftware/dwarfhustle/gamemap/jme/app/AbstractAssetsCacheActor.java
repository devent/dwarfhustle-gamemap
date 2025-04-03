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
package com.anrisoftware.dwarfhustle.gamemap.jme.app;

import java.util.function.Consumer;

import org.eclipse.collections.api.factory.primitive.LongObjectMaps;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.map.mutable.primitive.SynchronizedLongObjectMap;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetCacheObject;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheElementEventMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage.CacheGetSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutsMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage.CacheSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheRetrieveFromBackendMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheRetrieveMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheRetrieveMessage.CacheRetrieveResponseMessage;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public abstract class AbstractAssetsCacheActor implements ObjectsGetter {

    /**
     * Factory to create {@link AbstractAssetsCacheActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface AbstractAssetsCacheActorFactory {

        AbstractAssetsCacheActor create(ActorContext<Message> context);
    }

    public static Behavior<Message> create(Injector injector, AbstractAssetsCacheActorFactory actorFactory) {
        return Behaviors.setup(context -> actorFactory.create(context).start());
    }

    @Inject
    protected ActorSystemProvider actor;

    @Inject
    @Assisted
    protected ActorContext<Message> context;

    protected MutableLongObjectMap<AssetCacheObject> cache;

    /**
     * Returns behavior from {@link #getInitialBehavior()}
     */
    public Behavior<Message> start() {
        actor.registerObjectsGetter(getId(), this);
        this.cache = new SynchronizedLongObjectMap<>(LongObjectMaps.mutable.ofInitialCapacity(1000));
        return getInitialBehavior()//
                .build();
    }

    /**
     * Returns a behavior for the messages from {@link #getInitialBehavior()}.
     */
    @SuppressWarnings("unchecked")
    private Behavior<Message> onCachePut(@SuppressWarnings("rawtypes") CachePutMessage m) {
        log.debug("onCachePut {}", m);
        cache.put(m.value.getId(), (AssetCacheObject) m.value);
        m.replyTo.tell(new CacheSuccessMessage<>(m));
        return Behaviors.same();
    }

    /**
     * Returns a behavior for the messages from {@link #getInitialBehavior()}.
     */
    @SuppressWarnings("unchecked")
    private Behavior<Message> onCachePuts(@SuppressWarnings("rawtypes") CachePutsMessage m) {
        log.debug("onCachePuts {}", m);
        for (var o : m.values) {
            var go = (GameObject) o;
            cache.put(go.getId(), (AssetCacheObject) go);
        }
        m.replyTo.tell(new CacheSuccessMessage<>(m));
        return Behaviors.same();
    }

    /**
     * Returns a behavior for the messages from {@link #getInitialBehavior()}.
     */
    @SuppressWarnings("unchecked")
    private Behavior<Message> onCacheGet(@SuppressWarnings("rawtypes") CacheGetMessage m) {
        log.debug("onCacheGet {}", m);
        var v = cache.get(m.key);
        if (v == null) {
            m.onMiss.run();
            handleCacheMiss(m);
        } else {
            m.consumer.accept(v);
            m.replyTo.tell(new CacheGetSuccessMessage<>(m, v));
        }
        return Behaviors.same();
    }

    @SuppressWarnings("unchecked")
    protected void handleCacheMiss(@SuppressWarnings("rawtypes") CacheGetMessage m) {
        context.getSelf().tell(new CacheRetrieveFromBackendMessage(m, go -> {
            cache.put(m.key, (AssetCacheObject) go);
            m.replyTo.tell(new CacheGetSuccessMessage<>(m, go));
        }));
    }

    /**
     * Handle {@link CacheElementEventMessage}. Returns a behavior for the messages
     * from {@link #getInitialBehavior()}
     */
    protected Behavior<Message> onCacheElementEvent(Object m) {
        log.debug("onCacheElementEvent {}", m);
        return Behaviors.same();
    }

    /**
     * Handle {@link CacheElementEventMessage}. Replies with a
     * {@link CacheRetrieveResponseMessage} message. Returns a behavior for the
     * messages from {@link #getInitialBehavior()}
     */
    protected Behavior<Message> onCacheRetrieve(CacheRetrieveMessage m) {
        log.debug("onCacheRetrieve {}", m);
        if (m.id == getId()) {
            m.replyTo.tell(new CacheRetrieveResponseMessage(m, cache));
        }
        return Behaviors.same();
    }

    /**
     * Handle {@link CacheRetrieveFromBackendMessage}. Returns a behavior for the
     * messages from {@link #getInitialBehavior()}
     */
    protected Behavior<Message> onCacheRetrieveFromBackend(CacheRetrieveFromBackendMessage m) {
        log.debug("onCacheRetrieveFromBackend {}", m);
        retrieveValueFromBackend(m.m, m.consumer);
        return Behaviors.same();
    }

    /**
     * Returns the behaviors after the cache was initialized. Returns a behavior for
     * the messages:
     *
     * <ul>
     * <li>{@link CachePutMessage}
     * <li>{@link CachePutsMessage}
     * <li>{@link CacheGetMessage}
     * <li>{@link CacheRetrieveFromBackendMessage}
     * <li>{@link CacheRetrieveMessage}
     * <li>{@link CacheElementEventMessage}
     * </ul>
     */
    protected BehaviorBuilder<Message> getInitialBehavior() {
        return Behaviors.receive(Message.class)//
                .onMessage(CachePutMessage.class, this::onCachePut)//
                .onMessage(CachePutsMessage.class, this::onCachePuts)//
                .onMessage(CacheGetMessage.class, this::onCacheGet)//
                .onMessage(CacheRetrieveFromBackendMessage.class, this::onCacheRetrieveFromBackend)//
                .onMessage(CacheRetrieveMessage.class, this::onCacheRetrieve)//
                .onMessage(CacheElementEventMessage.class, this::onCacheElementEvent)//
        ;
    }

    /**
     * Returns the ID of the cache.
     */
    protected abstract int getId();

    /**
     * Retrieves the value from the backend.
     */
    protected abstract void retrieveValueFromBackend(CacheGetMessage<?> m, Consumer<GameObject> consumer);

    /**
     * Returns the value from the backend.
     */
    protected abstract <T extends GameObject> AssetCacheObject getValueFromBackend(int type, Object key);

    /**
     * Returns the value for the key directly from the cache without sending of
     * messages. Should be used for performance critical code.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends GameObject> T get(int type, long key) {
        return (T) cache.getIfAbsentPut(key, () -> supplyValue(type, key));
    }

    private AssetCacheObject supplyValue(int type, Object key) {
        return getValueFromBackend(type, key);
    }

}
