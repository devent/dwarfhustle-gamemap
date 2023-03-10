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

import java.util.Locale;
import java.util.Map;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.ObservableGameSettings;
import com.anrisoftware.dwarfhustle.gui.states.KeyMapping;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;

import akka.actor.typed.ActorRef;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code info_ui.fxml}
 *
 * @author Erwin Müller
 */
@Slf4j
public class InfoPaneController {

    @FXML
    public BorderPane infoPane;

    @FXML
    public ListView<String> infoList;

	private Locale locale;

	private Texts texts;

	private Images images;

	private IconSize iconSize;

	private ObservableGameSettings gs;

	public void updateLocale(Locale locale, Texts texts, Images images, IconSize iconSize, ObservableGameSettings gs) {
		this.locale = locale;
		this.texts = texts;
		this.images = images;
		this.iconSize = iconSize;
		this.gs = gs;
	}

	public void initListeners(ActorRef<Message> actor, ObservableGameSettings gs) {
		log.debug("initListeners");
		setupImagePropertiesFields(gs);
	}

	public void initButtons(GlobalKeys globalKeys, Map<String, KeyMapping> keyMappings, ObservableGameSettings gs) {
	}

	private void setupImagePropertiesFields(ObservableGameSettings gs) {
	}

}
