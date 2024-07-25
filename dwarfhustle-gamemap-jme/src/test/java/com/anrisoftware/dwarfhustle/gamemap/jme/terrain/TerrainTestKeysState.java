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

import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunksStore;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Inputs for testing.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainTestKeysState extends BaseAppState implements ActionListener {

    private static final String SHOW_SELECTED_BLOCK_MAPPING = "SHOW_SELECTED_BLOCK_MAPPING";

    private static final String[] MAPPINGS = new String[] { SHOW_SELECTED_BLOCK_MAPPING };

    @Inject
    private InputManager inputManager;

    private GameMap gm;

    private MapChunksStore store;

    private boolean keyInit = false;

    private boolean showSelectedBlock = false;

    private float time = 0;

    private GameBlockPos oldCursor = new GameBlockPos();

    @Inject
    public TerrainTestKeysState() {
        super(TerrainTestKeysState.class.getSimpleName());
    }

    public void setRetriever(MapChunksStore store) {
        this.store = store;
    }

    public void setGameMap(GameMap gm) {
        this.gm = gm;
        this.oldCursor = gm.getCursor();
        if (!keyInit) {
            initKeys();
        }
    }

    @Override
    protected void initialize(Application app) {
        log.debug("initialize");
    }

    @Override
    protected void cleanup(Application app) {
        log.debug("cleanup");
    }

    @Override
    protected void onEnable() {
        log.debug("onEnable");
    }

    @Override
    protected void onDisable() {
        log.debug("onDisable");
        if (keyInit) {
            deleteKeys();
        }
    }

    private void initKeys() {
        inputManager.addMapping(SHOW_SELECTED_BLOCK_MAPPING, new KeyTrigger(KeyInput.KEY_I));
        inputManager.addListener(this, MAPPINGS);
        this.keyInit = true;
    }

    private void deleteKeys() {
        inputManager.removeListener(this);
        for (String element : MAPPINGS) {
            inputManager.deleteMapping(element);
        }
        this.keyInit = false;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed) {
            switch (name) {
            case SHOW_SELECTED_BLOCK_MAPPING: {
                showSelectedBlock = !showSelectedBlock;
                break;
            }
            default:
                throw new IllegalArgumentException("Unexpected value: " + name);
            }
        }
    }

    @Override
    public void update(float tpf) {
        this.time += tpf;
        if (this.time > 0.333f) {
            this.time = 0;
            if (showSelectedBlock) {
                if (!oldCursor.equals(gm.getCursor())) {
                    this.oldCursor = gm.getCursor();
                    var mb = store.findBlock(gm.getCursor());
                    System.out.println(mb);
                }
            }
        }
    }
}
