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
package com.anrisoftware.dwarfhustle.gamemap.jme.objectsrender;

import static com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject.kid2Id;

import org.eclipse.collections.api.factory.primitive.IntObjectMaps;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.ModelCacheObject;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.google.inject.assistedinject.Assisted;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
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

    private Node sceneNode;

    private GameMap gm;

    @Inject
    public ObjectsRenderSystem() {
        super(ObjectMeshComponent.f, 0.05f);
        this.objectNodes = IntObjectMaps.mutable.withInitialCapacity(100);
    }

    public void setSceneNode(Node node) {
        this.sceneNode = node;
    }

    public void setGameMap(GameMap gm) {
        this.gm = gm;
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
        final var c = entity.getComponent(ObjectMeshComponent.class);
        System.out.println("ObjectsRenderSystem.addObject() " + c.object); // TODO
        final var node = new Node("" + c.object.getId());
        final ModelCacheObject model = models.get(ModelCacheObject.OBJECT_TYPE, kid2Id(c.object.getKid()));
        node.attachChild(model.getModel().clone());
        node.setShadowMode(ShadowMode.Off);
        updateLocation(c.object, node, entity);
        objectNodes.put(entity.hashCode(), node);
        this.sceneNode.attachChild(node);
    }

    private void removeObject(Entity entity) {
        final var c = entity.getComponent(ObjectMeshComponent.class);
        System.out.println("ObjectsRenderSystem.removeObject() " + c.object); // TODO
        final var node = objectNodes.remove(entity.hashCode());
        sceneNode.detachChild(node);
    }

    @Override
    protected void processEntity(Entity entity) {
        final var c = entity.getComponent(ObjectMeshComponent.class);
        final var node = objectNodes.get(entity.hashCode());
        updateLocation(c.object, node, entity);
    }

    private void updateLocation(GameMapObject o, Node node, Entity entity) {
        final var ec = entity.getComponent(ObjectElevatedComponent.class);
        final float tx = -gm.getWidth() + 2f * o.getPos().getX() + 1f;
        final float ty = gm.getHeight() - 2f * o.getPos().getY() - 1f;
        final float tz = -1f + 2f * (gm.getCursorZ() - o.getPos().getZ()) + ec.zoff;
        node.setLocalTranslation(tx, ty, tz);
    }

}
