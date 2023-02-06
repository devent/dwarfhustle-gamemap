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
package com.anrisoftware.dwarfhustle.gamemap.jme.app;
import javax.inject.Named;

import com.anrisoftware.dwarfhustle.gamemap.jme.actors.GamemapActorsModule;
import com.anrisoftware.dwarfhustle.gui.actor.GamemapGuiActorsModule;
import com.anrisoftware.dwarfhustle.gui.controllers.GamemapGuiControllersModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

import lombok.RequiredArgsConstructor;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
public class GamemapJmeModule extends AbstractModule {

    private final SimpleApplication owner;

    @Override
    protected void configure() {
		install(new GamemapGuiActorsModule());
		install(new GamemapGuiControllersModule());
		install(new GamemapActorsModule());
    }

    @Provides
    public Application getApp() {
        return owner;
    }

    @Provides
    @Named("pivotNode")
    public Node getPivotNode() {
        return owner.getRootNode();
    }

    @Provides
    public InputManager getInputManager() {
        return owner.getInputManager();
    }

    @Provides
    public AssetManager getAssetManager() {
        return owner.getAssetManager();
    }

    @Provides
    public Camera getCamera() {
        return owner.getCamera();
    }

    @Provides
    public com.jme3.opencl.Context getOpenCLContext() {
        return owner.getContext().getOpenCLContext();
    }
}
