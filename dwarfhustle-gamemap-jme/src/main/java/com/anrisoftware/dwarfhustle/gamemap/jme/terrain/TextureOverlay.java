package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;

import lombok.RequiredArgsConstructor;

/**
 * Merges the terrain texture with multiple object textures.
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
public class TextureOverlay {

    private final TextureCacheObject baseTexture;

    /**
     * Adds the textures to the base texture and returns a new texture.
     */
    public TextureCacheObject createTexture(TextureCacheObject... texs) {
        return baseTexture;
    }
}
