/*
 * dwarfhustle-gamemap-tester-gui-javafx - Game map.
 * Copyright © 2023 Erwin Müller (erwin.mueller@anrisoftware.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
