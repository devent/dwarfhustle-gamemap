package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static org.junit.jupiter.params.provider.Arguments.of

import java.util.stream.Stream

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class TerrainLoadImageTest {

    static final URL TERRAIN_8_8_1_FILE = TerrainLoadImageTest.class.getResource("terrain-8-8-1.png")

    static final List TERRAIN_8_8_1_EXPECT = new Terrain_8_8_1_Expect().run()

    static final URL TERRAIN_8_8_4_FILE = TerrainLoadImageTest.class.getResource("terrain-8-8-4.png")

    static final List TERRAIN_8_8_4_EXPECT = new Terrain_8_8_4_Expect().run()

    static final URL TERRAIN_8_8_8_FILE = TerrainLoadImageTest.class.getResource("terrain-8-8-8.png")

    static final List TERRAIN_8_8_8_EXPECT = new Terrain_8_8_8_Expect().run()

    static Stream test_load() {
        Stream.of(
                of(8, 8, 1, 1, TERRAIN_8_8_1_FILE, TERRAIN_8_8_1_EXPECT), //
                of(8, 8, 4, 2, TERRAIN_8_8_4_FILE, TERRAIN_8_8_4_EXPECT), //
                of(8, 8, 8, 4, TERRAIN_8_8_8_FILE, TERRAIN_8_8_8_EXPECT), //
                )
    }

    @ParameterizedTest
    @MethodSource
    void test_load(int w, int h, int d, int columns, URL file, List expect) {
        def terrain = new TerrainLoadImage(d, h, w, columns).load(file);
        assert terrain.length == d
        assert terrain[0].length == h
        assert terrain[0][0].length == w
        assert terrain == expect
    }

    @Test
    void test_convert2Id() {
        int id = TerrainLoadImage.convert2Id(0x2F, 0x03, 0x00)
        assert id == 815
    }
}
