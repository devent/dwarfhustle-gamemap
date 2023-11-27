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
