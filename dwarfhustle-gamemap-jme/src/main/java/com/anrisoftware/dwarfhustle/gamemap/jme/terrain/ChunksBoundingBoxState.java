package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import javax.inject.Inject;
import javax.inject.Named;

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

    private Node node;

    private MutableLongObjectMap<Entity> entities;

    @Override
    protected void initialize(Application app) {
        this.entities = LongObjectMaps.mutable.empty();
        this.node = new Node("ChunksBoundingBoxNode");
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
}
