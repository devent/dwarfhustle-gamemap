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
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.TempVars;

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
        // var geo = ((Geometry) ((Node) model).getChild(0)).clone();
        // geo.setLocalRotation(new Quaternion(data.rotation));
        // mo.model = geo;
        var mesh = ((Geometry) ((Node) model).getChild(0)).getMesh().deepClone();
        if (data.rid == 821) {
            System.out.println("AssetsLoadObjectModels.loadModelMap()"); // TODO
            rotateMechGeo(mesh, data.rotation, true);
        } else {
            rotateMechGeo(mesh, data.rotation, false);
        }
        var geo = new Geometry(data.model, mesh);
        mo.model = geo;
        cache.put(mo.id, mo);
    }

    public static Mesh rotateMechGeo(Mesh mesh, float[] rotation, boolean debug) {
        System.out.println("AssetsLoadObjectModels.rotateMechGeo()"); // TODO
        mesh.getFloatBuffer(Type.Position);
        var q = new Quaternion(rotation);
        var index = mesh.getShortBuffer(Type.Index).rewind();
        var normal = mesh.getFloatBuffer(Type.Normal).rewind();
        var pos = mesh.getFloatBuffer(Type.Position).rewind();
        short in0, in1, in2, i0, i1, i2;
        float n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z;
        var tmp = TempVars.get();
        for (int i = 0; i < normal.limit() / 9; i++) {
            normal.mark();
            n0x = normal.get();
            n0y = normal.get();
            n0z = normal.get();
            n1x = normal.get();
            n1y = normal.get();
            n1z = normal.get();
            n2x = normal.get();
            n2y = normal.get();
            n2z = normal.get();
            tmp.vect1.x = n0x;
            tmp.vect1.y = n0y;
            tmp.vect1.z = n0z;
            tmp.vect2.x = n1x;
            tmp.vect2.y = n1y;
            tmp.vect2.z = n1z;
            tmp.vect3.x = n2x;
            tmp.vect3.y = n2y;
            tmp.vect3.z = n2z;
            if (debug) {
                System.out.printf("%s-%s-%s\n", tmp.vect1, tmp.vect2, tmp.vect3); // TODO
            }
            q.multLocal(tmp.vect1);
            q.multLocal(tmp.vect2);
            q.multLocal(tmp.vect3);
            if (debug) {
                System.out.printf("%s-%s-%s\n", tmp.vect1, tmp.vect2, tmp.vect3); // TODO
            }
            normal.reset();
            normal.put(tmp.vect1.x);
            normal.put(tmp.vect1.y);
            normal.put(tmp.vect1.z);
            normal.put(tmp.vect2.x);
            normal.put(tmp.vect2.y);
            normal.put(tmp.vect2.z);
            normal.put(tmp.vect3.x);
            normal.put(tmp.vect3.y);
            normal.put(tmp.vect3.z);
        }
        for (int i = 0; i < pos.limit(); i += 3) {
            tmp.vect1.x = pos.get(i);
            tmp.vect1.y = pos.get(i + 1);
            tmp.vect1.z = pos.get(i + 2);
            if (debug) {
                System.out.printf("%s\n", tmp.vect1); // TODO
            }
            q.multLocal(tmp.vect1);
            if (debug) {
                System.out.printf("%s\n", tmp.vect1); // TODO
            }
            pos.put(i, tmp.vect1.x);
            pos.put(i + 1, tmp.vect1.y);
            pos.put(i + 2, tmp.vect1.z);
        }
        tmp.release();
        mesh.updateBound();
        return mesh;
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
