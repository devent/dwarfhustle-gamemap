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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.MapCursor;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.util.TempVars;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Selects the map block under the mouse cursor.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainSelectBlockState extends BaseAppState implements ActionListener, AnalogListener, RawInputListener {

    private static final String[] MAPPINGS = new String[] {};

    private final Vector2f mouse;

    @Inject
    private InputManager inputManager;

    @Inject
    private Camera camera;

    private GameMap gm;

    private Optional<Consumer<GameMap>> saveSelectedMapBlock = Optional.empty();

    private Function<Integer, MapChunk> retriever;

    @Inject
    public TerrainSelectBlockState() {
        super(TerrainSelectBlockState.class.getSimpleName());
        this.mouse = new Vector2f();
    }

    public void setRetriever(Function<Integer, MapChunk> retriever) {
        this.retriever = retriever;
    }

    public void setSaveSelectedMapBlock(Consumer<GameMap> saveSelectedMapBlock) {
        this.saveSelectedMapBlock = Optional.of(saveSelectedMapBlock);
    }

    public void setGameMap(GameMap gm) {
        this.gm = gm;
    }

    private void saveSelectedMapBlock() {
        saveSelectedMapBlock.ifPresent(it -> it.accept(gm));
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
        initKeys();
    }

    @Override
    protected void onDisable() {
        log.debug("onDisable");
        deleteKeys();
    }

    private void initKeys() {
        inputManager.addListener(this, MAPPINGS);
        inputManager.addRawInputListener(this);
    }

    private void deleteKeys() {
        inputManager.removeListener(this);
        inputManager.removeRawInputListener(this);
        for (var i = 0; i < MAPPINGS.length; i++) {
            inputManager.deleteMapping(MAPPINGS[i]);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
    }

    @Override
    public void beginInput() {
    }

    @Override
    public void endInput() {
    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {
    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
        mouse.x = evt.getX();
        mouse.y = evt.getY();
        var temp = TempVars.get();
        var oldpos = temp.vect1;
        var newpos = temp.vect2;
        camera.getWorldCoordinates(new Vector2f(camera.getWidth() / 2f, camera.getHeight() / 2f), 0.0f, oldpos);
        camera.getWorldCoordinates(mouse, 0.0f, newpos);
        updateSelectedObject();
        temp.release();
    }

    private void updateSelectedObject() {
        if (gm == null) {
            return;
        }
        var temp = TempVars.get();
        var rootchunk = retriever.apply(0);
        var z = gm.cursor.z;
        var chunk = findChunkUnderCursor(temp, mouse, rootchunk, z, gm.width, gm.height);
        if (chunk != null) {
            var mb = findBlockUnderCursor(temp, mouse, chunk, z, gm.width, gm.height);
            if (mb != null) {
                gm.cursor = new MapCursor(mb.pos.x, mb.pos.y, z);
                saveSelectedMapBlock();
            }
        }
        temp.release();
    }

    private MapBlock findBlockUnderCursor(TempVars temp, Vector2f mouse, MapChunk chunk, int z, float w, float h) {
        for (var block : chunk.getBlocks()) {
            float tx = -w + 2f * block.pos.x + 1f;
            float ty = h - 2f * block.pos.y - 1f;
            float centerx = tx;
            float centery = ty;
            float centerz = 0;
            float extentx = 1f;
            float extenty = 1f;
            float extentz = 1f;
            if (block.pos.z == z
                    && checkCenterExtent(temp, mouse, centerx, centery, centerz, extentx, extenty, extentz)) {
                return block;
            }
        }
        return null;
    }

    private MapChunk findChunkUnderCursor(TempVars temp, Vector2f mouse, MapChunk chunk, int z, float w, float h) {
        if (chunk.blocks.isEmpty()) {
            for (var kvalues : chunk.getChunks().keyValuesView()) {
                var id = kvalues.getOne();
                var c = findChunkUnderCursor(temp, mouse, retriever.apply(id), z, w, h);
                if (c == null) {
                    continue;
                } else {
                    return c;
                }
            }
        } else {
            float tx = -w + 2f * chunk.pos.x + chunk.pos.getSizeX();
            float ty = h - 2f * chunk.pos.y - chunk.pos.getSizeY();
            float centerx = tx;
            float centery = ty;
            float centerz = 0f;
            float extentx = chunk.pos.getSizeX();
            float extenty = chunk.pos.getSizeY();
            float extentz = chunk.pos.getSizeZ();
            if (chunk.pos.z <= z && chunk.getPos().ep.z > z
                    && checkCenterExtent(temp, mouse, centerx, centery, centerz, extentx, extenty, extentz)) {
                return chunk;
            }
        }
        return null;
    }

    private boolean checkCenterExtent(TempVars temp, Vector2f mouse, float centerx, float centery, float centerz,
            float extentx, float extenty, float extentz) {
        var bottomc = temp.vect1;
        var topc = temp.vect2;
        bottomc.x = centerx - extentx;
        bottomc.y = centery - extenty;
        bottomc.z = centerz - extentz;
        camera.getScreenCoordinates(bottomc, bottomc);
        topc.x = centerx + extentx;
        topc.y = centery + extenty;
        topc.z = centerz + extentz;
        camera.getScreenCoordinates(topc, topc);
        if (mouse.x >= bottomc.x && mouse.y >= bottomc.y && mouse.x <= topc.x && mouse.y <= topc.y) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
    }

    @Override
    public void onTouchEvent(TouchEvent evt) {
    }

}
