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

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.collections.impl.factory.Lists;

import com.google.inject.Injector;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code object_pane_ui.fxml}
 *
 * @author Erwin Müller
 */
@Slf4j
public class ObjectPaneController {

    @FXML
    public BorderPane objectPane;

    @FXML
    public Label objectTitleLabel;

    @FXML
    public TabPane objectTabPane;

    @FXML
    public Tab propertiesTab;

    @FXML
    public ListView<ObjectPropertyItem> propertiesList;

    public ObservableList<ObjectPaneTab> objectTabs;

    private Injector injector;

    public void setup(Injector injector) {
        log.debug("setup()");
        this.injector = injector;
        List<ObjectPaneTab> list = Lists.mutable.empty();
        this.objectTabs = FXCollections.observableList(list);
        objectTabs.addListener((ListChangeListener<ObjectPaneTab>) c -> {
            while (c.next()) {
                if (c.wasPermutated()) {
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        // permutate
                    }
                } else if (c.wasUpdated()) {
                    // update item
                } else {
                    c.getRemoved().forEach(this::removeObjectPaneTab);
                    c.getAddedSubList().forEach(this::addObjectPaneTab);
                }
            }
        });
    }

    public void setOnMouseEnteredGui(Consumer<Boolean> consumer) {
        objectPane.setOnMouseEntered(e -> consumer.accept(true));
        objectPane.setOnMouseExited(e -> consumer.accept(false));
    }

    private void removeObjectPaneTab(ObjectPaneTab objectTab) {
    }

    private void addObjectPaneTab(ObjectPaneTab objectTab) {
        objectTab.create(injector, objectTabPane);
    }

}
