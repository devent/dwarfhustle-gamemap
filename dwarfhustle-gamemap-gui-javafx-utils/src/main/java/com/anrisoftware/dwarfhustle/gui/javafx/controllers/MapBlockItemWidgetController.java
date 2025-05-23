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
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Controller for {@code map_block_item_widget_ui.fxml}
 *
 * @author Erwin Müller
 */
public class MapBlockItemWidgetController {

    @FXML
    public BorderPane objectInfoPane;

    @FXML
    public Label objectInfoTitle;

    @FXML
    public VBox objectInfoBox;

    public void setup(MapBlockInfoPaneItem item) {
        item.update(this);
        objectInfoPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        objectInfoPane.autosize();
    }
}
