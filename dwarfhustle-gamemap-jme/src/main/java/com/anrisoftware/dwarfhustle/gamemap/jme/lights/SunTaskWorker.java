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
package com.anrisoftware.dwarfhustle.gamemap.jme.lights;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.SunModel;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.google.inject.assistedinject.Assisted;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import jakarta.inject.Inject;

/**
 * Updates the light based on the position of the sun.
 *
 * @author Erwin Müller
 */
public class SunTaskWorker implements Runnable {

    public interface SunTaskWorkerFactory {

        SunTaskWorker create(WorldMap wm, GameMap gm);
    }

    private final Entity entity;

    @Inject
    @Assisted
    private WorldMap wm;

    @Inject
    @Assisted
    private GameMap gm;

    @Inject
    private SunModel model;

    private final Engine engine;

    @Inject
    public SunTaskWorker(Engine engine) {
        this.engine = engine;
        var dlc = new DirectionalLightComponent(new Vector3f(), new ColorRGBA());
        dlc.enabled = false;
        dlc.shadow = true;
        this.entity = engine.createEntity().add(new AmbientLightComponent(new ColorRGBA())).add(dlc);
        engine.addEntity(entity);
    }

    @Override
    public void run() {
        var lat = gm.getArea().getCenter().lat;
        var lng = gm.getArea().getCenter().lon;
        model.update(wm.getTime().atZone(gm.getTimeZone()), lat, lng);
        var ac = AmbientLightComponent.m.get(entity);
        ac.color.r = model.ambientColor[0];
        ac.color.g = model.ambientColor[1];
        ac.color.b = model.ambientColor[2];
        var dc = DirectionalLightComponent.m.get(entity);
        dc.enabled = model.visible;
        dc.shadow = model.visible;
        dc.d.x = -model.x;
        dc.d.y = -model.y;
        dc.d.z = -model.z;
        dc.d.normalizeLocal();
        dc.color.r = model.color[0];
        dc.color.g = model.color[1];
        dc.color.b = model.color[2];
        gm.setSunPosition(dc.d.x, dc.d.y, dc.d.z);
    }

    public void stop() {
        engine.removeEntity(entity);
    }
}
