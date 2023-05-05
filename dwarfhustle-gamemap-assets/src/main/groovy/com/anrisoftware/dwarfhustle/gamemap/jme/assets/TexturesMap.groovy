package com.anrisoftware.dwarfhustle.gamemap.jme.assets

import groovy.transform.ToString;

/**
 * Contains the textures as {@code <MATERIAL-TYPE> := [<IMAGE>, <FRAMES>]} map.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
class TexturesMap {

    public Map<String, TexturesMapData> data = [:]

    def propertyMissing(String name) {
        if (data[name] == null) {
            data[name] = new TexturesMapData()
        }
        data[name]
    }
}

/**
 * Contains the textures frames as {@code <RID> := [<FRAME>]} map.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
class TexturesMapData {

    public String image

    public Map<Long, TexturesMapFramesData> frames = [:]

    def frames(def args) {
        long rid = args.rid
        def data = new TexturesMapFramesData()
        data.image = image
        data.set(args)
        frames[rid] = data
        this
    }
}

/**
 * Contains the texture with additional rendering properties.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
class TexturesMapFramesData {

    public String image

    public int x, y, w, h

    public float[] color = [1f, 1f, 1f, 1f]

    public float[] specular = [1f, 1f, 1f, 1f]

    public boolean transparent = false

    public float glossiness = 0f

    public float metallic = 0f

    public float roughness = 1f

    def set(Map args) {
        x = args.getOrDefault("x", 0)
        y = args.getOrDefault("y", 0)
        w = args.getOrDefault("w", 0)
        h = args.getOrDefault("h", 0)
        color = args.getOrDefault("color", color)
        specular = args.getOrDefault("specular", specular)
        transparent = args.getOrDefault("transparent", transparent)
        glossiness = args.getOrDefault("glossiness", glossiness)
        metallic = args.getOrDefault("metallic", metallic)
        roughness = args.getOrDefault("roughness", roughness)
        this
    }
}

