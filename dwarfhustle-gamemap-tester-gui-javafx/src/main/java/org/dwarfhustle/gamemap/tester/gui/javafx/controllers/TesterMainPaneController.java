/*
 * dwarfhustle-gamemap-tester-gui-javafx - Game map.
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
import static com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil.runFxThread;

import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.AbstractStatusController;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMapping;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.StringObject;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code main_ui.fxml}
 *
 * @author Erwin Müller
 */
@Slf4j
public class TesterMainPaneController extends AbstractStatusController {

    @FXML
    public BorderPane mainPanel;

    @FXML
    public Label fortressNameLabel;

    @FXML
    public Label worldNameLabel;

    @FXML
    public Label gameTimeLabel;

    @FXML
    public Button settingsButton;

    @FXML
    public Button aboutButton;

    @FXML
    public Button quitButton;

    @FXML
    public Label statusLabel;

    @FXML
    public ScrollBar levelBar;

    @FXML
    public Label levelLabel;

    @FXML
    public VBox testerButtonsBox;

    @FXML
    public ToggleGroup mainButtonsGroup;

    @FXML
    public ToggleButton paintButton;

    @FXML
    public ToggleButton insertButton;

    @FXML
    public ToggleButton deleteButton;

    @FXML
    public ToggleButton timeButton;

    @FXML
    public ToggleGroup gameSpeedGroup;

    @FXML
    public ToggleButton gameSpeedPauseButton;

    @FXML
    public ToggleButton gameSpeedNormalButton;

    @FXML
    public ToggleButton gameSpeedFastButton;

    private Locale locale;

    private Texts texts;

    private Images images;

    private IconSize iconSize;

    @Inject
    private GameSettingsProvider gs;

    private Toggle gameSpeedLastSelectedButton;

    public void updateLocale(Locale locale, Texts texts, Images images, IconSize iconSize) {
        this.locale = locale;
        this.texts = texts;
        this.images = images;
        this.iconSize = iconSize;
    }

    public void initListeners(Supplier<GameMap> gm, Consumer<GameMap> saveZ) {
        log.debug("initListeners");
        setupImages();
        runFxThread(() -> {
            levelBar.setMin(1);
            levelBar.setMax(gm.get().getDepth());
            levelBar.setValue(gm.get().getCursorZ() + 1);
            levelLabel.setText(Integer.toString(gm.get().getCursorZ() + 1));
        });
        levelBar.valueProperty().addListener((o, ov, nv) -> {
            runFxThread(() -> {
                levelLabel.setText(Integer.toString(nv.intValue()));
                gm.get().setCursorZ(nv.intValue() - 1);
                saveZ.accept(gm.get());
            });
        });
    }

    public void initButtons(GlobalKeys globalKeys, Map<String, KeyMapping> keyMappings) {
        quitButton.setOnAction(e -> {
            globalKeys.runAction(keyMappings.get("QUIT_MAPPING"));
        });
        settingsButton.setOnAction(e -> {
            globalKeys.runAction(keyMappings.get("SETTINGS_MAPPING"));
        });
        aboutButton.setOnAction(e -> {
            globalKeys.runAction(keyMappings.get("ABOUT_DIALOG_MAPPING"));
        });
        mainButtonsGroup.selectedToggleProperty().addListener((o, oval, nval) -> {
            if (nval != null && nval.isSelected()) {
                if (nval == paintButton) {
                    globalKeys.runAction(keyMappings.get("OPEN_MATERIALS_BUTTONS_MAPPING"));
                } else if (nval == insertButton) {
                    globalKeys.runAction(keyMappings.get("OPEN_OBJECTS_BUTTONS_MAPPING"));
                } else if (nval == deleteButton) {
                    globalKeys.runAction(keyMappings.get("OPEN_DELETE_BUTTONS_MAPPING"));
                } else if (nval == timeButton) {
                    globalKeys.runAction(keyMappings.get("OPEN_TIME_BUTTONS_MAPPING"));
                }
                return;
            }
            if (oval != null && !oval.isSelected()) {
                if (oval == paintButton) {
                    globalKeys.runAction(keyMappings.get("CLOSE_MATERIALS_BUTTONS_MAPPING"));
                } else if (oval == insertButton) {
                    globalKeys.runAction(keyMappings.get("CLOSE_OBJECTS_BUTTONS_MAPPING"));
                } else if (oval == deleteButton) {
                    globalKeys.runAction(keyMappings.get("CLOSE_DELETE_BUTTONS_MAPPING"));
                } else if (oval == timeButton) {
                    globalKeys.runAction(keyMappings.get("CLOSE_TIME_BUTTONS_MAPPING"));
                }
            }
        });
        gameSpeedGroup.selectedToggleProperty().addListener((o, oval, nval) -> {
            if (nval == null) {
                oval.setSelected(true);
            } else if (nval != null && nval.isSelected()) {
                if (nval == gameSpeedPauseButton) {
                    globalKeys.runAction(keyMappings.get("GAME_SPEED_PAUSE_MAPPING"));
                } else if (nval == gameSpeedNormalButton) {
                    globalKeys.runAction(keyMappings.get("GAME_SPEED_NORMAL_MAPPING"));
                    gameSpeedLastSelectedButton = gameSpeedNormalButton;
                } else if (nval == gameSpeedFastButton) {
                    globalKeys.runAction(keyMappings.get("GAME_SPEED_FAST_MAPPING"));
                    gameSpeedLastSelectedButton = gameSpeedFastButton;
                }
                return;
            }
        });
    }

