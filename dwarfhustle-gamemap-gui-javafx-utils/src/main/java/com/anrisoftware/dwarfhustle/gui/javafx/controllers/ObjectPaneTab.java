package com.anrisoftware.dwarfhustle.gui.javafx.controllers;

/**
 * Additional object pane tab.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public interface ObjectPaneTab<T extends ObjectPaneTabController> {

    ObjectPaneTabController create();

    void update(T c);

}
