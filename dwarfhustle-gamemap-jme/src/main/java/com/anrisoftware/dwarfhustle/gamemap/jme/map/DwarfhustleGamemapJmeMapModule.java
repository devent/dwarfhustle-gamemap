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

import com.anrisoftware.dwarfhustle.gamemap.jme.map.GameMapActor.GameMapActorFactory;
import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrain.MapTerrainFactory;
import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrainLevel.MapTerrainLevelFactory;
import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrainModel.MapTerrainModelFactory;
import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrainTile.MapTerrainTileFactory;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class DwarfhustleGamemapJmeMapModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(GameMapActor.class, GameMapActor.class)
				.build(GameMapActorFactory.class));
        install(new FactoryModuleBuilder().implement(MapTerrain.class, MapTerrain.class)
                .build(MapTerrainFactory.class));
        install(new FactoryModuleBuilder().implement(MapTerrainLevel.class, MapTerrainLevel.class)
                .build(MapTerrainLevelFactory.class));
        install(new FactoryModuleBuilder().implement(MapTerrainTile.class, MapTerrainTile.class)
                .build(MapTerrainTileFactory.class));
        install(new FactoryModuleBuilder().implement(MapTerrainModel.class, MapTerrainModel.class)
                .build(MapTerrainModelFactory.class));
	}
}
