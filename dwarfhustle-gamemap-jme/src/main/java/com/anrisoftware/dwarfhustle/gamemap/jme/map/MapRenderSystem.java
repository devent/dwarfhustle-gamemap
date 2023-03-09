/*
 * dwarfhustle-gamemap-gui-javafx - GUI in Javafx.
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

import javax.inject.Inject;

import org.eclipse.collections.api.map.primitive.IntObjectMap;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.jme3.app.Application;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
@Slf4j
public class MapRenderSystem extends IntervalIteratingSystem {

    @Inject
    private GameSettingsProvider gs;

    @Inject
    private Application app;

    private MapTerrain terrain;

    private MapTerrainModel model;

    @Inject
    public MapRenderSystem() {
        super(Family.all(MapCursorComponent.class).get(), 0.33f);
    }

    public void setTerrain(MapTerrain terrain) {
        this.terrain = terrain;
    }

    public void setModel(MapTerrainModel model) {
        this.model = model;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(getFamily(), new EntityListener() {

            @Override
            public void entityRemoved(Entity entity) {
            }

            @Override
            public void entityAdded(Entity entity) {
            }
        });
    }

    @Override
    protected void processEntity(Entity entity) {
        var c = MapCursorComponent.m.get(entity);
        model.setTileCursor(c.z, c.y, c.x);
        terrain.getLevels().each(this::updateLevel);
    }

    private void updateLevel(MapTerrainLevel level) {
        level.yxtiles.each(this::updateTiles);
    }

    private void updateTiles(IntObjectMap<MapTerrainTile> tiles) {
        tiles.each(this::updateTile);
    }

    private void updateTile(MapTerrainTile tile) {
        tile.update();
    }
}
