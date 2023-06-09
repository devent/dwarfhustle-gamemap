package com.anrisoftware.dwarfhustle.gamemap.jme.actors

import static org.mockito.Mockito.*

import org.apache.commons.jcs3.access.CacheAccess
import org.junit.jupiter.api.Test

import com.anrisoftware.dwarfhustle.gamemap.jme.assets.TexturesMapData
import com.anrisoftware.dwarfhustle.gamemap.jme.assets.TexturesMapFramesData
import com.jme3.asset.AssetManager

/**
 * @see AssetsLoadMaterialTextures
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class AssetsLoadMaterialTexturesTest {

    @Test
    void test_loadTexturesMaterials() {
        def engine = new GroovyScriptEngine(
                [
                    AssetsLoadMaterialTextures.class.getResource("/TexturesMaterials.groovy")
                ] as URL[])
        def binding = new Binding();
        def m = engine.run("TexturesMaterials.groovy", binding);
        int ww = 128, hh = 128
        assert m.data.size() == 10
        assert m.data["Clay"] instanceof TexturesMapData
        assert m.Clay.image =~ /.*png/
        assert m.Clay.frames.size() == 5
        assert m.Clay.frames[875l] instanceof TexturesMapFramesData
        assert m.Clay.frames[875l].image =~ /.*png/
        assert m.Clay.frames[875l].x == 0
        assert m.Clay.frames[875l].y == 256
        assert m.Clay.frames[875l].w == ww
        assert m.Clay.frames[875l].h == hh
        assert m.Gas.image =~ /.*png/
        assert m.Gas.frames[897l].transparent
        assert m.Sand.image =~ /.*png/
        assert m.Sand.frames[881l].x == 128
        assert m.Sand.frames[881l].y == 0
    }

    @Test
    void test_loadMaterialTextures() {
        def am = mock(AssetManager.class)
        def cache = mock(CacheAccess.class)
        def loader = new AssetsLoadMaterialTextures()
        loader.am = am
        loader.loadMaterialTextures(cache)
    }
}
