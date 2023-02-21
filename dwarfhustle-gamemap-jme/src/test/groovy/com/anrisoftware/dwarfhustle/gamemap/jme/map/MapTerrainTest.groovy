package com.anrisoftware.dwarfhustle.gamemap.jme.map

import javax.inject.Singleton

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrain.MapTerrainFactory
import com.anrisoftware.dwarfhustle.model.actor.ModelActorsModule
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provides
import com.jme3.app.Application
import com.jme3.app.SimpleApplication
import com.jme3.asset.AssetManager
import com.jme3.asset.DesktopAssetManager

/**
 * @see MapTerrain
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class MapTerrainTest {

    static Injector injector

    static MapTerrainFactory mapTerrainFactory

    @BeforeAll
    static void setup() {
        this.injector = Guice.createInjector(new GamemapJmeMapModule(), new ModelActorsModule(), new AbstractModule() {
                    @Override
                    protected void configure() {
                    }

                    @Provides
                    @Singleton
                    AssetManager getAssetManager() {
                        new DesktopAssetManager()
                    }

                    @Provides
                    @Singleton
                    Application getApplication() {
                        new SimpleApplication() {

                                    @Override
                                    public void simpleInitApp() {
                                    }
                                }
                    }
                })
        this.mapTerrainFactory = injector.getInstance(MapTerrainFactory)
    }

    @Test
    void set_levels() {
        def gm = new GameMap(1)
        gm.width = 8
        gm.height = 8
        gm.depth = 8
        def terrain = mapTerrainFactory.create(gm)
        terrain.setLevels(2)
        assert terrain.terrainLevels.size() == 2
        assert terrain.terrainLevels[0].level == 1
        assert terrain.terrainLevels[1].level == 0
        assert terrain.oldLevels.size() == 0
        terrain.setLevels(4)
        assert terrain.terrainLevels.size() == 4
        assert terrain.terrainLevels[0].level == 3
        assert terrain.terrainLevels[1].level == 2
        assert terrain.terrainLevels[2].level == 1
        assert terrain.terrainLevels[3].level == 0
        assert terrain.oldLevels.size() == 0
        terrain.setLevels(2)
        assert terrain.terrainLevels.size() == 2
        assert terrain.terrainLevels[0].level == 1
        assert terrain.terrainLevels[1].level == 0
        assert terrain.oldLevels.size() == 2
        assert terrain.oldLevels[0].level == 2
        assert terrain.oldLevels[1].level == 3
        terrain.setLevels(4)
        assert terrain.terrainLevels.size() == 4
        assert terrain.terrainLevels[0].level == 3
        assert terrain.terrainLevels[1].level == 2
        assert terrain.terrainLevels[2].level == 1
        assert terrain.terrainLevels[3].level == 0
        assert terrain.oldLevels.size() == 0
    }
}
