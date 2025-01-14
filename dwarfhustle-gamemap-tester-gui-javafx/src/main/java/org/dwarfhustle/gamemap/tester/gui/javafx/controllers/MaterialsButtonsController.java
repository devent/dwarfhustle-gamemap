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
package org.dwarfhustle.gamemap.tester.gui.javafx.controllers;

import static com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil.getImageView;

import java.util.Locale;
import java.util.Map;

import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMapping;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;
import com.google.inject.Injector;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code tester_materials_buttons.fxml}
 * <p>
 * {@code ^\s*<(\w+) fx:id="(\w+)".*$}
 * <p>
 * {@code @FXML public $1 $2;}
 *
 * @author Erwin Müller
 */
@Slf4j
public class MaterialsButtonsController {

    @FXML
    public Pane materialsBox;
    @FXML
    public ToggleButton fireClayButton;
    @FXML
    public ToggleButton siltyClayButton;
    @FXML
    public ToggleButton sandyClayButton;
    @FXML
    public ToggleButton clayLoamButton;
    @FXML
    public ToggleButton clayButton;
    @FXML
    public ToggleButton so2Button;
    @FXML
    public ToggleButton co2Button;
    @FXML
    public ToggleButton pollutedO2Button;
    @FXML
    public ToggleButton o2Button;
    @FXML
    public ToggleButton vacuumButton;
    @FXML
    public ToggleButton rhyoliteButton;
    @FXML
    public ToggleButton obsidianButton;
    @FXML
    public ToggleButton daciteButton;
    @FXML
    public ToggleButton basaltButton;
    @FXML
    public ToggleButton andesiteButton;
    @FXML
    public ToggleButton graniteButton;
    @FXML
    public ToggleButton gabbroButton;
    @FXML
    public ToggleButton dioriteButton;
    @FXML
    public ToggleButton slateButton;
    @FXML
    public ToggleButton schistButton;
    @FXML
    public ToggleButton quartziteButton;
    @FXML
    public ToggleButton phylliteButton;
    @FXML
    public ToggleButton marbleButton;
    @FXML
    public ToggleButton gneissButton;
    @FXML
    public ToggleButton yellowSandButton;
    @FXML
    public ToggleButton whiteSandButton;
    @FXML
    public ToggleButton redSandButton;
    @FXML
    public ToggleButton blackSandButton;
    @FXML
    public ToggleButton sandButton;
    @FXML
    public ToggleButton calcareousOozeButton;
    @FXML
    public ToggleButton siliceousOozeButton;
    @FXML
    public ToggleButton pelagicClayButton;
    @FXML
    public ToggleButton siltstoneButton;
    @FXML
    public ToggleButton shaleButton;
    @FXML
    public ToggleButton sandstoneButton;
    @FXML
    public ToggleButton rockSaltButton;
    @FXML
    public ToggleButton mudstoneButton;
    @FXML
    public ToggleButton limestoneButton;
    @FXML
    public ToggleButton dolomiteButton;
    @FXML
    public ToggleButton conglomerateButton;
    @FXML
    public ToggleButton claystoneButton;
    @FXML
    public ToggleButton chertButton;
    @FXML
    public ToggleButton chalkButton;
    @FXML
    public ToggleButton magmaButton;
    @FXML
    public ToggleButton waterButton;
    @FXML
    public ToggleButton siltLoamButton;
    @FXML
    public ToggleButton siltyClayLoamButton;
    @FXML
    public ToggleButton siltButton;
    @FXML
    public ToggleButton sandyLoamButton;
    @FXML
    public ToggleButton sandyClayLoamButton;
    @FXML
    public ToggleButton peatButton;
    @FXML
    public ToggleButton loamySandButton;
    @FXML
    public ToggleButton loamButton;
    @FXML
    public ToggleButton clayDefaultButton;
    @FXML
    public ToggleButton oxygenDefaultButton;
    @FXML
    public ToggleButton extrusiveDefaultButton;
    @FXML
    public ToggleButton intrusiveDefaultButton;
    @FXML
    public ToggleButton metamorphicDefaultButton;
    @FXML
    public ToggleButton sandDefaultButton;
    @FXML
    public ToggleButton seabedDefaultButton;
    @FXML
    public ToggleButton sedimentaryDefaultButton;
    @FXML
    public ToggleButton waterDefaultButton;
    @FXML
    public ToggleButton topsoilDefaultButton;
    @FXML
    public TabPane materialsTabPane;
    @FXML
    public Tab claysTab;
    @FXML
    public Tab gasesTab;
    @FXML
    public Tab extrusiveTab;
    @FXML
    public Tab intrusiveTab;
    @FXML
    public Tab metamorphicTab;
    @FXML
    public Tab sandTab;
    @FXML
    public Tab seabedTab;
    @FXML
    public Tab sedimentaryTab;
    @FXML
    public Tab liquidTab;
    @FXML
    public Tab topsoilTab;
    @FXML
    public ToggleGroup materialGroup;

