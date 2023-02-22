package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrainTile.MapTerrainTileFactory;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.google.inject.assistedinject.Assisted;
import com.jme3.scene.Node;

import lombok.ToString;

/**
 * Game map terrain level consiting of width x height map tiles.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString(onlyExplicitlyIncluded = true)
public class MapTerrainLevel {

    public interface MapTerrainLevelFactory {
        MapTerrainLevel create(GameMap gm, int level);
    }

    public final GameMap gm;

    public final int level;

    @ToString.Include
    public final Node node;

    @Inject
    public MapTerrainLevel(@Assisted GameMap gm, @Assisted int level, MapTerrainTileFactory tileFactory) {
        this.gm = gm;
        this.level = level;
        this.node = new Node(MapTerrainLevel.class.getSimpleName() + "-" + level);
        int h = gm.getHeight();
        int w = gm.getWidth();
        float h2 = gm.getHeight() / 2f;
        float w2 = gm.getWidth() / 2f;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                var tile = tileFactory.create(level, y, x);
                float tx = -w2 + x + 0.5f;
                float ty = -h2 + y + 0.5f;
                tile.node.setLocalTranslation(tx, ty, 0f);
                node.attachChild(tile.node);
            }
        }
    }
}
