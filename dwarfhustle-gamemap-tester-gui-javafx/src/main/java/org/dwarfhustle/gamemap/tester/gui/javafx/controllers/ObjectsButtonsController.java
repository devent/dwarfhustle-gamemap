/*
 * dwarfhustle-gamemap-tester-gui-javafx - Game map.
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
package org.dwarfhustle.gamemap.tester.gui.javafx.controllers;

import static com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil.getImageView;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.DescribedAs.describedAs;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.collections.api.factory.Maps;

import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMapping;
import com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code tester_materials_buttons.fxml}
 * <p>
 * {@code ^\s*<(\w+) fx:id="(\w+)".*$}
 * <p>
 * {@code @FXML public $1 $2;}
 *
 * @author Erwin Müller
 */
@Slf4j
public class ObjectsButtonsController {

    @FXML
    public Pane objectsBox;
    @FXML
    public TabPane objectsTabPane;
    @FXML
    public Tab blockTab;
    @FXML
    public ListView<String> blockList;
    @FXML
    public Tab grassTab;
    @FXML
    public ListView<String> grassList;
    @FXML
    public Tab shrubTab;
    @FXML
    public ListView<String> shrubList;
    @FXML
    public Tab treeTab;
    @FXML
    public ListView<String> treeList;

    private final Map<String, ListView<String>> tabListviews = Maps.mutable.empty();

    public Optional<String> selectedObject = Optional.empty();

    @FXML
    private void initialize() {
        tabListviews.put("block", blockList);
        tabListviews.put("grass", grassList);
        tabListviews.put("shrub", shrubList);
        tabListviews.put("sapling", treeList);
    }

    public void setupObjects(Map<String, List<String>> tabsButtons) {
        for (var entry : tabsButtons.entrySet()) {
            createObjects(tabListviews.get(entry.getKey()), entry.getValue());
        }
    }

    private void createObjects(ListView<String> listView, List<String> names) {
        for (String name : names) {
            listView.getItems().add(name);
        }
    }

    public void updateLocale(Locale locale, Texts texts, Images images, IconSize iconSize) {
    }

    private void setButtonImage(ButtonBase b, String name, Locale locale, Images images, IconSize iconSize) {
        b.setGraphic(getImageView(images, name, locale, iconSize));
        b.setText(null);
    }

    public void initListeners(GlobalKeys globalKeys, Map<String, KeyMapping> keyMappings) {
        setActions(blockList, "BLOCK_OBJECT_INSERT_MAPPING", globalKeys, keyMappings);
        setActions(grassList, "GRASS_OBJECT_INSERT_MAPPING", globalKeys, keyMappings);
        setActions(shrubList, "SHRUB_OBJECT_INSERT_MAPPING", globalKeys, keyMappings);
        setActions(treeList, "TREE_OBJECT_INSERT_MAPPING", globalKeys, keyMappings);
    }

    private void setActions(ListView<String> list, String name, GlobalKeys globalKeys,
            Map<String, KeyMapping> keyMappings) {
        assertThat(keyMappings.get(name), is(describedAs("keyMappings %0", notNullValue(), name)));
        list.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                this.selectedObject = Optional.of(nv);
                globalKeys.runAction(keyMappings.get(name));
            }
        });
    }

    public void setOnMouseEnteredGui(Consumer<Boolean> consumer) {
        JavaFxUtil.forEachController(this, TabPane.class, c -> {
            c.setOnMouseEntered(e -> consumer.accept(true));
            c.setOnMouseExited(e -> consumer.accept(false));
        });
    }

    public void setSelectedObject(boolean b) {
        if (!b) {
            blockList.getSelectionModel().clearSelection();
            grassList.getSelectionModel().clearSelection();
            shrubList.getSelectionModel().clearSelection();
            treeList.getSelectionModel().clearSelection();
            this.selectedObject = Optional.empty();
        }
    }

}
