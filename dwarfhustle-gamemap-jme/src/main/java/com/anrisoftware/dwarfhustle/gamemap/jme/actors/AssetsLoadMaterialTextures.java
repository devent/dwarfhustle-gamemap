package com.anrisoftware.dwarfhustle.gamemap.jme.actors;

import java.net.URL;

import javax.inject.Inject;

import org.apache.commons.jcs3.access.CacheAccess;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;

import com.anrisoftware.dwarfhustle.gamemap.jme.assets.TexturesMap;
import com.anrisoftware.dwarfhustle.gamemap.jme.assets.TexturesMapData;
import com.anrisoftware.dwarfhustle.gamemap.jme.assets.TexturesMapFramesData;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Loads material textures from {@code TexturesMaterial.groovy}
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class AssetsLoadMaterialTextures {

    @Inject
    private AssetManager am;

    private Texture unknownTextures;

    private TexturesMap materialTexturesMap;

    private MutableLongObjectMap<TexturesMapFramesData> texturesMapFramesDataMap = LongObjectMaps.mutable.empty();

    @SneakyThrows
    public void loadMaterialTextures(CacheAccess<Object, GameObject> cache) {
        var engine = new GroovyScriptEngine(
                new URL[] { AssetsLoadMaterialTextures.class.getResource("/TexturesMaterials.groovy") });
        var binding = new Binding();
        unknownTextures = am.loadTexture("Textures/Tiles/Unknown/unknown-02-128.png");
        this.materialTexturesMap = (TexturesMap) engine.run("TexturesMaterials.groovy", binding);
        materialTexturesMap.data.values().parallelStream().forEach((e) -> loadTextureMap(cache, e));
    }

    public void loadTextureMap(CacheAccess<Object, GameObject> cache, TexturesMapData data) {
        var tex = loadTexture(data.image);
        for (var v : data.frames.entrySet()) {
            texturesMapFramesDataMap.put(v.getKey(), v.getValue());
            var to = loadTextureData(v.getValue());
            to.tex = tex;
            long id = KnowledgeObject.rid2Id(v.getKey());
            to.setId(id);
            to.setRid(v.getKey());
            cache.put(id, to);
        }
    }

    private TextureCacheObject loadTextureData(TexturesMapFramesData data) {
        var to = new TextureCacheObject();
        to.specular = new ColorRGBA(data.specular[0], data.specular[1], data.specular[2], data.specular[3]);
        to.baseColor = new ColorRGBA(data.color[0], data.color[1], data.color[2], data.color[3]);
        to.metallic = data.metallic;
        to.glossiness = data.glossiness;
        to.roughness = data.roughness;
        to.x = data.x;
        to.y = data.y;
        to.w = data.w;
        to.h = data.h;
        return to;
    }

    private Texture loadTexture(String image) {
        Texture tex = null;
        log.trace("Loading texture {}", image);
        try {
            tex = am.loadTexture(image);
        } catch (AssetNotFoundException e) {
            tex = unknownTextures;
            log.error("Error loading texture", e);
        }
        return tex;
    }

    public TextureCacheObject loadTextureObject(long key) {
        var d = texturesMapFramesDataMap.get(key);
        var to = loadTextureData(d);
        to.tex = loadTexture(d.image);
        return to;
    }

}
