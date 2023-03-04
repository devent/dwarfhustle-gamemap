package com.anrisoftware.dwarfhustle.gamemap.jme.lights;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.jme3.math.ColorRGBA;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Ambient light color.
 *
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
@ToString
@RequiredArgsConstructor
public class AmbientLightComponent implements Component {

    public static final ComponentMapper<AmbientLightComponent> m = ComponentMapper.getFor(AmbientLightComponent.class);

    public final ColorRGBA color;
}
