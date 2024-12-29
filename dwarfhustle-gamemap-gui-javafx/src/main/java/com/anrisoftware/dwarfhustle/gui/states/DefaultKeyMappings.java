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
package com.anrisoftware.dwarfhustle.gui.states;

import static com.jme3.input.KeyInput.KEY_F10;
import static com.jme3.input.KeyInput.KEY_Q;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static javafx.scene.input.KeyCode.F10;
import static javafx.scene.input.KeyCode.Q;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

import java.util.Optional;

import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameQuitMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GuiMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.ResetCameraMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AboutDialogMessage.AboutDialogOpenTriggeredMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.SettingsDialogMessage.SettingsDialogOpenTriggeredMessage;
import com.jme3.input.controls.KeyTrigger;

import javafx.scene.input.KeyCodeCombination;
import lombok.RequiredArgsConstructor;

/**
 * Default key mappings.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
public enum DefaultKeyMappings {

    // general
    QUIT_MAPPING(new GameQuitMessage(), of(new KeyCodeCombination(Q, CONTROL_DOWN)), of(new KeyTrigger(KEY_Q))),

    SETTINGS_MAPPING(new SettingsDialogOpenTriggeredMessage(), empty(), empty()),

    ABOUT_DIALOG_MAPPING(new AboutDialogOpenTriggeredMessage(), empty(), empty()),

    // camera
    RESET_CAMERA_MAPPING(new ResetCameraMessage(), of(new KeyCodeCombination(F10)), of(new KeyTrigger(KEY_F10)));

    public final GuiMessage message;

    public final Optional<KeyCodeCombination> keyCode;

    public final Optional<KeyTrigger> keyTrigger;

}
