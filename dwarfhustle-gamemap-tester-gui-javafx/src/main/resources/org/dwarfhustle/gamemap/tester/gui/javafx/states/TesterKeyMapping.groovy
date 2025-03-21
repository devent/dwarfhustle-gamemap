package org.dwarfhustle.gamemap.tester.gui.javafx.states
import static com.jme3.input.KeyInput.*
import static java.util.Optional.*
import static javafx.scene.input.KeyCode.*
import static javafx.scene.input.KeyCombination.*

import org.dwarfhustle.gamemap.tester.gui.javafx.messages.DeleteButtonsCloseTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.DeleteButtonsOpenTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialSetTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialsButtonsCloseTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialsButtonsOpenTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectSetTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsCloseTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsOpenTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.TimeButtonsCloseTriggeredMessage
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.TimeButtonsOpenTriggeredMessage

import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameQuitMessage
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameSpeedFastTriggeredMessage
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameSpeedNormalTriggeredMessage
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameSpeedPauseTriggeredMessage
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameSpeedTogglePauseTriggeredMessage
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AboutDialogMessage.AboutDialogOpenTriggeredMessage
import com.anrisoftware.dwarfhustle.gui.javafx.messages.SettingsDialogMessage.SettingsDialogOpenTriggeredMessage
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMappingMap
import com.anrisoftware.dwarfhustle.model.api.buildings.KnowledgeBuilding
import com.anrisoftware.dwarfhustle.model.api.map.BlockObject
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeGrass
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeShrub
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeTreeSapling
import com.jme3.input.controls.KeyTrigger

import javafx.scene.input.KeyCodeCombination

m = new KeyMappingMap()

m.QUIT_MAPPING = [message: new GameQuitMessage(), code: of(new KeyCodeCombination(Q, CONTROL_DOWN)), trigger: of(new KeyTrigger(KEY_Q))]
m.SETTINGS_MAPPING = [message: new SettingsDialogOpenTriggeredMessage(), code: empty(), trigger: empty()]
m.ABOUT_DIALOG_MAPPING = [message: new AboutDialogOpenTriggeredMessage(), code: empty(), trigger: empty()]

m.GAME_SPEED_PAUSE_MAPPING = [message: new GameSpeedPauseTriggeredMessage(), code: empty(), trigger: empty()]
m.GAME_SPEED_NORMAL_MAPPING = [message: new GameSpeedNormalTriggeredMessage(), code: empty(), trigger: empty()]
m.GAME_SPEED_FAST_MAPPING = [message: new GameSpeedFastTriggeredMessage(), code: empty(), trigger: empty()]
m.GAME_SPEED_PAUSE_TOGGLE_MAPPING = [message: new GameSpeedTogglePauseTriggeredMessage(), code: of(new KeyCodeCombination(SPACE)), trigger: of(new KeyTrigger(KEY_SPACE))]

m.OPEN_MATERIALS_BUTTONS_MAPPING = [message: new MaterialsButtonsOpenTriggeredMessage(), code: empty(), trigger: empty()]
m.CLOSE_MATERIALS_BUTTONS_MAPPING = [message: new MaterialsButtonsCloseTriggeredMessage(), code: empty(), trigger: empty()]

m.OPEN_OBJECTS_BUTTONS_MAPPING = [message: new ObjectsButtonsOpenTriggeredMessage(), code: empty(), trigger: empty()]
m.CLOSE_OBJECTS_BUTTONS_MAPPING = [message: new ObjectsButtonsCloseTriggeredMessage(), code: empty(), trigger: empty()]

m.OPEN_DELETE_BUTTONS_MAPPING = [message: new DeleteButtonsOpenTriggeredMessage(), code: empty(), trigger: empty()]
m.CLOSE_DELETE_BUTTONS_MAPPING = [message: new DeleteButtonsCloseTriggeredMessage(), code: empty(), trigger: empty()]

m.OPEN_TIME_BUTTONS_MAPPING = [message: new TimeButtonsOpenTriggeredMessage(), code: empty(), trigger: empty()]
m.CLOSE_TIME_BUTTONS_MAPPING = [message: new TimeButtonsCloseTriggeredMessage(), code: empty(), trigger: empty()]

