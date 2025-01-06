package org.dwarfhustle.gamemap.tester.gui.javafx.messages;

import com.anrisoftware.dwarfhustle.gui.javafx.messages.GuiMessage;

import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

/**
 * Opens the terrain buttons.
 * 
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
@RequiredArgsConstructor
public class MaterialsButtonsOpenMessage extends GuiMessage {

    public final VBox testerButtonsBox;
}
