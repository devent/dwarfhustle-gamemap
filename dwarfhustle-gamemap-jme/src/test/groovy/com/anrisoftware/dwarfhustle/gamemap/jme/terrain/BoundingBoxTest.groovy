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
package com.anrisoftware.dwarfhustle.gamemap.jme.terrain

import org.junit.jupiter.api.Test

import com.jme3.bounding.BoundingBox
import com.jme3.math.Vector3f

/**
 * @see BoundingBox
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class BoundingBoxTest {

    @Test
    void test_center_extents() {
        def bb = new BoundingBox()
        bb.setMinMax(new Vector3f(0, 0, 0), new Vector3f(2, 2, 2))
        assert bb.center.x == 1f
        assert bb.center.y == 1f
        assert bb.center.z == 1f
        assert bb.xExtent == 1f
        assert bb.yExtent == 1f
        assert bb.zExtent == 1f
    }
}
