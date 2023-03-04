package com.anrisoftware.dwarfhustle.gamemap.jme.lights;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Directional light color and direction.
 *
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
@ToString
@RequiredArgsConstructor
public class DirectionalLightComponent implements Component {

    public static final ComponentMapper<DirectionalLightComponent> m = ComponentMapper.getFor(DirectionalLightComponent.class);

    public final Vector3f d;

    public final ColorRGBA color;

    public boolean enabled;

    public boolean shadow;
}
