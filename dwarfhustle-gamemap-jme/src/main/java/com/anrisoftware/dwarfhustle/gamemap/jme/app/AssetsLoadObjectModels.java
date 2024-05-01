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

import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;

import com.anrisoftware.dwarfhustle.gamemap.jme.assets.ModelMap;
import com.anrisoftware.dwarfhustle.gamemap.jme.assets.ModelMapData;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetCacheObject;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.ModelCacheObject;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.scene.Spatial;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Loads models from {@code ObjectModels.groovy}
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class AssetsLoadObjectModels {

    @Inject
    private AssetManager am;

    private Spatial unknown;

    private ModelMap modelMap;

    @SneakyThrows
    public void loadObjectModels(MutableLongObjectMap<AssetCacheObject> cache) {
        var engine = new GroovyScriptEngine(
                new URL[] { AssetsLoadObjectModels.class.getResource("/ObjectModels.groovy") });
        var binding = new Binding();
        unknown = am.loadModel("Models/tile-block/tile-block.j3o");
        this.modelMap = (ModelMap) engine.run("ObjectModels.groovy", binding);
        modelMap.data.values().parallelStream().forEach((e) -> loadModelMap(cache, e));
    }

    public void loadModelMap(MutableLongObjectMap<AssetCacheObject> cache, ModelMapData data) {
        var mo = loadModelData(data);
        var model = loadModel(data.model);
        mo.model = model;
        cache.put(mo.id, mo);
    }

    private ModelCacheObject loadModelData(ModelMapData data) {
        var mo = new ModelCacheObject();
        mo.id = KnowledgeObject.kid2Id(data.rid);
        mo.rid = data.rid;
        return mo;
    }

    private Spatial loadModel(String name) {
        Spatial model;
        log.trace("Loading model {}", name);
        try {
            model = am.loadModel(name);
        } catch (AssetNotFoundException e) {
            model = unknown;
            log.error("Error loading model", e);
        }
        return model;
    }

    public ModelCacheObject loadModelObject(long key) {
        var d = modelMap.data.get(KnowledgeObject.id2Kid(key));
        var to = loadModelData(d);
        to.model = loadModel(d.model);
        return to;
    }

}
