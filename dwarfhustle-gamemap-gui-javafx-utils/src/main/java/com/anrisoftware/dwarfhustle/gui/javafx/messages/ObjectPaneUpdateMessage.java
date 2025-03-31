package com.anrisoftware.dwarfhustle.gui.javafx.messages;

import com.anrisoftware.dwarfhustle.gui.javafx.controllers.ObjectPaneController;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Message to update the object pane.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
@ToString
public class ObjectPaneUpdateMessage extends GuiMessage {
    public final int type;
    public final long id;
    public final ObjectPaneController c;
}
