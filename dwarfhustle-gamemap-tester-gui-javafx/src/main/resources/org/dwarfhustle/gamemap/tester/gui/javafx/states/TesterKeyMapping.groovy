package org.dwarfhustle.gamemap.tester.gui.javafx.states
import static com.jme3.input.KeyInput.*
import static java.util.Optional.*
import static javafx.scene.input.KeyCode.*
import static javafx.scene.input.KeyCombination.*

import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialSetTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialsButtonsCloseTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialsButtonsOpenTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectSetTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsCloseTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsOpenTriggeredMessage

import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameQuitMessage
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AboutDialogMessage.AboutDialogOpenTriggeredMessage
import com.anrisoftware.dwarfhustle.gui.javafx.messages.SettingsDialogMessage.SettingsDialogOpenTriggeredMessage
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMappingMap
import com.anrisoftware.dwarfhustle.model.api.map.BlockObject
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeGrass
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeShrub
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeTree
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

m.CLAY_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("clay", "clay"), keyCode: empty(), keyTrigger: empty()]
m.CLAY_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("clay", "clay"), keyCode: empty(), keyTrigger: empty()]
m.CLAYLOAM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("clay", "Clay-Loam"), keyCode: empty(), keyTrigger: empty()]
m.FIRECLAY_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("clay", "Fire-Clay"), keyCode: empty(), keyTrigger: empty()]
m.SANDYCLAY_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("clay", "Sandy-Clay"), keyCode: empty(), keyTrigger: empty()]
m.SILTYCLAY_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("clay", "Silty-Clay"), keyCode: empty(), keyTrigger: empty()]
m.OXYGEN_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("gas", "Oxygen"), keyCode: empty(), keyTrigger: empty()]
m.SO2_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("gas", "Sulfur-Dioxide"), keyCode: empty(), keyTrigger: empty()]
m.CO2_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("gas", "Carbon-Dioxide"), keyCode: empty(), keyTrigger: empty()]
m.POLLUTED_O2_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("gas", "Polluted-Oxygen"), keyCode: empty(), keyTrigger: empty()]
m.O2_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("gas", "Oxygen"), keyCode: empty(), keyTrigger: empty()]
m.VACUUM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("gas", "Vacuum"), keyCode: empty(), keyTrigger: empty()]
m.EXTRUSIVE_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Extrusive", "Andesite"), keyCode: empty(), keyTrigger: empty()]
m.ANDESITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Extrusive", "andesite"), keyCode: empty(), keyTrigger: empty()]
m.BASALT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Extrusive", "basalt"), keyCode: empty(), keyTrigger: empty()]
m.DACITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Extrusive", "dacite"), keyCode: empty(), keyTrigger: empty()]
m.OBSIDIAN_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Extrusive", "obsidian"), keyCode: empty(), keyTrigger: empty()]
m.RHYOLITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Extrusive", "rhyolite"), keyCode: empty(), keyTrigger: empty()]
m.INTRUSIVE_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Intrusive", "Granite"), keyCode: empty(), keyTrigger: empty()]
m.DIORITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Intrusive", "diorite"), keyCode: empty(), keyTrigger: empty()]
m.GABBRO_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Intrusive", "gabbro"), keyCode: empty(), keyTrigger: empty()]
m.GRANITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Intrusive", "granite"), keyCode: empty(), keyTrigger: empty()]
m.METAMORPHIC_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "Marble"), keyCode: empty(), keyTrigger: empty()]
m.SLATE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "slate"), keyCode: empty(), keyTrigger: empty()]
m.SCHIST_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "schist"), keyCode: empty(), keyTrigger: empty()]
m.QUARTZITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "quartzite"), keyCode: empty(), keyTrigger: empty()]
m.PHYLLITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "phyllite"), keyCode: empty(), keyTrigger: empty()]
m.MARBLE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "marble"), keyCode: empty(), keyTrigger: empty()]
m.GNEISS_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "gneiss"), keyCode: empty(), keyTrigger: empty()]
m.SAND_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("sand", "sand"), keyCode: empty(), keyTrigger: empty()]
m.YELLOWSAND_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("sand", "yellowSand"), keyCode: empty(), keyTrigger: empty()]
m.WHITESAND_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("sand", "whiteSand"), keyCode: empty(), keyTrigger: empty()]
m.REDSAND_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("sand", "redSand"), keyCode: empty(), keyTrigger: empty()]
m.BLACKSAND_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("sand", "blackSand"), keyCode: empty(), keyTrigger: empty()]
m.SAND_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("sand", "sand"), keyCode: empty(), keyTrigger: empty()]
m.SEABED_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Seabed", "Pelagic-Clay"), keyCode: empty(), keyTrigger: empty()]
m.CALCAREOUSOOZE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Seabed", "Calcareous-Ooze"), keyCode: empty(), keyTrigger: empty()]
m.SILICEOUSOOZE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Seabed", "Siliceous-Ooze"), keyCode: empty(), keyTrigger: empty()]
m.PELAGICCLAY_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Seabed", "Pelagic-Clay"), keyCode: empty(), keyTrigger: empty()]
m.SEDIMENTARY_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "Sandstone"), keyCode: empty(), keyTrigger: empty()]
m.SILTSTONE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "siltstone"), keyCode: empty(), keyTrigger: empty()]
m.SHALE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "shale"), keyCode: empty(), keyTrigger: empty()]
m.SANDSTONE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "sandstone"), keyCode: empty(), keyTrigger: empty()]
m.ROCKSALT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "rockSalt"), keyCode: empty(), keyTrigger: empty()]
m.MUDSTONE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "mudstone"), keyCode: empty(), keyTrigger: empty()]
m.LIMESTONE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "limestone"), keyCode: empty(), keyTrigger: empty()]
m.DOLOMITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "dolomite"), keyCode: empty(), keyTrigger: empty()]
m.CONGLOMERATE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "conglomerate"), keyCode: empty(), keyTrigger: empty()]
m.CLAYSTONE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "claystone"), keyCode: empty(), keyTrigger: empty()]
m.CHERT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "chert"), keyCode: empty(), keyTrigger: empty()]
m.CHALK_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "chalk"), keyCode: empty(), keyTrigger: empty()]
m.WATER_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Liquid", "Water"), keyCode: empty(), keyTrigger: empty()]
m.MAGMA_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Liquid", "magma"), keyCode: empty(), keyTrigger: empty()]
m.WATER_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Liquid", "water"), keyCode: empty(), keyTrigger: empty()]
m.TOPSOIL_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Loam"), keyCode: empty(), keyTrigger: empty()]
m.SILTLOAM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Silt-Loam"), keyCode: empty(), keyTrigger: empty()]
m.SILTYCLAYLOAM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Silty-Clay-Loam"), keyCode: empty(), keyTrigger: empty()]
m.SILT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Silt"), keyCode: empty(), keyTrigger: empty()]
m.SANDYLOAM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Sandy-Loam"), keyCode: empty(), keyTrigger: empty()]
m.SANDYCLAYLOAM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Sandy-Clay-Loam"), keyCode: empty(), keyTrigger: empty()]
m.PEAT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Peat"), keyCode: empty(), keyTrigger: empty()]
m.LOAMYSAND_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Loamy-Sand"), keyCode: empty(), keyTrigger: empty()]
m.LOAM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Loam"), keyCode: empty(), keyTrigger: empty()]

m.BLOCK_OBJECT_INSERT_MAPPING = [message: new ObjectSetTriggeredMessage(BlockObject.TYPE), keyCode: empty(), keyTrigger: empty()]
m.GRASS_OBJECT_INSERT_MAPPING = [message: new ObjectSetTriggeredMessage(KnowledgeGrass.TYPE), keyCode: empty(), keyTrigger: empty()]
m.SHRUB_OBJECT_INSERT_MAPPING = [message: new ObjectSetTriggeredMessage(KnowledgeShrub.TYPE), keyCode: empty(), keyTrigger: empty()]
m.TREE_OBJECT_INSERT_MAPPING = [message: new ObjectSetTriggeredMessage(KnowledgeTree.TYPE), keyCode: empty(), keyTrigger: empty()]

m
