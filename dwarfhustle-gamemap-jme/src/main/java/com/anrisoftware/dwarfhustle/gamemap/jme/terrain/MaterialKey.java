package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;

public class MaterialKey {

    public final int hash;

    public final TextureCacheObject tex;

    public final TextureCacheObject object;

    public final TextureCacheObject emissive;

    public final Material m;

    public final boolean transparent;

    public MaterialKey(AssetManager assets, TextureCacheObject tex, boolean transparent) {
        this(assets, tex, null, null, transparent);
    }

    public MaterialKey(AssetManager assets, TextureCacheObject tex, TextureCacheObject object,
            TextureCacheObject emissive, boolean transparent) {
        this.hash = calcHash(tex.rid, object != null ? object.rid : null, emissive != null ? emissive.rid : null,
                transparent);
        this.tex = tex;
        this.emissive = emissive;
        this.object = object;
        this.m = new Material(assets, "MatDefs/PBRLightingBlock.j3md");
        this.transparent = transparent;
        m.setTexture("BaseColorMap", tex.tex);
        m.setColor("BaseColor", tex.baseColor);
        m.setFloat("Metallic", tex.metallic);
        m.setFloat("Roughness", tex.roughness);
        m.setBoolean("UseVertexColor", false);
        m.getAdditionalRenderState().setBlendMode(tex.transparent ? BlendMode.Alpha : BlendMode.Off);
        if (emissive != null) {
            m.setFloat("SelectedPower", 3.0f);
            m.setFloat("SelectedIntensity", 2.0f);
            // m.setColor("Selected", ColorRGBA.Green);
            m.setTexture("SelectedMap", emissive.tex);
        }
    }

    public static int calcHash(long texrid, Long object, Long emissive, boolean transparent) {
        final int PRIME = 59;
        int result = 1;
        final long temp1 = Long.hashCode(texrid);
        result = (result * PRIME) + (int) (temp1 ^ (temp1 >>> 32));
        if (object != null) {
            final long temp2 = Long.hashCode(object);
            result = (result * PRIME) + (int) (temp2 ^ (temp2 >>> 32));
        }
        if (emissive != null) {
            final long temp2 = Long.hashCode(emissive);
            result = (result * PRIME) + (int) (temp2 ^ (temp2 >>> 32));
        }
        final long temp3 = Boolean.hashCode(transparent);
        result = (result * PRIME) + (int) (temp3 ^ (temp3 >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MaterialKey other)) {
            return false;
        }
        if (other.hash != this.hash) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public boolean isMaterial(long id) {
        return tex.id == id;
    }

}
