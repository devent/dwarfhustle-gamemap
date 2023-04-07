package com.anrisoftware.dwarfhustle.gamemap.model.resources;

import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Texture with additional material parameters.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class TextureObject extends AssetObject {

    private static final long serialVersionUID = 1L;

    public static final String OBJECT_TYPE = TextureObject.class.getSimpleName();

    public Texture tex;

    public ColorRGBA specular;

    public ColorRGBA baseColor;

    public float metallic;

    public float glossiness;

    public float roughness;

    public TextureObject(byte[] idbuf) {
        super(idbuf);
    }

    public TextureObject(long id) {
        super(id);
    }

    @Override
    public String getObjectType() {
        return TextureObject.OBJECT_TYPE;
    }

}
