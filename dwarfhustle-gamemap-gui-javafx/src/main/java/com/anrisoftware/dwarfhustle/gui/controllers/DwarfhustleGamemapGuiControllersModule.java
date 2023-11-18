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
package com.anrisoftware.dwarfhustle.gui.controllers;

import static com.google.inject.name.Names.named;

import java.util.Map;

import com.anrisoftware.dwarfhustle.gui.states.JmeMapping;
import com.anrisoftware.dwarfhustle.gui.states.JmeMappingsProvider;
import com.anrisoftware.dwarfhustle.gui.states.KeyMapping;
import com.anrisoftware.dwarfhustle.gui.states.KeyMappingsProvider;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class DwarfhustleGamemapGuiControllersModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(new TypeLiteral<Map<String, KeyMapping>>() {
        }).annotatedWith(named("keyMappings")).toProvider(KeyMappingsProvider.class).asEagerSingleton();
        bind(new TypeLiteral<Map<String, JmeMapping>>() {
        }).annotatedWith(named("jmeMappings")).toProvider(JmeMappingsProvider.class).asEagerSingleton();
    }
}
