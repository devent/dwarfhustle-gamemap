package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import javax.inject.Inject;
import javax.inject.Named;

import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrain.MapTerrainFactory;
import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapTerrainModel.MapTerrainModelFactory;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapTile;
import com.badlogic.ashley.core.Engine;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;

/**
 * Constructs the game map from {@link MapBlock} and {@link MapTile}.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class GameMapState extends BaseAppState {

    @Inject
    private Engine engine;

    @Inject
    private MapRenderSystem mapRenderSystem;

    @Inject
    private MapTerrainModelFactory modelFactory;

    @Inject
    private MapTerrainFactory terrainFactory;

    @Inject
    @Named("rootNode")
    private Node rootNode;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    private Application app;

    private MapTerrainModel model;

    private MapTerrain terrain;

    public MapRenderSystem getMapRenderSystem() {
        return mapRenderSystem;
    }

    public MapTerrainModel getModel() {
        return model;
    }

    public MapTerrain getTerrain() {
        return terrain;
    }

    @Override
    protected void initialize(Application app) {
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        engine.addSystem(mapRenderSystem);
    }

    @Override
    protected void onDisable() {
        engine.removeSystem(mapRenderSystem);
    }

    public void createMapBlockBox(GameMap gm, MapBlock mb) {
        this.model = modelFactory.create();
        this.terrain = terrainFactory.create(model, gm);
        terrain.setLevels(gs.get().visibleDepthLayers.get());
        rootNode.attachChild(terrain.node);
        model.setTerrain(terrain, mb);
        mapRenderSystem.setModel(model);
        mapRenderSystem.setTerrain(terrain);
        gs.get().visibleDepthLayers.addListener((o, ov, nv) -> {
            app.enqueue(() -> {
                terrain.setLevels(nv.intValue());
            });
        });
    }

}
