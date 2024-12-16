package com.anrisoftware.dwarfhustle.gamemap.jme.terrain

import static com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject.kid2Id

import org.apache.commons.imaging.Imaging
import org.eclipse.collections.api.factory.primitive.LongObjectMaps
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import com.anrisoftware.dwarfhustle.gamemap.jme.app.AssetsLoadMaterialTextures
import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetCacheObject
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Provides
import com.jme3.asset.AssetManager
import com.jme3.system.JmeSystem

/**
 * @see TextureOverlay
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class TextureOverlayTest {

    static MutableLongObjectMap<AssetCacheObject> texCache

    @BeforeAll
    static void loadTextures() {
        def injector = Guice.createInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                    }
                    @Provides
                    public AssetManager getAssetManager() {
                        def assetCfgUrl = JmeSystem.getPlatformAssetConfigURL();
                        return JmeSystem.newAssetManager(assetCfgUrl);
                    }
                })
        def texLoader = injector.getInstance(AssetsLoadMaterialTextures)
        texCache = LongObjectMaps.mutable.withInitialCapacity(100)
        texLoader.loadMaterialTextures(texCache)
    }

    @Test
    void add_grass_wheat_textures() {
        TextureCacheObject baseTex = texCache.get(kid2Id(857l))
        def overlay = new TextureOverlay(baseTex)
        println Imaging.getImageSize(baseTex.tex.image.data[0].array())
    }
}
