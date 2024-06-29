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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.eclipse.collections.api.map.primitive.LongObjectMap;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;
import org.lable.oss.uniqueid.IDGenerator;

import com.anrisoftware.dwarfhustle.gamemap.jme.app.DwarfhustleGamemapJmeAppModule;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.GameTickActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.MaterialAssetsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.ModelsAssetsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.lights.DwarfhustleGamemapJmeLightsModule;
import com.anrisoftware.dwarfhustle.gamemap.jme.lights.SunActor;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppPausedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AssetsResponseMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.StartTerrainForGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.DwarfhustleModelActorsModule;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.DwarfhustleModelApiObjectsModule;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.IdsObjectsProvider.IdsObjects;
import com.anrisoftware.dwarfhustle.model.api.objects.MapArea;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunksStore;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.cache.DwarfhustleModelDbCacheModule;
import com.anrisoftware.dwarfhustle.model.db.cache.DwarfhustleModelDbMockCacheModule;
import com.anrisoftware.dwarfhustle.model.db.cache.MockStoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DwarfhustleModelDbStoragesSchemasModule;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.DwarfhustlePowerloomModule;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.anrisoftware.dwarfhustle.model.terrainimage.TerrainImage;
import com.badlogic.ashley.core.Engine;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.jme3.app.Application;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ConstantVerifierState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.AskPattern;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class AbstractTerrainApp extends SimpleApplication {

    private static final Duration CREATE_ACTOR_TIMEOUT = Duration.ofSeconds(30);

    public static void main(String[] args) {
        var injector = Guice.createInjector(new DwarfhustleModelActorsModule(), new DwarfhustleModelApiObjectsModule());
        var app = injector.getInstance(AbstractTerrainApp.class);
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

    private final ResetCameraState resetCameraState;

    private WorldMap wm;

    private TerrainImage terrainImage;

    private MapChunksStore store;

    private Node sceneNode;

    public AbstractTerrainApp() {
        super(new StatsAppState(), new ConstantVerifierState(), new DebugKeysAppState()
        // , new FlyCamAppState()
        );
        this.resetCameraState = new ResetCameraState();
        getStateManager().attach(resetCameraState);
    }

    public void start(Injector parent) {
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
                install(new DwarfhustleModelDbCacheModule());
                install(new DwarfhustleGamemapJmeAppModule());
                install(new DwarfhustleGamemapJmeLightsModule());
                install(new DwarfhustleModelDbMockCacheModule());
                bind(GameSettingsProvider.class).asEagerSingleton();
            }

            @Provides
            public Application getApp() {
                return AbstractTerrainApp.this;
            }

            @Provides
            public Camera getCamera() {
                return AbstractTerrainApp.this.getCamera();
            }

            @Provides
            public InputManager getInputManager() {
                return AbstractTerrainApp.this.getInputManager();
            }

            @Provides
            @Named("rootNode")
            public Node getRootNode() {
                return AbstractTerrainApp.this.getRootNode();
            }

            @Provides
            @Named("sceneNode")
            public Node getSceneNode() {
                return AbstractTerrainApp.this.sceneNode;
            }

            @Provides
            public AssetManager getAssetManger() {
                return AbstractTerrainApp.this.assetManager;
            }

            @Provides
            public LongObjectMap<GameObject> getBackendIdsObjects() {
                return backendIdsObjects;
            }

            @Provides
            public Engine getEngine() {
                return engine;
            }

            @Provides
            public ViewPort getViewPort() {
                return AbstractTerrainApp.this.viewPort;
            }

        });
        createMockTerrain();
        setupApp();
        start();
    }

    @Override
    public void simpleInitApp() {
        log.debug("simpleInitApp");
        this.sceneNode = new Node("Scene-Node");
        rootNode.attachChild(sceneNode);
        this.simpleUpdateCall = tpl -> {
            if (texturesLoaded && modelsLoaded) {
                actor.tell(new SetGameMapMessage(gm));
                actor.tell(new StartTerrainForGameMapMessage(gm, store));
                resetCameraState.updateCamera(gm);
                simpleUpdateCall = tpl1 -> {
                };
            }
        };
        createMaterialAssets();
        createModelsAssets();
        createTerrain();
        createSun();
        createPowerLoom();
        createObjectsCache();
        createGameTick();
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
        this.terrainImage = TerrainImage.terrain_32_32_32_8;
        createGameMap();
        createMapStorage();
        // var block = mcRoot.findBlock(0, 0, 0, id -> store.getChunk(id));
        // block.setMined(true);
        // block.setMaterialRid(898);
    }

    @SneakyThrows
    private void createMapStorage() {
        var fileName = String.format("terrain_%d_%d_%d_%d_%d.map", terrainImage.w, terrainImage.h, terrainImage.d,
                terrainImage.chunkSize, terrainImage.chunksCount);
        var file = Files.createTempDirectory("TerrainTest").resolve(fileName);
        var res = new File("/home/devent/Projects/dwarf-hustle/docu/terrain-maps/" + fileName).toURI().toURL();
        assert res != null;
        IOUtils.copy(res, file.toFile());
        this.store = new MapChunksStore(file, gm.width, gm.height, terrainImage.chunkSize, terrainImage.chunksCount);
        this.mcRoot = store.getChunk(0);
    }

    @SneakyThrows
    private void createGameMap() {
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
        var gs = injector.getInstance(GameSettingsProvider.class).get();
        gs.visibleDepthLayers.set(4);
        gs.currentMap.set(gm);
        gs.currentWorld.set(wm);
    }

    private void setupApp() {
        getStateManager().attach(injector.getInstance(DebugCoordinateAxesState.class));
        var s = new AppSettings(true);
        s.setResizable(true);
        // 960x720
        s.setWidth(960);
        s.setHeight(720);
        s.setVSync(false);
        s.setOpenCLSupport(false);
        setPauseOnLostFocus(true);
        setSettings(s);
    }

    private void createTerrain() {
        TerrainActor.create(injector, CREATE_ACTOR_TIMEOUT, actor.getObjectGetterAsync(MaterialAssetsCacheActor.ID),
                actor.getObjectGetterAsync(ModelsAssetsCacheActor.ID), actor.getActorAsync(PowerLoomKnowledgeActor.ID))
                .whenComplete((ret, ex) -> {
                    if (ex != null) {
                        log.error("TerrainActor.create", ex);
                        actor.tell(new AppErrorMessage(ex));
                    } else {
                        log.debug("TerrainActor created");
                    }
                });
    }

    private void createSun() {
        SunActor.create(injector, CREATE_ACTOR_TIMEOUT).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("SunActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("SunActor created");
            }
        });
    }

    private void createGameTick() {
        GameTickActor.create(injector, CREATE_ACTOR_TIMEOUT).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("GameTickActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("GameTickActor created");
            }
        });
    }

    private void createPowerLoom() {
        PowerLoomKnowledgeActor
                .create(injector, CREATE_ACTOR_TIMEOUT, actor.getActorAsync(StoredObjectsJcsCacheActor.ID))
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
        KnowledgeJcsCacheActor
                .create(injector, CREATE_ACTOR_TIMEOUT, actor.getObjectGetterAsync(PowerLoomKnowledgeActor.ID))
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
        var task = MockStoredObjectsJcsCacheActor.create(injector, CREATE_ACTOR_TIMEOUT,
                CompletableFuture.supplyAsync(() -> og));
        task.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("ObjectsJcsCacheActor.create", ex);
            } else {
                log.debug("ObjectsJcsCacheActor created");
            }
        });
    }

    private void createMaterialAssets() {
        var task = MaterialAssetsCacheActor.create(injector, CREATE_ACTOR_TIMEOUT);
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
        var task = ModelsAssetsCacheActor.create(injector, CREATE_ACTOR_TIMEOUT);
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
                CREATE_ACTOR_TIMEOUT, actor.getScheduler());
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
                CREATE_ACTOR_TIMEOUT, actor.getScheduler());
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
