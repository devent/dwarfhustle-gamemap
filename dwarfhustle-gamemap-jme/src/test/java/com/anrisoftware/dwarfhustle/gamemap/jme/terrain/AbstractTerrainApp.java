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
import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.RetrieveKnowledgesMessage.askRetrieveKnowledges;
import static java.time.Duration.ofSeconds;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import org.lable.oss.uniqueid.IDGenerator;

import com.anrisoftware.dwarfhustle.gamemap.jme.app.DwarfhustleGamemapJmeAppModule;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.GameTickActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.GameTimeActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.MaterialAssetsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.ModelsAssetsCacheActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.lights.DwarfhustleGamemapJmeLightsModule;
import com.anrisoftware.dwarfhustle.gamemap.jme.lights.SunActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.model.DwarfhustleGamemapJmeModelModule;
import com.anrisoftware.dwarfhustle.gamemap.jme.objectsmodel.DwarfhustleGamemapJmeObjectsmodelModule;
import com.anrisoftware.dwarfhustle.gamemap.jme.objectsmodel.ObjectsModelActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.objectsrender.DwarfhustleGamemapJmeObjectsrenderModule;
import com.anrisoftware.dwarfhustle.gamemap.jme.objectsrender.ObjectsRenderActor;
import com.anrisoftware.dwarfhustle.gamemap.jme.terrain.TerrainTestKeysActor.TerrainTestKeysActorFactory;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppPausedMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AssetsResponseMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadModelsMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetWorldMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.StartTerrainForGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.IdsObjectsProvider.IdsObjects;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.cache.DwarfhustleModelDbCacheModule;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.lmbd.GameObjectsLmbdStorage;
import com.anrisoftware.dwarfhustle.model.db.lmbd.MapObjectsLmbdStorage;
import com.anrisoftware.dwarfhustle.model.db.strings.StringsLuceneStorage;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.DwarfhustleModelKnowledgePowerloomPlModule;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.anrisoftware.dwarfhustle.model.objects.DwarfhustleModelObjectsModule;
import com.anrisoftware.dwarfhustle.model.objects.ObjectsActor;
import com.badlogic.ashley.core.Engine;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.jme3.app.Application;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ConstantVerifierState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import akka.actor.typed.ActorRef;
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
public abstract class AbstractTerrainApp extends SimpleApplication {

    protected static final Duration CREATE_ACTOR_TIMEOUT = Duration.ofSeconds(30);

    protected static final Consumer<Float> SIMPLE_UPDATE_CALL_NOP = tpl -> {
    };

    @Inject
    protected ActorSystemProvider actor;

    @Inject
    @IdsObjects
    protected IDGenerator ids;

    protected Injector injector;

    private Engine engine;

    protected GameMap gm;

    protected MapChunk mcRoot;

    private boolean texturesLoaded = false;

    private boolean modelsLoaded = false;

    protected Consumer<Float> simpleUpdateCall;

    private final ResetCameraState resetCameraState;

    protected WorldMap wm;

    private Node sceneNode;

    protected GameObjectsLmbdStorage goStorage;

    protected StringsLuceneStorage soStorage;

    protected MapObjectsLmbdStorage moStorage;

    public AbstractTerrainApp() {
        super(new ConstantVerifierState(), new DebugKeysAppState()
        // , new FlyCamAppState()
        // , new StatsAppState()
        );
        this.resetCameraState = new ResetCameraState();
        getStateManager().attach(resetCameraState);
    }

    public void start(Injector parent) {
        this.engine = new Engine();
        this.injector = parent.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new DwarfhustleModelKnowledgePowerloomPlModule());
                install(new DwarfhustleModelDbCacheModule());
                install(new DwarfhustleModelObjectsModule());
                install(new DwarfhustleGamemapJmeAppModule());
                install(new DwarfhustleGamemapJmeLightsModule());
                install(new DwarfhustleGamemapJmeModelModule());
                install(new DwarfhustleGamemapJmeObjectsmodelModule());
                install(new DwarfhustleGamemapJmeObjectsrenderModule());
                install(new DwarfhustleGamemapJmeTerrainModule());
                bind(GameSettingsProvider.class).asEagerSingleton();
                install(new FactoryModuleBuilder().implement(TerrainTestKeysActor.class, TerrainTestKeysActor.class)
                        .build(TerrainTestKeysActorFactory.class));
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
            public Engine getEngine() {
                return engine;
            }

