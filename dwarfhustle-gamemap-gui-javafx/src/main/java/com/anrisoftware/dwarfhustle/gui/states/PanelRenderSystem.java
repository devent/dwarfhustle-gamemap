/*
 * Dwarf Hustle Game Map - Game map.
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
package com.anrisoftware.dwarfhustle.gui.states;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.gui.messages.MainWindowResizedMessage;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.jme3.renderer.Camera;

/**
 * If the window width or height changes, send a
 * {@link MainWindowResizedMessage} message to the panel actor.
 *
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
public class PanelRenderSystem extends IntervalIteratingSystem {

    private final Camera camera;

    private int panelWidth;

    private int panelHeight;

    @Inject
    public PanelRenderSystem(Camera camera) {
        super(Family.all(PanelComponent.class).get(), 0.33f);
        this.camera = camera;
        this.panelWidth = camera.getWidth();
        this.panelHeight = camera.getHeight();
    }

    @Override
    protected void processEntity(Entity entity) {
        int camWidth = camera.getWidth();
        int camHeight = camera.getHeight();
        if (panelWidth != camWidth || panelHeight != camHeight) {
            this.panelWidth = camWidth;
            this.panelHeight = camHeight;
            var actor = PanelComponent.m.get(entity).actor;
            actor.tell(new MainWindowResizedMessage(camWidth, camHeight));
        }
    }

}
