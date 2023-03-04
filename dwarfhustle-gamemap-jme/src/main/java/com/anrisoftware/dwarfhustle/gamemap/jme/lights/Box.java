package com.anrisoftware.dwarfhustle.gamemap.jme.lights;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

/**
 * Box for testing.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class Box {

    public Geometry node;

    public Box(AssetManager assetManager, Vector3f pos) {
        /**
         * A bumpy rock with a shiny light effect. To make bumpy objects you must create
         * a NormalMap.
         */
        Sphere sphereMesh = new Sphere(32, 32, 0.5f);
        Geometry sphereGeo = new Geometry("Shiny rock", sphereMesh);
        sphereMesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
        TangentBinormalGenerator.generate(sphereMesh); // for lighting effect
        Material sphereMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        sphereMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
        sphereMat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
        sphereMat.setBoolean("UseMaterialColors", true);
        sphereMat.setColor("Diffuse", ColorRGBA.White);
        sphereMat.setColor("Specular", ColorRGBA.White);
        sphereMat.setFloat("Shininess", 64f); // [0,128]
        sphereGeo.setMaterial(sphereMat);
        // sphereGeo.setMaterial((Material)
        // assetManager.loadMaterial("Materials/MyCustomMaterial.j3m"));
        sphereGeo.setLocalTranslation(pos); // Move it a bit
        sphereGeo.rotate(1.6f, 0, 0); // Rotate it a bit
        this.node = sphereGeo;
    }
}
