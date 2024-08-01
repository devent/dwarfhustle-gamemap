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
package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import org.apache.commons.io.IOUtils;

import com.anrisoftware.dwarfhustle.model.actor.DwarfhustleModelActorsModule;
import com.anrisoftware.dwarfhustle.model.api.objects.DwarfhustleModelApiObjectsModule;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapArea;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.store.MapChunksStore;
import com.anrisoftware.dwarfhustle.model.terrainimage.TerrainImage;
import com.google.inject.Guice;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainImageTest extends AbstractTerrainApp {

    public static void main(String[] args) {
        var injector = Guice.createInjector(new DwarfhustleModelActorsModule(), new DwarfhustleModelApiObjectsModule());
        var app = injector.getInstance(TerrainImageTest.class);
        app.start(injector);
    }

    private TerrainImage terrainImage;

    @Override
    public void simpleInitApp() {
        log.debug("simpleInitApp");
        super.simpleInitApp();
    }

    @Override
    @SneakyThrows
    protected void loadTerrain() {
        this.terrainImage = TerrainImage.terrain_32_32_32_8;
        var file = Files.createTempDirectory("TerrainTest");
        loadGameMap();
        createMapStorage(file);
        // var block = mcRoot.findBlock(0, 0, 0, id -> store.getChunk(id));
        // block.setMined(true);
        // block.setMaterialRid(898);
    }

    @SneakyThrows
    private void createMapStorage(Path root) {
        var fileName = String.format("terrain_%d_%d_%d_%d_%d.map", terrainImage.w, terrainImage.h, terrainImage.d,
                terrainImage.chunkSize, terrainImage.chunksCount);
        var res = new File("/home/devent/Projects/dwarf-hustle/docu/terrain-maps/" + fileName).toURI().toURL();
        assert res != null;
        var file = root.resolve(fileName + ".map");
        IOUtils.copy(res, file.toFile());
        this.mapStore = new MapChunksStore(file, gm.width, gm.height, terrainImage.chunkSize, terrainImage.chunksCount);
        this.mcRoot = mapStore.getChunk(0);
    }

    @SneakyThrows
    private void loadGameMap() {
        gm = new GameMap(ids.generate());
        wm = new WorldMap(ids.generate());
        wm.currentMap = gm.id;
        wm.time = LocalDateTime.of(2023, Month.APRIL, 15, 12, 0);
        wm.distanceLat = 100f;
        wm.distanceLon = 100f;
        gm.world = wm.id;
        gm.chunkSize = terrainImage.chunkSize;
        gm.width = terrainImage.w;
        gm.height = terrainImage.h;
        gm.depth = terrainImage.d;
        gm.area = MapArea.create(50.99819f, 10.98348f, 50.96610f, 11.05610f);
        gm.timeZone = ZoneOffset.ofHours(1);

//        Camera Position: (-6.882555, 0.09426513, 34.164684)
//        Camera Rotation: (0.0, 1.0, 0.0, 0.0)
//        Camera Direction: (0.0, 0.0, -1.0)
//        cam.setLocation(new Vector3f(-6.882555f, 0.09426513f, 34.164684f));
//        cam.setRotation(new Quaternion(0.0f, 1.0f, 0.0f, 0.0f));

        gm.setCameraPos(0.0f, 0.0f, 34f);
        gm.setCameraRot(0.0f, 1.0f, 0.0f, 0.0f);
        gm.setCursorZ(8);
    }

}
