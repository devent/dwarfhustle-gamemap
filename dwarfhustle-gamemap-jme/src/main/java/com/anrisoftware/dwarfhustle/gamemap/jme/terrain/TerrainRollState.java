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

import static com.jme3.input.MouseInput.BUTTON_RIGHT;
import static com.jme3.math.FastMath.DEG_TO_RAD;

import jakarta.inject.Inject;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

import lombok.extern.slf4j.Slf4j;

/**
 * Rolls the terrain.
 *
 * <ul>
 * <li>Shift+M_R+Mouse Up/Down: rolls the terrain around the X-axis.
 * <li>Shift+M_R+Mouse Left/Right: rolls the terrain around the Y-axis.
 * <li>F10: resets the terrain.
 * </ul>
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainRollState extends BaseAppState implements ActionListener, RawInputListener {

    private static final String RIGHT_BUTTON_MAPPING = "TerrainRollState_right";

    private static final String SHIFT_MAPPING = "TerrainRollState_shift";

    private static final String RESET_MAPPING = "TerrainRollState_reset";

    private static final String[] MAPPINGS = new String[] { RIGHT_BUTTON_MAPPING, SHIFT_MAPPING, RESET_MAPPING };

    private final Vector2f mouse;

    @Inject
    private InputManager inputManager;

    private boolean rightMouseDown;

    private Node terrainNode;

    private boolean shiftDown = false;

    private float roll;

    private float pitch;

    private Node boundingNode;

    @Inject
    public TerrainRollState() {
        super(TerrainRollState.class.getSimpleName());
        this.mouse = new Vector2f();
        this.roll = 0f;
        this.pitch = 0f;
    }

    public void resetTerrain() {
        this.roll = 0f;
        this.pitch = 0f;
        terrainNode.setLocalRotation(new Quaternion().fromAngles(0f, 0f, 0f));
        boundingNode.setLocalRotation(new Quaternion().fromAngles(0f, 0f, 0f));
    }

    public void setTerrainNode(Node node) {
        this.terrainNode = node;
    }

    public void setBoundingNode(Node node) {
        this.boundingNode = node;
    }

    @Override
    protected void initialize(Application app) {
    }

    @Override
    protected void cleanup(Application app) {
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
        inputManager.addMapping(RESET_MAPPING, new KeyTrigger(KeyInput.KEY_F10));
        inputManager.addMapping(RIGHT_BUTTON_MAPPING, new MouseButtonTrigger(BUTTON_RIGHT));
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
        case RIGHT_BUTTON_MAPPING:
            rightMouseDown = isPressed;
            break;
        case SHIFT_MAPPING:
            shiftDown = isPressed;
            break;
        case RESET_MAPPING:
            resetTerrain();
            break;
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
        float dx = evt.getDX();
        float dy = -evt.getDY();
        var s = 0.1f;
        if (shiftDown) {
            if (rightMouseDown) {
                doTilt(dx, dy, s);
            }
        }
    }

    private void doTilt(float dx, float dy, float s) {
        roll += (dy + s) * 0.1f;
        if (roll > 45f) {
            roll = 45f;
        }
        if (roll < -45f) {
            roll = -45f;
        }
        pitch += (dx + s) * 0.1f;
        if (pitch > 45f) {
            pitch = 45f;
        }
        if (pitch < -45f) {
            pitch = -45f;
        }
        terrainNode.setLocalRotation(new Quaternion().fromAngles(DEG_TO_RAD * roll, DEG_TO_RAD * pitch, 0f));
        boundingNode.setLocalRotation(new Quaternion().fromAngles(DEG_TO_RAD * roll, DEG_TO_RAD * pitch, 0f));
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
