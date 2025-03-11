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
package org.dwarfhustle.gamemap.tester.gui.javafx.states;

import com.anrisoftware.dwarfhustle.gui.javafx.states.AbstractKeyMappingProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMappingMap;

import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Provides the {@link KeyMappingMap} from {@code TesterKeyMapping.groovy.}
 *
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
public class TesterKeyMappingProvider extends AbstractKeyMappingProvider {

    public TesterKeyMappingProvider() throws ResourceException, ScriptException {
        super(TesterKeyMappingProvider.class.getResource("TesterKeyMapping.groovy"), "TesterKeyMapping.groovy");
    }

}
