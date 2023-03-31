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
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.factory.Stacks;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;

import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrainLevel.MapTerrainLevelFactory;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.google.inject.assistedinject.Assisted;
import com.jme3.bounding.BoundingBox;
import com.jme3.scene.Node;

/**
 * Game map terrain having multiple z levels.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class MapTerrain {

    public interface MapTerrainFactory {
        MapTerrain create(MapTerrainModel model, GameMap gm);
    }

    public Node node;

    private MutableStack<MapTerrainLevel> currentLevels;

    private MutableStack<MapTerrainLevel> oldLevels;

    @Inject
    private MapTerrainLevelFactory levelFactory;

    private GameMap gm;

    private final MutableIntObjectMap<MapTerrainLevel> terrainLevels = IntObjectMaps.mutable.empty();

    @Inject
    @Assisted
    private MapTerrainModel model;

    @Inject
    public MapTerrain(@Assisted GameMap gm) {
        this.gm = gm;
        this.node = new Node(MapTerrain.class.getSimpleName());
        this.currentLevels = Stacks.mutable.empty();
        this.oldLevels = Stacks.mutable.empty();
    }

    public IntObjectMap<MapTerrainLevel> getLevels() {
        return terrainLevels;
    }

    public void setLevels(int levels) {
        var size = currentLevels.size();
        if (size < levels) {
            fillLevels(levels - size, size);
        } else if (size > levels) {
            removeLevels(size - levels, size);
        }
    }

    private void fillLevels(int levels, int size) {
        for (var i = 0; i < levels; i++) {
            MapTerrainLevel tl;
            if (!oldLevels.isEmpty()) {
                tl = oldLevels.pop();
            } else {
                tl = levelFactory.create(model, gm, size + i);
                tl.node.setLocalTranslation(0f, 0f, -1f * (size + i));
            }
            currentLevels.push(tl);
            node.attachChild(tl.node);
            terrainLevels.put(tl.level, tl);
        }
    }

    private void removeLevels(int levels, int size) {
        for (var i = 0; i < levels; i++) {
            var tl = currentLevels.pop();
            node.detachChild(tl.node);
            oldLevels.push(tl);
            terrainLevels.remove(tl.level);
        }
    }

    public BoundingBox getWorldBound() {
        node.updateModelBound();
        return (BoundingBox) node.getWorldBound();
    }

    public MapTerrainLevel getLevel(int level) {
        return terrainLevels.get(level);
    }
}
