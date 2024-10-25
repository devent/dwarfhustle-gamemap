package com.anrisoftware.dwarfhustle.gamemap.jme.model;

import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;

@FunctionalInterface
public interface MapBlockVisible {

    /**
     * Checks if the map block is visible.
     * 
     * @param chunk the {@link MapChunk}.
     * @param i     the map block index.
     * @param x     the map block X coordinate.
     * @param y     the map block Y coordinate.
     * @param z     the map block Z coordinate.
     */
    boolean isVisible(MapChunk chunk, int i, int x, int y, int z);
}
