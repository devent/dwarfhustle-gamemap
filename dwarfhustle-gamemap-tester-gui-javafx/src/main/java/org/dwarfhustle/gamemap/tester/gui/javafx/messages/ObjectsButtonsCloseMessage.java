package org.dwarfhustle.gamemap.tester.gui.javafx.messages;

import com.anrisoftware.dwarfhustle.gui.javafx.messages.GuiMessage;

import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

/**
 * Closes the terrain materials buttons.
 * 
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
@RequiredArgsConstructor
public class ObjectsButtonsCloseMessage extends GuiMessage {

    public final VBox testerButtonsBox;
}
