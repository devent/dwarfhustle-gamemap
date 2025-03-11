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
package com.anrisoftware.dwarfhustle.gui.javafx.states

import static java.util.Optional.empty

import com.anrisoftware.dwarfhustle.gui.javafx.messages.GuiMessage
import com.jme3.input.controls.KeyTrigger

import groovy.transform.ToString
import javafx.scene.input.KeyCodeCombination

/**
 * Contains the key mappings.
 *
 * @see KeyMapping
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
class KeyMappingMap {

    public Map<String, KeyMapping> data = [:]

    void setProperty(String name, Object value) {
        if (data[name] == null) {
            data[name] = new KeyMapping()
            data[name].name = name
        }
        data[name].set(value)
    }

    def propertyMissing(String name) {
        if (data[name] == null) {
            data[name] = new KeyMapping()
            data[name].name = name
        }
        return data[name]
    }
}

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString(includeFields = true)
class KeyMapping {

    public String name;

    public Optional<KeyCodeCombination> code;

    public Optional<KeyTrigger> trigger;

    public GuiMessage message;

    def set(Map args) {
        code = args.getOrDefault("code", empty())
        trigger = args.getOrDefault("trigger", empty())
        message = args.get("message")
    }
}
