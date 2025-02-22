package org.dwarfhustle.gamemap.tester.gui.javafx.messages;

import java.util.function.Consumer;

import com.anrisoftware.dwarfhustle.gui.javafx.messages.GuiMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;

import lombok.ToString;

/**
 * The set object of the objects buttons was triggered by key binding or button
 * click.
 *
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
@ToString(callSuper = true)
public class ObjectSetTriggeredMessage extends GuiMessage {

    private final static Consumer<GameMapObject> NOP = go -> {
    };

    public final String type;

    private final Consumer<GameMapObject> setup;

    public ObjectSetTriggeredMessage(String type) {
        this(type, NOP);
    }

    public ObjectSetTriggeredMessage(String type, Consumer<GameMapObject> setup) {
        this.type = type;
        this.setup = setup;
    }

}
