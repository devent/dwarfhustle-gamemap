package com.anrisoftware.dwarfhustle.gamemap.jme.model;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;

/**
 * Functional interface to test that the map block if visible.
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@FunctionalInterface
public interface MapBlockVisible {

    /**
     * Checks if the map block is visible.
     * 
     * @param gm    the {@link GameMap}.
     * @param chunk the {@link MapChunk}.
     * @param i     the map block index.
     * @param x     the map block X coordinate.
     * @param y     the map block Y coordinate.
     * @param z     the map block Z coordinate.
     */
    boolean isVisible(GameMap gm, MapChunk chunk, int i, int x, int y, int z);
}
