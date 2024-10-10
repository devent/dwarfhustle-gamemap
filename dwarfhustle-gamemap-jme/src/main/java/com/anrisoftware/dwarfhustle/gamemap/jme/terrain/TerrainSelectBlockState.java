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

import java.util.concurrent.ForkJoinPool;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
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
import lombok.RequiredArgsConstructor;
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

    private boolean keyInit = false;

    private ObjectsGetter chunks;

    private boolean searchingSelected;

    @Inject
    public TerrainSelectBlockState() {
        super(TerrainSelectBlockState.class.getSimpleName());
        this.mouse = new Vector2f();
    }

    public void setStorage(ObjectsGetter chunks) {
        this.chunks = chunks;
    }

    public void setGameMap(GameMap gm) {
        this.gm = gm;
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
        inputManager.addRawInputListener(this);
        inputManager.addListener(this, MAPPINGS);
        this.keyInit = true;
    }

    private void deleteKeys() {
        inputManager.removeListener(this);
        inputManager.removeRawInputListener(this);
        for (String element : MAPPINGS) {
            inputManager.deleteMapping(element);
        }
        this.keyInit = false;
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
        try {
            updateSelectedObject(temp, mouse);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            temp.release();
        }
    }

    @RequiredArgsConstructor
    private class UpdateSelectedObjectAction implements Runnable {

        private final Vector2f mouse;

        private final int cursorZ;

        private final int chunksCount;

        @Override
        public void run() {
            var temp = TempVars.get();
            for (int i = 0; i < chunksCount; i++) {
                MapChunk chunk = chunks.get(MapChunk.OBJECT_TYPE, cid2Id(i));
                if (chunk.isLeaf() && chunk.pos.z <= cursorZ && chunk.pos.ep.z > cursorZ) {
                    var c = chunk.getCenterExtent();
                    if (checkCenterExtent(temp, mouse, c.centerx, c.centery, c.centerz, c.extentx, c.extenty,
                            c.extentz)) {
                        if (findBlockUnderCursor(temp, mouse, chunk, gm.cursor.z, gm.width, gm.height)) {
                            break;
                        }
                    }
                }
            }
            temp.release();
            searchingSelected = false;
        }

    }

    private void updateSelectedObject(TempVars temp, Vector2f mouse) {
        if (this.searchingSelected) {
            return;
        }
        this.searchingSelected = true;
        var pool = ForkJoinPool.commonPool();
        pool.execute(new UpdateSelectedObjectAction(mouse, gm.cursor.z, gm.chunksCount));
    }

    private boolean findBlockUnderCursor(TempVars temp, Vector2f mouse, MapChunk chunk, int cursorZ, float w, float h) {
        for (int x = chunk.getPos().getX(); x < chunk.getPos().getEp().getX(); x++) {
            for (int y = chunk.getPos().getY(); y < chunk.getPos().getEp().getY(); y++) {
                for (int z = chunk.getPos().getZ(); z < chunk.getPos().getEp().getZ(); z++) {
                    float tx = -w + 2f * x + 1f;
                    float ty = h - 2f * y - 1f;
                    float centerx = tx;
                    float centery = ty;
                    float centerz = 0;
                    float extentx = 1f;
                    float extenty = 1f;
                    float extentz = 1f;
                    if (z == cursorZ
                            && checkCenterExtent(temp, mouse, centerx, centery, centerz, extentx, extenty, extentz)) {
                        gm.setCursor(x, y, z);
                        return true;
                    }
                }
            }
        }
        return false;
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
