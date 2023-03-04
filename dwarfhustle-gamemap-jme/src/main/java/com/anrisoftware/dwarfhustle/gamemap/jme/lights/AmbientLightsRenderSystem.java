/**
 * Dwarf Hustle :: Terrain :: JMonkeyEngine - Dwarf Hustle Terrain using the JMonkeyEngine engine.
 * Copyright © 2021 Erwin Müller (erwin.mueller@anrisoftware.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.anrisoftware.dwarfhustle.gamemap.jme.lights;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.collections.impl.factory.Maps;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.jme3.light.AmbientLight;
import com.jme3.scene.Node;

import lombok.extern.slf4j.Slf4j;

/**
 * Creates and updates the ambient light sources.
 *
 * @see AmbientLightComponent
 * @author Erwin Müller
 */
@Slf4j
public class AmbientLightsRenderSystem extends IntervalIteratingSystem {

    private final EntityListener entityLister;

    private final Map<Entity, AmbientLight> lights;

    private Node sceneNode;

    @Inject
    public AmbientLightsRenderSystem(GameSettingsProvider gs) {
        super(Family.all(AmbientLightComponent.class).get(), gs.get().timeUpdateInterval.get());
        this.lights = Maps.mutable.empty();
        this.entityLister = new EntityListener() {

            @Override
            public void entityRemoved(Entity entity) {
                removeLight(entity);
            }

            @Override
            public void entityAdded(Entity entity) {
                addLight(entity);
            }
        };
    }

    @Inject
    public void setSceneNode(@Named("rootNode") Node sceneNode) {
        this.sceneNode = sceneNode;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(getFamily(), 100, entityLister);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntityListener(entityLister);
    }

    @Override
    public void processEntity(Entity entity) {
        var c = AmbientLightComponent.m.get(entity);
        AmbientLight light = lights.get(entity);
        light.setColor(c.color);
    }

    private void addLight(Entity entity) {
        log.debug("Add ambient light entity {}", entity);
        var c = AmbientLightComponent.m.get(entity);
        var light = new AmbientLight(c.color);
        sceneNode.addLight(light);
        lights.put(entity, light);
    }

    private void removeLight(Entity entity) {
        log.debug("Remove ambient light entity {}", entity);
        var light = lights.get(entity);
        sceneNode.removeLight(light);
    }

}
