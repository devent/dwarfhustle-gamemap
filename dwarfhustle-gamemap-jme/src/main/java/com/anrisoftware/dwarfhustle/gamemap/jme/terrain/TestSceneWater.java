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

import java.io.File;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.HttpZipLocator;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.RectangleMesh;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.SkyFactory;
import com.jme3.water.SimpleWaterProcessor;

public class TestSceneWater extends SimpleApplication {

    // set default for applets
    private static boolean useHttp = false;

    public static void main(String[] args) {

        TestSceneWater app = new TestSceneWater();
        app.start();
    }

    private SimpleWaterProcessor waterProcessor;
    private Vector3f waterLocation;

    @Override
    public void simpleInitApp() {
        File file = new File("wildhouse.zip");
        if (!file.exists()) {
            useHttp = true;
        }

        this.flyCam.setMoveSpeed(10);
        Node mainScene = new Node();
        cam.setLocation(new Vector3f(-27.0f, 1.0f, 75.0f));
        cam.setRotation(new Quaternion(0.03f, 0.9f, 0f, 0.4f));

        // load sky
        mainScene.attachChild(
                SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));

        // create the geometry and attach it
        // load the level from zip or http zip
        if (useHttp) {
            assetManager.registerLocator(
                    "https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/jmonkeyengine/wildhouse.zip",
                    HttpZipLocator.class);
        } else {
            assetManager.registerLocator("wildhouse.zip", ZipLocator.class);
        }
        Spatial scene = assetManager.loadModel("main.scene");

        DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir = new Vector3f(-0.37352666f, -0.50444174f, -0.7784704f);
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(2));
        scene.addLight(sun);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
        // add lightPos Geometry
        Sphere lite = new Sphere(8, 8, 3.0f);
        Geometry lightSphere = new Geometry("lightsphere", lite);
        lightSphere.setMaterial(mat);
        Vector3f lightPos = lightDir.multLocal(-400);
        lightSphere.setLocalTranslation(lightPos);
        rootNode.attachChild(lightSphere);

        this.waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(mainScene);
        waterProcessor.setDebug(true);
        waterProcessor.setLightPosition(lightPos);
        waterProcessor.setRefractionClippingOffset(1.0f);
        waterProcessor.setWaterColor(ColorRGBA.Red);
        waterProcessor.setWaterTransparency(1000.0f);
        waterProcessor.setWaterDepth(.1f);

        // setting the water plane
        this.waterLocation = new Vector3f(0, -20, 0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
        WaterUI waterUi = new WaterUI(inputManager, waterProcessor);
        // lower render size for higher performance
//        waterProcessor.setRenderSize(128,128);
        // raise depth to see through water
//        waterProcessor.setWaterDepth(20);
        // lower the distortion scale if the waves appear too strong
//        waterProcessor.setDistortionScale(0.1f);
        // lower the speed of the waves if they are too fast
//        waterProcessor.setWaveSpeed(0.01f);

        RectangleMesh rect = new RectangleMesh(new Vector3f(-200, -20, 250), new Vector3f(200, -20, 250),
                new Vector3f(-200, -20, -150));

        // the texture coordinates define the general size of the waves
        rect.scaleTextureCoordinates(new Vector2f(6f, 6f));

        Geometry water = new Geometry("water", rect);
        water.setShadowMode(ShadowMode.Receive);
        water.setMaterial(waterProcessor.getMaterial());

        rootNode.attachChild(water);

        viewPort.addProcessor(waterProcessor);

        mainScene.attachChild(scene);
        rootNode.attachChild(mainScene);
    }

    float time = 0f;

    @Override
    public void simpleUpdate(float tpf) {
        time += tpf;
        if (time > 1f) {
            time = 0f;
            this.waterLocation = new Vector3f(0, -20, 0);
            waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
//            waterProcessor.setDebug(true);
//            waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
//            waterProcessor.setWaterColor(ColorRGBA.Blue.mult(1.0f)); // transparency of water
//            waterProcessor.setWaterDepth(10f); // transparency of water
//            waterProcessor.setDistortionScale(1.05f); // strength of waves
//            waterProcessor.setWaveSpeed(0.05f); // speed of waves
//            // if (geo != null) {
//            // geo.setMaterial(waterProcessor.getMaterial());
//            // mesh.scaleTextureCoordinates(new Vector2f(2f, 2f));
            // }
        }
    }

}