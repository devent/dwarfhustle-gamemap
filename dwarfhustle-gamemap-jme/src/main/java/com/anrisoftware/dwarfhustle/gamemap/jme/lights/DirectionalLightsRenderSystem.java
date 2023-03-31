/*
 * Dwarf Hustle Game Map - Game map.
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
import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.FastMath;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;

import lombok.extern.slf4j.Slf4j;

/**
 * Creates and updates the directional light sources.
 *
 * @see DirectionalLightComponent
 * @author Erwin Müller
 */
@Slf4j
public class DirectionalLightsRenderSystem extends IntervalIteratingSystem {

    private final EntityListener entityLister;

    private final Map<Entity, DirectionalLight> lights;

    private final Map<Entity, FilterPostProcessor> postPrecessors;

    private final Map<Entity, DirectionalLightShadowFilter> filters;

    private Node sceneNode;

    @Inject
    private AssetManager assetManager;

    @Inject
    private ViewPort viewPort;

    @Inject
    public DirectionalLightsRenderSystem(GameSettingsProvider gs) {
        super(Family.all(DirectionalLightComponent.class).get(), gs.get().timeUpdateInterval.get());
        this.lights = Maps.mutable.empty();
        this.postPrecessors = Maps.mutable.empty();
        this.filters = Maps.mutable.empty();
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
        var c = DirectionalLightComponent.m.get(entity);
        if (lights.containsKey(entity)) {
            var light = lights.get(entity);
            light.setColor(c.color);
            light.setDirection(c.d);
            light.setEnabled(c.enabled);
            light.setEnabled(true);
            var filter = filters.get(entity);
            if (filter != null) {
                filter.setEnabled(c.shadow);
            }
        }
    }

    private void addLight(Entity entity) {
        log.debug("Add directional light entity {}", entity);
        var c = DirectionalLightComponent.m.get(entity);
        var light = new DirectionalLight(c.d, c.color);
        light.setEnabled(c.enabled);
        if (c.shadow) {
            createShadowFilter(entity, c, light);
        }
        sceneNode.addLight(light);
        lights.put(entity, light);
    }

    private void createShadowFilter(Entity entity, DirectionalLightComponent c, DirectionalLight light) {
        final int SHADOWMAP_SIZE = (int) FastMath.pow(2f, 10f);
        int splits = 4;
        var dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, splits);
        dlsf.setLight(light);
        dlsf.setLambda(0.55f);
        dlsf.setShadowIntensity(0.8f);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        dlsf.setEnabled(true);
        var fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        viewPort.addProcessor(fpp);
        filters.put(entity, dlsf);
        postPrecessors.put(entity, fpp);
    }

    private void removeLight(Entity entity) {
        log.debug("Remove directional light entity {}", entity);
        var light = lights.get(entity);
        sceneNode.removeLight(light);
        lights.remove(entity);
        var c = DirectionalLightComponent.m.get(entity);
        if (c.shadow) {
            var fpp = postPrecessors.get(entity);
            viewPort.removeProcessor(fpp);
            filters.remove(entity);
            postPrecessors.remove(entity);
        }
    }

}
