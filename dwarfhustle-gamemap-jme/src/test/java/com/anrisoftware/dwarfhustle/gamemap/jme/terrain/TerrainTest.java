/*
 * Dwarf Hustle Game Map - Game map.
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

import static com.anrisoftware.dwarfhustle.model.db.cache.CachePutsMessage.askCachePuts;
import static java.time.Duration.ofSeconds;

import java.time.Duration;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.LongObjectMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectLongMaps;
import org.lable.oss.uniqueid.IDGenerator;

import com.anrisoftware.dwarfhustle.gamemap.jme.actors.DwarfhustleGamemapActorsModule;
import com.anrisoftware.dwarfhustle.gamemap.jme.actors.MaterialAssetsJcsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.actors.ModelsAssetsJcsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.map.DebugCoordinateAxesState;
import com.anrisoftware.dwarfhustle.gamemap.jme.terrain.MockStoredObjectsJcsCacheActor.MockStoredObjectsJcsCacheActorFactory;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppPausedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AssetsResponseMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.DwarfhustleModelActorsModule;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.DwarfhustleModelApiObjectsModule;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameChunkPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.IdsObjectsProvider.IdsObjects;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.NeighboringDir;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage.CacheErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage.CacheSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.DwarfhustleModelDbcacheModule;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DwarfhustleModelDbStoragesSchemasModule;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.DwarfhustlePowerloomModule;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.badlogic.ashley.core.Engine;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.jme3.app.Application;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.LostFocusBehavior;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ConstantVerifierState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.AskPattern;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainTest extends SimpleApplication {

    public static void main(String[] args) {
        var injector = Guice.createInjector(new DwarfhustleModelActorsModule(), new DwarfhustleModelApiObjectsModule());
        var app = injector.getInstance(TerrainTest.class);
        app.start(injector);
    }

    @Inject
    private ActorSystemProvider actor;

    @Inject
    @IdsObjects
    private IDGenerator ids;

    private Injector injector;

    private Engine engine;

    private GameMap gm;

    private MapChunk mcRoot;

    private ObjectsGetter og;

    private LongObjectMap<GameObject> backendIdsObjects;

    private boolean texturesLoaded = false;

    private boolean modelsLoaded = false;

    private Consumer<Float> simpleUpdateCall;

    private long[] terrain;

    private Deque<byte[]> idsBatch;

    private ResetCameraState resetCameraState;

    public TerrainTest() {
        super(new StatsAppState(), new ConstantVerifierState(), new DebugKeysAppState()
        // , new FlyCamAppState()
        );
        this.resetCameraState = new ResetCameraState();
        getStateManager().attach(resetCameraState);
    }

    private void start(Injector parent) {
        this.backendIdsObjects = LongObjectMaps.mutable.empty();
        this.engine = new Engine();
        this.og = new ObjectsGetter() {

            @SuppressWarnings("unchecked")
            @Override
            public <T extends GameObject> T get(Class<T> typeClass, String type, Object key)
                    throws ObjectsGetterException {
                return (T) backendIdsObjects.get((long) key);
            }
        };
        this.injector = parent.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new DwarfhustleGamemapJmeTerrainModule());
                install(new DwarfhustlePowerloomModule());
                install(new DwarfhustleModelDbStoragesSchemasModule());
                install(new DwarfhustleModelDbcacheModule());
                install(new DwarfhustleGamemapActorsModule());
                install(new FactoryModuleBuilder()
                        .implement(MockStoredObjectsJcsCacheActor.class, MockStoredObjectsJcsCacheActor.class)
                        .build(MockStoredObjectsJcsCacheActorFactory.class));
            }

            @Provides
            public Application getApp() {
                return TerrainTest.this;
            }

            @Provides
            public Camera getCamera() {
                return TerrainTest.this.getCamera();
            }

            @Provides
            public InputManager getInputManager() {
                return TerrainTest.this.getInputManager();
            }

            @Provides
            @Named("rootNode")
            public Node getRootNode() {
                return TerrainTest.this.getRootNode();
            }

            @Provides
            public AssetManager getAssetManger() {
                return TerrainTest.this.assetManager;
            }

            @Provides
            public LongObjectMap<GameObject> getBackendIdsObjects() {
                return backendIdsObjects;
            }

            @Provides
            public Engine getEngine() {
                return engine;
            }

        });
        createMockTerrain();
        setupApp();
        start();
    }

    @Override
    public void simpleInitApp() {
        log.debug("simpleInitApp");
        this.simpleUpdateCall = tpl -> {
            if (texturesLoaded && modelsLoaded) {
                actor.tell(new SetGameMapMessage(gm));
                resetCameraState.updateCamera(gm);
                simpleUpdateCall = tpl1 -> {
                };
            }
        };
        createMaterialAssets();
        createModelsAssets();
        createTerrain();
        createPowerLoom();
        createObjectsCache();
    }

    @Override
    public void gainFocus() {
        super.gainFocus();
        if (!paused) {
            actor.tell(new AppPausedMessage(false));
        }
    }

    @Override
    public void loseFocus() {
        super.loseFocus();
        if (paused) {
            actor.tell(new AppPausedMessage(true));
        }
    }

    private void createMockTerrain() {
        createGameMap();
        createMap(mcRoot, 0, 0, 0, gm.width, gm.height, gm.depth);
        createNeighbors((MapChunk) backendIdsObjects.get(gm.getRootid()));
        putObjectToBackend(gm);
        var block = mcRoot.findMapBlock(0, 0, 0, id -> (MapChunk) backendIdsObjects.get(id));
        block.setMined(true);
        block.setMaterialRid(898);
    }

    @SneakyThrows
    private void createGameMap() {
        this.mcRoot = new MapChunk(ids.generate());
        this.gm = new GameMap(ids.generate());
//        gm.chunkSize = 16;
//        gm.width = 128;
//        gm.height = 128;
//        gm.depth = 128;
//        gm.chunkSize = 8;
//        gm.width = 64;
//        gm.height = 64;
//        gm.depth = 64;
//        gm.chunkSize = 2;
//        gm.width = 8;
//        gm.height = 8;
//        gm.depth = 8;
//        gm.chunkSize = 2;
//        gm.width = 4;
//        gm.height = 4;
//        gm.depth = 4;
        gm.chunkSize = 1;
        gm.width = 2;
        gm.height = 2;
        gm.depth = 2;
        gm.setCenterOffset(gm.width / 2f);
        gm.setBlockSize(2f);
        gm.rootid = mcRoot.getId();
//        gm.setCameraPos(0.0f, 0.0f, 83.0f);
        gm.setCameraPos(0.0f, 0.0f, 12.0f);
        gm.setCameraRot(0.0f, 1.0f, 0.0f, 0.0f);
        int ground_depth = Math.round(gm.depth * 0.5f);
        int magma_depth = Math.round(gm.depth * 0.9f);
        this.terrain = new long[gm.depth];
        for (int z = 0; z < gm.depth; z++) {
            terrain[z] = 889; // LOAMY-SAND
        }
//        for (int z = 0; z < ground_depth; z++) {
//            terrain[z] = 898; // oxygen
//        }
//        terrain[ground_depth] = 890; // topsoil
//        terrain[ground_depth + 1] = 888; // topsoil
//        for (int z = ground_depth + 2; z < magma_depth; z++) { // rock
//            terrain[z] = 825;
//        }
//        for (int z = magma_depth; z < gm.depth; z++) { // magma
//            terrain[z] = 815;
//        }
        // gm.setCursorZ(ground_depth + 1);
        gm.setCursorZ(0);
    }

    private void setupApp() {
        getStateManager().attach(injector.getInstance(DebugCoordinateAxesState.class));
        var s = new AppSettings(true);
        s.setResizable(true);
        s.setWidth(1280);
        s.setHeight(960);
        s.setVSync(false);
        s.setOpenCLSupport(false);
        setLostFocusBehavior(LostFocusBehavior.PauseOnLostFocus);
        setSettings(s);
    }

    private void createTerrain() {
        TerrainActor.create(injector, ofSeconds(1), CompletableFuture.supplyAsync(() -> og),
                actor.getObjectsAsync(MaterialAssetsJcsCacheActor.ID),
                actor.getObjectsAsync(ModelsAssetsJcsCacheActor.ID)).whenComplete((ret, ex) -> {
                    if (ex != null) {
                        log.error("TerrainActor.create", ex);
                        actor.tell(new AppErrorMessage(ex));
                    } else {
                        log.debug("TerrainActor created");
                    }
                });
    }

    private void createPowerLoom() {
        PowerLoomKnowledgeActor.create(injector, ofSeconds(1), actor.getActorAsync(StoredObjectsJcsCacheActor.ID))
                .whenComplete((ret, ex) -> {
                    if (ex != null) {
                        log.error("PowerLoomKnowledgeActor.create", ex);
                        actor.tell(new AppErrorMessage(ex));
                    } else {
                        log.debug("PowerLoomKnowledgeActor created");
                        createKnowledgeCache(ret);
                    }
                });
    }

    private void createKnowledgeCache(ActorRef<Message> powerLoom) {
        KnowledgeJcsCacheActor.create(injector, ofSeconds(10), actor.getObjectsAsync(PowerLoomKnowledgeActor.ID))
                .whenComplete((ret, ex) -> {
                    if (ex != null) {
                        log.error("KnowledgeJcsCacheActor.create", ex);
                        actor.tell(new AppErrorMessage(ex));
                    } else {
                        log.debug("KnowledgeJcsCacheActor created");
                    }
                });
    }

    private void createObjectsCache() {
        var task = MockStoredObjectsJcsCacheActor.create(injector, Duration.ofSeconds(30),
                CompletableFuture.supplyAsync(() -> og));
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("ObjectsJcsCacheActor.create", ex);
            } else {
                log.debug("ObjectsJcsCacheActor created");
                cacheAllObjects();
            }
        });
    }

    private void createMaterialAssets() {
        var task = MaterialAssetsJcsCacheActor.create(injector, Duration.ofSeconds(30));
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("MaterialAssetsJcsCacheActor.create", ex);
            } else {
                log.debug("MaterialAssetsJcsCacheActor created");
                loadTextures();
            }
        });
    }

    private void createModelsAssets() {
        var task = ModelsAssetsJcsCacheActor.create(injector, Duration.ofSeconds(30));
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("ModelsAssetsJcsCacheActor.create", ex);
            } else {
                log.debug("ModelsAssetsJcsCacheActor created");
                loadModels();
            }
        });
    }

    private void loadTextures() {
        CompletionStage<AssetsResponseMessage<?>> result = AskPattern.ask(actor.get(), LoadTexturesMessage::new,
                Duration.ofSeconds(60), actor.getScheduler());
        result.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("LoadTexturesMessage", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("LoadTexturesMessage {}", ret);
                texturesLoaded = true;
            }
        });
    }

    private void loadModels() {
        CompletionStage<AssetsResponseMessage<?>> result = AskPattern.ask(actor.get(), LoadModelsMessage::new,
                Duration.ofSeconds(30), actor.getScheduler());
        result.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("LoadModelsMessage", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("LoadModelsMessage {}", ret);
                modelsLoaded = true;
            }
        });
    }

    private void createNeighbors(MapChunk rootc) {
        var backend = (MutableLongObjectMap<GameObject>) this.backendIdsObjects;
        int mapid = rootc.pos.mapid;
        var pos = rootc.pos;
        int xs = (pos.ep.x - pos.x) / 2;
        int ys = (pos.ep.y - pos.y) / 2;
        int zs = (pos.ep.z - pos.z) / 2;
        Function<Long, MapChunk> r = id -> (MapChunk) backend.get(id);
        for (int x = pos.x; x < pos.ep.x; x += xs) {
            for (int y = pos.y; y < pos.ep.y; y += ys) {
                for (int z = pos.z; z < pos.ep.z; z += zs) {
                    var chunk = (MapChunk) backend
                            .get(rootc.getChunks().get(new GameChunkPos(mapid, x, y, z, x + xs, y + ys, z + zs)));
                    if (xs > gm.getChunkSize() && ys > gm.getChunkSize() && zs > gm.getChunkSize()) {
                        createNeighbors(chunk);
                    }
                    int bz = z + zs;
                    int tz = z - zs;
                    int sy = y + zs;
                    int ny = y - zs;
                    int ex = x + zs;
                    int wx = x - zs;
                    long b, t, s, n, e, w;
                    if ((b = mcRoot.findChild(x, y, bz, x + xs, y + ys, bz + zs, r)) != 0) {
                        chunk.setNeighborBottom(b);
                    }
                    if ((t = mcRoot.findChild(x, y, tz, x + xs, y + ys, tz + zs, r)) != 0) {
                        chunk.setNeighborTop(t);
                    }
                    if ((s = mcRoot.findChild(x, sy, z, x + xs, sy + ys, z + zs, r)) != 0) {
                        chunk.setNeighborSouth(s);
                    }
                    if ((n = mcRoot.findChild(x, ny, z, x + xs, ny + ys, z + zs, r)) != 0) {
                        chunk.setNeighborNorth(n);
                    }
                    if ((e = mcRoot.findChild(ex, y, z, ex + xs, y + ys, z + zs, r)) != 0) {
                        chunk.setNeighborEast(e);
                    }
                    if ((w = mcRoot.findChild(wx, y, z, wx + xs, y + ys, z + zs, r)) != 0) {
                        chunk.setNeighborWest(w);
                    }
                    if (chunk.getBlocks().notEmpty()) {
                        chunk.getBlocks().forEachValue(mb -> setupBlockNeighbors(chunk, mb));
                    }
                    putObjectToBackend(chunk);
                }
            }
        }
    }

    private void setupBlockNeighbors(MapChunk chunk, MapBlock mb) {
        var pos = mb.pos;
        var t = pos.addZ(-1);
        var tb = chunk.getBlock(t);
        if (tb.isPresent()) {
            mb.setNeighborTop(tb.get().getId());
        } else {
            long chunkid;
            if ((chunkid = chunk.getNeighborTop()) != 0) {
                var c = (MapChunk) backendIdsObjects.get(chunkid);
                tb = c.getBlock(t);
                if (tb.isPresent()) {
                    mb.setNeighborTop(tb.get().getId());
                }
            }
        }
        for (var d : NeighboringDir.values()) {
            var b = pos.add(d.pos);
            var bb = chunk.getBlock(b);
            if (bb.isPresent()) {
                mb.setNeighbor(d, bb.get().getId());
            } else {
                long chunkid;
                if ((chunkid = chunk.getNeighbor(d)) != 0) {
                    var c = (MapChunk) backendIdsObjects.get(chunkid);
                    bb = c.getBlock(b);
                    if (bb.isPresent()) {
                        mb.setNeighbor(d, bb.get().getId());
                    }
                }
            }
        }
        putObjectToBackend(mb);
    }

    @SneakyThrows
    private void createMap(MapChunk chunk, int sx, int sy, int sz, int ex, int ey, int ez) {
        chunk.setPos(new GameChunkPos(gm.mapid, sx, sy, sz, ex, ey, ez));
        if (chunk.pos.ep.x == gm.width && chunk.pos.ep.y == gm.height && chunk.pos.ep.z == gm.depth) {
            chunk.setRoot(true);
        }
        MutableObjectLongMap<GameChunkPos> chunks = ObjectLongMaps.mutable.empty();
        this.idsBatch = ids.batch(128);
        Supplier<byte[]> idsSupplier = this::supplyIds;
        int cs = (ex - sx) / 2;
        for (int xx = sx; xx < ex; xx += cs) {
            for (int yy = sy; yy < ey; yy += cs) {
                for (int zz = sz; zz < ez; zz += cs) {
                    createChunk(idsSupplier, terrain, chunk, chunks, gm.mapid, xx, yy, zz, xx + cs, yy + cs, zz + cs);
                }
            }
        }
        chunk.setChunks(chunks);
        chunk.updateCenterExtent(gm.centerOffsetX, gm.centerOffsetY, gm.centerOffsetZ, gm.blockSizeX, gm.blockSizeY,
                gm.blockSizeZ);
        putObjectToBackend(chunk);
    }

    @SneakyThrows
    private byte[] supplyIds() {
        var next = idsBatch.poll();
        if (next == null) {
            idsBatch = ids.batch(128);
            next = idsBatch.poll();
        }
        return next;
    }

    @SneakyThrows
    private void createChunk(Supplier<byte[]> ids, long[] terrain, MapChunk parent,
            MutableObjectLongMap<GameChunkPos> chunks, int mapid, int x, int y, int z, int ex, int ey, int ez) {
        var chunk = new MapChunk(this.ids.generate());
        chunk.setParent(parent.getId());
        chunk.setPos(new GameChunkPos(mapid, x, y, z, ex, ey, ez));
        chunk.updateCenterExtent(gm.centerOffsetX, gm.centerOffsetY, gm.centerOffsetZ, gm.blockSizeX, gm.blockSizeY,
                gm.blockSizeZ);
        int csize = gm.getChunkSize();
        if (ex - x == csize && ey - y == csize && ez - z == csize) {
            MutableMap<GameBlockPos, MapBlock> blocks = Maps.mutable.empty();
            for (int xx = x; xx < x + csize; xx++) {
                for (int yy = y; yy < y + csize; yy++) {
                    for (int zz = z; zz < z + csize; zz++) {
                        var mb = new MapBlock(ids.get());
                        mb.pos = new GameBlockPos(mapid, xx, yy, zz);
                        mb.setMaterialRid(terrain[zz]);
                        mb.setObjectRid(809);
                        if (mb.getMaterialRid() == 898) {
                            mb.setMined(true);
                        }
                        blocks.put(mb.pos, mb);
                    }
                }
            }
            chunk.setBlocks(blocks);
        } else {
            createMap(chunk, x, y, z, ex, ey, ez);
        }
        chunks.put(chunk.pos, chunk.getId());
        putObjectToBackend(chunk);
    }

    private void putObjectToBackend(GameObject go) {
        var backend = (MutableLongObjectMap<GameObject>) this.backendIdsObjects;
        backend.put(go.getId(), go);
    }

    @SneakyThrows
    private void cacheAllObjects() {
        log.debug("cacheAllObjects");
        var cache = actor.getActorAsync(StoredObjectsJcsCacheActor.ID).toCompletableFuture().get(30, TimeUnit.SECONDS);
        askCachePuts(cache, Long.class, GameObject::getId, backendIdsObjects.values(), ofSeconds(10),
                actor.getScheduler()).whenComplete((reply, failure) -> {
                    if (failure != null) {
                        log.error("Cache failure", failure);
                    } else {
                        if (reply instanceof CacheSuccessMessage<?> m) {
                            log.debug("CacheSuccessMessage {}", m);
                        } else if (reply instanceof CacheErrorMessage<?> em) {
                            log.error("CacheErrorMessage", em.error);
                        } else {
                            log.error("CachePutMessage", failure);
                        }
                    }
                });
    }

    @Override
    public void stop(boolean waitFor) {
        actor.get().tell(new ShutdownMessage());
        super.stop(waitFor);
    }

    @Override
    public void simpleUpdate(float tpf) {
        simpleUpdateCall.accept(tpf);
        engine.update(tpf);
    }
}
