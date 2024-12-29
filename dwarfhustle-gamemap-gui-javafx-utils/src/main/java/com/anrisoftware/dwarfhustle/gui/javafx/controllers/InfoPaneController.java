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
package com.anrisoftware.dwarfhustle.gui.javafx.controllers;

import java.util.List;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.google.inject.Injector;

import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code info_pane_ui.fxml}
 *
 * @author Erwin Müller
 */
@Slf4j
public class InfoPaneController implements ListChangeListener<MapTileItem> {

    @FXML
    public BorderPane infoPane;

    @FXML
    public VBox infoBox;

    @Inject
    private GameSettingsProvider gs;

    public ObservableList<MapTileItem> items;

    private Injector injector;

    private MutableMap<MapTileItem, MapTileItemWidgetController> widgets = Maps.mutable.empty();

    public void setup(Injector injector) {
        log.debug("setup()");
        this.injector = injector;
        List<MapTileItem> list = Lists.mutable.empty();
        this.items = FXCollections.observableList(list);
        items.addListener(this);
    }

    @Override
    public void onChanged(Change<? extends MapTileItem> c) {
        while (c.next()) {
            if (c.wasPermutated()) {
                for (int i = c.getFrom(); i < c.getTo(); ++i) {
                    // permutate
                }
            } else if (c.wasUpdated()) {
                // update item
            } else {
                c.getRemoved().forEach(this::removeMapTileItemWidget);
                c.getAddedSubList().forEach(this::addMapTileItemWidget);
            }
        }
    }

    private void addMapTileItemWidget(MapTileItem item) {
        var widget = createWidget();
        widget.setup(item);
        infoBox.getChildren().add(widget.objectInfoPane);
        widgets.put(item, widget);
    }

    private void removeMapTileItemWidget(MapTileItem item) {
        var widget = widgets.remove(item);
        infoBox.getChildren().remove(widget.objectInfoPane);
    }

    @SneakyThrows
    private MapTileItemWidgetController createWidget() {
        var loader = new FXMLLoader(getClass().getResource("/map_tile_item_widget_ui.fxml"));
        loader.load();
        MapTileItemWidgetController c = loader.getController();
        injector.injectMembers(c);
        return c;
    }

}
