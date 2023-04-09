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

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.apache.commons.jcs3.JCS;
import org.apache.commons.jcs3.access.CacheAccess;
import org.apache.commons.jcs3.access.exception.CacheException;

import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrainModel;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.GetTextureMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.GetTextureMessage.GetTextureSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetKey;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetKey.MaterialTextureKey;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureObject;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.db.cache.AbstractJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutsMessage;
import com.google.inject.Injector;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.ServiceKey;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
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
        return AbstractJcsCacheActor.create(injector, actorFactory, AssetKey.class, initCacheAsync);
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
    private AssetManager am;

    private Texture unknownTextures;

    private Map<Integer, Map<String, Object>> materialTexturesMap;

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
        if (m.key instanceof MaterialTextureKey tkey) {
            var to = loadTextureObject(materialTexturesMap.get(tkey.key.intValue()), tkey.key);
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
    protected GameObject retrieveValueFromDb(String type, Object key) {
        if (key instanceof MaterialTextureKey tk) {
            var to = loadTextureObject(materialTexturesMap.get(tk.key.intValue()), tk.key);
            return to;
        }
        throw new IllegalArgumentException();
    }

    @Override
    protected BehaviorBuilder<Message> getInitialBehavior() {
        return super.getInitialBehavior()//
                .onMessage(LoadTexturesMessage.class, this::onLoadTextures)//
                .onMessage(GetTextureMessage.class, this::onGetTexture)//
        ;
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
            loadMaterialTextures();
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
        var to = (TextureObject) cache.get(m.key, () -> retriveTexture(m));
        m.consumer.accept(to);
        m.replyTo.tell(new GetTextureSuccessMessage<>(m, to));
        return Behaviors.stopped();
    }

    private TextureObject retriveTexture(GetTextureMessage<?> m) {
        if (m.key instanceof MaterialTextureKey tk) {
            var to = loadTextureObject(materialTexturesMap.get(tk.key.intValue()), tk.key);
            return to;
        }
        throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private void loadMaterialTextures() {
        am.registerLocator("../dwarfhustle-assetpack.zip", ZipLocator.class);
        var engine = new GroovyScriptEngine(new URL[] { MapTerrainModel.class.getResource("/TexturesMap.groovy") });
        var binding = new Binding();
        unknownTextures = am.loadTexture("Textures/Tiles/Unknown/unknown-02.png");
        this.materialTexturesMap = (Map<Integer, Map<String, Object>>) engine.run("TexturesMap.groovy", binding);
        materialTexturesMap.entrySet().parallelStream().forEach(this::loadTextureMap);
    }

    private void loadTextureMap(Map.Entry<Integer, Map<String, Object>> texentry) {
        long id = texentry.getKey();
        var to = loadTextureObject(texentry.getValue(), id);
        cache.put(new MaterialTextureKey(id), to);
    }

    private TextureObject loadTextureObject(Map<String, Object> map, long id) {
        var to = new TextureObject();
        to.tex = loadTexture(map, id, "baseColorMap");
        to.specular = putColor(map, id, "specular", new ColorRGBA());
        to.baseColor = putColor(map, id, "baseColor", new ColorRGBA());
        to.metallic = putFloat(map, id, "metallic", 1f);
        to.glossiness = putFloat(map, id, "glossiness", 1f);
        to.roughness = putFloat(map, id, "roughness", 1f);
        return to;
    }

    private float putFloat(Map<String, Object> map, long id, String name, float f) {
        var vv = (Float) map.get(name);
        if (vv != null) {
            return vv;
        } else {
            return f;
        }
    }

    private ColorRGBA putColor(Map<String, Object> map, long id, String name, ColorRGBA d) {
        @SuppressWarnings("unchecked")
        var vv = (List<Float>) map.get(name);
        if (vv != null) {
            return new ColorRGBA(vv.get(0), vv.get(1), vv.get(2), vv.get(3));
        } else {
            return d;
        }
    }

    private Texture loadTexture(Map<String, Object> map, long id, String name) {
        var texname = (String) map.get(name);
        Texture tex = null;
        log.trace("Loading {} texture {}:={}", name, id, texname);
        try {
            tex = am.loadTexture(texname);
        } catch (AssetNotFoundException e) {
            tex = unknownTextures;
            log.error("Error loading texture", e);
        }
        return tex;
    }

}
