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
public abstract class AssetKey<T> {

    /**
     * Texture key for a material texture. The material is identified by a long
     * integer key.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class MaterialTextureKey extends AssetKey<Long> {

        public MaterialTextureKey(Long key) {
            super(key);
        }
    }

    public final T key;
}
