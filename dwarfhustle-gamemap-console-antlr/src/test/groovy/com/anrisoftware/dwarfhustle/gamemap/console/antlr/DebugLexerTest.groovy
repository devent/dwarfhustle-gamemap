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
				of("set coordinates x=4,y=5,z=6 to dwarf with id = 445566", { DebugConsoleParserService service ->
					assertEquals "coordinates", service.property
					assertEquals 4, service.x
					assertEquals 5, service.y
					assertEquals 6, service.z
					assertEquals "dwarf", service.object
					assertEquals 445566, service.id
				}),
				//
				of("set coordinates z=4, y=5, x=6 to dwarf with id = 445566", { DebugConsoleParserService service ->
				}),
				//
				of("set coordinates x=4,y=5,z=6 xx=4.4,yy=5.5,zz=6.6 to dwarf with id = 445566", { DebugConsoleParserService service ->
				}),
				//
				of("set rotation x=4.4,y=5.5,z=6.6 to tiles", { DebugConsoleParserService service ->
				}),
				//
				of("set rotation x=4.4,y=5.5,z=6.6 to dwarf with id = 445566", { DebugConsoleParserService service ->
				}),
				//
				of("set scale x=4.4,y=5.5,z=6.6 to dwarf with id = 445566", { DebugConsoleParserService service ->
				}),
				//
				of("set position x=4.4,y=5.5,z=6.6 to camera", { DebugConsoleParserService service ->
					assertEquals "position", service.property
					assertEquals 4.4d, service.xx, delta
					assertEquals 5.5d, service.yy, delta
					assertEquals 6.6d, service.zz, delta
					assertEquals "camera", service.object
				}),
				//
				of("set panningVelocity x=4.4,y=5.5,z=6.6 to camera", { DebugConsoleParserService service ->
					assertEquals "panningVelocity", service.property
					assertEquals 4.4d, service.xx, delta
					assertEquals 5.5d, service.yy, delta
					assertEquals 6.6d, service.zz, delta
					assertEquals "camera", service.object
				}),
				//
				of('add Dwarf x=4,y=5,z=6', { DebugConsoleParserService service ->
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
				of('apply impulse vx=0.5, vy=0.0, vz=0.0 z=4.0, y=5.0, x=6.0 to Dwarf with id = -3466937467377025024', { DebugConsoleParserService service ->
					assertEquals "apply", service.verb
					assertEquals "impulse", service.physics
					assertEquals 0.5f, service.vx
					assertEquals 0f, service.vy
					assertEquals 0f, service.vz
					assertEquals 6f, service.xx
					assertEquals 5f, service.yy
					assertEquals 4f, service.zz
					assertEquals "Dwarf", service.object
					assertEquals(-3466937467377025024, service.id)
				}),
				//
				of('apply impulse vx=0.5, vy=0.0, vz=0.0 to Dwarf with id = -3466937467377025024', { DebugConsoleParserService service ->
					assertEquals "apply", service.verb
					assertEquals "impulse", service.physics
					assertEquals 0.5f, service.vx
					assertEquals 0f, service.vy
					assertEquals 0f, service.vz
					assertEquals null, service.xx
					assertEquals null, service.yy
					assertEquals null, service.zz
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
