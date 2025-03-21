/*
 * dwarfhustle-gamemap-tester-gui-javafx - Game map.
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
package org.dwarfhustle.gamemap.tester.gui.javafx.states

import org.junit.jupiter.api.Test

import com.google.inject.Guice

/**
 * @see TestingKeyMappingsProvider
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
class TesterKeyMappingsProviderTest {

    @Test
    void load_TestingKeyMappingsProvider() {
        def injector = Guice.createInjector()
        def mappings = injector.getInstance(TesterKeyMappingProvider)
        assert mappings.get().size() == 82
    }
}
