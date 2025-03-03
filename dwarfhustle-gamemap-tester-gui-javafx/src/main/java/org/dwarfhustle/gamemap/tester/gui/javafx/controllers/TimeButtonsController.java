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

import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMapping;
import com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@code tester_time_buttons.fxml}
 * <p>
 * {@code ^\s*<(\w+) fx:id="(\w+)".*$}
 * <p>
 * {@code @FXML public $1 $2;}
 *
 * @author Erwin Müller
 */
@Slf4j
public class TimeButtonsController {

    @FXML
    public Pane timeBox;

    @FXML
    public TextField timeText;

    @FXML
    public Button timeSetButton;

    @FXML
    private void initialize() {
    }

    public void updateLocale(Locale locale, Texts texts, Images images, IconSize iconSize) {
    }

    private void setButtonImage(ButtonBase b, String name, Locale locale, Images images, IconSize iconSize) {
        b.setGraphic(getImageView(images, name, locale, iconSize));
        b.setText(null);
    }

    public void initListeners(GlobalKeys globalKeys, Map<String, KeyMapping> keyMappings) {
    }

    public void setOnMouseEnteredGui(Consumer<Boolean> consumer) {
        JavaFxUtil.forEachController(this, ButtonBase.class, c -> {
            c.setOnMouseEntered(e -> consumer.accept(true));
            c.setOnMouseExited(e -> consumer.accept(false));
        });
        JavaFxUtil.forEachController(this, TextField.class, c -> {
            c.setOnMouseEntered(e -> consumer.accept(true));
            c.setOnMouseExited(e -> consumer.accept(false));
        });
    }

}
