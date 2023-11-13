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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Provider;

/**
 * Provides the key mappings from {@link DefaultKeyMappings}.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class KeyMappingsProvider implements Provider<Map<String, KeyMapping>> {

    private final Map<String, KeyMapping> map;

    public KeyMappingsProvider() {
        var m = new HashMap<String, KeyMapping>();
        for (var v : DefaultKeyMappings.values()) {
            m.put(v.name(), KeyMapping.create(v.name(), v.keyCode.orElse(null), v.keyTrigger.orElse(null), v.message));
        }
        this.map = Collections.unmodifiableMap(m);
    }

    @Override
    public Map<String, KeyMapping> get() {
        return map;
    }

}
