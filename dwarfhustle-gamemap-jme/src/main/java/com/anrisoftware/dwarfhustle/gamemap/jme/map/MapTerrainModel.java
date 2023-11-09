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

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.impl.factory.Lists;

import com.anrisoftware.dwarfhustle.gamemap.jme.actors.GameAssets;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetCacheKey.MaterialCacheKey;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObjects;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.util.TempVars;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides the properties from the {@link MapBlock} data to the
 * {@link MapTerrainTile} view.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class MapTerrainModel {

    public interface MapTerrainModelFactory {
        MapTerrainModel create();
    }

    @Inject
    private GameSettingsProvider gs;

    private int tileCursorLevel;

    private int tileCursorY;

    private int tileCursorX;

    private BoundingBox rootWorldBound = new BoundingBox(Vector3f.ZERO, 64f, 64f, 64f);

    private float rootWidth;

    private float rootHeight;

    private float rootDepth;

    private int tileMouseLevel;

    private int tileMouseY;

    private int tileMouseX;

    private MapTerrain terrain;

    private int currentZ = -1;

    private MapChunk mb;

    /**
     * Caches map tiles by level := y := x
     */
    private MutableList<MutableList<MutableList<MapBlock>>> tiles;

    @Inject
    private GameObjects gameObjects;

    @Inject
    private GameAssets gameAssets;

    private GameMap gm;

    public void setGameObjects(GameObjects objects) {
        this.gameObjects = objects;
    }

    public void setGameAssets(GameAssets assets) {
        this.gameAssets = assets;
    }

    public void setTileCursor(int level, int y, int x) {
        this.tileCursorLevel = level;
        this.tileCursorY = y;
        this.tileCursorX = x;
    }

    public boolean isTileCursor(int level, int y, int x) {
        return tileCursorLevel == level && tileCursorY == y && tileCursorX == x;
    }

    public void setTileMouse(int level, int y, int x) {
        this.tileMouseLevel = level;
        this.tileMouseY = y;
        this.tileMouseX = x;
    }

    public boolean isTileMouse(int level, int y, int x) {
        return tileMouseLevel == level && tileMouseY == y && tileMouseX == x;
    }

    public void setTerrain(MapTerrain terrain, MapChunk mb) {
        this.terrain = terrain;
        this.mb = mb;
        this.rootWorldBound = terrain.getWorldBound();
        this.rootWidth = mb.getWidth();
        this.rootHeight = mb.getHeight();
        this.rootDepth = mb.getDepth();
        int visible = gs.get().visibleDepthLayers.get();
        this.tiles = Lists.mutable.ofInitialCapacity(visible);
        this.gm = gs.get().currentMap.get();
        for (int i = 0; i < visible; i++) {
            tiles.add(Lists.mutable.ofInitialCapacity(gm.getHeight()));
            for (int y = 0; y < gm.getHeight(); y++) {
                tiles.get(i).add(Lists.mutable.ofInitialCapacity(gm.getWidth()));
                for (int x = 0; x < gm.getWidth(); x++) {
                    tiles.get(i).get(y).add(null);
                }
            }
        }
    }

    public float getWidth() {
        return rootWidth;
    }

    public float getHeight() {
        return rootHeight;
    }

    public float getDepth() {
        return rootDepth;
    }

    /**
     * Returns the top right and bottom left of the noise quad in screen
     * coordinates.
     */
    public void getScreenCoordinatesMap(Camera camera, Vector3f topRight, Vector3f bottomLeft) {
        var temp = TempVars.get();
        try {
            var bb = rootWorldBound;
            var btr = bb.getMax(temp.vect1);
            var bbl = bb.getMin(temp.vect2);
            camera.getScreenCoordinates(btr, topRight);
            camera.getScreenCoordinates(bbl, bottomLeft);
        } finally {
            temp.release();
        }
    }

    public synchronized void update() {
        int z = gs.get().currentMap.get().getCursorZ();
        if (currentZ != z) {
            this.currentZ = z;
            loadMapTiles(mb);
        }
        terrain.getLevels().each(this::updateLevel);
    }

    private void updateLevel(MapTerrainLevel level) {
        if (level.level + currentZ >= gm.getDepth()) {
            level.setPropertyHidden(true);
            level.update();
        } else {
            level.setPropertyHidden(false);
            level.update();
            level.yxtiles.each(this::updateTiles);
        }
    }

    private void updateTiles(IntObjectMap<MapTerrainTile> tiles) {
        tiles.each(this::updateTile);
    }

    private void updateTile(MapTerrainTile tile) {
        int level = tile.level;
        int y = tile.y;
        int x = tile.x;
        updateProperties(tile, level, y, x);
        updateTexture(tile, level, y, x);
    }

    private void updateTexture(MapTerrainTile tile, int level, int y, int x) {
        var mt = tiles.get(level).get(y).get(x);
        long material = mt.getMaterial();
        var tex = (TextureCacheObject) gameAssets.get(new MaterialCacheKey(material));
        tile.setBaseColorMap(tex.tex);
        tile.setSpecularColor(tex.specular);
        tile.setBaseColor(tex.baseColor);
        tile.setRoughness(tex.roughness);
        tile.setMetallic(tex.metallic);
        tile.setGlossiness(tex.glossiness);
    }

    private void updateProperties(MapTerrainTile tile, int level, int y, int x) {
        int bits = 0;
        bits |= isTileMouse(level, y, x) ? MapTerrainTile.focused : 0;
        bits |= isTileCursor(level, y, x) ? MapTerrainTile.selected : 0;
        tile.setPropertiesBits(bits);
    }

    private void loadMapTiles(MapChunk mb) {
        terrain.getLevels().forEach(this::loadMapTilesLevel);
    }

    private void loadMapTilesLevel(MapTerrainLevel level) {
        level.yxtiles.forEach(this::loadMapTilesXy);
    }

    private void loadMapTilesXy(IntObjectMap<MapTerrainTile> tiles) {
        tiles.forEach(this::loadMapTilesY);
    }

    private void loadMapTilesY(MapTerrainTile tile) {
        var pos = new GameBlockPos(mb.getPos().getMapid(), tile.x, tile.y, currentZ + tile.level);
        tiles.get(tile.level).get(tile.y).set(tile.x, mb.findMapBlock(pos, this::retrieveMapBlock));
    }

    private MapChunk retrieveMapBlock(long id) {
        return (MapChunk) gameObjects.get(id);
    }

}
