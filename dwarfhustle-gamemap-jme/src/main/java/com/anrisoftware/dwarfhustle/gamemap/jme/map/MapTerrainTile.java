package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

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

    private Spatial geo;

    @Inject
    public MapTerrainTile(@Assisted("level") int level, @Assisted("y") int y, @Assisted("x") int x, AssetManager am) {
        this.node = new Node(MapTerrainTile.class.getSimpleName() + "_" + level + "_" + y + "_" + x);
        this.geo = am.loadModel("Models/tile-cube/tile-cube.j3o");
        geo.updateModelBound();
        node.attachChild(geo);
    }
}
