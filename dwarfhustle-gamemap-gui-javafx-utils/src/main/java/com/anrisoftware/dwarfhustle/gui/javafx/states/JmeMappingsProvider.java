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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Provider;

/**
 * Provides the key mappings for JME3.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class JmeMappingsProvider implements Provider<Map<String, JmeMapping>> {

    private final Map<String, JmeMapping> map;

    public JmeMappingsProvider() {
        var m = new HashMap<String, JmeMapping>();
        m.put(JmeMapping.CONTROL_MAPPING.name, JmeMapping.CONTROL_MAPPING);
        this.map = Collections.unmodifiableMap(m);
    }

    @Override
    public Map<String, JmeMapping> get() {
        return map;
    }

}
