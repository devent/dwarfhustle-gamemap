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
package com.anrisoftware.dwarfhustle.gamemap.jme.actors;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.inject.Inject;

import org.apache.commons.jcs3.JCS;
import org.apache.commons.jcs3.access.CacheAccess;
import org.apache.commons.jcs3.access.exception.CacheException;
import org.eclipse.collections.api.factory.primitive.LongObjectMaps;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.map.mutable.primitive.SynchronizedLongObjectMap;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.GetTextureMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.GetTextureMessage.GetTextureSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.db.cache.AbstractJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.google.inject.Injector;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Cache for material textures.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class MaterialAssetsJcsCacheActor extends AbstractJcsCacheActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            MaterialAssetsJcsCacheActor.class.getSimpleName());

    public static final String NAME = MaterialAssetsJcsCacheActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    /**
     * Factory to create {@link MaterialAssetsJcsCacheActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface MaterialAssetsJcsCacheActorFactory extends AbstractJcsCacheActorFactory {

        @Override
        MaterialAssetsJcsCacheActor create(ActorContext<Message> context, StashBuffer<Message> stash, ObjectsGetter og);
    }

    public static Behavior<Message> create(Injector injector, AbstractJcsCacheActorFactory actorFactory,
            CompletionStage<ObjectsGetter> og, CompletionStage<CacheAccess<Object, GameObject>> initCacheAsync) {
        return AbstractJcsCacheActor.create(injector, actorFactory, og, initCacheAsync);
    }

    /**
     * Creates the {@link MaterialAssetsJcsCacheActor}.
     *
     * @param injector the {@link Injector} injector.
     * @param timeout  the {@link Duration} timeout.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        var actorFactory = injector.getInstance(MaterialAssetsJcsCacheActorFactory.class);
        var initCache = createInitCacheAsync();
        CompletionStage<ObjectsGetter> og = CompletableFuture.supplyAsync(() -> new ObjectsGetter() {

            @Override
            public <T extends GameObject> T get(Class<T> typeClass, String type, Object key)
                    throws ObjectsGetterException {
                throw new UnsupportedOperationException();
            }
        });
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, actorFactory, og, initCache));
    }

    public static CompletableFuture<CacheAccess<Object, GameObject>> createInitCacheAsync() {
        CompletableFuture<CacheAccess<Object, GameObject>> initCache = CompletableFuture.supplyAsync(() -> {
            try {
                return JCS.getInstance("assets-material");
            } catch (CacheException e) {
                throw new RuntimeException(e);
            }
        });
        return initCache;
    }

    @Inject
    private AssetsLoadMaterialTextures textures;

    private MutableLongObjectMap<TextureCacheObject> localCache;

    @Override
    protected Behavior<Message> initialStage(InitialStateMessage m) {
        log.debug("initialStage {}", m);
        this.localCache = new SynchronizedLongObjectMap<>(LongObjectMaps.mutable.ofInitialCapacity(100));
        return super.initialStage(m);
    }

    @Override
    protected int getId() {
        return ID;
    }

    @Override
    protected void retrieveValueFromBackend(CacheGetMessage<?> m, Consumer<GameObject> consumer) {
        var to = textures.loadTextureObject((long) m.key);
        consumer.accept(to);
    }

    @Override
    protected void storeValueBackend(Object key, GameObject go) {
        // nop
    }

    @Override
    protected void storeValueBackend(Class<?> keyType, Function<GameObject, Object> key, GameObject go) {
        // nop
    }

    @SuppressWarnings("unchecked")
    @Override
    @SneakyThrows
    protected <T extends GameObject> T getValueFromBackend(Class<T> typeClass, String type, Object key) {
        var to = textures.loadTextureObject((long) key);
        return (T) to;
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    private Behavior<Message> onLoadTextures(@SuppressWarnings("rawtypes") LoadTexturesMessage m) {
        log.debug("onLoadTextures {}", m);
        try {
            textures.loadMaterialTextures(cache);
            m.replyTo.tell(new LoadTexturesSuccessMessage<>(m));
        } catch (Throwable e) {
            log.error("onLoadTextures", e);
            m.replyTo.tell(new LoadTexturesErrorMessage<>(m, e));
        }
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    private Behavior<Message> onGetTexture(@SuppressWarnings("rawtypes") GetTextureMessage m) {
        log.debug("onGetTexture {}", m);
        var to = (TextureCacheObject) cache.get(m.key, () -> retrieveTexture(m));
        m.consumer.accept(to);
        m.replyTo.tell(new GetTextureSuccessMessage<>(m, to));
        return Behaviors.stopped();
    }

    private TextureCacheObject retrieveTexture(GetTextureMessage<?> m) {
        var to = textures.loadTextureObject(m.key);
        return to;
    }

    @Override
    protected BehaviorBuilder<Message> getInitialBehavior() {
        return super.getInitialBehavior()//
                .onMessage(LoadTexturesMessage.class, this::onLoadTextures)//
                .onMessage(GetTextureMessage.class, this::onGetTexture)//
        ;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends GameObject> T get(Class<T> typeClass, String type, Object key) throws ObjectsGetterException {
        var v = localCache.get((long) key);
        if (v == null) {
            v = (TextureCacheObject) super.get(typeClass, type, key);
            localCache.put(v.id, v);
        }
        return (T) v;
    }
}
