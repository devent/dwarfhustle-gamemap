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

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.eclipse.collections.api.factory.primitive.LongObjectMaps;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;

import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bounding.BoundingBox;
import com.jme3.scene.Node;

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

    private MutableLongObjectMap<Entity> entities = LongObjectMaps.mutable.empty();

    @Override
    protected void initialize(Application app) {
        system.setNode(node);
    }

    @Override
    protected void cleanup(Application app) {
        entities.forEachValue(e -> {
            engine.removeEntity(e);
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

    public void setChunk(MapChunk root, BoundingBox bb) {
        var e = entities.get(root.id);
        if (e == null) {
            e = engine.createEntity();
            engine.addEntity(e);
            entities.put(root.id, e);
        }
        e.add(new ChunksBoundingBoxComponent(0, bb));
    }

    public Node getNode() {
        return node;
    }
}
