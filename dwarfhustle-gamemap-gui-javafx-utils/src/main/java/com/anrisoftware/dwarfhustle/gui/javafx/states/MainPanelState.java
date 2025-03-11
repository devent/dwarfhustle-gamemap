/*
 * dwarfhustle-gamemap-gui-javafx-utils - Game map.
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
package com.anrisoftware.dwarfhustle.gui.javafx.states;

import com.anrisoftware.dwarfhustle.gui.javafx.messages.MainWindowResizedMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.badlogic.ashley.core.Engine;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * This is the state for the main window pane for the game map.
 * <p>
 * Adds the {@link PanelRenderSystem} that reacts to window resize to send
 * {@link MainWindowResizedMessage}.
 * <p>
 * On disable it sends the {@link ShutdownMessage}.
 *
 * @author Erwin Müller
 */
@Slf4j
public class MainPanelState extends BaseAppState {

    @Inject
    private Engine engine;

    @Inject
    private PanelRenderSystem panelRenderSystem;

    @Inject
    private ActorSystemProvider actor;

    @Override
    protected void initialize(Application app) {
        log.debug("initialize");
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        log.debug("onEnable");
        engine.addSystem(panelRenderSystem);
    }

    @Override
    protected void onDisable() {
        log.debug("onDisable");
        engine.removeSystem(panelRenderSystem);
        actor.get().tell(new ShutdownMessage());
    }

}
