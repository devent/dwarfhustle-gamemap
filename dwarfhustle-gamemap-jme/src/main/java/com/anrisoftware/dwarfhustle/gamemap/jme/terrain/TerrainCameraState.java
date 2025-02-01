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

import static com.jme3.input.MouseInput.AXIS_WHEEL;
import static com.jme3.input.MouseInput.BUTTON_MIDDLE;
import static com.jme3.input.MouseInput.BUTTON_RIGHT;
import static java.lang.Math.abs;

import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bounding.BoundingBox;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
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

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Pans the camera in two directions. Zooms the camera in one axis.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainCameraState extends BaseAppState implements ActionListener, AnalogListener, RawInputListener {

    private static final String MIDDLE_BUTTON_MAPPING = "TerrainCameraState_middle";

    private static final String RIGHT_BUTTON_MAPPING = "TerrainCameraState_right";

    private static final String ZOOM_OUT_MAPPING = "TerrainCameraState_zoomout";

    private static final String ZOOM_IN_MAPPING = "TerrainCameraState_zoomin";

    private static final String Z_IN_MAPPING = "TerrainCameraState_z_in";

    private static final String Z_OUT_MAPPING = "TerrainCameraState_z_out";

    private static final String SHIFT_MAPPING = "TerrainCameraState_shift";

    private static final String[] MAPPINGS = new String[] { MIDDLE_BUTTON_MAPPING, RIGHT_BUTTON_MAPPING,
            ZOOM_IN_MAPPING, ZOOM_OUT_MAPPING, Z_IN_MAPPING, Z_OUT_MAPPING, SHIFT_MAPPING };

    private final Vector2f mouse;

    @Inject
    private InputManager inputManager;

    @Inject
    private Camera camera;

    private boolean middleMouseDown = false;

    private final Vector3f mapBottomLeft;

    private final Vector3f mapTopRight;

    private BiConsumer<Vector3f, Quaternion> onSaveCamera;

    private IntConsumer onSaveZ;

    private Supplier<GameMap> onRetrieveGameMap;

    private boolean rightMouseDown;

    private BoundingBox bounds;

    private boolean shiftDown = false;

    private boolean keyInit = false;

    private int currentZ;

    @Inject
    public TerrainCameraState() {
        super(TerrainCameraState.class.getSimpleName());
        this.mouse = new Vector2f();
        this.mapBottomLeft = new Vector3f();
        this.mapTopRight = new Vector3f();
        this.bounds = new BoundingBox(new Vector3f(), 10f, 10f, 10f);
    }

    public void setOnRetrieveMap(Supplier<GameMap> onRetrieveGameMap) {
        this.onRetrieveGameMap = onRetrieveGameMap;
    }

    public void setOnSaveCamera(BiConsumer<Vector3f, Quaternion> onSaveCamera) {
        this.onSaveCamera = onSaveCamera;
    }

    public void setOnSaveZ(IntConsumer onSaveZ) {
        this.onSaveZ = onSaveZ;
    }

    public void resetCamera() {
        camera.setLocation(new Vector3f(0.0f, 0.0f, 10.0f));
        camera.setRotation(new Quaternion(0.0f, 1.0f, 0.0f, 0.0f));
    }

    public void setTerrainBounds(BoundingBox bounds) {
        this.bounds = bounds;
        updateScreenCoordinatesMap();
    }

    public void updateCamera(float[] pos, float[] rot, int currentZ) {
        camera.setLocation(new Vector3f(pos[0], pos[1], pos[2]));
        camera.setRotation(new Quaternion(rot[0], rot[1], rot[2], rot[3]));
        this.currentZ = currentZ;
        if (!keyInit) {
            initKeys();
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
        resetCamera();
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
        inputManager.addListener(this, MAPPINGS);
        inputManager.addRawInputListener(this);
        inputManager.addMapping(SHIFT_MAPPING, new KeyTrigger(KeyInput.KEY_LSHIFT),
                new KeyTrigger(KeyInput.KEY_RSHIFT));
        inputManager.addMapping(MIDDLE_BUTTON_MAPPING, new MouseButtonTrigger(BUTTON_MIDDLE));
        inputManager.addMapping(RIGHT_BUTTON_MAPPING, new MouseButtonTrigger(BUTTON_RIGHT));
        inputManager.addMapping(Z_IN_MAPPING, new MouseAxisTrigger(AXIS_WHEEL, false));
        inputManager.addMapping(Z_OUT_MAPPING, new MouseAxisTrigger(AXIS_WHEEL, true));
        this.keyInit = true;
    }

    private void deleteKeys() {
        inputManager.removeListener(this);
        inputManager.removeRawInputListener(this);
        for (final String element : MAPPINGS) {
            inputManager.deleteMapping(element);
        }
        this.keyInit = false;
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
            break;
        case RIGHT_BUTTON_MAPPING:
            updateScreenCoordinatesMap();
            if (!isPressed && rightMouseDown) {
                saveCamera();
            }
            rightMouseDown = isPressed;
            break;
        case SHIFT_MAPPING:
            shiftDown = isPressed;
            break;
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (shiftDown) {
            return;
        }
        final var m = 1f;
        updateScreenCoordinatesMap();
        switch (name) {
        case Z_IN_MAPPING:
            if (canZ(m)) {
                boundZMove(m);
                saveZ();
            }
            break;
        case Z_OUT_MAPPING:
            if (canZ(-m)) {
                boundZMove(-m);
                saveZ();
            }
            break;
        }
    }

    private boolean canZ(float m) {
        final var gm = onRetrieveGameMap.get();
        final var z = gm.getCursorZ();
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
        final var temp = TempVars.get();
        final var oldpos = temp.vect1;
        final var newpos = temp.vect2;
        camera.getWorldCoordinates(new Vector2f(camera.getWidth() / 2f, camera.getHeight() / 2f), 0.0f, oldpos);
        camera.getWorldCoordinates(mouse, 0.0f, newpos);
        if (!shiftDown && (middleMouseDown || rightMouseDown)) {
            updateScreenCoordinatesMap();
            final float dx = evt.getDX();
            final float dy = -evt.getDY();
            final var s = calcSpeed(Math.max(abs(dx), abs(dy)));
            if (middleMouseDown) {
                doMove(dx, dy, s);
            } else if (rightMouseDown) {
                doZoom(dx, dy, s, oldpos, newpos);
            }
        }
        temp.release();
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
        final var temp = TempVars.get();
        final var btr = bounds.getMax(temp.vect1);
        final var bbl = bounds.getMin(temp.vect2);
        camera.getScreenCoordinates(btr, mapTopRight);
        camera.getScreenCoordinates(bbl, mapBottomLeft);
        temp.release();
    }

    private void boundMove(float dx, float dy, float dz) {
        final var pos = camera.getLocation();
        pos.x += dx;
        pos.y += dy;
        pos.z += dz;
        camera.setLocation(pos);
        camera.update();
        saveCamera();
    }

    private void boundZMove(float d) {
        final var dd = (int) d;
        this.currentZ += dd;
    }

    private void doZoom(float dx, float dy, float s, Vector3f oldpos, Vector3f newpos) {
        final var tmpc = camera.clone();
        final var pos = tmpc.getLocation();
        pos.z += dy * s;
        final var temp = TempVars.get();
        final var tmpTopRight = temp.vect1;
        final var tmpBottomLeft = temp.vect2;
        final var topright = temp.vect3.set(2f, 2f, 0f);
        final var bottomleft = temp.vect4.set(-2f, -2f, 0f);
        tmpc.getScreenCoordinates(topright, tmpTopRight);
        tmpc.getScreenCoordinates(bottomleft, tmpBottomLeft);
        if (dy > 0) {
            // zoom out
            if (tmpTopRight.x < 0f || tmpBottomLeft.x < 0 || tmpTopRight.x - tmpBottomLeft.x > 10) {
                boundMove(0f, 0f, dy * s);
            }
        } else // zoom in
        if (tmpTopRight.x > 0f && tmpBottomLeft.x > 0 && tmpTopRight.x - tmpBottomLeft.x < camera.getWidth()) {
            boundMove(0f, 0f, dy * s);
        }
        temp.release();
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
            return mapTopRight.y < 0f || mapTopRight.y + dy * s > camera.getHeight() / 2f;
        } else {
            // up
            return mapBottomLeft.y - dy * s < camera.getHeight() / 2f;
        }
    }

    private float calcSpeed(float d) {
        return camera.getLocation().z * 0.0025f;
    }

    private void saveCamera() {
        onSaveCamera.accept(camera.getLocation(), camera.getRotation());
    }

    private void saveZ() {
        onSaveZ.accept(currentZ);
    }

}
