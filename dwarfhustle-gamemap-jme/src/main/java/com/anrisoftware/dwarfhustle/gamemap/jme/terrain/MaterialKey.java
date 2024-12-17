package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static java.lang.String.format;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;

public class MaterialKey {

    public final int hash;

    public final TextureCacheObject tex;

    public final TextureCacheObject[] objects;

    public final TextureCacheObject emissive;

    public final Material m;

    public final boolean transparent;

    public MaterialKey(AssetManager assets, TextureCacheObject tex, boolean transparent) {
        this(assets, tex, new TextureCacheObject[0], new Long[0], null, transparent);
    }

    public MaterialKey(AssetManager assets, TextureCacheObject tex, TextureCacheObject[] objectTexs, Long[] objects,
            TextureCacheObject emissive, boolean transparent) {
        this.hash = calcHash(tex.rid, objects, emissive != null ? emissive.rid : null, transparent);
        this.tex = tex;
        this.emissive = emissive;
        this.objects = objectTexs;
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
        for (int i = 0; i < (objects.length > 0 ? 1 : 0); i++) {
            if (objectTexs[i] != null) {
                m.setTexture(format("ObjectColorMap_%d", i + 1), objectTexs[i].tex);
            }
        }
    }

    public static int calcHash(long texrid, Long[] objects, Long emissive, boolean transparent) {
        final int PRIME = 59;
        int result = 1;
        final long temp1 = Long.hashCode(texrid);
        result = (result * PRIME) + (int) (temp1 ^ (temp1 >>> 32));
        for (Long o : objects) {
            if (o != null) {
                final long temp2 = Long.hashCode(o);
                result = (result * PRIME) + (int) (temp2 ^ (temp2 >>> 32));
            }
        }
        if (emissive != null) {
            final long temp3 = Long.hashCode(emissive);
            result = (result * PRIME) + (int) (temp3 ^ (temp3 >>> 32));
        }
        final long temp4 = Boolean.hashCode(transparent);
        result = (result * PRIME) + (int) (temp4 ^ (temp4 >>> 32));
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
