/*
 * Dwarf Hustle Game Map - Game map.
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
package com.anrisoftware.dwarfhustle.gui.controllers

import static com.anrisoftware.dwarfhustle.gui.controllers.JavaFxUtil.*

import javax.inject.Inject
import javax.inject.Named

import com.anrisoftware.dwarfhustle.gui.states.JmeMapping
import com.anrisoftware.dwarfhustle.gui.states.KeyMapping
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider
import com.jayfella.jme.jfx.JavaFxUI
import com.jme3.input.InputManager
import com.jme3.input.controls.ActionListener

import groovy.util.logging.Slf4j
import javafx.scene.Scene
import javafx.scene.input.KeyCombination.ModifierValue

/**
 * Setups the global keys.
 *
 * @author Erwin Müller
 */
@Slf4j
class GlobalKeys implements ActionListener {

	InputManager inputManager

	@Inject
	ActorSystemProvider actor

	@Inject
	@Named("keyMappings")
	Map<String, KeyMapping> keyMappings;

	@Inject
	@Named("jmeMappings")
	Map<String, JmeMapping> jmeMappings;

	boolean controlDown = false

	void setup(JavaFxUI instance, InputManager inputManager) {
		this.inputManager = inputManager
		setupControls(instance.scene)
		initKeys(inputManager)
	}

	void remove(KeyMapping mapping, JmeMapping jmeMapping) {
		runFxThread {
			def acc = JavaFxUI.getInstance().scene.accelerators
			acc.remove(mapping.code)
		}
		runInJmeThread {
			inputManager.deleteMapping(mapping.name)
		}
		if (jmeMapping) {
			runInJmeThread {
				inputManager.deleteMapping(jmeMapping.name)
			}
		}
	}

	void add(KeyMapping mapping, JmeMapping jmeMapping) {
		runFxThread {
			def acc = JavaFxUI.getInstance().scene.accelerators
			acc.put mapping.code, { runInJmeThread({ runAction(mapping) }) }
		}
		runInJmeThread {
			inputManager.addMapping(mapping.name, mapping.trigger)
		}
		if (jmeMapping) {
			runInJmeThread {
				inputManager.addMapping(jmeMapping.name, jmeMapping.trigger)
			}
		}
	}

	void setupControls(Scene scene) {
		def acc = scene.accelerators
		keyMappings.values().find { it.code.present }.each { m ->
			acc.put m.code.get(), { runInJmeThread({ runAction(m) }) }
		}
	}

	void initKeys(InputManager inputManager) {
		inputManager.addListener(this, keyMappings.values().findAll { it.trigger.present }.inject([]) { l, v ->
			inputManager.addMapping(v.name, v.trigger.get())
			l << v.name
		} as String[])
		inputManager.addListener(this, jmeMappings.values().inject([]) { l, v ->
			inputManager.addMapping(v.name, v.trigger)
			l << v.name
		} as String[])
	}

	@Override
	void onAction(String name, boolean isPressed, float tpf) {
		switch (name) {
			case JmeMapping.CONTROL_MAPPING.name:
				controlDown = isPressed
				return
		}
		if (!isPressed) {
			return
		}
		def mapping = keyMappings[name]
		def code = mapping.code
		if (code.present) {
			if (code.get().control != ModifierValue.ANY) {
				if (code.get().control == ModifierValue.DOWN && !controlDown) {
					return;
				}
				if (code.get().control == ModifierValue.UP && controlDown) {
					return;
				}
			}
		}
		runAction(mapping)
	}

	void runAction(KeyMapping mapping) {
		log.debug("Post message: {}", mapping.message);
		actor.get().tell(mapping.message);
	}
}
