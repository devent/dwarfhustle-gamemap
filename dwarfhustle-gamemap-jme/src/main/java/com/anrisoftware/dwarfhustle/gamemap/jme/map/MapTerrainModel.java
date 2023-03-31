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

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableLongFloatMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.LongFloatMaps;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObjects;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapTile;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.texture.Texture;
import com.jme3.util.TempVars;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides the properties from the {@link MapTile} data to the
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

    private int currentZ;

    private MapBlock mb;

    /**
     * Caches map tiles by level := y := x
     */
    private MutableList<MutableList<MutableList<MapTile>>> tiles;

    @Inject
    private GameObjects<Long, GameObject> objects;

    private MutableLongObjectMap<Texture> baseColorMapTextures = LongObjectMaps.mutable.empty();

    private MutableLongObjectMap<ColorRGBA> specularColor = LongObjectMaps.mutable.empty();

    private MutableLongObjectMap<ColorRGBA> baseColor = LongObjectMaps.mutable.empty();

    private MutableLongFloatMap metallic = LongFloatMaps.mutable.empty();

    private MutableLongFloatMap glossiness = LongFloatMaps.mutable.empty();

    private MutableLongFloatMap roughness = LongFloatMaps.mutable.empty();

    private Texture unknownTextures;

    private AssetManager am;

    public void setObjects(GameObjects<Long, GameObject> objects) {
        this.objects = objects;
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

    public void setTerrain(MapTerrain terrain, MapBlock mb) {
        this.terrain = terrain;
        this.mb = mb;
        this.rootWorldBound = terrain.getWorldBound();
        this.rootWidth = mb.getWidth();
        this.rootHeight = mb.getHeight();
        this.rootDepth = mb.getDepth();
        this.tiles = Lists.mutable.empty();
        var gm = gs.get().currentMap.get();
        for (int i = 0; i < gs.get().visibleDepthLayers.get(); i++) {
            tiles.add(Lists.mutable.empty());
            for (int y = 0; y < gm.getHeight(); y++) {
                tiles.get(i).add(Lists.mutable.empty());
            }
        }
        loadMapTiles(mb);
    }

    private void loadMapTiles(MapBlock mb) {
        this.currentZ = gs.get().currentMap.get().getCursorZ();
        terrain.getLevels().forEach(this::loadMapTilesLevel);
    }

    private void loadMapTilesLevel(MapTerrainLevel level) {
        level.yxtiles.forEach(this::loadMapTilesXy);
    }

    private void loadMapTilesXy(IntObjectMap<MapTerrainTile> tiles) {
        tiles.forEach(this::loadMapTilesY);
    }

    private void loadMapTilesY(MapTerrainTile tile) {
        var pos = new GameMapPos(mb.getPos().getMapid(), tile.x, tile.y, currentZ + tile.level);
        tiles.get(tile.level).get(tile.y).add(mb.findMapTile(pos, this::retrieveMapBlock));
    }

    private MapBlock retrieveMapBlock(long id) {
        return (MapBlock) objects.get(id);
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

    @Inject
    @SneakyThrows
    public void setAssetManager(AssetManager am) {
        this.am = am;
        am.registerLocator("../dwarfhustle-assetpack.zip", ZipLocator.class);
        var engine = new GroovyScriptEngine(new URL[] { MapTerrainModel.class.getResource("/TexturesMap.groovy") });
        var binding = new Binding();
        @SuppressWarnings("unchecked")
        var res = (Map<Integer, Map<String, Object>>) engine.run("TexturesMap.groovy", binding);
        res.entrySet().parallelStream().forEach(this::loadTextureMap);
        unknownTextures = am.loadTexture("Textures/Tiles/Unknown/unknown-02.png");
    }

    @SuppressWarnings("unchecked")
    private void loadTextureMap(Map.Entry<Integer, Map<String, Object>> texentry) {
        long id = texentry.getKey();
        var value = texentry.getValue();
        loadTexture(value, id, "baseColorMap");
        if (value.containsKey("render")) {
            var render = (Map<String, Object>) value.get("render");
            putColor(specularColor, render, id, "specular", new ColorRGBA());
            putColor(baseColor, render, id, "baseColor", new ColorRGBA());
            putFloat(metallic, render, id, "metallic", 1f);
            putFloat(glossiness, render, id, "glossiness", 1f);
            putFloat(roughness, render, id, "roughness", 1f);
        }
    }

    private void putFloat(MutableLongFloatMap dest, Map<String, Object> map, long id, String name, float f) {
        var vv = (Float) map.get(name);
        if (vv != null) {
            float v = vv;
            dest.put(id, v);
        } else {
            dest.put(id, f);
        }
    }

    private void putColor(MutableLongObjectMap<ColorRGBA> dest, Map<String, Object> map, long id, String name,
            ColorRGBA d) {
        @SuppressWarnings("unchecked")
        var vv = (List<Float>) map.get(name);
        if (vv != null) {
            var v = new ColorRGBA(vv.get(0), vv.get(1), vv.get(2), vv.get(3));
            dest.put(id, v);
        } else {
            dest.put(id, d);
        }
    }

    private void loadTexture(Map<String, Object> map, long id, String name) {
        var texname = (String) map.get(name);
        Texture tex = null;
        log.trace("Loading {} texture {}:={}", name, id, texname);
        try {
            tex = am.loadTexture(texname);
        } catch (AssetNotFoundException e) {
            tex = unknownTextures;
            log.error("Error loading texture", e);
        }
        baseColorMapTextures.put(id, tex);
    }

    public synchronized void update() {
        this.currentZ = gs.get().currentMap.get().getCursorZ();
        terrain.getLevels().each(this::updateLevel);
    }

    private void updateLevel(MapTerrainLevel level) {
        level.yxtiles.each(this::updateTiles);
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
        int z = level + currentZ;
        if (z == currentZ) {
            var mt = tiles.get(level).get(y).get(x);
            long material = mt.getMaterial();
            var baseColorMap = baseColorMapTextures.get(material);
            tile.setBaseColorMap(baseColorMap);
            tile.setSpecularColor(specularColor.get(material));
            tile.setBaseColor(baseColor.get(material));
            tile.setRoughness(roughness.get(material));
            tile.setMetallic(metallic.get(material));
            tile.setGlossiness(glossiness.get(material));
        }
    }

    private void updateProperties(MapTerrainTile tile, int level, int y, int x) {
        int bits = 0;
        bits |= isTileMouse(level, y, x) ? MapTerrainTile.focused : 0;
        bits |= isTileCursor(level, y, x) ? MapTerrainTile.selected : 0;
        tile.setPropertiesBits(bits);
    }
}
