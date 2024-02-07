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

import java.net.URL;

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
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import jakarta.inject.Inject;
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
        unknownTextures = am.loadTexture("Textures/Tiles/unknown-02-128.png");
        this.materialTexturesMap = (TexturesMap) engine.run("TexturesMaterials.groovy", binding);
        var syncMap = texturesMapFramesDataMap.asSynchronized();
        materialTexturesMap.data.values().parallelStream().forEach((e) -> loadTextureMap(syncMap, cache, e));
    }

    public void loadTextureMap(MutableLongObjectMap<TexturesMapFramesData> map, CacheAccess<Object, GameObject> cache,
            TexturesMapData data) {
        var tex = loadTexture(data.image);
        for (var v : data.frames.entrySet()) {
            map.put(v.getKey(), v.getValue());
            var to = loadTextureData(v.getValue());
            to.tex = tex;
            long id = KnowledgeObject.kid2Id(v.getKey());
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
        to.x = data.x / (float) data.ww;
        to.y = data.y / (float) data.hh;
        to.w = data.w / (float) data.ww;
        to.h = data.h / (float) data.hh;
        return to;
    }

    private Texture loadTexture(String image) {
        Texture tex = null;
        log.trace("Loading texture {}", image);
        try {
            tex = am.loadTexture(image);
            tex.setMagFilter(MagFilter.Bilinear);
            tex.setMinFilter(MinFilter.Trilinear);
        } catch (AssetNotFoundException e) {
            tex = unknownTextures;
            log.error("Error loading texture", e);
        }
        return tex;
    }

    public TextureCacheObject loadTextureObject(long key) {
        var d = texturesMapFramesDataMap.get(key);
        if (d == null) {
            throw new RuntimeException("No texture object with id " + key);
        }
        var to = loadTextureData(d);
        to.tex = loadTexture(d.image);
        return to;
    }

}
