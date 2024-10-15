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

import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.cid2Id;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.getChunk;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.findBlock;

import org.eclipse.collections.api.factory.Sets;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeShrub;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeTreeSampling;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import akka.actor.typed.ActorRef;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Inputs for testing.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainTestKeysState extends BaseAppState implements ActionListener {

    private static final String SHOW_SELECTED_BLOCK_MAPPING = "TerrainTestKeysState_SHOW_SELECTED_BLOCK_MAPPING";

    private static final String SHOW_OBJECTS_BLOCK_MAPPING = "TerrainTestKeysState_SHOW_OBJECTS_BLOCK_MAPPING";

    private static final String DELETE_VEGETATION_MAPPING = "TerrainTestKeysState_DELETE_VEGETATION_MAPPING";

    private static final String ADD_SHRUB_MAPPING = "TerrainTestKeysState_ADD_SHRUB_MAPPING";

    private static final String ADD_SAMPLING_MAPPING = "TerrainTestKeysState_ADD_SAMPLING_MAPPING";

    private static final String TOGGLE_UNDISCOVERED_MAPPING = "TerrainTestKeysState_TOGGLE_UNDISCOVERED_MAPPING";

    private static final String CURSOR_NORTH_MAPPING = "TerrainTestKeysState_CURSOR_NORTH_MAPPING";

    private static final String CURSOR_SOUTH_MAPPING = "TerrainTestKeysState_CURSOR_SOUTH_MAPPING";

    private static final String CURSOR_EAST_MAPPING = "TerrainTestKeysState_CURSOR_EAST_MAPPING";

    private static final String CURSOR_WEST_MAPPING = "TerrainTestKeysState_CURSOR_WEST_MAPPING";

    private static final String[] MAPPINGS = new String[] { SHOW_SELECTED_BLOCK_MAPPING, SHOW_OBJECTS_BLOCK_MAPPING,
            ADD_SHRUB_MAPPING, ADD_SAMPLING_MAPPING, TOGGLE_UNDISCOVERED_MAPPING, CURSOR_NORTH_MAPPING,
            CURSOR_SOUTH_MAPPING, CURSOR_EAST_MAPPING, CURSOR_WEST_MAPPING, DELETE_VEGETATION_MAPPING };

    private static final Runnable EMPTY_ACTION = () -> {
    };

    @RequiredArgsConstructor
    @ToString
    private static class WrappedCacheResponse extends Message {
        private final CacheResponseMessage<?> response;
    }

    @Inject
    private InputManager inputManager;

    private GameMap gm;

    private boolean keyInit = false;

    private boolean showSelectedBlock = false;

    private boolean showObjectsBlock = false;

    private float time = 0;

    private GameBlockPos oldCursor = new GameBlockPos();

    private Runnable nextAction = EMPTY_ACTION;

    private ActorRef<Message> actor;

    private ObjectsGetter chunks;

    @Inject
    public TerrainTestKeysState() {
        super(TerrainTestKeysState.class.getSimpleName());
    }

    public void setActor(ActorRef<Message> actor) {
        this.actor = actor;
    }

    public void setGameMap(GameMap gm) {
        this.gm = gm;
        this.oldCursor = gm.getCursor();
        if (!keyInit) {
            initKeys();
        }
    }

    public void setChunks(ObjectsGetter chunks) {
        this.chunks = chunks;
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
        inputManager.addMapping(SHOW_OBJECTS_BLOCK_MAPPING, new KeyTrigger(KeyInput.KEY_O));
        inputManager.addMapping(DELETE_VEGETATION_MAPPING, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(ADD_SHRUB_MAPPING, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(ADD_SAMPLING_MAPPING, new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping(TOGGLE_UNDISCOVERED_MAPPING, new KeyTrigger(KeyInput.KEY_F9));
        inputManager.addMapping(CURSOR_NORTH_MAPPING, new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(CURSOR_SOUTH_MAPPING, new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(CURSOR_EAST_MAPPING, new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(CURSOR_WEST_MAPPING, new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addListener(this, MAPPINGS);
        System.out.println("I     - " + SHOW_SELECTED_BLOCK_MAPPING);
        System.out.println("O     - " + SHOW_OBJECTS_BLOCK_MAPPING);
        System.out.println("D     - " + DELETE_VEGETATION_MAPPING);
        System.out.println("S     - " + ADD_SHRUB_MAPPING);
        System.out.println("T     - " + ADD_SAMPLING_MAPPING);
        System.out.println("F9    - " + TOGGLE_UNDISCOVERED_MAPPING);
        System.out.println("UP    - " + CURSOR_NORTH_MAPPING);
        System.out.println("DOWN  - " + CURSOR_SOUTH_MAPPING);
        System.out.println("RIGHT - " + CURSOR_EAST_MAPPING);
        System.out.println("LEFT  - " + CURSOR_WEST_MAPPING);
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
                this.showSelectedBlock = !showSelectedBlock;
                System.out.println("Toggle show selected block: " + showSelectedBlock);
                break;
            }
            case SHOW_OBJECTS_BLOCK_MAPPING: {
                this.showObjectsBlock = !showObjectsBlock;
                System.out.println("Toggle show objects block: " + showObjectsBlock);
                break;
            }
            case DELETE_VEGETATION_MAPPING: {
                this.nextAction = this::deleteVegetation;
                break;
            }
            case ADD_SHRUB_MAPPING: {
                this.nextAction = this::addShrub;
                break;
            }
            case ADD_SAMPLING_MAPPING: {
                this.nextAction = this::addSampling;
                break;
            }
            case TOGGLE_UNDISCOVERED_MAPPING: {
                this.nextAction = this::toggleUndiscovered;
                break;
            }
            case CURSOR_NORTH_MAPPING: {
                if (gm.cursor.canAddY(-1, gm.height)) {
                    gm.setCursor(gm.cursor.addY(-1));
                }
                break;
            }
            case CURSOR_SOUTH_MAPPING: {
                if (gm.cursor.canAddY(1, gm.height)) {
                    gm.setCursor(gm.cursor.addY(1));
                }
                break;
            }
            case CURSOR_EAST_MAPPING: {
                if (gm.cursor.canAddX(1, gm.width)) {
                    gm.setCursor(gm.cursor.addX(1));
                }
                break;
            }
            case CURSOR_WEST_MAPPING: {
                if (gm.cursor.canAddX(-1, gm.width)) {
                    gm.setCursor(gm.cursor.addX(-1));
                }
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
            nextAction.run();
            this.nextAction = EMPTY_ACTION;
            if (showSelectedBlock) {
                if (!oldCursor.equals(gm.getCursor())) {
                    actor.tell(new ShowSelectedBlockMessage(gm.getCursor()));
                }
            }
            if (showSelectedBlock) {
                if (!oldCursor.equals(gm.getCursor())) {
                    actor.tell(new ShowObjectsOnBlockMessage(gm.getCursor()));
                }
            }
            if (gm != null) {
                this.oldCursor = gm.getCursor();
            }
        }
    }

    private void toggleUndiscovered() {
        actor.tell(new ToggleUndiscoveredMessage());
    }

    private void addShrub() {
        this.oldCursor = gm.getCursor();
        actor.tell(new AddObjectOnBlockMessage(oldCursor, KnowledgeShrub.TYPE,
                (mb) -> mb.isEmpty() && mb.isDiscovered() && isDownDirt(mb)));
    }

    private boolean isDownDirt(MapBlock mb) {
        var down = mb.pos.addZ(1);
        var c = getChunk(chunks, cid2Id(mb.parent));
        var downBlock = findBlock(c, down, chunks);
        if (downBlock == null) {
            return false;
        }
        return downBlock.getMaterial() == 864 || downBlock.getMaterial() == 863 || downBlock.getMaterial() == 862
                || downBlock.getMaterial() == 861 || downBlock.getMaterial() == 860 || downBlock.getMaterial() == 859
                || downBlock.getMaterial() == 858 || downBlock.getMaterial() == 857;
    }

    private void addSampling() {
        this.oldCursor = gm.getCursor();
        actor.tell(new AddObjectOnBlockMessage(oldCursor, KnowledgeTreeSampling.TYPE,
                (mb) -> mb.isFilled() && mb.isDiscovered()));
    }

    private void deleteVegetation() {
        this.oldCursor = gm.getCursor();
        actor.tell(new DeleteVegetationOnBlockMessage(oldCursor,
                Sets.immutable.of(KnowledgeShrub.TYPE, KnowledgeTreeSampling.TYPE)));
    }

}
