/*
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - View
 * ****************************************************************************
 *
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - View is a derivative work based on Josua Tippetts' C++ library:
 * http://accidentalnoise.sourceforge.net/index.html
 * ****************************************************************************
 *
 * Copyright (C) 2011 Joshua Tippetts
 *
 *   This software is provided 'as-is', without any express or implied
 *   warranty.  In no event will the authors be held liable for any damages
 *   arising from the use of this software.
 *
 *   Permission is granted to anyone to use this software for any purpose,
 *   including commercial applications, and to alter it and redistribute it
 *   freely, subject to the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not
 *      claim that you wrote the original software. If you use this software
 *      in a product, an acknowledgment in the product documentation would be
 *      appreciated but is not required.
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *   3. This notice may not be removed or altered from any source distribution.
 *
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - View bundles and uses the RandomCL library:
 * https://github.com/bstatcomp/RandomCL
 * ****************************************************************************
 *
 * BSD 3-Clause License
 *
 * Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import static com.jme3.input.MouseInput.AXIS_WHEEL;
import static com.jme3.input.MouseInput.BUTTON_MIDDLE;
import static com.jme3.input.MouseInput.BUTTON_RIGHT;
import static java.lang.Math.abs;

import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.util.TempVars;

import lombok.extern.slf4j.Slf4j;

/**
 * Pans the camera in two directions. Zooms the camera in one axis.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class CameraPanningState extends BaseAppState implements ActionListener, AnalogListener, RawInputListener {

    private static final String MIDDLE_BUTTON_MAPPING = "MoveMapMouseState_middle";

    private static final String RIGHT_BUTTON_MAPPING = "MoveMapMouseState_right";

    private static final String ZOOM_OUT_MAPPING = "MouseMoveMapState_zoomout";

    private static final String ZOOM_IN_MAPPING = "MouseMoveMapState_zoomin";

    private static final String Z_IN_MAPPING = "MouseMoveMapState_z_in";

    private static final String Z_OUT_MAPPING = "MouseMoveMapState_z_out";

    private static final String[] MAPPINGS = new String[] { MIDDLE_BUTTON_MAPPING, RIGHT_BUTTON_MAPPING,
            ZOOM_IN_MAPPING, ZOOM_OUT_MAPPING, Z_IN_MAPPING, Z_OUT_MAPPING };

    private final Vector2f mouse;

    @Inject
    private InputManager inputManager;

    @Inject
    private Camera camera;

    private MapTerrainModel model;

    private boolean middleMouseDown;

    private final Vector3f mapBottomLeft;

    private final Vector3f mapTopRight;

    private GameMap gm;

    private Optional<Consumer<GameMap>> saveCamera = Optional.empty();

    private Optional<Consumer<GameMap>> saveZ = Optional.empty();

    private boolean rightMouseDown;

    @Inject
    public CameraPanningState() {
        super(CameraPanningState.class.getSimpleName());
        this.middleMouseDown = false;
        this.mouse = new Vector2f();
        this.mapBottomLeft = new Vector3f();
        this.mapTopRight = new Vector3f();
    }

    public void setSaveCamera(Consumer<GameMap> saveCamera) {
        this.saveCamera = Optional.of(saveCamera);
    }

    public void setSaveZ(Consumer<GameMap> saveZ) {
        this.saveZ = Optional.of(saveZ);
    }

    public void resetCamera() {
        camera.setLocation(new Vector3f(0.0f, 0.0f, 10.0f));
        camera.setRotation(new Quaternion(0.0f, 1.0f, 0.0f, 0.0f));
    }

    public void setTerrainModel(MapTerrainModel model) {
        this.model = model;
        updateScreenCoordinatesMap();
    }

    public void updateCamera(GameMap gm) {
        this.gm = gm;
        camera.setLocation(new Vector3f(gm.getCameraPos()[0], gm.getCameraPos()[1], gm.getCameraPos()[2]));
        camera.setRotation(
                new Quaternion(gm.getCameraRot()[0], gm.getCameraRot()[1], gm.getCameraRot()[2], gm.getCameraRot()[3]));
    }

    private void saveCamera() {
        if (saveCamera.isPresent()) {
            saveCamera.get().accept(gm);
        }
    }

    private void saveZ() {
        if (saveZ.isPresent()) {
            saveZ.get().accept(gm);
        }
    }

    public void setCameraPos(float x, float y, float z) {
        camera.setLocation(new Vector3f(x, y, z));
        camera.update();
        saveCamera();
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
        inputManager.addMapping(MIDDLE_BUTTON_MAPPING, new MouseButtonTrigger(BUTTON_MIDDLE));
        inputManager.addMapping(RIGHT_BUTTON_MAPPING, new MouseButtonTrigger(BUTTON_RIGHT));
        inputManager.addMapping(Z_IN_MAPPING, new MouseAxisTrigger(AXIS_WHEEL, false));
        inputManager.addMapping(Z_OUT_MAPPING, new MouseAxisTrigger(AXIS_WHEEL, true));
    }

    private void deleteKeys() {
        inputManager.removeRawInputListener(this);
        for (var i = 0; i < MAPPINGS.length; i++) {
            inputManager.deleteMapping(MAPPINGS[i]);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
        case MIDDLE_BUTTON_MAPPING:
            updateScreenCoordinatesMap();
            if (!isPressed && middleMouseDown) {
                saveCamera();
            }
            middleMouseDown = isPressed;
            return;
        case RIGHT_BUTTON_MAPPING:
            updateScreenCoordinatesMap();
            if (!isPressed && rightMouseDown) {
                saveCamera();
            }
            rightMouseDown = isPressed;
            return;
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        var m = 1f;
        updateScreenCoordinatesMap();
        switch (name) {
        case Z_IN_MAPPING:
            if (canZ(m)) {
                boundZMove(m);
                saveZ();
            }
            return;
        case Z_OUT_MAPPING:
            if (canZ(-m)) {
                boundZMove(-m);
                saveZ();
            }
            return;
        }
    }

    private boolean canZ(float m) {
        var z = gm.getCursor().z;
        if (m > 0) {
            return z + m < gm.getDepth();
        } else {
            return z + m >= 0;
        }
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
        if (!middleMouseDown && !rightMouseDown) {
            return;
        }
        var temp = TempVars.get();
        try {
            var oldpos = temp.vect1;
            var newpos = temp.vect2;
            camera.getWorldCoordinates(new Vector2f(camera.getWidth() / 2f, camera.getHeight() / 2f), 0.0f, oldpos);
            camera.getWorldCoordinates(mouse, 0.0f, newpos);
            updateScreenCoordinatesMap();
            float dx = evt.getDX();
            float dy = -evt.getDY();
            var s = calcSpeed(Math.max(abs(dx), abs(dy)));
            if (middleMouseDown) {
                doMove(dx, dy, s);
            } else if (rightMouseDown) {
                doZoom(dx, dy, s, oldpos, newpos);
            }
        } finally {
            temp.release();
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

    private void updateScreenCoordinatesMap() {
        model.getScreenCoordinatesMap(camera, mapTopRight, mapBottomLeft);
    }

    private void boundMove(float dx, float dy, float dz) {
        var pos = camera.getLocation();
        pos.x += dx;
        pos.y += dy;
        pos.z += dz;
        gm.setCameraPos(pos.x, pos.y, pos.z);
        camera.update();
    }

    private void boundZMove(float d) {
        var dd = (int) d;
        gm.addCursorZ(dd);
    }

    private void doZoom(float dx, float dy, float s, Vector3f oldpos, Vector3f newpos) {
        var tmpc = camera.clone();
        var pos = tmpc.getLocation();
        pos.z += dy * s;
        var temp = TempVars.get();
        try {
            var tmpTopRight = temp.vect1;
            var tmpBottomLeft = temp.vect2;
            var topright = temp.vect3.set(2f, 2f, 0f);
            var bottomleft = temp.vect4.set(-2f, -2f, 0f);
            tmpc.getScreenCoordinates(topright, tmpTopRight);
            tmpc.getScreenCoordinates(bottomleft, tmpBottomLeft);
            if (dy > 0) {
                // zoom out
                if (tmpTopRight.x < 0f || tmpBottomLeft.x < 0 || tmpTopRight.x - tmpBottomLeft.x > 50) {
                    boundMove(0f, 0f, dy * s);
                }
            } else {
                // zoom in
                if (tmpTopRight.x > 0f && tmpBottomLeft.x > 0 && tmpTopRight.x - tmpBottomLeft.x < camera.getWidth()) {
                    boundMove(0f, 0f, dy * s);
                }
            }
        } finally {
            temp.release();
        }
    }

    private void doMove(float dx, float dy, float s) {
        if (!canMoveX(dx, s)) {
            dx = 0;
        }
        if (!canMoveY(dy, s)) {
            dy = 0;
        }
        boundMove(-dx * s, dy * s, 0);
    }

    private boolean canMoveX(float dx, float s) {
        if (dx > 0) {
            // right
            return mapBottomLeft.x + dx * s < camera.getWidth() / 2f;
        } else {
            // left
            return mapTopRight.x - dx * s > camera.getWidth() / 2f;
        }
    }

    private boolean canMoveY(float dy, float s) {
        if (dy > 0) {
            // down
            return mapTopRight.y + dy * s > camera.getHeight() / 2f;
        } else {
            // up
            return mapBottomLeft.y - dy * s < camera.getHeight() / 2f;
        }
    }

    private float calcSpeed(float d) {
        return camera.getLocation().z * 0.0025f;
    }

}
