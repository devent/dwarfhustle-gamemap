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

    public Map<Integer, ModelMapData> data = [:]

    def putAt(int rid, def v) {
        if (data[rid] == null) {
            data[rid] = new ModelMapData()
        }
        data[rid].rid = rid
        data[rid].set(v)
    }

    def getAt(int rid) {
        data[rid]
    }

    def propertyMissing(int rid) {
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

    public int rid

    public String model

    public float[] rotation = new float[3]

    def set(Map args) {
        this.model = args.model
        if (args.rotation) {
            this.rotation = args.rotation
        }
        if (args.rotationDeg) {
            args.rotationDeg.eachWithIndex { it, i -> rotation[i] = Math.toRadians(it) }
        }
        this
    }
}

