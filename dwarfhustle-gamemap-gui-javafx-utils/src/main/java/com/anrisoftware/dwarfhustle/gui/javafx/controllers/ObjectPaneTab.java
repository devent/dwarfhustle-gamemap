package com.anrisoftware.dwarfhustle.gui.javafx.controllers;

/**
 * Additional object pane tab.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public interface ObjectPaneTab {

    ObjectPaneTabController create();

    void update(ObjectPaneTabController c);

}
