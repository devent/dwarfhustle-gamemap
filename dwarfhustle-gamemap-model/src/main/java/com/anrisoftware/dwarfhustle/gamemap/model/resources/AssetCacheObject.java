/*
 * Dwarf Hustle Game Map - Game map.
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

import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Game asset cached object.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class AssetCacheObject extends GameObject {

    private static final long serialVersionUID = 1L;

    public static final String OBJECT_TYPE = AssetCacheObject.class.getSimpleName();

    public AssetCacheObject(byte[] idbuf) {
        super(idbuf);
    }

    public AssetCacheObject(long id) {
        super(id);
    }

    @Override
    public String getObjectType() {
        return AssetCacheObject.OBJECT_TYPE;
    }

}
