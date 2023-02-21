package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Component for {@link MapBlock}.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@RequiredArgsConstructor
public class MapBlockComponent implements Component {

	public static final ComponentMapper<MapBlockComponent> m = ComponentMapper.getFor(MapBlockComponent.class);

    public final GameMap gm;

	public final MapBlock mb;
}
