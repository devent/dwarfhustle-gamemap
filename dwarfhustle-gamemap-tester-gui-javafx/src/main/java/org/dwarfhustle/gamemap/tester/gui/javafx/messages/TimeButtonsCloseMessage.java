package org.dwarfhustle.gamemap.tester.gui.javafx.messages;

import com.anrisoftware.dwarfhustle.gui.javafx.messages.GuiMessage;

import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

/**
 * Close of the time buttons message.
 *
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
@RequiredArgsConstructor
public class TimeButtonsCloseMessage extends GuiMessage {

    public final VBox testerButtonsBox;
}