    public void setup(Injector injector) {
        log.debug("setup()");
    }

    public void updateLocale(Locale locale, Texts texts, Images images, IconSize iconSize) {
        clayDefaultButton.setGraphic(getImageView(images, "buttons_clay", locale, iconSize));
        clayDefaultButton.setText(null);
        clayButton.setGraphic(getImageView(images, "buttons_clay", locale, iconSize));
        clayButton.setText(null);
        clayLoamButton.setGraphic(getImageView(images, "buttons_clay_loam", locale, iconSize));
        clayLoamButton.setText(null);
        fireClayButton.setGraphic(getImageView(images, "buttons_fire_clay", locale, iconSize));
        fireClayButton.setText(null);
        sandyClayButton.setGraphic(getImageView(images, "buttons_sandy_clay", locale, iconSize));
        sandyClayButton.setText(null);
        siltyClayButton.setGraphic(getImageView(images, "buttons_silty_clay", locale, iconSize));
        siltyClayButton.setText(null);
        oxygenDefaultButton.setGraphic(getImageView(images, "buttons_o2", locale, iconSize));
        oxygenDefaultButton.setText(null);
        so2Button.setGraphic(getImageView(images, "buttons_o2", locale, iconSize));
        so2Button.setText(null);
        co2Button.setGraphic(getImageView(images, "buttons_co2", locale, iconSize));
        co2Button.setText(null);
        pollutedO2Button.setGraphic(getImageView(images, "buttons_polluted_o2", locale, iconSize));
        pollutedO2Button.setText(null);
        o2Button.setGraphic(getImageView(images, "buttons_o2", locale, iconSize));
        o2Button.setText(null);
        vacuumButton.setGraphic(getImageView(images, "buttons_vacuum", locale, iconSize));
        vacuumButton.setText(null);
        claysTab.setGraphic(getImageView(images, "buttons_clay", locale, iconSize));
        claysTab.setText(null);
        sandTab.setGraphic(getImageView(images, "buttons_sand", locale, iconSize));
        sandTab.setText(null);
        extrusiveTab.setGraphic(getImageView(images, "buttons_andesite", locale, iconSize));
        extrusiveTab.setText(null);
        setButtonImage(extrusiveDefaultButton, "buttons_andesite", locale, images, iconSize);
        setButtonImage(andesiteButton, "buttons_andesite", locale, images, iconSize);
        setButtonImage(basaltButton, "buttons_basalt", locale, images, iconSize);
        setButtonImage(daciteButton, "buttons_dacite", locale, images, iconSize);
        setButtonImage(obsidianButton, "buttons_obsidian", locale, images, iconSize);
        setButtonImage(rhyoliteButton, "buttons_rhyolite", locale, images, iconSize);
        intrusiveTab.setGraphic(getImageView(images, "buttons_granite", locale, iconSize));
        intrusiveTab.setText(null);
        setButtonImage(intrusiveDefaultButton, "buttons_granite", locale, images, iconSize);
        setButtonImage(dioriteButton, "buttons_diorite", locale, images, iconSize);
        setButtonImage(gabbroButton, "buttons_gabbro", locale, images, iconSize);
        setButtonImage(graniteButton, "buttons_granite", locale, images, iconSize);
        metamorphicTab.setGraphic(getImageView(images, "buttons_marble", locale, iconSize));
        metamorphicTab.setText(null);
        setButtonImage(metamorphicDefaultButton, "buttons_marble", locale, images, iconSize);
        setButtonImage(slateButton, "buttons_slate", locale, images, iconSize);
        setButtonImage(schistButton, "buttons_schist", locale, images, iconSize);
        setButtonImage(quartziteButton, "buttons_quartzite", locale, images, iconSize);
        setButtonImage(phylliteButton, "buttons_phyllite", locale, images, iconSize);
        setButtonImage(marbleButton, "buttons_marble", locale, images, iconSize);
        setButtonImage(gneissButton, "buttons_gneiss", locale, images, iconSize);
        sedimentaryTab.setGraphic(getImageView(images, "buttons_sandstone", locale, iconSize));
        sedimentaryTab.setText(null);
        setButtonImage(sedimentaryDefaultButton, "buttons_sandstone", locale, images, iconSize);
        setButtonImage(siltstoneButton, "buttons_siltstone", locale, images, iconSize);
        setButtonImage(shaleButton, "buttons_shale", locale, images, iconSize);
        setButtonImage(sandstoneButton, "buttons_sandstone", locale, images, iconSize);
        setButtonImage(rockSaltButton, "buttons_rock_salt", locale, images, iconSize);
        setButtonImage(mudstoneButton, "buttons_mudstone", locale, images, iconSize);
        setButtonImage(limestoneButton, "buttons_limestone", locale, images, iconSize);
        setButtonImage(dolomiteButton, "buttons_dolomite", locale, images, iconSize);
        setButtonImage(conglomerateButton, "buttons_conglomerate", locale, images, iconSize);
        setButtonImage(claystoneButton, "buttons_claystone", locale, images, iconSize);
        setButtonImage(chertButton, "buttons_chert", locale, images, iconSize);
        setButtonImage(chalkButton, "buttons_chalk", locale, images, iconSize);
    }

