/*
 * Dwarf Hustle Game Map - Game map.
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

import javax.inject.Inject;

import org.apache.commons.jcs3.JCS;
import org.apache.commons.jcs3.access.CacheAccess;
import org.apache.commons.jcs3.access.exception.CacheException;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage.LoadModelsErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage.LoadModelsSuccessMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.db.cache.AbstractJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutsMessage;
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
 * Cache for models.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class ModelsAssetsJcsCacheActor extends AbstractJcsCacheActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            ModelsAssetsJcsCacheActor.class.getSimpleName());

    public static final String NAME = ModelsAssetsJcsCacheActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    /**
     * Factory to create {@link ModelsAssetsJcsCacheActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface ModelsAssetsJcsCacheActorFactory extends AbstractJcsCacheActorFactory {

        @Override
        ModelsAssetsJcsCacheActor create(ActorContext<Message> context, StashBuffer<Message> stash, ObjectsGetter og);
    }

    public static Behavior<Message> create(Injector injector, AbstractJcsCacheActorFactory actorFactory,
            CompletionStage<ObjectsGetter> og, CompletionStage<CacheAccess<Object, GameObject>> initCacheAsync) {
        return AbstractJcsCacheActor.create(injector, actorFactory, og, initCacheAsync);
    }

    /**
     * Creates the {@link ModelsAssetsJcsCacheActor}.
     *
     * @param injector the {@link Injector} injector.
     * @param timeout  the {@link Duration} timeout.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        var actorFactory = injector.getInstance(ModelsAssetsJcsCacheActorFactory.class);
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
                return JCS.getInstance("assets-models");
            } catch (CacheException e) {
                throw new RuntimeException(e);
            }
        });
        return initCache;
    }

    @Inject
    private AssetsLoadObjectModels models;

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
    protected void retrieveValueFromDb(CacheGetMessage<?> m, Consumer<GameObject> consumer) {
        var to = models.loadModelObject((long) m.key);
        consumer.accept(to);
    }

    @Override
    protected void storeValueDb(CachePutMessage<?> m) {
        // nop
    }

    @Override
    protected void storeValueDb(CachePutsMessage<?> m) {
        // nop
    }

    @SuppressWarnings("unchecked")
    @Override
    @SneakyThrows
    protected <T extends GameObject> T getValueFromDb(Class<T> typeClass, String type, Object key) {
        var to = models.loadModelObject((long) key);
        return (T) to;
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    private Behavior<Message> onLoadModels(@SuppressWarnings("rawtypes") LoadModelsMessage m) {
        log.debug("onLoadModels {}", m);
        try {
            models.loadObjectModels(cache);
            m.replyTo.tell(new LoadModelsSuccessMessage<>(m));
        } catch (Throwable e) {
            log.error("onLoadModels", e);
            m.replyTo.tell(new LoadModelsErrorMessage<>(m, e));
        }
        return Behaviors.same();
    }

    @Override
    protected BehaviorBuilder<Message> getInitialBehavior() {
        return super.getInitialBehavior()//
                .onMessage(LoadModelsMessage.class, this::onLoadModels)//
        ;
    }

}
