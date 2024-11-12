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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.anrisoftware.dwarfhustle.gamemap.model.cache.AppCachesConfig;
import com.anrisoftware.dwarfhustle.model.actor.DwarfhustleModelActorsModule;
import com.anrisoftware.dwarfhustle.model.api.objects.DwarfhustleModelApiObjectsModule;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.lmbd.DwarfhustleModelDbLmbdModule;
import com.anrisoftware.dwarfhustle.model.db.lmbd.GameObjectsLmbdStorage.GameObjectsLmbdStorageFactory;
import com.anrisoftware.dwarfhustle.model.db.lmbd.MapChunksLmbdStorage;
import com.anrisoftware.dwarfhustle.model.db.lmbd.MapChunksLmbdStorage.MapChunksLmbdStorageFactory;
import com.anrisoftware.dwarfhustle.model.db.lmbd.MapObjectsLmbdStorage.MapObjectsLmbdStorageFactory;
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

    @Inject
    private MapChunksLmbdStorageFactory storageFactory;

    private MapChunksLmbdStorage chunksStorage;

    public TerrainLoadGameTest() {
    }

    @Override
    public void simpleInitApp() {
        log.debug("simpleInitApp");
        super.simpleInitApp();
    }

    @Override
    protected void createObjectsCache() {
        var task = StoredObjectsJcsCacheActor.create(injector, CREATE_ACTOR_TIMEOUT, goStorage, goStorage);
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("ObjectsJcsCacheActor.create", ex);
            } else {
                log.debug("ObjectsJcsCacheActor created");
            }
        });
    }

    @Override
    protected void createChunksCache() {
        var task = MapChunksJcsCacheActor.create(injector, CREATE_ACTOR_TIMEOUT, chunksStorage, chunksStorage);
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("MapChunksJcsCacheActor.create", ex);
            } else {
                log.debug("MapChunksJcsCacheActor created");
            }
        });
    }

    @Override
    @SneakyThrows
    protected void loadTerrain() {
        var root = Path.of("/home/devent/Projects/dwarf-hustle/terrain-maps/");
        root = root.resolve("game");
        // loadTerrain(root, "terrain_4_4_4_2", 1);
        // loadTerrain(root, "terrain_32_32_32_8", 9);
        loadTerrain(root, "terrain_512_512_128_16", 171, 189, 18, new float[] { -180.88005f, 114.93917f, 55.877968f },
                new float[] { 0.0f, 1.0f, 0.0f, 0.0f });
        // var block = mcRoot.findBlock(0, 0, 0, id -> store.getChunk(id));
        // block.setMined(true);
        // block.setMaterialRid(898);
    }

    private void loadTerrain(Path root, String name, int x, int y, int z, float[] cameraPos, float[] cameraRot)
            throws IOException {
        root = root.resolve(name);
        var tmp = Files.createTempDirectory(name);
        injector.getInstance(AppCachesConfig.class).create(tmp.toFile());
        initGameObjectsStorage(root);
        loadGameMap();
        this.chunksStorage = initMapStorage(root);
        initMapObjectsStorage(root, gm);
        gm.cursor.x = x;
        gm.cursor.y = y;
        gm.cursor.z = z;
        gm.cameraPos = cameraPos;
        gm.cameraRot = cameraRot;
    }

    private void initMapObjectsStorage(Path root, GameMap gm) {
        var path = root.resolve("map-" + gm.id);
        if (!path.toFile().isDirectory()) {
            path.toFile().mkdir();
        }
        this.moStorage = mapObjectsFactory.create(path, gm);
    }

    private void initGameObjectsStorage(Path root) {
        var path = root.resolve("objects");
        if (!path.toFile().isDirectory()) {
            path.toFile().mkdir();
        }
        this.goStorage = gameObjectsFactory.create(path);
    }

    @SneakyThrows
    private MapChunksLmbdStorage initMapStorage(Path root) {
        var path = root.resolve(String.format("%d-%d", wm.id, gm.id));
        var storage = storageFactory.create(path, gm.chunkSize);
        this.mcRoot = storage.getChunk(0);
        return storage;
    }

    @SneakyThrows
    private void loadGameMap() {
        try (var it = goStorage.getObjects(WorldMap.OBJECT_TYPE)) {
            assertThat(it.hasNext(), is(true));
            this.wm = (WorldMap) it.next();
        }
        this.gm = goStorage.getObject(GameMap.OBJECT_TYPE, wm.currentMap);
    }

}
