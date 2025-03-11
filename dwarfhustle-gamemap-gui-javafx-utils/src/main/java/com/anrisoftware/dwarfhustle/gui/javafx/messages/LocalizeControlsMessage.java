/*
 * dwarfhustle-gamemap-gui-javafx-utils - Game map.
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
package com.anrisoftware.dwarfhustle.gui.javafx.messages;

import java.util.Locale;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.ObservableGameSettings;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextPosition;
import com.anrisoftware.resources.images.external.IconSize;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Message that the GUI controls should be updated with a new locale, icon size
 * and text position.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
@ToString(callSuper = true)
public class LocalizeControlsMessage extends GuiMessage {

    public final Locale locale;

    public final IconSize iconSize;

    public final TextPosition textPosition;

    public LocalizeControlsMessage(ObservableGameSettings gs) {
        this.locale = gs.locale.get();
        this.iconSize = gs.iconSize.get();
        this.textPosition = gs.textPosition.get();
    }
}
