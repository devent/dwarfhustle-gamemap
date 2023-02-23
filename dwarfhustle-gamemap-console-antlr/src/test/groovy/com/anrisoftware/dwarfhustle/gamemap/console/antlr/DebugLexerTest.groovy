/*
 * dwarfhustle-gamemap-console-antlr - Console debug commands defined in ANTLR 4.
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
package com.anrisoftware.dwarfhustle.gamemap.console.antlr;

import static org.junit.jupiter.api.Assertions.*
import static org.junit.jupiter.params.provider.Arguments.of

import java.util.stream.Stream

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource


/**
 * @see DebugLexer
 *
 * @author Erwin Müller
 */
class DebugLexerTest {

    static double delta = 1e-7d

    static parseExpressionsSource() {
        Stream.of(
                //
                of('set position 0,0,10 to camera', { DebugConsoleParserService service ->
                    assertEquals "set", service.verb
                    assertEquals "position", service.property
                    assertEquals "camera", service.object
                    assertEquals 0f, service.x
                    assertEquals 0f, service.y
                    assertEquals 10f, service.z
                }),
                //
                of("set coordinates 4,5,6 to dwarf with id = 445566", { DebugConsoleParserService service ->
                    assertEquals "coordinates", service.property
                    assertEquals 4, service.x
                    assertEquals 5, service.y
                    assertEquals 6, service.z
                    assertEquals "dwarf", service.object
                    assertEquals 445566, service.id
                }),
                //
                of("set coordinates 4, 5, 6 to dwarf with id = 445566", { DebugConsoleParserService service ->
                    assertEquals "coordinates", service.property
                    assertEquals 4, service.x
                    assertEquals 5, service.y
                    assertEquals 6, service.z
                    assertEquals "dwarf", service.object
                    assertEquals 445566, service.id
                }),
                //
                of("set coordinates 4,5,6 4.4,5.5,6.6 to dwarf with id = 445566", { DebugConsoleParserService service ->
                    assertEquals "coordinates", service.property
                    assertEquals 4, service.x
                    assertEquals 5, service.y
                    assertEquals 6, service.z
                    assertEquals 4.4f, service.xx
                    assertEquals 5.5f, service.yy
                    assertEquals 6.6f, service.zz
                    assertEquals "dwarf", service.object
                    assertEquals 445566, service.id
                }),
                //
                of("set rotation 4.4,5.5,6.6 to tiles", { DebugConsoleParserService service ->
                }),
                //
                of("set rotation 4.4,5.5,6.6 to dwarf with id = 445566", { DebugConsoleParserService service ->
                }),
                //
                of("set scale 4.4,5.5,6.6 to dwarf with id = 445566", { DebugConsoleParserService service ->
                }),
                //
                of("set position 4.4,5.5,6.6 to camera", { DebugConsoleParserService service ->
                    assertEquals "position", service.property
                    assertEquals 4.4f, service.x, delta
                    assertEquals 5.5f, service.y, delta
                    assertEquals 6.6f, service.z, delta
                    assertEquals "camera", service.object
                }),
                //
                of("set panningVelocity 4.4,5.5,6.6 to camera", { DebugConsoleParserService service ->
                    assertEquals "panningVelocity", service.property
                    assertEquals 4.4d, service.x, delta
                    assertEquals 5.5d, service.y, delta
                    assertEquals 6.6d, service.z, delta
                    assertEquals "camera", service.object
                }),
                //
                of('add Dwarf 4,5,6', { DebugConsoleParserService service ->
                    assertEquals "add", service.verb
                    assertEquals 4, service.x
                    assertEquals 5, service.y
                    assertEquals 6, service.z
                    assertEquals "Dwarf", service.objectType
                }),
                //
                of('add Dwarf here', { DebugConsoleParserService service ->
                    assertEquals "add", service.verb
                    assertEquals null, service.x
                    assertEquals null, service.y
                    assertEquals null, service.z
                    assertEquals "Dwarf", service.objectType
                }),
                //
                of('apply impulse 0.5, 0.0, 0.0 4.0, 5.0, 6.0 to Dwarf with id = -3466937467377025024', { DebugConsoleParserService service ->
                    assertEquals "apply", service.verb
                    assertEquals "impulse", service.physics
                    assertEquals 0.5f, service.vx
                    assertEquals 0f, service.vy
                    assertEquals 0f, service.vz
                    assertEquals 4f, service.x
                    assertEquals 5f, service.y
                    assertEquals 6f, service.z
                    assertEquals "Dwarf", service.object
                    assertEquals(-3466937467377025024, service.id)
                }),
                //
                of('apply impulse 0.5, 0.0, 0.0 to Dwarf with id = -3466937467377025024', { DebugConsoleParserService service ->
                    assertEquals "apply", service.verb
                    assertEquals "impulse", service.physics
                    assertEquals 0.5f, service.vx
                    assertEquals 0f, service.vy
                    assertEquals 0f, service.vz
                    assertEquals null, service.x
                    assertEquals null, service.y
                    assertEquals null, service.z
                    assertEquals "Dwarf", service.object
                    assertEquals(-3466937467377025024, service.id)
                }),
                //
                )
    }

    @ParameterizedTest
    @MethodSource("parseExpressionsSource")
    void parseExpressions(String str, def expression) {
        def service = new DebugConsoleParserService()
        service.parse(str)
        expression(service)
    }
}
