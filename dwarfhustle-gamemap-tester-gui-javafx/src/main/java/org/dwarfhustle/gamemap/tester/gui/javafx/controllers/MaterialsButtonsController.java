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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.DescribedAs.describedAs;

import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMapping;
import com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;
import com.google.inject.Injector;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
        setButtonAction(clayDefaultButton, "CLAY_DEFAULT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(clayButton, "CLAY_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(clayLoamButton, "CLAYLOAM_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(fireClayButton, "FIRECLAY_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(sandyClayButton, "SANDYCLAY_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(siltyClayButton, "SILTYCLAY_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(oxygenDefaultButton, "OXYGEN_DEFAULT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(so2Button, "SO2_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(co2Button, "CO2_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(pollutedO2Button, "POLLUTED_O2_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(o2Button, "O2_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(vacuumButton, "VACUUM_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(extrusiveDefaultButton, "EXTRUSIVE_DEFAULT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(andesiteButton, "ANDESITE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(basaltButton, "BASALT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(daciteButton, "DACITE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(obsidianButton, "OBSIDIAN_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(rhyoliteButton, "RHYOLITE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(intrusiveDefaultButton, "INTRUSIVE_DEFAULT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(dioriteButton, "DIORITE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(gabbroButton, "GABBRO_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(graniteButton, "GRANITE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(metamorphicDefaultButton, "METAMORPHIC_DEFAULT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(slateButton, "SLATE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(schistButton, "SCHIST_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(quartziteButton, "QUARTZITE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(phylliteButton, "PHYLLITE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(marbleButton, "MARBLE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(gneissButton, "GNEISS_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(sandDefaultButton, "SAND_DEFAULT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(yellowSandButton, "YELLOWSAND_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(whiteSandButton, "WHITESAND_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(redSandButton, "REDSAND_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(blackSandButton, "BLACKSAND_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(sandButton, "SAND_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(seabedDefaultButton, "SEABED_DEFAULT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(calcareousOozeButton, "CALCAREOUSOOZE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(siliceousOozeButton, "SILICEOUSOOZE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(pelagicClayButton, "PELAGICCLAY_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(sedimentaryDefaultButton, "SEDIMENTARY_DEFAULT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(siltstoneButton, "SILTSTONE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(shaleButton, "SHALE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(sandstoneButton, "SANDSTONE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(rockSaltButton, "ROCKSALT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(mudstoneButton, "MUDSTONE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(limestoneButton, "LIMESTONE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(dolomiteButton, "DOLOMITE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(conglomerateButton, "CONGLOMERATE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(claystoneButton, "CLAYSTONE_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(chertButton, "CHERT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(chalkButton, "CHALK_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(waterDefaultButton, "WATER_DEFAULT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(magmaButton, "MAGMA_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(waterButton, "WATER_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(topsoilDefaultButton, "TOPSOIL_DEFAULT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(siltLoamButton, "SILTLOAM_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(siltyClayLoamButton, "SILTYCLAYLOAM_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(siltButton, "SILT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(sandyLoamButton, "SANDYLOAM_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(sandyClayLoamButton, "SANDYCLAYLOAM_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(peatButton, "PEAT_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(loamySandButton, "LOAMYSAND_MATERIAL_MAPPING", globalKeys, keyMappings);
        setButtonAction(loamButton, "LOAM_MATERIAL_MAPPING", globalKeys, keyMappings);
    }

    private void setButtonAction(ButtonBase button, String name, GlobalKeys globalKeys,
            Map<String, KeyMapping> keyMappings) {
        assertThat(keyMappings.get(name), is(describedAs("keyMappings %0", notNullValue(), name)));
        button.setOnAction(e -> {
            globalKeys.runAction(keyMappings.get(name));
        });
    }

    public void setOnMouseEnteredGui(Consumer<Boolean> consumer) {
        JavaFxUtil.forEachController(this, ButtonBase.class, c -> {
            c.setOnMouseEntered(e -> consumer.accept(true));
            c.setOnMouseExited(e -> consumer.accept(false));
        });
        JavaFxUtil.forEachController(this, TabPane.class, c -> {
            c.setOnMouseEntered(e -> consumer.accept(true));
            c.setOnMouseExited(e -> consumer.accept(false));
        });
    }

}
