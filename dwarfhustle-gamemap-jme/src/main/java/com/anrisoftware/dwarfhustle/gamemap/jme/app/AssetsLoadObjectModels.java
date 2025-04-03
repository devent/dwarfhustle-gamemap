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

import static com.jme3.math.FastMath.approximateEquals;

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
        unknown = am.loadModel("Models/blocks/block-normal/block-normal.j3o");
        this.modelMap = (ModelMap) engine.run("ObjectModels.groovy", binding);
        modelMap.data.values().parallelStream().forEach((e) -> loadModelMap(cache, e));
    }

    public void loadModelMap(MutableLongObjectMap<AssetCacheObject> cache, ModelMapData data) {
        final var mo = loadModelData(data);
        final var model = loadModel(data.model);
        if (data.rid == 1072) {
            System.out.println(); // TODO
        }
        final var node = setupMesh(data, model);
        mo.model = node;
        cache.put(mo.getId(), mo);
    }

    private Spatial setupMesh(ModelMapData data, Spatial model) {
        final var node = setupTexCoord3(data, model);
        if (approximateEquals(data.rotation[0], 0f) && approximateEquals(data.rotation[1], 0f)
                && approximateEquals(data.rotation[2], 0f) && approximateEquals(data.scale[0], 1f)
                && approximateEquals(data.scale[1], 1f) && approximateEquals(data.scale[2], 1f)) {
            return node;
        }
        for (final var child : node.getChildren()) {
            final var geo = (Geometry) child;
            final var mesh = geo.getMesh();
            rotateMechGeo(mesh, data.rotation, data.scale);
        }
        return node;
    }

    private Node setupTexCoord3(ModelMapData data, Spatial model) {
        final var oldnode = (Node) model;
        final var node = new Node("model-" + data.rid);
        node.setLocalTransform(oldnode.getLocalTransform());
        for (final var child : oldnode.getChildren()) {
            final var geo = (Geometry) child;
            final var mesh = geo.getMesh().deepClone();
            final var tex = mesh.getBuffer(Type.TexCoord);
            if (tex == null) {
                log.warn("{} does not have textures coordinates", data.model);
            } else {
                mesh.setBuffer(tex.clone(Type.TexCoord3));
                mesh.setBuffer(tex.clone(Type.TexCoord4));
                mesh.setBuffer(tex.clone(Type.TexCoord5));
                mesh.setBuffer(tex.clone(Type.TexCoord6));
                mesh.setBuffer(tex.clone(Type.TexCoord7));
                mesh.setBuffer(tex.clone(Type.TexCoord8));
            }
            final var newgeo = new Geometry("mesh", mesh);
            newgeo.setMaterial(geo.getMaterial());
            node.attachChild(newgeo);
        }
        return node;
    }

    public static Mesh rotateMechGeo(Mesh mesh, float[] rotation, float[] scale) {
        mesh.getFloatBuffer(Type.Position);
        final var q = new Quaternion(rotation);
        final var normal = mesh.getFloatBuffer(Type.Normal).rewind();
        final var pos = mesh.getFloatBuffer(Type.Position).rewind();
        float n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z;
        final var tmp = TempVars.get();
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
            q.multLocal(tmp.vect1);
            q.multLocal(tmp.vect2);
            q.multLocal(tmp.vect3);
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
            q.multLocal(tmp.vect1);
            tmp.vect1.multLocal(scale[0], scale[1], scale[2]);
            pos.put(i, tmp.vect1.x);
            pos.put(i + 1, tmp.vect1.y);
            pos.put(i + 2, tmp.vect1.z);
        }
        tmp.release();
        mesh.updateBound();
        return mesh;
    }

    private ModelCacheObject loadModelData(ModelMapData data) {
        final var mo = new ModelCacheObject();
        mo.setId(KnowledgeObject.kid2Id(data.rid));
        mo.rid = data.rid;
        return mo;
    }

    private Spatial loadModel(String name) {
        Spatial model;
        log.trace("Loading model {}", name);
        try {
            model = am.loadModel(name);
        } catch (final AssetNotFoundException e) {
            model = unknown;
            log.error("Error loading model", e);
        }
        return model;
    }

    public ModelCacheObject loadModelObject(long key) {
        var d = modelMap.data.get(KnowledgeObject.id2Kid(key));
        if (d == null) {
            d = modelMap.data.get(819);
            log.error("No model for KID {}", KnowledgeObject.id2Kid(key));
        }
        final var to = loadModelData(d);
        final var model = loadModel(d.model);
        to.model = setupMesh(d, model);
        return to;
    }

}
