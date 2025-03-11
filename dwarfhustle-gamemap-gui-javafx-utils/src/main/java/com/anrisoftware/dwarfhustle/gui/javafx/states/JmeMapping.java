/*
 * dwarfhustle-gamemap-gui-javafx-utils - Game map.
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
package com.anrisoftware.dwarfhustle.gui.javafx.states;

import static com.jme3.input.KeyInput.KEY_LCONTROL;
import static com.jme3.input.KeyInput.KEY_RCONTROL;

import com.jme3.input.controls.KeyTrigger;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Provides the mapping of {@link KeyTrigger}s for JME.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class JmeMapping {

    public static final JmeMapping CONTROL_MAPPING = new JmeMapping("CONTROL_MAPPING",
            new KeyTrigger[] { new KeyTrigger(KEY_RCONTROL), new KeyTrigger(KEY_LCONTROL) });

    @EqualsAndHashCode.Include
    public final String name;

    public final KeyTrigger[] trigger;

}
