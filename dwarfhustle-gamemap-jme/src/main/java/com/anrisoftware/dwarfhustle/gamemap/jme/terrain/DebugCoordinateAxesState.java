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

import jakarta.inject.Inject;
import jakarta.inject.Named;

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
