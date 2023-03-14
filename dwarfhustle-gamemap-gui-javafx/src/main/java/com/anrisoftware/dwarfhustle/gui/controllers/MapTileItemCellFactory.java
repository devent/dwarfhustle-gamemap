package com.anrisoftware.dwarfhustle.gui.controllers;

import com.anrisoftware.dwarfhustle.model.api.objects.MapTile;
import com.anrisoftware.dwarfhustle.model.api.objects.Person;
import com.google.inject.Injector;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import lombok.SneakyThrows;

/**
 * Displays a custom widget in the object info {@link ListView} for a
 * {@link MapTileItem}.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class MapTileItemCellFactory implements Callback<ListView<MapTileItem>, ListCell<MapTileItem>> {

    private Injector injector;

    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    @Override
    public ListCell<MapTileItem> call(ListView<MapTileItem> param) {
        return new ListCell<>() {

            final MapTileItemWidgetController widget = createWidget();

            @Override
            public void updateItem(MapTileItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item != null) {
                    setText(null);
                    setupWidget(widget, item);
                    setGraphic(widget.objectInfoPane);
                } else {
                    setText("null");
                    setGraphic(null);
                }
            }

        };
    }

    @SneakyThrows
    private MapTileItemWidgetController createWidget() {
        var loader = new FXMLLoader(getClass().getResource("/map_tile_item_widget_ui.fxml"));
        loader.load();
        MapTileItemWidgetController c = loader.getController();
        injector.injectMembers(c);
        return c;
    }

    private void setupWidget(MapTileItemWidgetController widget, MapTileItem item) {
        if (item.item instanceof MapTile mt) {
            widget.objectInfoTitle.setText("Tile");
            widget.objectInfoList.getItems().clear();
            widget.objectInfoList.getItems().add(mt.getMaterial());
        } else if (item.item instanceof Person p) {
            widget.objectInfoTitle.setText("Person");
            widget.objectInfoList.getItems().clear();
            widget.objectInfoList.getItems().add(p.getFirstName());
            widget.objectInfoList.getItems().add(p.getSecondName());
            widget.objectInfoList.getItems().add(p.getLastName());
        }
        widget.objectInfoPane.layout();
    }
}
