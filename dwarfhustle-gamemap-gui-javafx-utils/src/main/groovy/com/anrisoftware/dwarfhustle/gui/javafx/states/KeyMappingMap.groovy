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
