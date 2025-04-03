/*
 * dwarfhustle-gamemap-jme - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static java.lang.String.format;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;

public class MaterialKey {

    public final int hash;

    public final TextureCacheObject tex;

    public final TextureCacheObject[] objects;

    public final TextureCacheObject emissive;

    public final Material m;

    public final boolean transparent;

    public MaterialKey(AssetManager assets, TextureCacheObject tex, boolean selected, boolean transparent) {
        this(assets, tex, new TextureCacheObject[0], new Long[0], null, selected, transparent);
    }

    public MaterialKey(AssetManager assets, TextureCacheObject tex, TextureCacheObject[] objectTexs, Long[] objects,
            TextureCacheObject emissive, boolean selected, boolean transparent) {
        this.hash = calcHash(tex.rid, objects, emissive != null ? emissive.rid : null, selected, transparent);
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
        if (selected) {
            m.setColor("Selected", new ColorRGBA(0.0f, 1.0f, 1.0f, 0.5f));
            m.setFloat("SelectedPower", 3.0f);
            m.setFloat("SelectedIntensity", 2.0f);
        }
        m.getAdditionalRenderState().setBlendMode(tex.transparent ? BlendMode.Alpha : BlendMode.Off);
        if (emissive != null) {
            m.setFloat("SelectedPower", 3.0f);
            m.setFloat("SelectedIntensity", 2.0f);
            // m.setColor("Selected", ColorRGBA.Green);
            m.setTexture("SelectedMap", emissive.tex);
        }
        for (int i = 0; i < objects.length; i++) {
            if (objectTexs[i] != null) {
                m.setTexture(format("ObjectColorMap_%d", i + 1), objectTexs[i].tex);
            }
        }
    }

    public static int calcHash(long texrid, Long[] objects, Long emissive, boolean selected, boolean transparent) {
        final int PRIME = 59;
        int result = 1;
        final long temp1 = Long.hashCode(texrid);
        result = (result * PRIME) + (int) (temp1 ^ (temp1 >>> 32));
        for (final Long o : objects) {
            if (o != null) {
                final long temp2 = Long.hashCode(o);
                result = (result * PRIME) + (int) (temp2 ^ (temp2 >>> 32));
            }
        }
        if (emissive != null) {
            final long temp3 = Long.hashCode(emissive);
            result = (result * PRIME) + (int) (temp3 ^ (temp3 >>> 32));
        }
        final long temp4 = Boolean.hashCode(selected);
        result = (result * PRIME) + (int) (temp4 ^ (temp4 >>> 32));
        final long temp5 = Boolean.hashCode(transparent);
        result = (result * PRIME) + (int) (temp5 ^ (temp5 >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof final MaterialKey other)) {
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
        return tex.getId() == id;
    }

}
