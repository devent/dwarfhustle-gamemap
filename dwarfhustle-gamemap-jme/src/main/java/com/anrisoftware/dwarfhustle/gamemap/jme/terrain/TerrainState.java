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
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import com.jme3.util.TempVars;
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
    @Named("sceneNode")
    private Node sceneNode;

    @Inject
    private ViewPort viewPort;

    private final Node terrainNode = new Node("Terrain-Node");

    private final Node ceilingNode = new Node("Ceiling-Node");

    private final Node waterNode = new Node("Water-Node");

    private final Node skyNode = new Node("Sky-Node");

    private SimpleWaterProcessor waterProcessor;

    private Spatial sky;

    private final Vector3f lightDir = new Vector3f(0, 0, -1);

    private Mesh waterMesh;

    private Geometry waterGeo;

    private SimpleWaterProcessor magmaProcessor;

    private Geometry magmaGeo;

    private Mesh magmaMesh;

    private float waterPos;

    private float magmaPos;

    @Override
    protected void initialize(Application app) {
        createSky(app.getAssetManager());
        this.waterProcessor = createWaterProcessor(app.getAssetManager());
        this.magmaProcessor = createMagmaProcessor(app.getAssetManager());
        viewPort.addProcessor(waterProcessor);
        viewPort.addProcessor(magmaProcessor);
    }

    private SimpleWaterProcessor createWaterProcessor(AssetManager as) {
        var p = new SimpleWaterProcessor(as);
        p.setReflectionScene(sceneNode);
        p.setRefractionClippingOffset(0.3f);
        p.setDistortionScale(0.05f);
        p.setWaterColor(ColorRGBA.Blue.mult(0.1f));
        p.setWaterTransparency(1f);
        p.setWaterDepth(0.1f);
        p.setWaveSpeed(0.01f);
        p.setLightPosition(lightDir);
        p.setDebug(false);
        return p;
    }

    private SimpleWaterProcessor createMagmaProcessor(AssetManager as) {
        var p = new SimpleWaterProcessor(as);
        p.setReflectionScene(sceneNode);
        p.setReflectionClippingOffset(-5f);
        p.setRefractionClippingOffset(0.5f);
        p.setDistortionScale(0.05f);
        p.setWaterColor(ColorRGBA.Red.mult(1.0f));
        p.setWaterTransparency(1000f);
        p.setWaterDepth(0.1f);
        p.setWaveSpeed(0.01f);
        return p;
    }

    private void createSky(AssetManager as) {
        this.sky = SkyFactory.createSky(as, "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap);
        sky.rotate(DEG_TO_RAD * 90f, 0, 0);
        skyNode.attachChild(sky);
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        sceneNode.attachChild(terrainNode);
        sceneNode.attachChild(ceilingNode);
        sceneNode.attachChild(skyNode);
        rootNode.attachChild(waterNode);
    }

    @Override
    protected void onDisable() {
        sceneNode.detachChild(terrainNode);
        sceneNode.detachChild(ceilingNode);
        sceneNode.detachChild(skyNode);
        rootNode.detachChild(waterNode);
    }

    public void clearBlockNodes() {
        terrainNode.detachAllChildren();
    }

    public void addBlockMesh(Geometry geo) {
        terrainNode.attachChild(geo);
    }

    public Node getTerrainNode() {
        return terrainNode;
    }

    public Node getWaterNode() {
        return waterNode;
    }

    public void clearWaterNodes() {
        waterNode.detachAllChildren();
    }

    public void addWaterMesh(Geometry geo) {
        this.waterGeo = geo;
        this.waterMesh = geo.getMesh();
        waterProcessor.setWaterTransparency(100f);
        waterMesh.scaleTextureCoordinates(new Vector2f(0.25f, 0.25f));
        geo.setMaterial(waterProcessor.getMaterial());
        waterNode.attachChild(geo);
    }

    public void addMagmaMesh(Geometry geo) {
        this.magmaGeo = geo;
        this.magmaMesh = geo.getMesh();
        magmaMesh.scaleTextureCoordinates(new Vector2f(0.5f, 0.5f));
        geo.setMaterial(magmaProcessor.getMaterial());
        waterNode.attachChild(geo);
    }

    public void clearCeilingNodes() {
        ceilingNode.detachAllChildren();
    }

    public void addCeilingMesh(Geometry geo) {
        ceilingNode.attachChild(geo);
    }

    public void setLightDir(Vector3f lightDir) {
        setLightDir(lightDir.x, lightDir.y, lightDir.z);
    }

    public void setLightDir(float x, float y, float z) {
        this.lightDir.set(x, y, z);
        waterProcessor.setLightPosition(lightDir);
        magmaProcessor.setLightPosition(lightDir);
    }

    public void setWaterPos(float z) {
        if (waterPos != z) {
            this.waterPos = z;
            var tmp = TempVars.get();
            tmp.vect1.x = 0;
            tmp.vect1.y = 0;
            tmp.vect1.z = z;
            waterProcessor.setPlane(tmp.vect1, Vector3f.UNIT_Z);
            tmp.release();
        }
    }

    public void setMagmaPos(float z) {
        if (magmaPos != z) {
            this.magmaPos = z;
            var tmp = TempVars.get();
            tmp.vect1.x = 0;
            tmp.vect1.y = 0;
            tmp.vect1.z = z;
            magmaProcessor.setPlane(new Plane(Vector3f.UNIT_Z, tmp.vect1.dot(Vector3f.UNIT_Z)));
            tmp.release();
        }
    }

}
