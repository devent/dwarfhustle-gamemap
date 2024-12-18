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

import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;

import lombok.RequiredArgsConstructor;

/**
 * Mapping to {@link VertexBuffer.Type}.
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
public enum TexCoordinate {

    TexCoord1(VertexBuffer.Type.TexCoord),

    TexCoord2(VertexBuffer.Type.TexCoord2),

    TexCoord3(VertexBuffer.Type.TexCoord3),

    TexCoord4(VertexBuffer.Type.TexCoord4),

    TexCoord5(VertexBuffer.Type.TexCoord5),

    TexCoord6(VertexBuffer.Type.TexCoord6),

    TexCoord7(VertexBuffer.Type.TexCoord7),

    TexCoord8(VertexBuffer.Type.TexCoord8);

    public final VertexBuffer.Type type;

    public static Type getType(int i) {
        return values()[i].type;
    }
}
