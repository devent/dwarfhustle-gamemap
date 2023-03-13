package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Map tile selected under the mouse cursor.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@RequiredArgsConstructor
public class MapTileSelectedComponent implements Component {

    public static final ComponentMapper<MapTileSelectedComponent> m = ComponentMapper
            .getFor(MapTileSelectedComponent.class);

    public final int level;

    public final int y;

    public final int x;
}
