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

import static com.anrisoftware.dwarfhustle.gui.states.DefaultKeyMappings.ABOUT_DIALOG_MAPPING;
import static com.anrisoftware.dwarfhustle.gui.states.DefaultKeyMappings.QUIT_MAPPING;
import static com.anrisoftware.dwarfhustle.gui.states.DefaultKeyMappings.SETTINGS_MAPPING;

import java.util.Locale;
import java.util.Map;

import com.anrisoftware.dwarfhustle.gamemap.model.ObservableGameMainPaneProperties;
import com.anrisoftware.dwarfhustle.gui.states.KeyMapping;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;

import akka.actor.typed.ActorRef;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    public void updateLocale(Locale locale, Images images, IconSize iconSize) {
    }

    public void initializeListeners(ActorRef<Message> actor, ObservableGameMainPaneProperties np) {
        log.debug("initializeListeners");
        setupImagePropertiesFields(np);
    }

    public void initializeButtons(GlobalKeys globalKeys, Map<String, KeyMapping> keyMappings,
            ObservableGameMainPaneProperties gmpp) {
		quitButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get(QUIT_MAPPING.name()));
        });
        settingsButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get(SETTINGS_MAPPING.name()));
        });
        aboutButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get(ABOUT_DIALOG_MAPPING.name()));
        });
    }

    private void setupImagePropertiesFields(ObservableGameMainPaneProperties np) {
    }

}
