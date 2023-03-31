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

import javax.inject.Inject;

import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;

import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrainTile.MapTerrainTileFactory;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.google.inject.assistedinject.Assisted;
import com.jme3.scene.Node;

import lombok.SneakyThrows;
import lombok.ToString;

/**
 * Game map terrain level consiting of width x height map tiles.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString(onlyExplicitlyIncluded = true)
public class MapTerrainLevel {

    public interface MapTerrainLevelFactory {
        MapTerrainLevel create(MapTerrainModel model, GameMap gm, int level);
    }

    public final GameMap gm;

    public final int level;

    @ToString.Include
    public final Node node;

    public final IntObjectMap<IntObjectMap<MapTerrainTile>> yxtiles;

    @Inject
    @SneakyThrows
    public MapTerrainLevel(@Assisted MapTerrainModel model, @Assisted GameMap gm, @Assisted int level,
            MapTerrainTileFactory tileFactory) {
        this.gm = gm;
        this.level = level;
        this.node = new Node(MapTerrainLevel.class.getSimpleName() + "-" + level);
        MutableIntObjectMap<IntObjectMap<MapTerrainTile>> yxtiles = IntObjectMaps.mutable.empty();
        this.yxtiles = yxtiles;
        int h = gm.getHeight();
        int w = gm.getWidth();
        float h2 = -h / 2f;
        float w2 = -w / 2f;
        for (int y = 0; y < h; y++) {
            MutableIntObjectMap<MapTerrainTile> xtiles = IntObjectMaps.mutable.empty();
            yxtiles.put(y, xtiles);
            for (int x = 0; x < w; x++) {
                var tile = tileFactory.create(level, y, x);
                xtiles.put(x, tile);
                float tx = w2 + x * 2f + 0.5f;
                float ty = h2 + y * 2f + 0.5f;
                tile.node.setLocalTranslation(tx, ty, 0f);
                node.attachChild(tile.node);
            }
        }
    }
}
