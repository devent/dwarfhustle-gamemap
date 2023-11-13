/*
 * dwarfhustle-gamemap-gui-javafx - GUI in Javafx.
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
package com.anrisoftware.dwarfhustle.gui.states;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.util.Optional;

import com.anrisoftware.dwarfhustle.gui.messages.GuiMessage;
import com.jme3.input.controls.KeyTrigger;

import javafx.scene.input.KeyCodeCombination;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KeyMapping {

    public static KeyMapping create(String name, KeyCodeCombination code, KeyTrigger trigger, GuiMessage message) {
        return new KeyMapping(name, ofNullable(code), ofNullable(trigger), message);
    }

    public static KeyMapping create(String name, GuiMessage message) {
        return new KeyMapping(name, empty(), empty(), message);
    }

    @EqualsAndHashCode.Include
    public final String name;

    public final Optional<KeyCodeCombination> code;

    public final Optional<KeyTrigger> trigger;

    public final GuiMessage message;

}