    public void setOnMouseEnteredGui(Consumer<Boolean> consumer) {
        paintButton.setOnMouseEntered(e -> {
            consumer.accept(true);
        });
        paintButton.setOnMouseExited(e -> {
            consumer.accept(false);
        });
        insertButton.setOnMouseEntered(e -> {
            consumer.accept(true);
        });
        insertButton.setOnMouseExited(e -> {
            consumer.accept(false);
        });
        deleteButton.setOnMouseEntered(e -> {
            consumer.accept(true);
        });
        deleteButton.setOnMouseExited(e -> {
            consumer.accept(false);
        });
    }

    @SneakyThrows
    private void setupImages() {
        paintButton.setGraphic(getImageView(images, "buttons_materials", locale, iconSize));
        paintButton.setText(null);
        insertButton.setGraphic(getImageView(images, "buttons_objects", locale, iconSize));
        insertButton.setText(null);
        gameSpeedPauseButton.setGraphic(getImageView(images, "gameSpeedPauseButton", locale, iconSize));
        gameSpeedPauseButton.setText(null);
        gameSpeedNormalButton.setGraphic(getImageView(images, "gameSpeedNormalButton", locale, iconSize));
        gameSpeedNormalButton.setText(null);
        gameSpeedFastButton.setGraphic(getImageView(images, "gameSpeedFastButton", locale, iconSize));
        gameSpeedFastButton.setText(null);
    }

    public void setMap(WorldMap wm, StringObject wms, GameMap gm, StringObject gms) {
        fortressNameLabel.setText(texts.getResource("fortress_name", locale).getFormattedText(gms.getS()));
        worldNameLabel.setText(texts.getResource("world_name", locale).getFormattedText(wms.getS()));
        gameTimeLabel.setText(gs.get().gameTimeFormat.get().format(ZonedDateTime.of(wm.getTime(), gm.getTimeZone())));
        levelBar.setMin(1);
        levelBar.setMax(gm.getDepth());
        updateMapCursor(gm.getCursor());
    }

    public void updateMapCursor(GameBlockPos cursor) {
        levelBar.setValue(cursor.z + 1);
        levelLabel.setText(Integer.toString(cursor.z + 1));
    }

    @Override
    public Label getStatusLabel() {
        return statusLabel;
    }

    public void initGameSpeedButtons(boolean gamePaused, long gameSpeedCurrent, long gameSpeedNormal,
            long gameSpeedFast) {
        if (gamePaused) {
            gameSpeedGroup.selectToggle(gameSpeedPauseButton);
        } else if (gameSpeedCurrent == gameSpeedNormal) {
            gameSpeedGroup.selectToggle(gameSpeedNormalButton);
            gameSpeedLastSelectedButton = gameSpeedNormalButton;
        } else if (gameSpeedCurrent == gameSpeedFast) {
            gameSpeedGroup.selectToggle(gameSpeedFastButton);
            gameSpeedLastSelectedButton = gameSpeedFastButton;
        }
    }

    public void toggleGameSpeedPauseButton() {
        if (gameSpeedGroup.getSelectedToggle() == gameSpeedPauseButton) {
            gameSpeedGroup.selectToggle(gameSpeedLastSelectedButton);
        } else {
            gameSpeedLastSelectedButton = gameSpeedGroup.getSelectedToggle();
            gameSpeedGroup.selectToggle(gameSpeedPauseButton);
        }
    }
}
