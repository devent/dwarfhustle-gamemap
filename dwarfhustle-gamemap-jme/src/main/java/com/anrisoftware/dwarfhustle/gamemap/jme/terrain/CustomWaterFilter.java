package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.water.WaterFilter;

public class CustomWaterFilter extends WaterFilter {

    /**
     * Create a Water Filter
     */
    public CustomWaterFilter() {
        super();
    }

    public CustomWaterFilter(Node reflectionScene, Vector3f lightDirection) {
        super(reflectionScene, lightDirection);
    }

    @Override
    public Material getMaterial() {
        return super.getMaterial();
    }
}
