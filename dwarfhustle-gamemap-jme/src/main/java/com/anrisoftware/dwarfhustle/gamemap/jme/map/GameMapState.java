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
package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrain.MapTerrainFactory;
import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrainModel.MapTerrainModelFactory;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.badlogic.ashley.core.Engine;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;

/**
 * Constructs the game map from {@link MapChunk} and {@link MapBlock}.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class GameMapState extends BaseAppState {

    @Inject
    private Engine engine;

    @Inject
    private MapRenderSystem mapRenderSystem;

    @Inject
    private MapTerrainModelFactory modelFactory;

    @Inject
    private MapTerrainFactory terrainFactory;

    @Inject
    @Named("rootNode")
    private Node rootNode;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    private Application app;

    private MapTerrainModel model;

    private MapTerrain terrain;

    public MapRenderSystem getMapRenderSystem() {
        return mapRenderSystem;
    }

    public MapTerrainModel getModel() {
        return model;
    }

    public MapTerrain getTerrain() {
        return terrain;
    }

    @Override
    protected void initialize(Application app) {
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        // engine.addSystem(mapRenderSystem);
    }

    @Override
    protected void onDisable() {
        // engine.removeSystem(mapRenderSystem);
    }

    public void createMapBlockBox(GameMap gm, MapChunk mb) {
    }

}
