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

    static parseExpressionsSource() {
        Stream.of(
                //
                of('open scene', { DebugConsoleParserService service ->
                    assertEquals "open", service.verb.get()
                    assertEquals "scene", service.object.get()
                }),
                //
                of('set time 23:59:59 to world', { DebugConsoleParserService service ->
                    assertEquals "set", service.verb.get()
                    assertEquals "time", service.property.get()
                    assertEquals "world", service.object.get()
                    assertEquals 23, service.hours.get()
                    assertEquals 59, service.minutes.get()
                    assertEquals 59, service.seconds.get()
                }),
                //
                of('set layers 4 to terrain', { DebugConsoleParserService service ->
                    assertEquals "set", service.verb.get()
                    assertEquals "layers", service.property.get()
                    assertEquals "terrain", service.object.get()
                    assertEquals 4, service.layers.get()
                }),
                //
                of('set position 0,0,10 to camera', { DebugConsoleParserService service ->
                    assertEquals "set", service.verb.get()
                    assertEquals "position", service.property.get()
                    assertEquals "camera", service.object.get()
                    assertEquals 0f, service.x.get()
                    assertEquals 0f, service.y.get()
                    assertEquals 10f, service.z.get()
                }),
                //
                of("set coordinates 4,5,6 to dwarf with id = 445566", { DebugConsoleParserService service ->
                    assertEquals "coordinates", service.property.get()
                    assertEquals 4, service.x.get()
                    assertEquals 5, service.y.get()
                    assertEquals 6, service.z.get()
                    assertEquals "dwarf", service.object.get()
                    assertEquals 445566, service.id.get()
                }),
                //
                of("set coordinates 4, 5, 6 to dwarf with id = 445566", { DebugConsoleParserService service ->
                    assertEquals "coordinates", service.property.get()
                    assertEquals 4, service.x.get()
                    assertEquals 5, service.y.get()
                    assertEquals 6, service.z.get()
                    assertEquals "dwarf", service.object.get()
                    assertEquals 445566, service.id.get()
                }),
                //
                of("set coordinates 4,5,6 4.4,5.5,6.6 to dwarf with id = 445566", { DebugConsoleParserService service ->
                    assertEquals "coordinates", service.property.get()
                    assertEquals 4, service.x.get()
                    assertEquals 5, service.y.get()
                    assertEquals 6, service.z.get()
                    assertEquals 4.4f, service.xx.get()
                    assertEquals 5.5f, service.yy.get()
                    assertEquals 6.6f, service.zz.get()
                    assertEquals "dwarf", service.object.get()
                    assertEquals 445566, service.id.get()
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
                    assertEquals "position", service.property.get()
                    assertEquals 4.4f, service.x.get()
                    assertEquals 5.5f, service.y.get()
                    assertEquals 6.6f, service.z.get()
                    assertEquals "camera", service.object.get()
                }),
                //
                of("set panningVelocity 4.4,5.5,6.6 to camera", { DebugConsoleParserService service ->
                    assertEquals "panningVelocity", service.property.get()
                    assertEquals 4.4f, service.x.get()
                    assertEquals 5.5f, service.y.get()
                    assertEquals 6.6f, service.z.get()
                    assertEquals "camera", service.object.get()
                }),
                //
                of('add Dwarf 4,5,6', { DebugConsoleParserService service ->
                    assertEquals "add", service.verb.get()
                    assertEquals 4, service.x.get()
                    assertEquals 5, service.y.get()
                    assertEquals 6, service.z.get()
                    assertEquals "Dwarf", service.objectType.get()
                }),
                //
                of('add Dwarf here', { DebugConsoleParserService service ->
                    assertEquals "add", service.verb.get()
                    assertTrue service.x.isEmpty()
                    assertTrue service.y.isEmpty()
                    assertTrue service.z.isEmpty()
                    assertEquals "Dwarf", service.objectType.get()
                }),
                //
                of('apply impulse 0.5, 0.0, 0.0 4.0, 5.0, 6.0 to Dwarf with id = -3466937467377025024', { DebugConsoleParserService service ->
                    assertEquals "apply", service.verb.get()
                    assertEquals "impulse", service.physics.get()
                    assertEquals 0.5f, service.vx.get()
                    assertEquals 0f, service.vy.get()
                    assertEquals 0f, service.vz.get()
                    assertEquals 4f, service.x.get()
                    assertEquals 5f, service.y.get()
                    assertEquals 6f, service.z.get()
                    assertEquals "Dwarf", service.object.get()
                    assertEquals(-3466937467377025024, service.id.get())
                }),
                //
                of('apply impulse 0.5, 0.0, 0.0 to Dwarf with id = -3466937467377025024', { DebugConsoleParserService service ->
                    assertEquals "apply", service.verb.get()
                    assertEquals "impulse", service.physics.get()
                    assertEquals 0.5f, service.vx.get()
                    assertEquals 0f, service.vy.get()
                    assertEquals 0f, service.vz.get()
                    assertTrue service.x.isEmpty()
                    assertTrue service.y.isEmpty()
                    assertTrue service.z.isEmpty()
                    assertEquals "Dwarf", service.object.get()
                    assertEquals(-3466937467377025024, service.id.get())
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
