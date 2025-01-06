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
