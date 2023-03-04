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

import javax.inject.Inject;
import javax.inject.Named;

import com.badlogic.ashley.core.Engine;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

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

    @Named("rootNode")
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
        var box = new Box(getApplication().getAssetManager(), new Vector3f(0, 0, 0));
        sceneNode.attachChild(box.node);
    }

    @Override
    protected void onDisable() {
        log.debug("onDisable");
        engine.removeSystem(ambientLightsSystem);
        engine.removeSystem(dirLightsRenderSystem);
    }

}
