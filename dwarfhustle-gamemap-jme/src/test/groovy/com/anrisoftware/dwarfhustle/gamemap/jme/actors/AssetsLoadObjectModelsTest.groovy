package com.anrisoftware.dwarfhustle.gamemap.jme.actors

import static org.mockito.Mockito.*

import org.apache.commons.jcs3.access.CacheAccess
import org.junit.jupiter.api.Test

import com.jme3.asset.AssetManager

/**
 * @see AssetsLoadObjectModels
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class AssetsLoadObjectModelsTest {

    @Test
    void test_loadObjectModels_resource() {
        def engine = new GroovyScriptEngine(
                [
                    AssetsLoadObjectModels.class.getResource("/ObjectModels.groovy")
                ] as URL[])
        def binding = new Binding();
        def m = engine.run("ObjectModels.groovy", binding);
        assert m[809].rid == 809
        assert m[809].model =~ /.*j3o/
    }

    @Test
    void test_loadObjectModels() {
        def am = mock(AssetManager.class)
        def cache = mock(CacheAccess.class)
        def loader = new AssetsLoadObjectModels()
        loader.am = am
        loader.loadObjectModels(cache)
    }
}
