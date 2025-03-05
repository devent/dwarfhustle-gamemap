package org.dwarfhustle.gamemap.tester.gui.javafx.messages;

import com.anrisoftware.dwarfhustle.gui.javafx.messages.GuiMessage;

import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

/**
 * Open of the time buttons message.
 *
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
@RequiredArgsConstructor
public class TimeButtonsOpenMessage extends GuiMessage {

    public final VBox testerButtonsBox;
}
