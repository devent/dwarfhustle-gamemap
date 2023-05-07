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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.apache.commons.jcs3.JCS;
import org.apache.commons.jcs3.access.CacheAccess;
import org.apache.commons.jcs3.access.exception.CacheException;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.GetTextureMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.GetTextureMessage.GetTextureSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage.LoadModelsErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage.LoadModelsSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetCacheKey;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetCacheKey.MaterialCacheKey;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.db.cache.AbstractJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutsMessage;
import com.google.inject.Injector;
import com.jme3.scene.Spatial;

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
 * Cache for game assets.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class AssetsJcsCacheActor extends AbstractJcsCacheActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            AssetsJcsCacheActor.class.getSimpleName());

    public static final String NAME = AssetsJcsCacheActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    /**
     * Factory to create {@link AssetsJcsCacheActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface AssetsJcsCacheActorFactory extends AbstractJcsCacheActorFactory {

        @Override
        AssetsJcsCacheActor create(ActorContext<Message> context, StashBuffer<Message> stash, Class<?> keyType);
    }

    public static Behavior<Message> create(Injector injector, AbstractJcsCacheActorFactory actorFactory,
            CompletionStage<CacheAccess<Object, GameObject>> initCacheAsync) {
        return AbstractJcsCacheActor.create(injector, actorFactory, AssetCacheKey.class, initCacheAsync);
    }

    /**
     * Creates the {@link AssetsJcsCacheActor}.
     *
     * @param injector the {@link Injector} injector.
     * @param timeout  the {@link Duration} timeout.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        var actorFactory = injector.getInstance(AssetsJcsCacheActorFactory.class);
        var initCache = createInitCacheAsync();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, actorFactory, initCache));
    }

    public static CompletableFuture<CacheAccess<Object, GameObject>> createInitCacheAsync() {
        CompletableFuture<CacheAccess<Object, GameObject>> initCache = CompletableFuture.supplyAsync(() -> {
            try {
                return JCS.getInstance("assets");
            } catch (CacheException e) {
                throw new RuntimeException(e);
            }
        });
        return initCache;
    }

    @Inject
    private AssetsLoadMaterialTextures loadMaterialTextures;

    private Map<Integer, Map<String, Object>> modelsMap;

    private Spatial unknownModel;

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
        if (m.key instanceof MaterialCacheKey tkey) {
            var to = loadMaterialTextures.loadTextureObject(tkey.getKey());
            consumer.accept(to);
        }
    }

    @Override
    protected void storeValueDb(CachePutMessage<?> m) {
        // nop
    }

    @Override
    protected void storeValueDb(CachePutsMessage<?> m) {
        // nop
    }

    @Override
    @SneakyThrows
    protected GameObject getValueFromDb(Class<? extends GameObject> typeClass, String type, Object key) {
        if (key instanceof MaterialCacheKey tk) {
            var to = loadMaterialTextures.loadTextureObject(tk.key);
            return to;
        }
        throw new IllegalArgumentException();
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
            loadMaterialTextures.loadMaterialTextures(cache);
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
        if (m.key instanceof MaterialCacheKey tk) {
            var to = loadMaterialTextures.loadTextureObject(tk.key);
            return to;
        }
        throw new IllegalArgumentException();
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
            //loadModels();
            m.replyTo.tell(new LoadModelsSuccessMessage<>(m));
        } catch (Throwable e) {
            log.error("onLoadModels", e);
            m.replyTo.tell(new LoadModelsErrorMessage<>(m, e));
        }
        return Behaviors.same();
    }

//    @SuppressWarnings("unchecked")
//    @SneakyThrows
//    private void loadModels() {
//        var engine = new GroovyScriptEngine(new URL[] { MapTerrainModel.class.getResource("/ModelsMap.groovy") });
//        var binding = new Binding();
//        this.modelsMap = (Map<Integer, Map<String, Object>>) engine.run("ModelsMap.groovy", binding);
//        unknownModel = am.loadModel("Models/Tiles/Unknown/unknown-02.png");
//        modelsMap.entrySet().parallelStream().forEach(this::loadModelMap);
//    }
//
//    private void loadModelMap(Map.Entry<Integer, Map<String, Object>> mentry) {
//        long id = mentry.getKey();
//        var to = loadModelObject(mentry.getValue(), id);
//        cache.put(new ModelCacheKey(KnowledgeObject.rid2Id(id)), to);
//    }
//
//    private ModelCacheObject loadModelObject(Map<String, Object> map, long id) {
//        var co = new ModelCacheObject();
//        co.model = loadModel(map, id, "model");
//        return null;
//    }
//
//    private Spatial loadModel(Map<String, Object> map, long id, String name) {
//        var texname = (String) map.get(name);
//        Spatial model = null;
//        log.trace("Loading {} model {}:={}", name, id, texname);
//        try {
//            model = am.loadModel(name);
//        } catch (AssetNotFoundException e) {
//            model = unknownModel;
//            log.error("Error loading model", e);
//        }
//        return model;
//    }

    @Override
    protected BehaviorBuilder<Message> getInitialBehavior() {
        return super.getInitialBehavior()//
                .onMessage(LoadTexturesMessage.class, this::onLoadTextures)//
                .onMessage(LoadModelsMessage.class, this::onLoadModels)//
                .onMessage(GetTextureMessage.class, this::onGetTexture)//
        ;
    }

}
