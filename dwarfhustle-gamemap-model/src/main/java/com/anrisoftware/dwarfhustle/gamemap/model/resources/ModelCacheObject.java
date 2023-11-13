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

import com.jme3.scene.Spatial;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Model with additional material parameters.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class ModelCacheObject extends AssetCacheObject {

    private static final long serialVersionUID = 1L;

    public static final String OBJECT_TYPE = ModelCacheObject.class.getSimpleName();

    /**
     * {@link Spatial} model.
     */
    public Spatial model;

    /**
     * Resource ID.
     */
    public long rid;

    public ModelCacheObject(byte[] idbuf) {
        super(idbuf);
    }

    public ModelCacheObject(long id) {
        super(id);
    }

    @Override
    public String getObjectType() {
        return ModelCacheObject.OBJECT_TYPE;
    }

}
