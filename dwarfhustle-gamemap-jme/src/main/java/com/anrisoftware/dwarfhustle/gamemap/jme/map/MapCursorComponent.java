package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Map cursor.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@RequiredArgsConstructor
public class MapCursorComponent implements Component {

	public static final ComponentMapper<MapCursorComponent> m = ComponentMapper.getFor(MapCursorComponent.class);

    public final int z;

    public final int y;

    public final int x;
}
