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
package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;

@FunctionalInterface
public interface NormalsPredicate {

    public static final NormalsPredicate FALSE = (chunk, index, n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z) -> {
        return false;
    };

    boolean test(MapChunk chunk, int index, float n0x, float n0y, float n0z, float n1x, float n1y, float n1z, float n2x,
            float n2y, float n2z);

}
