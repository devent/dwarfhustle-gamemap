package org.dwarfhustle.gamemap.tester.gui.javafx.messages;

import com.anrisoftware.dwarfhustle.gui.javafx.messages.GuiMessage;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The set material of the terrain buttons was triggered by key binding or
 * button click.
 *
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
@RequiredArgsConstructor
@ToString(callSuper = true)
public class MaterialSetTriggeredMessage extends GuiMessage {

    public final String material;
}
