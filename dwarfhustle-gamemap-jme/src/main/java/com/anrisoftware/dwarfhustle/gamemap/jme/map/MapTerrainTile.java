package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.google.inject.assistedinject.Assisted;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

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
        MapTerrainTile create(MapTerrainModel model, @Assisted("level") int level, @Assisted("y") int y,
                @Assisted("x") int x);
    }

    @Inject
    private GameSettingsProvider gs;

    @ToString.Include
    public final Node node;

    private final Geometry geo;

    private final int level;

    private final int y;

    private final int x;

    private MapTerrainModel model;

    @Inject
    public MapTerrainTile(@Assisted MapTerrainModel model, @Assisted("level") int level, @Assisted("y") int y,
            @Assisted("x") int x, AssetManager am) {
        this.model = model;
        this.node = new Node(NAME + "_" + level + "_" + y + "_" + x);
        var geomodel = (Node) am.loadModel("Models/tile-cube/tile-cube.j3o");
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
        var gm = gs.get().currentMap.get();
        var m = geo.getMaterial();
        if (model.isTileCurser(gm.getCursorZ() + level, y, x)) {
            m.setColor("Emissive", new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
        } else {
            m.setColor("Emissive", ColorRGBA.Black);
        }
    }
}
