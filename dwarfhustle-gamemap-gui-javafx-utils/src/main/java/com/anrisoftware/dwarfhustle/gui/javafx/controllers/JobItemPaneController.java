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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code work_job_item_ui.fxml}
 *
 * @author Erwin Müller
 */
@Slf4j
public class JobItemPaneController {

    @FXML
    public BorderPane jobItemPane;

    @FXML
    public Label jobNameLabel;

    @FXML
    public CheckBox jobActiveBox;

    @FXML
    public ToggleButton jobRepeatButton;

    @FXML
    public Button jobUpButton;

    @FXML
    public Button jobDownButton;

    @FXML
    public ToggleButton jobPauseButton;

    @FXML
    public Button jobCancelButton;

    public void setup() {
        log.debug("setup()");
    }

}
