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
package com.anrisoftware.dwarfhustle.gamemap.jme;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.google.inject.Guice;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class App extends SimpleApplication {

	public static void main(String[] args) {
		var injector = Guice.createInjector(new AppModule());
		var app = injector.getInstance(App.class);
		app.start();
	}

	@Inject
	private ActorSystemProvider actorSystem;

	@Override
	public void simpleInitApp() {
		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
	}

	@Override
	public void stop(boolean waitFor) {
		super.stop(waitFor);
		actorSystem.get().tell(new ShutdownMessage());
		try {
			Await.ready(actorSystem.getActorSystem().whenTerminated(), Duration.create(1, TimeUnit.MINUTES));
		} catch (TimeoutException | InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
