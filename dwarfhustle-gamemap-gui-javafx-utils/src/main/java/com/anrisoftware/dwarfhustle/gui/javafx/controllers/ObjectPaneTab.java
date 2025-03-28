package com.anrisoftware.dwarfhustle.gui.javafx.controllers;

import com.google.inject.Injector;

import javafx.scene.control.TabPane;

/**
 * Additional object pane tab.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public interface ObjectPaneTab {

    void create(Injector injector, TabPane tabPane);

}
