package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import org.eclipse.collections.api.map.primitive.IntObjectMap;

import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapTile;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
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
        this.rootWorldBound = terrain.getWorldBound();
        this.rootWidth = mb.getWidth();
        this.rootHeight = mb.getHeight();
        this.rootDepth = mb.getDepth();
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
        int bits = 0;
        bits |= isTileMouse(level, y, x) ? MapTerrainTile.focused : 0;
        bits |= isTileCursor(level, y, x) ? MapTerrainTile.selected : 0;
        tile.propertiesBits.replace(bits);
    }
}
