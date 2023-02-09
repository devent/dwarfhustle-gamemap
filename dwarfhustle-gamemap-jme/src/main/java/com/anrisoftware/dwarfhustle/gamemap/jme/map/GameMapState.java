package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapTile;
import com.badlogic.ashley.core.Engine;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;

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

	public MapRenderSystem getMapRenderSystem() {
		return mapRenderSystem;
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

}
