package com.anrisoftware.dwarfhustle.gui.javafx.messages;

import javafx.scene.control.Tab;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Message to attach the pane to the object tab.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
@ToString
public class ObjectPaneAttachToTabMessage extends GuiMessage {
    public final Tab tab;
}
