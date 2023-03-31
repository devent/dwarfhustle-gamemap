/*
 * Dwarf Hustle Game Map - Game map.
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
 * Settings dialog message.
 *
 * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
 */
@ToString(callSuper = true)
public class SettingsDialogMessage extends GuiMessage {

    /**
     * Message that the settings dialog was closed by canceling it.
     *
     * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
     */
    @ToString(callSuper = true)
    public static class SettingsDialogCancelTriggeredMessage extends SettingsDialogMessage {

    }

    /**
     * Message that the settings dialog was closed by Ok it.
     *
     * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
     */
    @ToString(callSuper = true)
    public static class SettingsDialogOkTriggeredMessage extends SettingsDialogMessage {

    }

    /**
     * Message that the settings dialog should be applied.
     *
     * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
     */
    @ToString(callSuper = true)
    public static class SettingsDialogApplyMessage extends SettingsDialogMessage {

    }

    /**
     * Message to open the settings dialog.
     *
     * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
     */
    @ToString(callSuper = true)
    public static class SettingsDialogOpenMessage extends GuiMessage {

    }

    /**
     * Message that the user clicked the Settings Dialog button or pressed a key
     * binding to open the Settings dialog.
     *
     * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
     */
    @ToString(callSuper = true)
    public static class SettingsDialogOpenTriggeredMessage extends GuiMessage {

    }
}
