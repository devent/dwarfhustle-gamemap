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

import static com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil.getImageView;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.controlsfx.control.textfield.CustomTextField;
import org.eclipse.collections.impl.factory.Lists;

import com.anrisoftware.dwarfhustle.model.api.buildings.KnowledgeWorkJob;
import com.anrisoftware.dwarfhustle.model.api.materials.KnowledgeMaterial;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code work_jobs_pane_ui.fxml}
 *
 * @author Erwin Müller
 */
@Slf4j
public class JobCreatePaneController implements ObjectPaneTabController {

    /**
     * Cell for {@link KnowledgeWorkJob}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    private static class KnowledgeWorkJobCell extends ListCell<KnowledgeWorkJob> {
        @Override
        protected void updateItem(KnowledgeWorkJob item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getName());
            }
        }
    }

    /**
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @Data
    public static class GameMapObjectItem {
        public final GameMapObject go;
        public final KnowledgeMaterial material;
    }

    /**
     * Cell for {@link GameMapObjectItem}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    private static class GameMapObjectCell extends ListCell<GameMapObjectItem> {
        @Override
        protected void updateItem(GameMapObjectItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.material.getName());
            }
        }
    }

    @FXML
    public BorderPane jobCreatePane;

    @FXML
    public ListView<KnowledgeWorkJob> jobNamesList;

    public ObservableList<KnowledgeWorkJob> jobNamesItems;

    @FXML
    public Label jobAvailableLabel;

    @FXML
    public CustomTextField jobSearchField;

    @FXML
    public Button jobAddButton;

    @FXML
    public Label jobMaterialLabel;

    @FXML
    public CustomTextField jobMaterialField;

    @FXML
    public ListView<GameMapObjectItem> jobMaterialsList;

    public ObservableList<GameMapObjectItem> jobMaterialsItems;

    private Tab tab;

    public Consumer<KnowledgeWorkJob> onUpdateSelectedJob;

    public Consumer<GameMapObjectItem> onUpdateSelectedInputItem;

    @Override
    public Tab getTab() {
        return tab;
    }

    @FXML
    private void initialize() {
        log.debug("initialize");
        this.tab = new Tab();
        tab.setText("Create Jobs");
        List<KnowledgeWorkJob> jobNamesItems = Lists.mutable.empty();
        this.jobNamesItems = FXCollections.observableList(jobNamesItems);
        jobNamesList.setItems(this.jobNamesItems);
        jobNamesList.setCellFactory((ListView<KnowledgeWorkJob> l) -> new KnowledgeWorkJobCell());
        jobNamesList.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                onUpdateSelectedJob.accept(nv);
            }
        });
        List<GameMapObjectItem> jobMaterialsItems = Lists.mutable.empty();
        this.jobMaterialsItems = FXCollections.observableList(jobMaterialsItems);
        jobMaterialsList.setItems(this.jobMaterialsItems);
        jobMaterialsList.setCellFactory((ListView<GameMapObjectItem> l) -> new GameMapObjectCell());
        jobMaterialsList.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                onUpdateSelectedInputItem.accept(nv);
            }
        });
    }

    public void setLocale(Texts texts, Images icons, Locale locale) {
        jobSearchField.setRight(getImageView(icons, "jobSearchField", locale, IconSize.SMALL));
        jobMaterialField.setRight(getImageView(icons, "jobMaterialField", locale, IconSize.SMALL));
        jobAddButton.setGraphic(getImageView(icons, "jobAddButton", locale, IconSize.SMALL));
        jobAddButton.setText(null);
    }
}
