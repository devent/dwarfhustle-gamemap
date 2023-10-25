package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import javax.inject.Inject;
import javax.inject.Named;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 * Shows the terrain blocks.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class TerrainState extends BaseAppState {

    @Inject
    @Named("rootNode")
    private Node rootNode;

    private Node terrainNode = new Node("terrainNode");

    @Override
    protected void initialize(Application app) {
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

}
