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

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.google.inject.Injector;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code info_pane_ui.fxml}
 *
 * @author Erwin Müller
 */
@Slf4j
public class InfoPaneController {

    @FXML
    public BorderPane infoPane;

    @FXML
    public ListView<MapTileItem> infoList;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    private MapTileItemCellFactory mapTileItemCellFactory;

    public void setup(Injector injector) {
        log.debug("setup()");
        mapTileItemCellFactory.setInjector(injector);
        infoList.setCellFactory(mapTileItemCellFactory);
        infoList.getItems().clear();
    }
}
