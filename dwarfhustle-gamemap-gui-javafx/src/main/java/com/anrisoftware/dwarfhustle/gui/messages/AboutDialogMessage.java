/*
 * dwarfhustle-gamemap-gui-javafx - GUI in Javafx.
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
package com.anrisoftware.dwarfhustle.gui.messages;

import lombok.ToString;

/**
 * About dialog message.
 *
 * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
 */
@ToString(callSuper = true)
public class AboutDialogMessage extends GuiMessage {

    /**
     * Message that the settings dialog was closed by closing it by either a)
     * trigger the close button, b) press Esc.
     *
     * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
     */
    @ToString(callSuper = true)
    public static class AboutDialogCloseTriggeredMessage extends AboutDialogMessage {

    }

    /**
     * Message to open the About dialog.
     *
     * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
     */
    @ToString(callSuper = true)
    public static class AboutDialogOpenMessage extends GuiMessage {

    }

    /**
     * Message that the user clicked the About Dialog button or pressed a key
     * binding to open the about dialog.
     *
     * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
     */
    @ToString(callSuper = true)
    public static class AboutDialogOpenTriggeredMessage extends GuiMessage {

    }
}
