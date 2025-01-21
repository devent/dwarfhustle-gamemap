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
package org.dwarfhustle.gamemap.tester.gui.javafx.controllers;

import static com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil.getImageView;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.collections.api.factory.Maps;

import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMapping;
import com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
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
    public Pane blockPane;
    @FXML
    public Tab grassTab;
    @FXML
    public Pane grassPane;
    @FXML
    public Tab shrubTab;
    @FXML
    public Pane shrubPane;
    @FXML
    public Tab treeTab;
    @FXML
    public Pane treePane;

    private final Map<String, Pane> tabPanes = Maps.mutable.empty();

    @FXML
    private void initialize() {
        tabPanes.put("block", blockPane);
        tabPanes.put("grass", grassPane);
        tabPanes.put("shrub", shrubPane);
        tabPanes.put("tree", treePane);
    }

    public void createButtons(Map<String, List<String>> tabsButtons) {
        for (var entry : tabsButtons.entrySet()) {
            createButtons(tabPanes.get(entry.getKey()), entry.getValue());
        }
    }

    private void createButtons(Pane pane, List<String> names) {
        for (String name : names) {
            pane.getChildren().add(createButton(name));
        }
    }

    private Node createButton(String name) {
        var button = new Button(name);
        return button;
    }

    public void updateLocale(Locale locale, Texts texts, Images images, IconSize iconSize) {
    }

    private void setButtonImage(ButtonBase b, String name, Locale locale, Images images, IconSize iconSize) {
        b.setGraphic(getImageView(images, name, locale, iconSize));
        b.setText(null);
    }

    public void initListeners(GlobalKeys globalKeys, Map<String, KeyMapping> keyMappings) {
    }

    private void setButtonAction(ButtonBase button, String name, GlobalKeys globalKeys,
            Map<String, KeyMapping> keyMappings) {
        button.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get(name));
        });
    }

    public void setOnMouseEnteredGui(Consumer<Boolean> consumer) {
        JavaFxUtil.forEachController(this, TabPane.class, (c) -> {
            c.setOnMouseEntered(e -> consumer.accept(true));
            c.setOnMouseExited(e -> consumer.accept(false));
        });
    }

}
