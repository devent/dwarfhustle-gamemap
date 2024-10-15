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
package com.anrisoftware.dwarfhustle.gamemap.jme.objects;

import org.eclipse.collections.api.factory.primitive.IntObjectMaps;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;

import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.google.inject.assistedinject.Assisted;
import com.jme3.scene.Node;

import jakarta.inject.Inject;

/**
 *
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
public class ObjectsRenderSystem extends IntervalIteratingSystem {

    /**
     * Creates {@link ObjectsRenderSystem}.
     * 
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface ObjectsRenderSystemFactory {
        ObjectsRenderSystem create(@Assisted("materials") ObjectsGetter materials,
                @Assisted("models") ObjectsGetter models);
    }

    private final MutableIntObjectMap<Node> objectNodes;

    @Inject
    @Assisted("materials")
    private ObjectsGetter materials;

    @Inject
    @Assisted("models")
    private ObjectsGetter models;

    private Node node;

    @Inject
    public ObjectsRenderSystem() {
        super(ObjectMeshComponent.f, 1);
        this.objectNodes = IntObjectMaps.mutable.empty();
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
                removeObject(entity);
            }

            @Override
            public void entityAdded(Entity entity) {
                addObject(entity);
            }
        });
    }

    private void addObject(Entity entity) {
        System.out.println("ObjectsRenderSystem.addObject()"); // TODO
        var c = entity.getComponent(ObjectMeshComponent.class);
        System.out.println(c.object); // TODO
        // var model = models.get(ModelCacheObject.OBJECT_TYPE,
        // c.object.getObjectType());
        // int x = c.object.getPos().getX(), y = c.object.getPos().getY(), z =
        // c.object.getPos().getZ();
        // node.attachChild(n);
        // objectNodes.put(entity.hashCode(), n);
    }

    private void removeObject(Entity entity) {
        System.out.println("ObjectsRenderSystem.removeObject()"); // TODO
        var c = entity.getComponent(ObjectMeshComponent.class);
        System.out.println(c.object); // TODO
        // var n = objectNodes.remove(entity.hashCode());
        // node.detachChild(n);
    }

    @Override
    protected void processEntity(Entity entity) {
        // var cc = ChunksBoundingBoxComponent.m.get(entity);
        // var n = objectNodes.get(entity.hashCode());
    }
}