            @Provides
            public ViewPort getViewPort() {
                return AbstractTerrainApp.this.viewPort;
            }

        }, getAdditionalModule());
        loadTerrain();
        setupGameSettings();
        setupApp();
        start();
    }

    protected abstract com.google.inject.Module getAdditionalModule();

    protected abstract void loadTerrain();

    private void setupGameSettings() {
        var gs = injector.getInstance(GameSettingsProvider.class).get();
        gs.visibleDepthLayers.set(4);
        gs.currentMap.set(gm.getId());
        gs.currentWorld.set(wm.getId());
    }

    private void setupApp() {
        getStateManager().attach(injector.getInstance(DebugCoordinateAxesState.class));
        var s = new AppSettings(true);
        s.setResizable(true);
        // 960 x 720
        s.setWidth(960);
        s.setHeight(720);
        // 1920 x 1080
        // s.setWidth(1920);
        // s.setHeight(1080);
        s.setVSync(false);
        // s.setFullscreen(true);
        s.setOpenCLSupport(false);
        setPauseOnLostFocus(true);
        setSettings(s);
    }

    @Override
    @SneakyThrows
    public void simpleInitApp() {
        log.debug("simpleInitApp");
        this.sceneNode = new Node("Scene-Node");
        rootNode.attachChild(sceneNode);
        this.simpleUpdateCall = tpl -> nextSetGameMap();
        createKnowledgeCache();
        createChunksCache();
        createMapObjectsCache();
        createStoredObjectsCache();
        createStringObjectCache();
        createModelObjects();
        createMaterialAssets();
        createModelsAssets();
        createObjectsRender();
        createObjectsModel();
        createTerrain();
        createSun();
        createGameTick();
        createGameTime();
        createTerrainTestKeys();
        loadMapObjects();
        storeGameMap();
    }

    @SneakyThrows
    protected void storeGameMap() {
        var gs = actor.getObjectSetterAsyncNow(StoredObjectsJcsCacheActor.ID);
        gs.set(GameMap.OBJECT_TYPE, gm);
    }

    protected abstract void loadMapObjects();

    private void nextSetGameMap() {
        if (texturesLoaded && modelsLoaded) {
            actor.tell(new SetWorldMapMessage(wm.getId()));
            actor.tell(new SetGameMapMessage(gm.getId()));
            resetCameraState.updateCamera(gm);
            simpleUpdateCall = tpl -> nextStartTerrainForGameMap();
        }
    }

    @SneakyThrows
    private void nextStartTerrainForGameMap() {
        actor.tell(new StartTerrainForGameMapMessage(gm.getId()));
        simpleUpdateCall = SIMPLE_UPDATE_CALL_NOP;
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

    protected abstract void createMapObjectsCache();

    protected abstract void createStoredObjectsCache();

    protected abstract void createStringObjectCache();

    protected abstract void createChunksCache();

    private void createTerrainTestKeys() {
        TerrainTestKeysActor.create(injector, CREATE_ACTOR_TIMEOUT).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("TerrainTestKeysActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("TerrainTestKeysActor created");
            }
        });
    }

    private void createObjectsModel() {
        ObjectsModelActor.create(injector, CREATE_ACTOR_TIMEOUT).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("ObjectsModelActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("ObjectsModelActor created");
            }
        });
    }

    private void createObjectsRender() {
        ObjectsRenderActor.create(injector, CREATE_ACTOR_TIMEOUT).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("ObjectsRenderActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("ObjectsRenderActor created");
            }
        });
    }

    private void createTerrain() {
        TerrainActor.create(injector, CREATE_ACTOR_TIMEOUT).whenComplete((ret, ex) -> {
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

    private void createGameTime() {
        GameTimeActor.create(injector, CREATE_ACTOR_TIMEOUT).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("GameTimeActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("GameTimeActor created");
            }
        });
    }

    private void createModelObjects() {
        ObjectsActor.create(injector, CREATE_ACTOR_TIMEOUT).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("ObjectsActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                createPowerLoom(ret);
                log.debug("ObjectsActor created");
            }
        });
    }

    private void createKnowledgeCache() {
        KnowledgeJcsCacheActor.create(injector, CREATE_ACTOR_TIMEOUT).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("KnowledgeJcsCacheActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                createPowerLoom(ret);
                log.debug("KnowledgeJcsCacheActor created");
            }
        });
    }

    private void createPowerLoom(ActorRef<Message> cache) {
        PowerLoomKnowledgeActor.create(injector, CREATE_ACTOR_TIMEOUT, actor.getActorAsync(KnowledgeJcsCacheActor.ID),
                actor.getObjectGetterAsync(KnowledgeJcsCacheActor.ID)).whenComplete((ret, ex) -> {
                    if (ex != null) {
                        log.error("PowerLoomKnowledgeActor.create", ex);
                        actor.tell(new AppErrorMessage(ex));
                    } else {
                        log.debug("PowerLoomKnowledgeActor created");
                        var res = askRetrieveKnowledges(actor.getActorSystem(), ofSeconds(30));
                        res.whenComplete((ret1, ex1) -> {
                            if (ex1 != null) {
                                log.error("askRetrieveKnowledges", ex1);
                                actor.tell(new AppErrorMessage(ex1));
                            } else {
                                log.debug("askRetrieveKnowledges done");
                            }
                        });
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
        CompletionStage<AssetsResponseMessage<?>> result = ask(actor.get(), LoadTexturesMessage::new,
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
        CompletionStage<AssetsResponseMessage<?>> result = ask(actor.get(), LoadModelsMessage::new,
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
