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

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.SkyFactory;
import com.jme3.water.SimpleWaterProcessor;

/**
 *
 * @author normenhansen
 */
public class TestSimpleWater extends SimpleApplication implements ActionListener {

    Material mat;
    Spatial waterPlane;
    Geometry lightSphere;
    SimpleWaterProcessor waterProcessor;
    Node sceneNode;
    boolean useWater = true;
    private final Vector3f lightPos = new Vector3f(33, 12, -29);
    private Geometry geom;
    private Box mesh;

    public static void main(String[] args) {
        TestSimpleWater app = new TestSimpleWater();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        initInput();
        initScene();

        // create processor
        waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(sceneNode);
        waterProcessor.setDebug(true);
        viewPort.addProcessor(waterProcessor);

        waterProcessor.setLightPosition(lightPos);

        // create water quad
        // waterPlane = waterProcessor.createWaterGeometry(100, 100);
        waterPlane = assetManager.loadModel("Models/WaterTest/WaterTest.mesh.xml");
        waterPlane.setMaterial(waterProcessor.getMaterial());
        waterPlane.setLocalScale(40);
        waterPlane.setLocalTranslation(-5, 0, 5);

        rootNode.attachChild(waterPlane);
    }

    private void initScene() {
        // init cam location
        cam.setLocation(new Vector3f(0, 10, 10));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        // init scene
        sceneNode = new Node("Scene");
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
        this.mesh = new Box(1, 1, 1);
        this.geom = new Geometry("Box", mesh);
        geom.setMaterial(mat);
        sceneNode.attachChild(geom);

        // load sky
        sceneNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
        rootNode.attachChild(sceneNode);

        // add lightPos Geometry
        Sphere lite = new Sphere(8, 8, 3.0f);
        lightSphere = new Geometry("lightsphere", lite);
        lightSphere.setMaterial(mat);
        lightSphere.setLocalTranslation(lightPos);
        rootNode.attachChild(lightSphere);
    }

    protected void initInput() {
        flyCam.setMoveSpeed(3);
        // init input
        inputManager.addMapping("use_water", new KeyTrigger(KeyInput.KEY_O));
        inputManager.addListener(this, "use_water");
        inputManager.addMapping("lightup", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(this, "lightup");
        inputManager.addMapping("lightdown", new KeyTrigger(KeyInput.KEY_G));
        inputManager.addListener(this, "lightdown");
        inputManager.addMapping("lightleft", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addListener(this, "lightleft");
        inputManager.addMapping("lightright", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener(this, "lightright");
        inputManager.addMapping("lightforward", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addListener(this, "lightforward");
        inputManager.addMapping("lightback", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addListener(this, "lightback");
    }

    float time = 0f;

    @Override
    public void simpleUpdate(float tpf) {
        fpsText.setText("Light Position: " + lightPos.toString()
                + " Change Light position with [U], [H], [J], [K] and [T], [G] Turn off water with [O]");
        lightSphere.setLocalTranslation(lightPos);
        waterProcessor.setLightPosition(lightPos);
        time += tpf;
        if (time > 1f) {
            time = 0f;
            waterProcessor.setDebug(true);
            waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, new Vector3f(0f, 0f, 0f)));
            waterProcessor.setWaterColor(new ColorRGBA(1f, 0f, 0f, 1f)); // transparency of water
            waterProcessor.setWaterDepth(1f); // transparency of water
            waterProcessor.setDistortionScale(1.05f); // strength of waves
            waterProcessor.setWaveSpeed(0.05f); // speed of waves
            if (waterPlane != null) {
                waterPlane.setLocalScale(40);
                waterPlane.setQueueBucket(Bucket.Translucent);
            }
        }
    }

    @Override
    public void onAction(String name, boolean value, float tpf) {
        if (name.equals("use_water") && value) {
            if (!useWater) {
                useWater = true;
                waterPlane.setMaterial(waterProcessor.getMaterial());
            } else {
                useWater = false;
                waterPlane.setMaterial(mat);
            }
        } else if (name.equals("lightup") && value) {
            lightPos.y++;
        } else if (name.equals("lightdown") && value) {
            lightPos.y--;
        } else if (name.equals("lightleft") && value) {
            lightPos.x--;
        } else if (name.equals("lightright") && value) {
            lightPos.x++;
        } else if (name.equals("lightforward") && value) {
            lightPos.z--;
        } else if (name.equals("lightback") && value) {
            lightPos.z++;
        }
    }
}