m.CLAY_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Clay", "Clay"), code: empty(), trigger: empty()]
m.CLAY_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Clay", "Clay"), code: empty(), trigger: empty()]
m.CLAYLOAM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Clay", "Clay-Loam"), code: empty(), trigger: empty()]
m.FIRECLAY_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Clay", "Fire-Clay"), code: empty(), trigger: empty()]
m.SANDYCLAY_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Clay", "Sandy-Clay"), code: empty(), trigger: empty()]
m.SILTYCLAY_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Clay", "Silty-Clay"), code: empty(), trigger: empty()]
m.OXYGEN_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Gas", "Oxygen"), code: empty(), trigger: empty()]
m.SO2_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Gas", "Sulfur-Dioxide"), code: empty(), trigger: empty()]
m.CO2_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Gas", "Carbon-Dioxide"), code: empty(), trigger: empty()]
m.POLLUTED_O2_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Gas", "Polluted-Oxygen"), code: empty(), trigger: empty()]
m.O2_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Gas", "Oxygen"), code: empty(), trigger: empty()]
m.VACUUM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Gas", "Vacuum"), code: empty(), trigger: empty()]
m.EXTRUSIVE_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Extrusive", "Andesite"), code: empty(), trigger: empty()]
m.ANDESITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Extrusive", "andesite"), code: empty(), trigger: empty()]
m.BASALT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Extrusive", "basalt"), code: empty(), trigger: empty()]
m.DACITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Extrusive", "dacite"), code: empty(), trigger: empty()]
m.OBSIDIAN_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Extrusive", "obsidian"), code: empty(), trigger: empty()]
m.RHYOLITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Extrusive", "rhyolite"), code: empty(), trigger: empty()]
m.INTRUSIVE_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Intrusive", "Granite"), code: empty(), trigger: empty()]
m.DIORITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Intrusive", "diorite"), code: empty(), trigger: empty()]
m.GABBRO_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Intrusive", "gabbro"), code: empty(), trigger: empty()]
m.GRANITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Igneous-Intrusive", "granite"), code: empty(), trigger: empty()]
m.METAMORPHIC_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "Marble"), code: empty(), trigger: empty()]
m.SLATE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "slate"), code: empty(), trigger: empty()]
m.SCHIST_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "schist"), code: empty(), trigger: empty()]
m.QUARTZITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "quartzite"), code: empty(), trigger: empty()]
m.PHYLLITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "phyllite"), code: empty(), trigger: empty()]
m.MARBLE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "marble"), code: empty(), trigger: empty()]
m.GNEISS_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("metamorphic", "gneiss"), code: empty(), trigger: empty()]
m.SAND_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sand", "Sand"), code: empty(), trigger: empty()]
m.YELLOWSAND_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sand", "Yellow-Sand"), code: empty(), trigger: empty()]
m.WHITESAND_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sand", "White-Sand"), code: empty(), trigger: empty()]
m.REDSAND_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sand", "Red-Sand"), code: empty(), trigger: empty()]
m.BLACKSAND_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sand", "Black-Sand"), code: empty(), trigger: empty()]
m.SAND_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sand", "Sand"), code: empty(), trigger: empty()]
m.SEABED_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Seabed", "Pelagic-Clay"), code: empty(), trigger: empty()]
m.CALCAREOUSOOZE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Seabed", "Calcareous-Ooze"), code: empty(), trigger: empty()]
m.SILICEOUSOOZE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Seabed", "Siliceous-Ooze"), code: empty(), trigger: empty()]
m.PELAGICCLAY_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Seabed", "Pelagic-Clay"), code: empty(), trigger: empty()]
m.SEDIMENTARY_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "Sandstone"), code: empty(), trigger: empty()]
m.SILTSTONE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "siltstone"), code: empty(), trigger: empty()]
m.SHALE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "shale"), code: empty(), trigger: empty()]
m.SANDSTONE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "sandstone"), code: empty(), trigger: empty()]
m.ROCKSALT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "rockSalt"), code: empty(), trigger: empty()]
m.MUDSTONE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "mudstone"), code: empty(), trigger: empty()]
m.LIMESTONE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "limestone"), code: empty(), trigger: empty()]
m.DOLOMITE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "dolomite"), code: empty(), trigger: empty()]
m.CONGLOMERATE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "conglomerate"), code: empty(), trigger: empty()]
m.CLAYSTONE_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "claystone"), code: empty(), trigger: empty()]
m.CHERT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "chert"), code: empty(), trigger: empty()]
m.CHALK_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Sedimentary", "chalk"), code: empty(), trigger: empty()]
m.WATER_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Liquid", "Water"), code: empty(), trigger: empty()]
m.MAGMA_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Liquid", "magma"), code: empty(), trigger: empty()]
m.WATER_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Liquid", "water"), code: empty(), trigger: empty()]
m.TOPSOIL_DEFAULT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Loam"), code: empty(), trigger: empty()]
m.SILTLOAM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Silt-Loam"), code: empty(), trigger: empty()]
m.SILTYCLAYLOAM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Silty-Clay-Loam"), code: empty(), trigger: empty()]
m.SILT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Silt"), code: empty(), trigger: empty()]
m.SANDYLOAM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Sandy-Loam"), code: empty(), trigger: empty()]
m.SANDYCLAYLOAM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Sandy-Clay-Loam"), code: empty(), trigger: empty()]
m.PEAT_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Peat"), code: empty(), trigger: empty()]
m.LOAMYSAND_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Loamy-Sand"), code: empty(), trigger: empty()]
m.LOAM_MATERIAL_MAPPING = [message: new MaterialSetTriggeredMessage("Topsoil", "Loam"), code: empty(), trigger: empty()]

m.BLOCK_OBJECT_INSERT_MAPPING = [message: new ObjectSetTriggeredMessage(BlockObject.TYPE), code: empty(), trigger: empty()]
m.GRASS_OBJECT_INSERT_MAPPING = [message: new ObjectSetTriggeredMessage(KnowledgeGrass.TYPE), code: empty(), trigger: empty()]
m.SHRUB_OBJECT_INSERT_MAPPING = [message: new ObjectSetTriggeredMessage(KnowledgeShrub.TYPE), code: empty(), trigger: empty()]
m.TREE_OBJECT_INSERT_MAPPING = [message: new ObjectSetTriggeredMessage(KnowledgeTreeSapling.TYPE), code: empty(), trigger: empty()]
m.BUILDING_OBJECT_INSERT_MAPPING = [message: new ObjectSetTriggeredMessage(KnowledgeBuilding.TYPE), code: empty(), trigger: empty()]

m
