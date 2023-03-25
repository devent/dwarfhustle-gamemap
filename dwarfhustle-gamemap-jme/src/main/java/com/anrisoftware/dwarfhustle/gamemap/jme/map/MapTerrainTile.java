package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.api.objects.PropertiesSet;
import com.google.inject.assistedinject.Assisted;
import com.jme3.asset.AssetManager;
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

    @Inject
    private GameSettingsProvider gs;

    @ToString.Include
    public final Node node;

    private final Geometry geo;

    public final int level;

    public final int y;

    public final int x;

    public Texture materialTexture;

    private Texture emptyTexture;

    private Texture selectedTexture;

    public PropertiesSet propertiesBits = new PropertiesSet();

    private Texture selectedFocusedTexture;

    private Texture focusedTexture;

    public static final int empty = 0x00000000;

    public static final int selected = 0x00000001;

    public static final int focused = 0x00000002;

    @Inject
    public MapTerrainTile(@Assisted("level") int level, @Assisted("y") int y, @Assisted("x") int x, AssetManager am) {
        this.node = new Node(NAME + "_" + level + "_" + y + "_" + x);
        var geomodel = (Node) am.loadModel("Models/tile-cube/tile-cube.j3o");
        this.emptyTexture = null;
        this.selectedTexture = am.loadTexture("Textures/borders/tile-selected-border-3.png");
        this.focusedTexture = am.loadTexture("Textures/borders/tile-selected-border-1.png");
        this.selectedFocusedTexture = am.loadTexture("Textures/borders/tile-selected-border-2.png");
        this.geo = (Geometry) geomodel.getChild(0);
        this.level = level;
        this.y = y;
        this.x = x;
        geo.updateModelBound();
        // geo.getMaterial().getAdditionalRenderState().setWireframe(true);
        node.setUserData("name", NAME);
        node.setUserData("level", level);
        node.setUserData("y", y);
        node.setUserData("x", x);
        node.attachChild(geo);
    }

    public void update() {
        var m = geo.getMaterial();
        m.setTexture("BaseColorMap", materialTexture);
        if (propertiesBits.same(empty)) {
            m.setTexture("EmissiveMap", emptyTexture);
        } else if (propertiesBits.contains(selected | focused)) {
            m.setTexture("EmissiveMap", selectedFocusedTexture);
        } else if (propertiesBits.contains(selected)) {
            m.setTexture("EmissiveMap", selectedTexture);
        } else if (propertiesBits.contains(focused)) {
            m.setTexture("EmissiveMap", focusedTexture);
        }
    }
}
