/*
 * dwarfhustle-gamemap-gui-javafx-utils - Game map.
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
package com.anrisoftware.dwarfhustle.gui.javafx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code object_properties_pane_ui.fxml}
 *
 * @author Erwin Müller
 */
@Slf4j
public class PropertiesPaneController implements ObjectPaneTabController {

    @FXML
    public BorderPane objectPropertiesPane;

    @FXML
    public ListView<ObjectPropertyItem> propertiesList;

    private Tab tab;

    @Override
    public Tab getTab() {
        return tab;
    }

    @FXML
    private void initialize() {
        log.debug("initialize");
        this.tab = new Tab();
        tab.setText("Properties");
    }
}
