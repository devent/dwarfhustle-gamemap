/*
 * dwarfhustle-gamemap-assets - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.jme.assets

import groovy.transform.ToString;

/**
 * Contains the models as {@code <OBJECT-TYPE> := [<PATH>]} map.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
class ModelMap {

    public Map<Long, ModelMapData> data = [:]

    def putAt(def rid, def v) {
        if (data[rid] == null) {
            data[rid] = new ModelMapData()
        }
        data[rid].rid = rid
        data[rid].set(v)
    }

    def getAt(def rid) {
        data[rid]
    }

    def propertyMissing(long rid) {
        if (data[rid] == null) {
            data[rid] = new ModelMapData()
        }
        data[rid].rid = rid
        data[rid]
    }
}

/**
 * Contains the model with additional rendering properties.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
class ModelMapData {

    public long rid

    public String model

    def set(Map args) {
        this.model = args.model
        this
    }
}

