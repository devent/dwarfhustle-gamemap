/*
 * dwarfhustle-gamemap-jme - Game map.
 * Copyright © 2023 Erwin Müller (erwin.mueller@anrisoftware.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
