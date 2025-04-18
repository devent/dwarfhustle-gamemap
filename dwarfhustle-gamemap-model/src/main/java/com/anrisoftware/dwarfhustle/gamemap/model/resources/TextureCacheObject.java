/*
 * dwarfhustle-gamemap-model - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.model.resources;

import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Texture with additional material parameters.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class TextureCacheObject extends AssetCacheObject {

    public static final int OBJECT_TYPE = TextureCacheObject.class.getSimpleName().hashCode();

    public static long ridTypeToId(int rid, int type) {
        return (((long) rid) << 32) | (type & 0xffffffffL);
    }

    public static long idToRid(long id) {
        return (int) (id >> 32);
    }

    public static long idToType(long id) {
        return (int) id;
    }

    public Texture tex;

    /**
     * Resource ID.
     */
    public int rid;

    /**
     * Sub-type.
     */
    public int type;

    public float x;

    public float y;

    public float w;

    public float h;

    public ColorRGBA specular;

    public ColorRGBA baseColor;

    public float metallic;

    public float glossiness;

    public float roughness;

    public boolean transparent;

    public TextureCacheObject(int rid, int type) {
        super(ridTypeToId(rid, type));
        this.rid = rid;
        this.type = type;
    }

    @Override
    public int getObjectType() {
        return TextureCacheObject.OBJECT_TYPE;
    }

}