    private void setButtonImage(ButtonBase b, String name, Locale locale, Images images, IconSize iconSize) {
        b.setGraphic(getImageView(images, name, locale, iconSize));
        b.setText(null);
    }

    public void initListeners(GlobalKeys globalKeys, Map<String, KeyMapping> keyMappings) {
        setButtonAction(clayDefaultButton, "CLAY_DEFAULT_MAPPING", globalKeys, keyMappings);
        setButtonAction(oxygenDefaultButton, "OXYGEN_DEFAULT_MAPPING", globalKeys, keyMappings);
        setButtonAction(extrusiveDefaultButton, "EXTRUSIVE_DEFAULT_MAPPING", globalKeys, keyMappings);
        setButtonAction(intrusiveDefaultButton, "INTRUSIVE_DEFAULT_MAPPING", globalKeys, keyMappings);
        setButtonAction(metamorphicDefaultButton, "METAMORPHIC_DEFAULT_MAPPING", globalKeys, keyMappings);
        setButtonAction(sandDefaultButton, "SAND_DEFAULT_MAPPING", globalKeys, keyMappings);
        setButtonAction(seabedDefaultButton, "SEABED_DEFAULT_MAPPING", globalKeys, keyMappings);
        setButtonAction(sedimentaryDefaultButton, "SEDIMENTARY_DEFAULT_MAPPING", globalKeys, keyMappings);
        setButtonAction(waterDefaultButton, "WATER_DEFAULT_MAPPING", globalKeys, keyMappings);
        setButtonAction(topsoilDefaultButton, "TOPSOIL_DEFAULT_MAPPING", globalKeys, keyMappings);
    }

    private void setButtonAction(ButtonBase button, String name, GlobalKeys globalKeys,
            Map<String, KeyMapping> keyMappings) {
        button.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get(name));
        });
    }
}
