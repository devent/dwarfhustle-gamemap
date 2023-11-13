/*
 * dwarfhustle-gamemap-jme - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.jme.actors

import static org.junit.jupiter.params.provider.Arguments.of

import java.util.stream.Stream

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import com.jme3.math.Vector3f

/**
 * @see Vector3f
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class Vector3fTest {

    static test_hashCode() {
        Stream.of(
                of(new Vector3f(0, 0, 0), 2030264), //
                of(new Vector3f(1, 1, 1), -627115336), //
                of(new Vector3f(-1, 1, 1), -627115336), //
                of(new Vector3f(1, -1, 1), -627115336), //
                of(new Vector3f(1, 1, -1), 1520368312), //
                )
    }

    @ParameterizedTest
    @MethodSource
    void test_hashCode(def v, def expected) {
        assert v.hashCode() == expected
    }

    static test_hashCode_custom() {
        Stream.of(
                of(new Vector3f(0, 0, 0), 59), //
                of(new Vector3f(1, 1, 1), 1098907707), //
                of(new Vector3f(-1, 1, 1), 1082130491), //
                of(new Vector3f(1, -1, 1), 1065353275), //
                of(new Vector3f(1, 1, -1), 1084227643), //
                )
    }

    @ParameterizedTest
    @MethodSource
    void test_hashCode_custom(def v, def expected) {
        assert toHash(v) == expected
    }

    static int toHash(def v) {
        final int PRIME = 59;
        int result = 1;
        float f = calcN(calcN(v.x, v.y), v.z)
        result = (result * PRIME) + (Float.hashCode(f));
        return result;
    }

    static float calcN(float x, float y) {
        return (x + y) * (x + y + 1) / 2 + y;
    }
}
