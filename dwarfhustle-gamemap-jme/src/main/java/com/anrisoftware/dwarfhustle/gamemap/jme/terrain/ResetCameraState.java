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

import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Resets the camera.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class ResetCameraState extends BaseAppState implements ActionListener {

    private static final String RESET_CAMERA_MAPPING = "ResetCameraState_reset_camera";

    private static final String[] MAPPINGS = new String[] { RESET_CAMERA_MAPPING };

    private InputManager inputManager;

    private Camera camera;

    private final Optional<Consumer<GameMap>> saveCamera = Optional.empty();

    private GameMap gm;

    private Vector3f initialLocation;

    private Quaternion initialRotation;

    @Inject
    public ResetCameraState() {
        super(ResetCameraState.class.getSimpleName());
    }

    public void resetCamera() {
        camera.setLocation(initialLocation);
        camera.setRotation(initialRotation);
        saveCamera();
    }

    public void updateCamera(GameMap gm) {
        this.gm = gm;
        camera.setLocation(new Vector3f(gm.getCameraPos()[0], gm.getCameraPos()[1], gm.getCameraPos()[2]));
        camera.setRotation(
                new Quaternion(gm.getCameraRot()[0], gm.getCameraRot()[1], gm.getCameraRot()[2], gm.getCameraRot()[3]));
        this.initialLocation = camera.getLocation().clone();
        this.initialRotation = camera.getRotation().clone();
        initKeys();
    }

    private void saveCamera() {
        gm.cameraPos[0] = camera.getLocation().x;
        gm.cameraPos[1] = camera.getLocation().y;
        gm.cameraPos[2] = camera.getLocation().z;
        gm.cameraRot[0] = camera.getRotation().getX();
        gm.cameraRot[1] = camera.getRotation().getY();
        gm.cameraRot[2] = camera.getRotation().getZ();
        gm.cameraRot[3] = camera.getRotation().getW();
        saveCamera.ifPresent(it -> it.accept(gm));
    }

    @Override
    protected void initialize(Application app) {
        log.debug("initialize");
        this.inputManager = app.getInputManager();
        this.camera = app.getCamera();
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
        deleteKeys();
    }

    private void initKeys() {
        inputManager.addListener(this, MAPPINGS);
        inputManager.addMapping(RESET_CAMERA_MAPPING, new KeyTrigger(KeyInput.KEY_F11));
        System.out.println("F11  - " + RESET_CAMERA_MAPPING);
    }

    private void deleteKeys() {
        inputManager.removeListener(this);
        for (String element : MAPPINGS) {
            inputManager.deleteMapping(element);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
        case RESET_CAMERA_MAPPING:
            resetCamera();
            break;
        }
    }

}
