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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.nio.file.Path;

import com.anrisoftware.dwarfhustle.model.actor.DwarfhustleModelActorsModule;
import com.anrisoftware.dwarfhustle.model.api.objects.DwarfhustleModelApiObjectsModule;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.lmbd.DwarfhustleModelDbLmbdModule;
import com.anrisoftware.dwarfhustle.model.db.lmbd.GameObjectsLmbdStorage.GameObjectsLmbdStorageFactory;
import com.anrisoftware.dwarfhustle.model.db.lmbd.MapObjectsLmbdStorage.MapObjectsLmbdStorageFactory;
import com.anrisoftware.dwarfhustle.model.db.store.MapChunksStore;
import com.google.inject.Guice;

import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainLoadGameTest extends AbstractTerrainApp {

    public static void main(String[] args) {
        var injector = Guice.createInjector(new DwarfhustleModelActorsModule(), new DwarfhustleModelApiObjectsModule(),
                new DwarfhustleModelDbLmbdModule());
        var app = injector.getInstance(TerrainLoadGameTest.class);
        app.start(injector);
    }

    @Inject
    private GameObjectsLmbdStorageFactory gameObjectsFactory;

    @Inject
    private MapObjectsLmbdStorageFactory mapObjectsFactory;

    public TerrainLoadGameTest() {
    }

    @Override
    public void simpleInitApp() {
        log.debug("simpleInitApp");
        super.simpleInitApp();
    }

    @Override
    @SneakyThrows
    protected void loadTerrain() {
        var root = Path.of("/home/devent/Projects/dwarf-hustle/docu/terrain-maps/");
        root = root.resolve("game");
        // root = root.resolve("terrain_4_4_4_2");
        root = root.resolve("terrain_32_32_32_8");
        // root = root.resolve("terrain_512_512_128_16");
        initGameObjectsStorage(root);
        loadGameMap();
        initMapStorage(root);
        initMapObjectsStorage(root, gm);
        gm.cursor.z = 8;
        // gm.cursor.z = 16;
        // var block = mcRoot.findBlock(0, 0, 0, id -> store.getChunk(id));
        // block.setMined(true);
        // block.setMaterialRid(898);
    }

    private void initMapObjectsStorage(Path root, GameMap gm) {
        var path = root.resolve("map-" + gm.id);
        if (!path.toFile().isDirectory()) {
            path.toFile().mkdir();
        }
        this.mapObjectsStorage = mapObjectsFactory.create(path, gm);
    }

    private void initGameObjectsStorage(Path root) {
        var path = root.resolve("objects");
        if (!path.toFile().isDirectory()) {
            path.toFile().mkdir();
        }
        this.gameObjectsStorage = gameObjectsFactory.create(path);
    }

    @SneakyThrows
    private void initMapStorage(Path root) {
        var path = root.resolve(String.format("%d-%d.map", wm.id, gm.id));
        this.mapStore = new MapChunksStore(path, gm.width, gm.height, gm.chunkSize, gm.chunksCount);
        this.mcRoot = mapStore.getChunk(0);
    }

    @SneakyThrows
    private void loadGameMap() {
        try (var it = gameObjectsStorage.getObjects(WorldMap.OBJECT_TYPE)) {
            assertThat(it.hasNext(), is(true));
            this.wm = (WorldMap) it.next();
        }
        this.gm = gameObjectsStorage.getObject(GameMap.OBJECT_TYPE, wm.currentMap);
    }

}