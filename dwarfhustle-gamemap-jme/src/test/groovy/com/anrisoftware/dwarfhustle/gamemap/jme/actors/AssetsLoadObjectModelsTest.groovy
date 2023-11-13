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
