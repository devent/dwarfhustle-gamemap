/*
 * dwarfhustle-gamemap-gui-javafx - GUI in Javafx.
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
package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;

import com.anrisoftware.dwarfhustle.gamemap.jme.map.MapBlockBox.MapBlockBoxFactory;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.util.TempVars;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
@Slf4j
public class MapRenderSystem extends IntervalIteratingSystem {

	private final MapBlockBoxFactory mapBlockBoxFactory;

	private final MutableMap<MapBlockComponent, MapBlockBox> boxes;

	@Inject
	private Camera camera;

	@Inject
	@Named("rootNode")
	private Node rootNode;

	private MapBlockBox rootMapBlockBox;

	private BoundingBox rootWorldBound;

	private float rootWidth;

	private float rootHeight;

	private float rootDepth;

	@Inject
	public MapRenderSystem(MapBlockBoxFactory mapBlockBoxFactory) {
		super(Family.all(MapBlockComponent.class).get(), 0.33f);
		this.mapBlockBoxFactory = mapBlockBoxFactory;
		this.boxes = Maps.mutable.of();
		this.rootWorldBound = new BoundingBox(Vector3f.ZERO, 64f, 64f, 64f);
		this.rootWidth = 64f;
		this.rootHeight = 64f;
		this.rootDepth = 64f;
		this.rootMapBlockBox = null;
	}

	/**
	 * Returns the top right and bottom left of the noise quad in screen
	 * coordinates.
	 */
	public void getScreenCoordinatesMap(Camera camera, Vector3f topRight, Vector3f bottomLeft) {
		var temp = TempVars.get();
		try {
			var bb = rootWorldBound;
			var btr = bb.getMax(temp.vect1);
			var bbl = bb.getMin(temp.vect2);
			camera.getScreenCoordinates(btr, topRight);
			camera.getScreenCoordinates(bbl, bottomLeft);
		} finally {
			temp.release();
		}
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(getFamily(), new EntityListener() {

			@Override
			public void entityRemoved(Entity entity) {
				log.debug("entityRemoved {}", entity);
			}

			@Override
			public void entityAdded(Entity entity) {
				log.debug("entityAdded {}", entity);
				createMapBlockBox(MapBlockComponent.m.get(entity));
			}
		});
	}

	private void createMapBlockBox(MapBlockComponent c) {
		var box = mapBlockBoxFactory.create(c);
		boxes.put(c, box);
		rootNode.attachChild(box.geo);
		if (c.mb.isRoot()) {
			this.rootMapBlockBox = box;
			this.rootWorldBound = rootMapBlockBox.getWorldBound();
			this.rootWidth = rootMapBlockBox.c.mb.getWidth();
			this.rootHeight = rootMapBlockBox.c.mb.getHeight();
			this.rootDepth = rootMapBlockBox.c.mb.getDepth();
		}
	}

	@Override
	protected void processEntity(Entity entity) {
	}

	public float getWidth() {
		return rootWidth;
	}

	public float getHeight() {
		return rootHeight;
	}

	public float getDepth() {
		return rootDepth;
	}

}
