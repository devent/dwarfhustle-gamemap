package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import javax.inject.Inject;
import javax.inject.Named;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;

/**
 * Debug coordinate axes.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class DebugCoordinateAxesState extends BaseAppState {

	@Inject
	@Named("rootNode")
	private Node rootNode;

	private Node axesNode;

	@Override
	protected void initialize(Application app) {
		axesNode = new Node("axes");
		attachCoordinateAxes(Vector3f.ZERO, axesNode);
	}

	@Override
	protected void cleanup(Application app) {
	}

	@Override
	protected void onEnable() {
		rootNode.attachChild(axesNode);
	}

	@Override
	protected void onDisable() {
		rootNode.detachChild(axesNode);
	}

	private void attachCoordinateAxes(Vector3f pos, Node node) {
		var arrow = new Arrow(Vector3f.UNIT_X);
		putShape(arrow, ColorRGBA.Red, node).setLocalTranslation(pos);
		arrow = new Arrow(Vector3f.UNIT_Y);
		putShape(arrow, ColorRGBA.Green, node).setLocalTranslation(pos);
		arrow = new Arrow(Vector3f.UNIT_Z);
		putShape(arrow, ColorRGBA.Blue, node).setLocalTranslation(pos);
	}

	private Geometry putShape(Mesh shape, ColorRGBA color, Node node) {
		var g = new Geometry("coordinate axis", shape);
		var mat = new Material(getApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setWireframe(true);
		mat.getAdditionalRenderState().setLineWidth(4);
		mat.setColor("Color", color);
		g.setMaterial(mat);
		node.attachChild(g);
		return g;
	}
}
