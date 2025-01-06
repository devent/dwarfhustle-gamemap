package org.dwarfhustle.gamemap.tester.gui.javafx.states
import static com.jme3.input.KeyInput.*
import static java.util.Optional.*
import static javafx.scene.input.KeyCode.*
import static javafx.scene.input.KeyCombination.*

import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialsButtonsCloseTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialsButtonsOpenTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsCloseTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsOpenTriggeredMessage

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
m.OPEN_MATERIALS_BUTTONS_MAPPING = [message: new MaterialsButtonsOpenTriggeredMessage(), keyCode: empty(), keyTrigger: empty()]
m.CLOSE_MATERIALS_BUTTONS_MAPPING = [message: new MaterialsButtonsCloseTriggeredMessage(), keyCode: empty(), keyTrigger: empty()]
m.OPEN_OBJECTS_BUTTONS_MAPPING = [message: new ObjectsButtonsOpenTriggeredMessage(), keyCode: empty(), keyTrigger: empty()]
m.CLOSE_OBJECTS_BUTTONS_MAPPING = [message: new ObjectsButtonsCloseTriggeredMessage(), keyCode: empty(), keyTrigger: empty()]

m
