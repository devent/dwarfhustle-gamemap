/*
 * Dwarf Hustle Game Map - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import java.util.Objects;

import jakarta.inject.Inject;

import com.anrisoftware.dwarfhustle.model.api.objects.PropertiesSet;
import com.google.inject.assistedinject.Assisted;
import com.jme3.asset.AssetManager;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;

import lombok.ToString;

/**
 * Map terrain tile.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString(onlyExplicitlyIncluded = true)
public class MapTerrainTile {

    public static final String NAME = MapTerrainTile.class.getSimpleName();

    public interface MapTerrainTileFactory {
        MapTerrainTile create(@Assisted("level") int level, @Assisted("y") int y, @Assisted("x") int x);
    }

    @ToString.Include
    public final Node node;

    private final Geometry geo;

    public final int level;

    public final int y;

    public final int x;

    private Texture emptyTexture;

    private Texture selectedTexture;

    public PropertiesSet propertiesBits = new PropertiesSet();

    private Texture selectedFocusedTexture;

    private Texture focusedTexture;

    @ToString.Include
    private boolean dirty = true;

    private Texture baseColorMapTexture;

    private ColorRGBA specularColor = new ColorRGBA();

    private ColorRGBA baseColor = new ColorRGBA();

    private float roughness = 1f;

    private float metallic = 1f;

    private float glossiness = 1f;

    private static final int PROPERTY_POS_HIDDEN = 0;

    public static final int empty = 0x00000000;

    private static final int hidden = 0x00000001;

    public static final int selected = 0x00000002;

    public static final int focused = 0x00000004;

    @Inject
    public MapTerrainTile(@Assisted("level") int level, @Assisted("y") int y, @Assisted("x") int x, AssetManager am) {
        this.node = new Node(NAME + "_" + level + "_" + y + "_" + x);
        var geomodel = (Node) am.loadModel("Models/tile-cube/tile-cube.j3o");
        this.emptyTexture = null;
        this.selectedTexture = am.loadTexture("Textures/Tiles/Borders/tile-selected-border-3.png");
        this.focusedTexture = am.loadTexture("Textures/Tiles/Borders/tile-selected-border-1.png");
        this.selectedFocusedTexture = am.loadTexture("Textures/Tiles/Borders/tile-selected-border-2.png");
        this.geo = (Geometry) geomodel.getChild(0);
        this.level = level;
        this.y = y;
        this.x = x;
        geo.updateModelBound();
        geo.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        geo.getMaterial().getAdditionalRenderState().setWireframe(false);
        node.setUserData("name", NAME);
        node.setUserData("level", level);
        node.setUserData("y", y);
        node.setUserData("x", x);
        node.attachChild(geo);
    }

    public void setBaseColorMap(Texture tex) {
        if (!Objects.equals(baseColorMapTexture, tex)) {
            this.baseColorMapTexture = tex;
            this.dirty = true;
        }
    }

    public void setSpecularColor(ColorRGBA color) {
        if (!Objects.equals(specularColor, color)) {
            this.specularColor = color;
            this.dirty = true;
        }
    }

    public void setBaseColor(ColorRGBA color) {
        if (!Objects.equals(baseColor, color)) {
            this.baseColor = color;
            this.dirty = true;
        }
    }

    public void setRoughness(float f) {
        if (roughness != f) {
            this.roughness = f;
            this.dirty = true;
        }
    }

    public void setMetallic(float f) {
        if (metallic != f) {
            this.metallic = f;
            this.dirty = true;
        }
    }

    public void setGlossiness(float f) {
        if (glossiness != f) {
            this.glossiness = f;
            this.dirty = true;
        }
    }

    public void setPropertiesBits(int bits) {
        if (propertiesBits.bits != bits) {
            this.propertiesBits.replace(bits);
            this.dirty = true;
        }
    }

    public void setPropertyHidden(boolean b) {
        if (propertiesBits.get(PROPERTY_POS_HIDDEN) != b) {
            propertiesBits.set(b, PROPERTY_POS_HIDDEN);
            this.dirty = true;
        }
    }

    public void update() {
        if (dirty) {
            this.dirty = false;
            var m = geo.getMaterial();
            m.setTexture("BaseColorMap", baseColorMapTexture);
            m.setColor("Specular", specularColor);
            m.setColor("BaseColor", baseColor);
            m.setFloat("Glossiness", glossiness);
            m.setFloat("Metallic", metallic);
            m.setFloat("Roughness", roughness);
            if (propertiesBits.same(empty)) {
                m.setTexture("EmissiveMap", emptyTexture);
                geo.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            } else if (propertiesBits.contains(selected | focused)) {
                m.setTexture("EmissiveMap", selectedFocusedTexture);
                geo.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.PremultAlpha);
            } else if (propertiesBits.contains(selected)) {
                m.setTexture("EmissiveMap", selectedTexture);
                geo.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.PremultAlpha);
            } else if (propertiesBits.contains(focused)) {
                m.setTexture("EmissiveMap", focusedTexture);
                geo.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.PremultAlpha);
            }
        }
    }

}
