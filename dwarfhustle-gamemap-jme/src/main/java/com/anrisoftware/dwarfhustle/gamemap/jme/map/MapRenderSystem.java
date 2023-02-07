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
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
@Slf4j
public class MapRenderSystem extends IntervalIteratingSystem {

	@Inject
	private Camera camera;

	@Inject
	@Named("rootNode")
	private Node rootNode;

	@Inject
	private MapBlockBoxFactory mapBlockBoxFactory;

	private final MutableMap<MapBlockComponent, MapBlockBox> boxes;

    @Inject
	public MapRenderSystem() {
		super(Family.all(MapBlockComponent.class).get(), 0.33f);
		this.boxes = Maps.mutable.of();
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
		rootNode.attachChild(box.getGeo());
	}

	@Override
    protected void processEntity(Entity entity) {
    }

}
