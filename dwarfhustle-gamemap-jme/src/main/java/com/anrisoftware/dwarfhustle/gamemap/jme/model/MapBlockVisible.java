/*
 * dwarfhustle-gamemap-jme - Game map.
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
