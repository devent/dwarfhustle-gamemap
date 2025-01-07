package org.dwarfhustle.gamemap.tester.gui.javafx.states
import static com.jme3.input.KeyInput.*
import static java.util.Optional.*
import static javafx.scene.input.KeyCode.*
import static javafx.scene.input.KeyCombination.*

import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialSetTriggeredMessage
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
m.CLAY_DEFAULT_MAPPING = [message: new MaterialSetTriggeredMessage("clay"), keyCode: empty(), keyTrigger: empty()]
m.OXYGEN_DEFAULT_MAPPING = [message: new MaterialSetTriggeredMessage("oxygen"), keyCode: empty(), keyTrigger: empty()]
m.EXTRUSIVE_DEFAULT_MAPPING = [message: new MaterialSetTriggeredMessage("Andesite"), keyCode: empty(), keyTrigger: empty()]
m.INTRUSIVE_DEFAULT_MAPPING = [message: new MaterialSetTriggeredMessage("Granite"), keyCode: empty(), keyTrigger: empty()]
m.METAMORPHIC_DEFAULT_MAPPING = [message: new MaterialSetTriggeredMessage("Marble"), keyCode: empty(), keyTrigger: empty()]
m.SAND_DEFAULT_MAPPING = [message: new MaterialSetTriggeredMessage("Sand"), keyCode: empty(), keyTrigger: empty()]
m.SEABED_DEFAULT_MAPPING = [message: new MaterialSetTriggeredMessage("Pelagic-Clay"), keyCode: empty(), keyTrigger: empty()]
m.SEDIMENTARY_DEFAULT_MAPPING = [message: new MaterialSetTriggeredMessage("Sandstone"), keyCode: empty(), keyTrigger: empty()]
m.WATER_DEFAULT_MAPPING = [message: new MaterialSetTriggeredMessage("Water"), keyCode: empty(), keyTrigger: empty()]
m.TOPSOIL_DEFAULT_MAPPING = [message: new MaterialSetTriggeredMessage("Loam"), keyCode: empty(), keyTrigger: empty()]

m
