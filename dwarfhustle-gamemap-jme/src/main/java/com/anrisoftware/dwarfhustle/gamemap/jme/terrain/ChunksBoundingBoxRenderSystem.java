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
package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import org.eclipse.collections.api.factory.primitive.IntObjectMaps;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.StripBox;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
@Slf4j
public class ChunksBoundingBoxRenderSystem extends IntervalIteratingSystem {

    public static final Family f = Family.all(ChunksBoundingBoxComponent.class).get();

    private final MutableIntObjectMap<Node> chunkNodes;

    @Inject
    private AssetManager am;

    private Node node;

    @Inject
    public ChunksBoundingBoxRenderSystem() {
        super(f, 1);
        this.chunkNodes = IntObjectMaps.mutable.empty();
    }

    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(getFamily(), new EntityListener() {

            @Override
            public void entityRemoved(Entity entity) {
                removeChunkBoundingBox(entity);
            }

            @Override
            public void entityAdded(Entity entity) {
                addChunkBoundingBox(entity);
            }
        });
    }

    private void addChunkBoundingBox(Entity entity) {
        var cc = ChunksBoundingBoxComponent.m.get(entity);
        Node n = new Node("chunkNode-" + cc.level);
        var box = new StripBox(0.51f, 0.51f, 0.51f);
        var geo = new Geometry("chunkNode-" + cc.level, box);
        geo.setMaterial(new Material(am, "Common/MatDefs/Misc/Unshaded.j3md"));
        geo.getMaterial().getAdditionalRenderState().setWireframe(true);
        geo.getMaterial().setColor("Color", new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
        n.setLocalTranslation(cc.bb.getCenter());
        n.setLocalScale(cc.bb.getXExtent(), cc.bb.getYExtent(), cc.bb.getZExtent());
        n.attachChild(geo);
        node.attachChild(n);
        chunkNodes.put(entity.hashCode(), n);
    }

    private void removeChunkBoundingBox(Entity entity) {
        var n = chunkNodes.remove(entity.hashCode());
        node.detachChild(n);
    }

    @Override
    protected void processEntity(Entity entity) {
        var cc = ChunksBoundingBoxComponent.m.get(entity);
        var n = chunkNodes.get(entity.hashCode());
        n.setLocalTranslation(cc.bb.getCenter());
        n.setLocalScale(cc.bb.getXExtent(), cc.bb.getYExtent(), cc.bb.getZExtent());
    }
}
