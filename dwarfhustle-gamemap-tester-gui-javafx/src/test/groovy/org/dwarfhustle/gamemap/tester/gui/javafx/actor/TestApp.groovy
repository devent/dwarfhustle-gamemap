/*
 * dwarfhustle-gamemap-tester-gui-javafx - Game map.
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
package org.dwarfhustle.gamemap.tester.gui.javafx.actor

import com.jme3.app.LostFocusBehavior
import com.jme3.app.SimpleApplication
import com.jme3.app.state.ConstantVerifierState
import com.jme3.system.AppSettings

/**
 * 
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
class TestApp extends SimpleApplication {

    public TestApp() {
        super(new ConstantVerifierState());
        setShowSettings(false);
        def s = new AppSettings(true);
        s.setResizable(true);
        s.setWidth(1280);
        s.setHeight(720);
        s.setVSync(false);
        s.setOpenCLSupport(false);
        setLostFocusBehavior(LostFocusBehavior.Disabled);
        setSettings(s);
    }

    @Override
    public void simpleInitApp() {
    }
}
