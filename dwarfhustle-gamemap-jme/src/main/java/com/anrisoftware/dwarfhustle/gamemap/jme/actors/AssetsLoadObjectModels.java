package com.anrisoftware.dwarfhustle.gamemap.jme.actors;

import java.net.URL;

import javax.inject.Inject;

import org.apache.commons.jcs3.access.CacheAccess;

import com.anrisoftware.dwarfhustle.gamemap.jme.assets.ModelMap;
import com.anrisoftware.dwarfhustle.gamemap.jme.assets.ModelMapData;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.ModelCacheObject;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.scene.Spatial;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
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
    public void loadObjectModels(CacheAccess<Object, GameObject> cache) {
        var engine = new GroovyScriptEngine(
                new URL[] { AssetsLoadObjectModels.class.getResource("/ObjectModels.groovy") });
        var binding = new Binding();
        unknown = am.loadModel("Models/tile-cube/tile-cube.j3o");
        this.modelMap = (ModelMap) engine.run("ObjectModels.groovy", binding);
        modelMap.data.values().parallelStream().forEach((e) -> loadModelMap(cache, e));
    }

    public void loadModelMap(CacheAccess<Object, GameObject> cache, ModelMapData data) {
        var mo = loadModelData(data);
        var model = loadModel(data.model);
        mo.model = model;
        cache.put(KnowledgeObject.rid2Id(data.rid), mo);
    }

    private ModelCacheObject loadModelData(ModelMapData data) {
        var mo = new ModelCacheObject();
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
        var d = modelMap.data.get(key);
        var to = loadModelData(d);
        to.model = loadModel(d.model);
        return to;
    }

}
