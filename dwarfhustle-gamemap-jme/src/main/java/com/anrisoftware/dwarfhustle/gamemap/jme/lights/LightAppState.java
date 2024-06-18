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
package com.anrisoftware.dwarfhustle.gamemap.jme.lights;

import com.badlogic.ashley.core.Engine;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Adds the {@link AmbientLightsRenderSystem} and
 * {@link DirectionalLightsRenderSystem}.
 *
 * @author Erwin Müller
 */
@Slf4j
public class LightAppState extends BaseAppState {

    @Inject
    private Engine engine;

    @Inject
    private AmbientLightsRenderSystem ambientLightsSystem;

    @Inject
    private DirectionalLightsRenderSystem dirLightsRenderSystem;

    @Named("sceneNode")
    @Inject
    private Node sceneNode;

    @Inject
    public LightAppState() {
        super(LightAppState.class.getSimpleName());
    }

    @Override
    @SneakyThrows
    protected void initialize(Application app) {
        log.debug("initialize");
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        log.debug("onEnable");
        engine.addSystem(ambientLightsSystem);
        engine.addSystem(dirLightsRenderSystem);
        var box1 = new Box(getApplication().getAssetManager(), new Vector3f(0f, 0f, 0f));
        sceneNode.attachChild(box1.node);
        var box2 = new Box(getApplication().getAssetManager(), new Vector3f(1f, 1f, 1f));
        sceneNode.attachChild(box2.node);
        var box3 = new Box(getApplication().getAssetManager(), new Vector3f(2f, 2f, 2f));
        sceneNode.attachChild(box3.node);
        var box4 = new Box(getApplication().getAssetManager(), new Vector3f(-1f, -1f, -1f));
        sceneNode.attachChild(box4.node);
    }

    @Override
    protected void onDisable() {
        log.debug("onDisable");
        engine.removeSystem(ambientLightsSystem);
        engine.removeSystem(dirLightsRenderSystem);
    }

}
