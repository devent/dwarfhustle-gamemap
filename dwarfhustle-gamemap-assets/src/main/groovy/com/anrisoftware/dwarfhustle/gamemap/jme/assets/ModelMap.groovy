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

