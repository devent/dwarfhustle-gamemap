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
package com.anrisoftware.dwarfhustle.gui.javafx.states;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import jakarta.inject.Provider;

/**
 * Provides the key mappings.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public abstract class AbstractKeyMappingProvider implements Provider<Map<String, KeyMapping>> {

	private final Map<String, KeyMapping> map;

	public AbstractKeyMappingProvider(URL[] roots, String keyMappingsMapGroovyScript)
			throws ResourceException, ScriptException {
		var engine = new GroovyScriptEngine(roots);
		var binding = new Binding();
		var keyMappingsMap = (KeyMappingMap) engine.run(keyMappingsMapGroovyScript, binding);
		this.map = Collections.unmodifiableMap(keyMappingsMap.data);
	}

	public AbstractKeyMappingProvider(URL root, String keyMappingsMapGroovyScript)
			throws ResourceException, ScriptException {
		this(new URL[] { root }, keyMappingsMapGroovyScript);
	}

	@Override
	public Map<String, KeyMapping> get() {
		return map;
	}

}
