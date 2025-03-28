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

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code work_jobs_pane_ui.fxml}
 *
 * @author Erwin Müller
 */
@Slf4j
public class JobsPaneController implements ObjectPaneTabController, ListChangeListener<JobPaneItem> {

    @FXML
    public BorderPane jobsPane;

    @FXML
    public ListView<JobPaneItem> jobsList;

    public ObservableList<JobPaneItem> jobItems;

    private final MutableMap<JobPaneItem, JobItemPaneController> jobItemPanel = Maps.mutable.empty();

    private Tab tab;

    @Override
    public Tab getTab() {
        return tab;
    }

    @FXML
    private void initialize() {
        log.debug("initialize");
        this.tab = new Tab();
        tab.setText("Jobs");
        List<JobPaneItem> list = Lists.mutable.empty();
        this.jobItems = FXCollections.observableList(list);
        jobItems.addListener(this);
        jobsList.setItems(jobItems);
    }

    @Override
    public void onChanged(Change<? extends JobPaneItem> c) {
        while (c.next()) {
            if (c.wasPermutated()) {
                for (int i = c.getFrom(); i < c.getTo(); ++i) {
                    // permutate
                }
            } else if (c.wasUpdated()) {
                // update item
            } else {
                c.getRemoved().forEach(this::removeJobItemPane);
                c.getAddedSubList().forEach(this::addJobItemPane);
            }
        }
    }

    private void addJobItemPane(JobPaneItem item) {
        var pane = createPane();
        item.update(pane);
        jobItems.add(item);
        jobItemPanel.put(item, pane);
    }

    private void removeJobItemPane(JobPaneItem item) {
        jobItemPanel.remove(item);
        jobItems.remove(item);
    }

    @SneakyThrows
    private JobItemPaneController createPane() {
        var loader = new FXMLLoader(getClass().getResource("/work_job_item_ui.fxml"));
        loader.load();
        JobItemPaneController c = loader.getController();
        return c;
    }

}
