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

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMapping;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;
import com.google.inject.Injector;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code tester_materials_buttons.fxml}
 * <p>
 * {@code ^\s*<Button fx:id="(\w+)" mnemonicParsing="false" text="(.+)" \/>}
 * <p>
 * {@code @FXML public Button $1;}
 *
 * @author Erwin Müller
 */
@Slf4j
public class MaterialsButtonsController {

    @FXML
    public Pane materialsBox;
    @FXML
    public Button fireClayButton;
    @FXML
    public Button siltyClayButton;
    @FXML
    public Button sandyClayButton;
    @FXML
    public Button clayLoamButton;
    @FXML
    public Button clayButton;
    @FXML
    public Button so2Button;
    @FXML
    public Button co2Button;
    @FXML
    public Button pollutedO2Button;
    @FXML
    public Button o2Button;
    @FXML
    public Button vacuumButton;
    @FXML
    public Button rhyoliteButton;
    @FXML
    public Button obsidianButton;
    @FXML
    public Button daciteButton;
    @FXML
    public Button basaltButton;
    @FXML
    public Button andesiteButton;
    @FXML
    public Button graniteButton;
    @FXML
    public Button gabbroButton;
    @FXML
    public Button dioriteButton;
    @FXML
    public Button slateButton;
    @FXML
    public Button schistButton;
    @FXML
    public Button quartziteButton;
    @FXML
    public Button phylliteButton;
    @FXML
    public Button marbleButton;
    @FXML
    public Button gneissButton;
    @FXML
    public Button yellowSandButton;
    @FXML
    public Button whiteSandButton;
    @FXML
    public Button redSandButton;
    @FXML
    public Button blackSandButton;
    @FXML
    public Button sandButton;
    @FXML
    public Button calcareousOozeButton;
    @FXML
    public Button siliceousOozeButton;
    @FXML
    public Button pelagicClayButton;
    @FXML
    public Button siltstoneButton;
    @FXML
    public Button shaleButton;
    @FXML
    public Button sandstoneButton;
    @FXML
    public Button rockSaltButton;
    @FXML
    public Button mudstoneButton;
    @FXML
    public Button limestoneButton;
    @FXML
    public Button dolomiteButton;
    @FXML
    public Button conglomerateButton;
    @FXML
    public Button claystoneButton;
    @FXML
    public Button chertButton;
    @FXML
    public Button chalkButton;
    @FXML
    public Button magmaButton;
    @FXML
    public Button waterButton;
    @FXML
    public Button siltLoamButton;
    @FXML
    public Button siltyClayLoamButton;
    @FXML
    public Button siltButton;
    @FXML
    public Button sandyLoamButton;
    @FXML
    public Button sandyClayLoamButton;
    @FXML
    public Button peatButton;
    @FXML
    public Button loamySandButton;
    @FXML
    public Button loamButton;
    @FXML
    public Button clayDefaultButton;
    @FXML
    public Button oxygenDefaultButton;
    @FXML
    public Button extrusiveDefaultButton;
    @FXML
    public Button intrusiveDefaultButton;
    @FXML
    public Button metamorphicDefaultButton;
    @FXML
    public Button sandDefaultButton;
    @FXML
    public Button seabedDefaultButton;
    @FXML
    public Button sedimentaryDefaultButton;
    @FXML
    public Button waterDefaultButton;
    @FXML
    public Button topsoilDefaultButton;

    @Inject
    private GameSettingsProvider gs;

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
    }

    public void initListeners(GlobalKeys globalKeys, Map<String, KeyMapping> keyMappings) {
        clayDefaultButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get("CLAY_DEFAULT_MAPPING"));
        });
        oxygenDefaultButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get("OXYGEN_DEFAULT_MAPPING"));
        });
        extrusiveDefaultButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get("EXTRUSIVE_DEFAULT_MAPPING"));
        });
        intrusiveDefaultButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get("INTRUSIVE_DEFAULT_MAPPING"));
        });
        metamorphicDefaultButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get("METAMORPHIC_DEFAULT_MAPPING"));
        });
        sandDefaultButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get("SAND_DEFAULT_MAPPING"));
        });
        seabedDefaultButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get("SEABED_DEFAULT_MAPPING"));
        });
        sedimentaryDefaultButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get("SEDIMENTARY_DEFAULT_MAPPING"));
        });
        waterDefaultButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get("WATER_DEFAULT_MAPPING"));
        });
        topsoilDefaultButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get("TOPSOIL_DEFAULT_MAPPING"));
        });
    }
}
