package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import javax.inject.Inject;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObjects;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapTile;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.texture.Texture;
import com.jme3.util.TempVars;

/**
 * Provides the properties from the {@link MapTile} data to the
 * {@link MapTerrainTile} view.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
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

    private MutableLongObjectMap<Texture> materialsTextures = LongObjectMaps.mutable.empty();

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
    public void setAssetManager(AssetManager am) {
        materialsTextures.put(882, am.loadTexture("Textures/tiles/gas/oxygen/oxygen-01.png"));
        materialsTextures.put(882, am.loadTexture("Textures/tiles/granite/granite-01.png"));
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
            var tex = materialsTextures.get(mt.getMaterial());
            tile.materialTexture = tex;
        }
    }

    private void updateProperties(MapTerrainTile tile, int level, int y, int x) {
        int bits = 0;
        bits |= isTileMouse(level, y, x) ? MapTerrainTile.focused : 0;
        bits |= isTileCursor(level, y, x) ? MapTerrainTile.selected : 0;
        tile.propertiesBits.replace(bits);
    }
}
