package com.anrisoftware.dwarfhustle.gamemap.jme.map;

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

    private int tileCursorZ;

    private int tileCursorY;

    private int tileCursorX;

    private BoundingBox rootWorldBound = new BoundingBox(Vector3f.ZERO, 64f, 64f, 64f);

    private float rootWidth;

    private float rootHeight;

    private float rootDepth;

    public void setTileCursor(int z, int y, int x) {
        this.tileCursorZ = z;
        this.tileCursorY = y;
        this.tileCursorX = x;
    }

    public boolean isTileCurser(int z, int y, int x) {
        return tileCursorZ == z && tileCursorY == y && tileCursorX == x;
    }

    public void setTerrain(MapTerrain terrain, MapBlock mb) {
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

}
