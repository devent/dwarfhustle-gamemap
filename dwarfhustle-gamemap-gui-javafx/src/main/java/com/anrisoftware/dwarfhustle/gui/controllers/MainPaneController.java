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
package com.anrisoftware.dwarfhustle.gui.controllers;

import static com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil.runFxThread;
import static com.anrisoftware.dwarfhustle.gui.states.DefaultKeyMappings.ABOUT_DIALOG_MAPPING;
import static com.anrisoftware.dwarfhustle.gui.states.DefaultKeyMappings.QUIT_MAPPING;
import static com.anrisoftware.dwarfhustle.gui.states.DefaultKeyMappings.SETTINGS_MAPPING;

import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.anrisoftware.dwarfhustle.gamemap.console.actor.LineMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMapping;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code main_ui.fxml}
 *
 * @author Erwin Müller
 */
@Slf4j
public class MainPaneController {

    @FXML
    public BorderPane mainPanel;

    @FXML
    public Label fortressNameLabel;

    @FXML
    public Label gameTimeLabel;

    @FXML
    public Button settingsButton;

    @FXML
    public Button aboutButton;

    @FXML
    public Button quitButton;

    @FXML
    public TextField commandLineText;

    @FXML
    public Label statusLabel;

    @FXML
    public ScrollBar levelBar;

    @FXML
    public Label levelLabel;

    private Locale locale;

    private Texts texts;

    private Images images;

    private IconSize iconSize;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameSettingsProvider gs;

    public void updateLocale(Locale locale, Texts texts, Images images, IconSize iconSize) {
        this.locale = locale;
        this.texts = texts;
        this.images = images;
        this.iconSize = iconSize;
        commandLineText.setText(gs.get().lastCommand.get());
    }

    public void initListeners(Consumer<GameMap> saveZ) {
        log.debug("initListeners");
        setupImagePropertiesFields();
        commandLineText.setOnAction(e -> {
            var text = commandLineText.getText();
            if (StringUtils.isNotBlank(text)) {
                actor.tell(new LineMessage(text));
                gs.get().lastCommand.set(text);
            }
        });
        gs.get().currentMap.addListener((o, ov, nv) -> {
            runFxThread(() -> {
                levelBar.setMin(1);
                levelBar.setMax(nv.getDepth());
                levelBar.setValue(nv.getCursorZ() + 1);
                levelLabel.setText(Integer.toString(nv.getCursorZ() + 1));
            });
        });
        levelBar.valueProperty().addListener((o, ov, nv) -> {
            runFxThread(() -> {
                levelLabel.setText(Integer.toString(nv.intValue()));
                gs.get().currentMap.get().setCursorZ(nv.intValue() - 1);
                saveZ.accept(gs.get().currentMap.get());
            });
        });
    }

    public void initButtons(GlobalKeys globalKeys, Map<String, KeyMapping> keyMappings) {
        quitButton.setOnAction(e -> {
            globalKeys.runAction(keyMappings.get(QUIT_MAPPING.name()));
        });
        settingsButton.setOnAction(e -> {
            globalKeys.runAction(keyMappings.get(SETTINGS_MAPPING.name()));
        });
        aboutButton.setOnAction(e -> {
            globalKeys.runAction(keyMappings.get(ABOUT_DIALOG_MAPPING.name()));
        });
    }

    private void setupImagePropertiesFields() {
    }

    public void setMap(WorldMap wm, GameMap gm) {
        fortressNameLabel
                .setText(texts.getResource("fortress_name", locale).getFormattedText(wm.getName(), gm.getName()));
        gameTimeLabel.setText(gs.get().gameTimeFormat.get().format(ZonedDateTime.of(wm.getTime(), gm.getTimeZone())));
    }

    public void updateMapCursor(GameBlockPos cursor) {
        levelBar.setValue(cursor.z + 1);
        levelLabel.setText(Integer.toString(cursor.z + 1));
    }

}
