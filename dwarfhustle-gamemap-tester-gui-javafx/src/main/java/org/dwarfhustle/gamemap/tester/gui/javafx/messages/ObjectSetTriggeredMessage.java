package org.dwarfhustle.gamemap.tester.gui.javafx.messages;

import com.anrisoftware.dwarfhustle.gui.javafx.messages.GuiMessage;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The set object of the objects buttons was triggered by key binding or button
 * click.
 *
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
@RequiredArgsConstructor
@ToString(callSuper = true)
public class ObjectSetTriggeredMessage extends GuiMessage {

    public final String type;

}
