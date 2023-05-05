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

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Abstract texture key.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class AssetCacheKey<T> {

    /**
     * Cache key for a material texture. The material is identified by a long
     * integer key.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class MaterialCacheKey extends AssetCacheKey<Long> {

        public MaterialCacheKey(long key) {
            super(key);
        }

        public long getKey() {
            return key;
        }
    }

    /**
     * Cache key for a model. The model is identified by a long integer key.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class ModelCacheKey extends AssetCacheKey<Long> {

        public ModelCacheKey(long key) {
            super(key);
        }

        public long getKey() {
            return key;
        }
    }

    public final T key;
}
