package org.dwarfhustle.gamemap.tester.gui.javafx.states
import static com.jme3.input.KeyInput.*
import static java.util.Optional.*
import static javafx.scene.input.KeyCode.*
import static javafx.scene.input.KeyCombination.*

import org.dwarfhustle.gamemap.tester.gui.javafx.messages.TerrainButtonsOpenTriggeredMessage

import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameQuitMessage
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AboutDialogMessage.AboutDialogOpenTriggeredMessage
import com.anrisoftware.dwarfhustle.gui.javafx.messages.SettingsDialogMessage.SettingsDialogOpenTriggeredMessage
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMappingMap
import com.jme3.input.controls.KeyTrigger

import javafx.scene.input.KeyCodeCombination

m = new KeyMappingMap()

m.QUIT_MAPPING = [message: new GameQuitMessage(), keyCode: of(new KeyCodeCombination(Q, CONTROL_DOWN)), keyTrigger: of(new KeyTrigger(KEY_Q))]
m.SETTINGS_MAPPING = [message: new SettingsDialogOpenTriggeredMessage(), keyCode: empty(), keyTrigger: empty()]
m.ABOUT_DIALOG_MAPPING = [message: new AboutDialogOpenTriggeredMessage(), keyCode: empty(), keyTrigger: empty()]
m.OPEN_TERRAIN_BUTTONS_MAPPING = [message: new TerrainButtonsOpenTriggeredMessage(), keyCode: empty(), keyTrigger: empty()]

m
