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

import com.anrisoftware.dwarfhustle.gamemap.jme.objectsrender.ObjectsRenderSystem.ObjectsRenderSystemFactory;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.badlogic.ashley.core.Engine;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;

import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Shows map objects.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class ObjectsState extends BaseAppState {

    @Inject
    @Named("rootNode")
    private Node rootNode;

    @Inject
    @Named("sceneNode")
    private Node sceneNode;

    @Inject
    private ObjectsRenderSystemFactory objectsRenderSystemFactory;

    @Inject
    private Engine engine;

    private ObjectsRenderSystem objectsRenderSystem;

    public void setup(ObjectsGetter materials, ObjectsGetter models) {
        this.objectsRenderSystem = objectsRenderSystemFactory.create(materials, models);
        objectsRenderSystem.setSceneNode(sceneNode);
        engine.addSystem(objectsRenderSystem);
    }

    @Override
    protected void initialize(Application app) {
    }

    @Override
    protected void cleanup(Application app) {
        if (objectsRenderSystem != null) {
            engine.removeSystem(objectsRenderSystem);
        }
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    public void setGameMap(GameMap gm) {
        objectsRenderSystem.setGameMap(gm);
    }

}
