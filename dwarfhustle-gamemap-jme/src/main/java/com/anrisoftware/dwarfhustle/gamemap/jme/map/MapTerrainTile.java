package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import java.awt.Color;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import lombok.ToString;

/**
 * Map terrain tile.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString(onlyExplicitlyIncluded = true)
public class MapTerrainTile {

    public interface MapTerrainTileFactory {
        MapTerrainTile create(@Assisted("level") int level, @Assisted("y") int y, @Assisted("x") int x);
    }

    @ToString.Include
    public final Node node;

    private Box box;

    private Geometry geo;

    @Inject
    public MapTerrainTile(@Assisted("level") int level, @Assisted("y") int y, @Assisted("x") int x, AssetManager am) {
        this.node = new Node(MapTerrainTile.class.getSimpleName() + "_" + level + "_" + y + "_" + x);
        this.box = new Box(0.5f, 0.5f, 0.5f);
        this.geo = new Geometry("box", box);
        var mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setLineWidth(4);
        var color = Color.getHSBColor(120, 232, 200 - level * 20);
        mat.setColor("Color", ColorRGBA.fromRGBA255(color.getRed(), color.getGreen(), color.getBlue(), 255));
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Front);
        geo.setMaterial(mat);
        geo.updateModelBound();
        node.attachChild(geo);
    }
}
