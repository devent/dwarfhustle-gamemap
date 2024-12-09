package com.anrisoftware.dwarfhustle.gamemap.jme.model;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;

/**
 * Functional interface to put the visible {@link MapBlock} block in a
 * collection.
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@FunctionalInterface
public interface PutMapBlock {

    /**
     * Puts the visible {@link MapBlock} block in a collection.
     * 
     * @param gm    the {@link GameMap}.
     * @param chunk the {@link MapChunk}.
     * @param i     the map block index.
     */
    void putBlock(GameMap gm, MapChunk chunk, int i);
}
