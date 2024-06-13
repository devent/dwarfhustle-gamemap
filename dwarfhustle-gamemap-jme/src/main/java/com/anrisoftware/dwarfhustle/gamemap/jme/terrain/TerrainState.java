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

import static com.jme3.math.FastMath.DEG_TO_RAD;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import com.jme3.water.SimpleWaterProcessor;

import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Shows the terrain blocks.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class TerrainState extends BaseAppState {

    @Inject
    @Named("rootNode")
    private Node rootNode;

    @Inject
    private ViewPort viewPort;

    private final Node terrainNode = new Node("terrainNode");

    private final Node skyNode = new Node("Sky");

    private SimpleWaterProcessor waterProcessor;

    private Spatial sky;

    private final Vector3f lightDir = new Vector3f(0, 0, -1);

    private Mesh mesh;

    private Geometry geo;

    @Override
    protected void initialize(Application app) {
        createSky(app.getAssetManager());
        waterProcessor = new SimpleWaterProcessor(app.getAssetManager());
        waterProcessor.setReflectionScene(skyNode);
        waterProcessor.setDebug(true);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Z, Vector3f.ZERO));
        waterProcessor.setWaterDepth(1); // transparency of water
        waterProcessor.setDistortionScale(0.05f); // strength of waves
        waterProcessor.setWaveSpeed(0.05f); // speed of waves
        viewPort.addProcessor(waterProcessor);
    }

    private void createSky(AssetManager as) {
        this.sky = SkyFactory.createSky(as, "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap);
        sky.rotate(DEG_TO_RAD * 90f, 0, 0);
        skyNode.attachChild(sky);
        rootNode.attachChild(skyNode);
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        rootNode.attachChild(terrainNode);
    }

    @Override
    protected void onDisable() {
        rootNode.detachChild(terrainNode);
    }

    public void clearBlockNodes() {
        terrainNode.detachAllChildren();
    }

    public void addBlockMesh(Geometry geo) {
        terrainNode.attachChild(geo);
    }

    public Node getNode() {
        return terrainNode;
    }

    public void clearWaterNodes() {
    }

    public void addWaterMesh(Geometry geo) {
        this.geo = geo;
        this.mesh = geo.getMesh();
        geo.setMaterial(waterProcessor.getMaterial());
    }

    public void setLightDir(Vector3f lightDir) {
        this.lightDir.set(lightDir);
    }

    public void setLightDir(float x, float y, float z) {
        this.lightDir.set(x, y, z);
    }

    float time = 0f;

    @Override
    public void update(float tpf) {
        time += tpf;
        if (time > 1f) {
            time = 0f;
            waterProcessor.setDebug(true);
            var waterLocation = new Vector3f(0, 0, 2f);
            waterProcessor.setPlane(new Plane(Vector3f.UNIT_Z, waterLocation.dot(Vector3f.UNIT_Z)));
            waterProcessor.setRefractionClippingOffset(1.0f);
            waterProcessor.setDistortionScale(1.05f); // strength of waves
            waterProcessor.setWaterColor(ColorRGBA.Red.mult(1.0f));
            waterProcessor.setWaterDepth(1000f); // transparency of water
            waterProcessor.setWaveSpeed(0.1f); // speed of waves
            if (geo != null) {
                // geo.setMaterial(waterProcessor.getMaterial());
                // mesh.scaleTextureCoordinates(new Vector2f(2f, 2f));
            }
        }
    }
}
