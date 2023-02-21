package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import javax.inject.Inject;

import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.factory.Stacks;

import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrainLevel.MapTerrainLevelFactory;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.google.inject.assistedinject.Assisted;
import com.jme3.scene.Node;

/**
 * Game map terrain having multiple z levels.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class MapTerrain {

    public interface MapTerrainFactory {
        MapTerrain create(GameMap gm);
    }

    public Node node;

    private MutableStack<MapTerrainLevel> terrainLevels;

    private MutableStack<MapTerrainLevel> oldLevels;

    @Inject
    private MapTerrainLevelFactory levelFactory;

    private GameMap gm;

    @Inject
    public MapTerrain(@Assisted GameMap gm) {
        this.gm = gm;
        this.node = new Node(MapTerrain.class.getSimpleName());
        this.terrainLevels = Stacks.mutable.empty();
        this.oldLevels = Stacks.mutable.empty();
    }

    public void setLevels(int levels) {
        var size = terrainLevels.size();
        if (size < levels) {
            fillLevels(levels - size, size);
        } else if (size > levels) {
            removeLevels(size - levels, size);
        }
    }

    private void fillLevels(int levels, int size) {
        for (var i = 0; i < levels; i++) {
            MapTerrainLevel tl;
            if (!oldLevels.isEmpty()) {
                tl = oldLevels.pop();
            } else {
                tl = levelFactory.create(gm, size + i);
            }
            terrainLevels.push(tl);
            node.attachChild(tl.node);
        }
    }

    private void removeLevels(int levels, int size) {
        for (var i = 0; i < levels; i++) {
            var tl = terrainLevels.pop();
            node.detachChild(tl.node);
            oldLevels.push(tl);
        }
    }

}
