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

import org.eclipse.collections.api.factory.primitive.FloatObjectMaps;
import org.eclipse.collections.api.map.primitive.MutableFloatObjectMap;

import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bounding.BoundingBox;
import com.jme3.scene.Node;

import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Shows the terrain blocks.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class ChunksBoundingBoxState extends BaseAppState {

    @Inject
    @Named("rootNode")
    private Node rootNode;

    @Inject
    private Engine engine;

    @Inject
    private ChunksBoundingBoxRenderSystem system;

    private Node node = new Node("ChunksBoundingBoxNode");

    private MutableFloatObjectMap<MutableFloatObjectMap<MutableFloatObjectMap<MutableFloatObjectMap<MutableFloatObjectMap<MutableFloatObjectMap<Entity>>>>>> entities;

    public ChunksBoundingBoxState() {
        this.entities = FloatObjectMaps.mutable.empty();
    }

    @Override
    protected void initialize(Application app) {
        system.setNode(node);
    }

    @Override
    protected void cleanup(Application app) {
        entities.forEachValue(cx -> {
            cx.forEachValue(cy -> {
                cy.forEachValue(cz -> {
                    cz.forEachValue(ex -> {
                        ex.forEachValue(ey -> {
                            ey.forEachValue(entity -> {
                                engine.removeEntity(entity);
                            });

                        });
                    });
                });
            });
        });
    }

    @Override
    protected void onEnable() {
        rootNode.attachChild(node);
        engine.addSystem(system);
    }

    @Override
    protected void onDisable() {
        rootNode.detachChild(node);
        engine.removeSystem(system);
    }

    public void setChunk(MapChunk chunk, BoundingBox bb) {
        var cx = entities.getIfAbsentPut(bb.getCenter().x, FloatObjectMaps.mutable.empty());
        var cy = cx.getIfAbsentPut(bb.getCenter().y, FloatObjectMaps.mutable.empty());
        var cz = cy.getIfAbsentPut(bb.getCenter().z, FloatObjectMaps.mutable.empty());
        var ex = cz.getIfAbsentPut(bb.getXExtent(), FloatObjectMaps.mutable.empty());
        var ey = ex.getIfAbsentPut(bb.getYExtent(), FloatObjectMaps.mutable.empty());
        var e = ey.get(bb.getZExtent());
        if (e == null) {
            e = engine.createEntity();
            engine.addEntity(e);
            entities.put(bb.getCenter().x, cx);
            cx.put(bb.getCenter().y, cy);
            cy.put(bb.getCenter().z, cz);
            cz.put(bb.getXExtent(), ex);
            ex.put(bb.getYExtent(), ey);
            ey.put(bb.getZExtent(), e);
        }
        e.add(new ChunksBoundingBoxComponent(0, bb));
    }

    public Node getNode() {
        return node;
    }
}
