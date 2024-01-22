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

import static com.anrisoftware.dwarfhustle.model.api.objects.MapBlock.getMapBlock;
import static com.jme3.input.MouseInput.AXIS_WHEEL;
import static com.jme3.input.MouseInput.BUTTON_MIDDLE;
import static com.jme3.input.MouseInput.BUTTON_RIGHT;
import static java.lang.Math.abs;

import java.util.Optional;
import java.util.function.Consumer;

import com.anrisoftware.dwarfhustle.model.api.objects.CenterExtent;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.MapCursor;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
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
import com.jme3.scene.Node;
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

    private ObjectsGetter objectsg;

    private boolean middleMouseDown = false;

    private final Vector3f mapBottomLeft;

    private final Vector3f mapTopRight;

    private GameMap gm;

    private Optional<Consumer<GameMap>> saveCamera = Optional.empty();

    private Optional<Consumer<GameMap>> saveZ = Optional.empty();

    private Optional<Consumer<GameMap>> saveSelectedMapBlock = Optional.empty();

    private boolean rightMouseDown;

    private BoundingBox bounds;

    private Node terrainNode;

    private boolean shiftDown = false;

    @Inject
    public TerrainCameraState() {
        super(TerrainCameraState.class.getSimpleName());
        this.mouse = new Vector2f();
        this.mapBottomLeft = new Vector3f();
        this.mapTopRight = new Vector3f();
        this.bounds = new BoundingBox(new Vector3f(), 10f, 10f, 10f);
    }

    public void setObjectsg(ObjectsGetter objectsg) {
        this.objectsg = objectsg;
    }

    public void setSaveCamera(Consumer<GameMap> saveCamera) {
        this.saveCamera = Optional.of(saveCamera);
    }

    public void setSaveZ(Consumer<GameMap> saveZ) {
        this.saveZ = Optional.of(saveZ);
    }

    public void setSaveSelectedMapBlock(Consumer<GameMap> saveSelectedMapBlock) {
        this.saveSelectedMapBlock = Optional.of(saveSelectedMapBlock);
    }

    public void resetCamera() {
        camera.setLocation(new Vector3f(0.0f, 0.0f, 10.0f));
        camera.setRotation(new Quaternion(0.0f, 1.0f, 0.0f, 0.0f));
    }

    public void setTerrainBounds(BoundingBox bounds) {
        this.bounds = bounds;
        updateScreenCoordinatesMap();
    }

    public void updateCamera(GameMap gm) {
        this.gm = gm;
        camera.setLocation(new Vector3f(gm.getCameraPos()[0], gm.getCameraPos()[1], gm.getCameraPos()[2]));
        camera.setRotation(
                new Quaternion(gm.getCameraRot()[0], gm.getCameraRot()[1], gm.getCameraRot()[2], gm.getCameraRot()[3]));
    }

    private void saveCamera() {
        saveCamera.ifPresent(it -> it.accept(gm));
    }

    private void saveZ() {
        saveZ.ifPresent(it -> it.accept(gm));
    }

    private void saveSelectedMapBlock() {
        saveSelectedMapBlock.ifPresent(it -> it.accept(gm));
    }

    public void setCameraPos(float x, float y, float z) {
        camera.setLocation(new Vector3f(x, y, z));
        camera.update();
        saveCamera();
    }

    public void setTerrainNode(Node node) {
        this.terrainNode = node;
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
        inputManager.addMapping(SHIFT_MAPPING, new KeyTrigger(KeyInput.KEY_LSHIFT),
                new KeyTrigger(KeyInput.KEY_RSHIFT));
        inputManager.addMapping(MIDDLE_BUTTON_MAPPING, new MouseButtonTrigger(BUTTON_MIDDLE));
        inputManager.addMapping(RIGHT_BUTTON_MAPPING, new MouseButtonTrigger(BUTTON_RIGHT));
        inputManager.addMapping(Z_IN_MAPPING, new MouseAxisTrigger(AXIS_WHEEL, false));
        inputManager.addMapping(Z_OUT_MAPPING, new MouseAxisTrigger(AXIS_WHEEL, true));
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
        var m = 1f;
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
        var z = gm.cursor.z;
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
        var temp = TempVars.get();
        var oldpos = temp.vect1;
        var newpos = temp.vect2;
        camera.getWorldCoordinates(new Vector2f(camera.getWidth() / 2f, camera.getHeight() / 2f), 0.0f, oldpos);
        camera.getWorldCoordinates(mouse, 0.0f, newpos);
        updateSelectedObject();
        if (!shiftDown && (middleMouseDown || rightMouseDown)) {
            updateScreenCoordinatesMap();
            float dx = evt.getDX();
            float dy = -evt.getDY();
            var s = calcSpeed(Math.max(abs(dx), abs(dy)));
            if (middleMouseDown) {
                doMove(dx, dy, s);
            } else if (rightMouseDown) {
                doZoom(dx, dy, s, oldpos, newpos);
            }
        }
        temp.release();
    }

    private void updateSelectedObject() {
        if (gm == null) {
            return;
        }
        var temp = TempVars.get();
        var rootchunk = objectsg.get(MapChunk.class, MapChunk.OBJECT_TYPE, gm.root);
        var z = gm.cursor.z;
        var chunk = findChunkUnderCursor(mouse, rootchunk, z, temp);
        if (chunk != null) {
            var mb = findBlockUnderCursor(mouse, chunk, z, temp);
            if (mb != null) {
                gm.cursor = new MapCursor(mb.pos.x, mb.pos.y, z);
                saveSelectedMapBlock();
            }
        }
        temp.release();
    }

    private MapBlock findBlockUnderCursor(Vector2f mouse, MapChunk chunk, int z, TempVars temp) {
        for (var k : chunk.blocks.keyValuesView()) {
            var blockid = k.getTwo();
            var block = getMapBlock(objectsg, blockid);
            if (block.pos.z == z && checkCenterExtent(mouse, block.centerExtent, temp)) {
                return block;
            }
        }
        return null;
    }

    private MapChunk findChunkUnderCursor(Vector2f mouse, MapChunk chunk, int z, TempVars temp) {
        if (chunk.blocks.isEmpty()) {
            for (var kvalues : chunk.chunks.keyValuesView()) {
                var id = kvalues.getTwo();
                var c = findChunkUnderCursor(mouse, objectsg.get(MapChunk.class, MapChunk.OBJECT_TYPE, id), z, temp);
                if (c == null) {
                    continue;
                } else {
                    return c;
                }
            }
        } else {
            if (chunk.pos.z <= z && chunk.pos.ep.z > z && checkCenterExtent(mouse, chunk.centerExtent, temp)) {
                return chunk;
            }
        }
        return null;
    }

    private boolean checkCenterExtent(Vector2f mouse, CenterExtent centerExtent, TempVars temp) {
        var bottomc = temp.vect1;
        var topc = temp.vect2;
        bottomc.x = centerExtent.getBottomX();
        bottomc.y = centerExtent.getBottomY();
        bottomc.z = centerExtent.getBottomZ();
        camera.getScreenCoordinates(bottomc, bottomc);
        topc.x = centerExtent.getTopX();
        topc.y = centerExtent.getTopY();
        topc.z = centerExtent.getTopZ();
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

    private void updateScreenCoordinatesMap() {
        var temp = TempVars.get();
        var btr = bounds.getMax(temp.vect1);
        var bbl = bounds.getMin(temp.vect2);
        camera.getScreenCoordinates(btr, mapTopRight);
        camera.getScreenCoordinates(bbl, mapBottomLeft);
        temp.release();
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
        var tmpTopRight = temp.vect1;
        var tmpBottomLeft = temp.vect2;
        var topright = temp.vect3.set(2f, 2f, 0f);
        var bottomleft = temp.vect4.set(-2f, -2f, 0f);
        tmpc.getScreenCoordinates(topright, tmpTopRight);
        tmpc.getScreenCoordinates(bottomleft, tmpBottomLeft);
        if (dy > 0) {
            // zoom out
            if (tmpTopRight.x < 0f || tmpBottomLeft.x < 0 || tmpTopRight.x - tmpBottomLeft.x > 10) {
                boundMove(0f, 0f, dy * s);
            }
        } else {
            // zoom in
            if (tmpTopRight.x > 0f && tmpBottomLeft.x > 0 && tmpTopRight.x - tmpBottomLeft.x < camera.getWidth()) {
                boundMove(0f, 0f, dy * s);
            }
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

}
