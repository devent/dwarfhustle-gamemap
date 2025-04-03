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

import static akka.actor.typed.javadsl.AskPattern.ask;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.getChunk;
import static java.lang.Math.pow;
import static java.time.Duration.ofSeconds;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ForkJoinPool;

import org.apache.commons.lang3.StringUtils;
import org.dwarfhustle.gamemap.tester.gui.javafx.actor.DwarfhustleGamemapTesterGuiJavafxActorModule;
import org.dwarfhustle.gamemap.tester.gui.javafx.actor.TesterMainPanelActor;

import com.anrisoftware.dwarfhustle.gamemap.model.cache.AppCachesConfig;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.DwarfhustleGamemapModelResourcesModule;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.DwarfhustleGamemapGuiJavafxUtilsModule;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AttachGuiMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AttachGuiMessage.AttachGuiFinishedMessage;
import com.anrisoftware.dwarfhustle.model.actor.DwarfhustleModelActorsModule;
import com.anrisoftware.dwarfhustle.model.api.objects.DwarfhustleModelApiObjectsModule;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer;
import com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StringObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.lmbd.DwarfhustleModelDbLmbdModule;
import com.anrisoftware.dwarfhustle.model.db.lmbd.GameObjectsLmbdStorage.GameObjectsLmbdStorageFactory;
import com.anrisoftware.dwarfhustle.model.db.lmbd.MapChunksLmbdStorage;
import com.anrisoftware.dwarfhustle.model.db.lmbd.MapChunksLmbdStorage.MapChunksLmbdStorageFactory;
import com.anrisoftware.dwarfhustle.model.db.lmbd.MapObjectsLmbdStorage;
import com.anrisoftware.dwarfhustle.model.db.lmbd.MapObjectsLmbdStorage.MapObjectsLmbdStorageFactory;
import com.anrisoftware.dwarfhustle.model.db.strings.DwarfhustleModelDbStringsModule;
import com.anrisoftware.dwarfhustle.model.db.strings.StringsLuceneStorage;
import com.anrisoftware.dwarfhustle.model.db.strings.StringsLuceneStorage.StringsLuceneStorageFactory;
import com.anrisoftware.resources.binary.internal.maps.BinariesDefaultMapsModule;
import com.anrisoftware.resources.binary.internal.resources.BinaryResourceModule;
import com.anrisoftware.resources.images.internal.images.ImagesResourcesModule;
import com.anrisoftware.resources.images.internal.mapcached.ResourcesImagesCachedMapModule;
import com.anrisoftware.resources.images.internal.scaling.ResourcesSmoothScalingModule;
import com.anrisoftware.resources.texts.internal.texts.TextsResourcesDefaultModule;
import com.anrisoftware.resources.texts.internal.texts.TextsResourcesModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainLoadGameTest extends AbstractTerrainApp {

    private static final Duration LOAD_MAP_OBJECTS_TIMEOUT = Duration.ofSeconds(30);

    public static void main(String[] args) {
        final var injector = Guice.createInjector(new DwarfhustleModelActorsModule(),
                new DwarfhustleModelApiObjectsModule(), new DwarfhustleModelDbLmbdModule(),
                new DwarfhustleModelDbStringsModule());
        final var app = injector.getInstance(TerrainLoadGameTest.class);
        app.start(injector);
    }

    @Inject
    private GameObjectsLmbdStorageFactory gameObjectsFactory;

    @Inject
    private MapObjectsLmbdStorageFactory mapObjectsFactory;

    @Inject
    private MapChunksLmbdStorageFactory storageFactory;

    @Inject
    private StringsLuceneStorageFactory stringsFactory;

    private MapChunksLmbdStorage chunksStorage;

    public TerrainLoadGameTest() {
    }

    @Override
    public void simpleInitApp() {
        log.debug("simpleInitApp");
        createTesterMainPanel();
        super.simpleInitApp();
    }

    @Override
    protected com.google.inject.Module getAdditionalModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                install(new BinaryResourceModule());
                install(new BinariesDefaultMapsModule());
                install(new ImagesResourcesModule());
                install(new ResourcesImagesCachedMapModule());
                install(new ResourcesSmoothScalingModule());
                install(new TextsResourcesDefaultModule());
                install(new TextsResourcesModule());
                install(new DwarfhustleGamemapModelResourcesModule());
                install(new DwarfhustleGamemapGuiJavafxUtilsModule());
                install(new DwarfhustleGamemapTesterGuiJavafxActorModule());
            }
        };
    }

    @Override
    protected void createMapObjectsCache() {
        final var task = MapObjectsJcsCacheActor.create(injector, CREATE_ACTOR_TIMEOUT, moStorage, moStorage);
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("MapObjectsJcsCacheActor.create", ex);
            } else {
                log.debug("MapObjectsJcsCacheActor created");
            }
        });
    }

    @Override
    protected void createStoredObjectsCache() {
        val task = StoredObjectsJcsCacheActor.create(injector, CREATE_ACTOR_TIMEOUT, goStorage, goStorage);
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("StoredObjectsJcsCacheActor.create", ex);
            } else {
                log.debug("StoredObjectsJcsCacheActor created");
            }
        });
    }

    @Override
    protected void createStringObjectCache() {
        val task = StringObjectsJcsCacheActor.create(injector, CREATE_ACTOR_TIMEOUT, soStorage, soStorage);
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("StringObjectsJcsCacheActor.create", ex);
            } else {
                log.debug("StringObjectsJcsCacheActor created");
            }
        });
    }

    @Override
    protected void createChunksCache() {
        final var task = MapChunksJcsCacheActor.create(injector, CREATE_ACTOR_TIMEOUT, chunksStorage, chunksStorage);
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("MapChunksJcsCacheActor.create", ex);
            } else {
                log.debug("MapChunksJcsCacheActor created");
            }
        });
    }

    protected void createTesterMainPanel() {
        final var task = TesterMainPanelActor.create(injector, CREATE_ACTOR_TIMEOUT);
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("MapChunksJcsCacheActor.create", ex);
            } else {
                log.debug("MapChunksJcsCacheActor created");
                final CompletionStage<AttachGuiFinishedMessage> result = ask(ret, AttachGuiMessage::new, ofSeconds(3),
                        actor.getScheduler());
                result.whenComplete((ret1, ex1) -> {
                    if (ex1 != null) {
                        log.error("AttachGuiMessage", ex1);
                    } else {
                        log.debug("AttachGuiMessage done");
                    }
                });
            }
        });
    }

    @Override
    @SneakyThrows
    protected void loadTerrain() {
        var root = Path.of("/home/devent/Projects/dwarf-hustle/terrain-maps/");
        if (StringUtils.startsWith(System.getProperty("os.name"), "Windows")) {
            var user = System.getProperty("user.home");
            root = Path.of(user + "/Projects/dwarf-hustle/terrain-maps/");
        } else {
            root = root.resolve("game");
        }
        // loadTerrain(root, "terrain_4_4_4_2", 1);
        loadTerrain(root, "terrain_32_32_32_8", 9);
//        loadTerrain(root, "terrain_512_512_128_16", 171, 189, 18, new float[] { -180.88005f, 114.93917f, 55.877968f },
//                new float[] { 0.0f, 1.0f, 0.0f, 0.0f });
    }

    private void loadTerrain(Path root, String name, int z) throws IOException {
        assertThat("Terrain root directory exist", root.resolve(name).toFile().isDirectory(), is(true));
        loadTerrain(root, name, 0, 0, z, new float[] { 0.0f, 0.0f, 50.821163f },
                new float[] { 0.0f, 1.0f, 0.0f, 0.0f });
    }

    private void loadTerrain(Path root, String name, int x, int y, int z, float[] cameraPos, float[] cameraRot)
            throws IOException {
        root = root.resolve(name);
        final var tmp = Files.createTempDirectory(name);
        injector.getInstance(AppCachesConfig.class).create(tmp.toFile());
        initGameObjectsStorage(root);
        loadGameMap();
        this.chunksStorage = initMapStorage(root);
        this.moStorage = initMapObjectsStorage(root, gm);
        this.soStorage = initStringsStorage(root);
        gm.setCursor(x, y, z);
        gm.setCameraPos(cameraPos);
        gm.setCameraRot(cameraRot);
        MapChunkBuffer.cacheCids(gm, chunksStorage);
    }

    @SneakyThrows
    @Override
    protected void loadMapObjects() {
        try (final var pool = new ForkJoinPool(4)) {
            val mg = actor.getObjectGetterAsyncNow(MapObjectsJcsCacheActor.ID);
            val ms = actor.getObjectSetterAsyncNow(MapObjectsJcsCacheActor.ID);
            val root = getChunk(chunksStorage, 0);
            pool.invoke(new LoadMapObjectsAction(root, chunksStorage, moStorage, mg, ms, LOAD_MAP_OBJECTS_TIMEOUT, gm,
                    0, 0, 0, gm.getWidth(), gm.getHeight(), gm.getDepth()));
        }
    }

    private MapObjectsLmbdStorage initMapObjectsStorage(Path root, GameMap gm) {
        final var path = root.resolve("map-" + gm.getId());
        if (!path.toFile().isDirectory()) {
            path.toFile().mkdir();
        }
        final long mapSize = 200 * (long) pow(10, 6);
        return mapObjectsFactory.create(path, gm, mapSize);
    }

    private void initGameObjectsStorage(Path root) {
        final var path = root.resolve("objects");
        if (!path.toFile().isDirectory()) {
            path.toFile().mkdir();
        }
        final long mapSize = 200 * (long) pow(10, 6);
        this.goStorage = gameObjectsFactory.create(path, mapSize);
    }

    @SneakyThrows
    private MapChunksLmbdStorage initMapStorage(Path root) {
        var path = root.resolve(String.format("%d-%d", wm.getId(), gm.getId()));
        var storage = storageFactory.create(path,
                gm.getChunksCount() * MapChunkBuffer.SIZE_MIN + gm.getBlocksCount() * MapBlockBuffer.SIZE);
        this.mcRoot = storage.getChunk(0);
        return storage;
    }

    @SneakyThrows
    private StringsLuceneStorage initStringsStorage(Path root) {
        var path = root.resolve("strings");
        var storage = stringsFactory.create(path);
        return storage;
    }

    @SneakyThrows
    private void loadGameMap() {
        try (var it = goStorage.getObjects(WorldMap.OBJECT_TYPE)) {
            assertThat(it.hasNext(), is(true));
            this.wm = (WorldMap) it.next();
        }
        this.gm = goStorage.getObject(GameMap.OBJECT_TYPE, wm.getCurrentMap());
    }

}
