/*
 * dwarfhustle-gamemap-model - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.model.resources

import static org.junit.jupiter.params.provider.Arguments.of

import java.util.stream.Stream

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import com.anrisoftware.dwarfhustle.gamemap.model.resources.ObservableGameSettings.GameSettings

import groovy.util.logging.Slf4j

/**
 * @see GameSettingsProvider
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
class GameSettingsProviderTest {

    static Stream gameSettingsSource() {
        Stream.of(
                of({
                    def gs = new GameSettings()
                    return gs
                }, """---
locale: "en_US"
gameTickDuration: 0.100000000
terrainUpdateDuration: 0.050000000
windowFullscreen: false
windowWidth: 1024
windowHeight: 768
iconSize: "MEDIUM"
textPosition: "RIGHT"
commandsSplitPosition: 0.71
lastCommand: ""
visibleDepthLayers: 1
timeUpdateInterval: 0.033333335
hideUndiscovered: true
"""), //
                )
    }

    @ParameterizedTest
    @MethodSource("gameSettingsSource")
    void save_to_string(def gsp, def expected) {
        def gs = gsp()
        def p = new GameSettingsProvider(gs)
        p.mapper = new ObjectMapperProvider().get()
        def s = p.saveAsString()
        log.info "save_to_string {} ```{}```\n```{}```", gs, s, expected
        assert s == expected
    }

    @ParameterizedTest
    @MethodSource("gameSettingsSource")
    void load_from_string(def gspExpected, def s) {
        GameSettings expected = gspExpected()
        def p = new GameSettingsProvider()
        p.mapper = new ObjectMapperProvider().get()
        p.loadFromString(s)
        log.info "load_from_string {} ```{}```", s, expected
        assert p.get().locale.get() == expected.locale
    }
}
