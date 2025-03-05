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

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMapping;
import com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.util.converter.DateTimeStringConverter;
import lombok.SneakyThrows;
import lombok.val;
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
    public TextField dateText;

    @FXML
    public Button timeSetButton;

    private TextFormatter<Date> timeTextFormatter;

    private ZoneOffset zone;

    private Optional<Consumer<ZonedDateTime>> saveTime = Optional.empty();

    private TextFormatter<Date> dateTextFormatter;

    @FXML
    private void initialize() {
    }

    public void updateLocale(Locale locale, Texts texts, Images images, IconSize iconSize) {
    }

    public void setSaveTime(Consumer<ZonedDateTime> saveTime) {
        this.saveTime = Optional.of(saveTime);
    }

    @SneakyThrows
    public void setTime(WorldMap wm, GameMap gm, GameSettingsProvider gs) {
        val dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        val time = ZonedDateTime.of(wm.getTime(), gm.getTimeZone());
        val date = Date.from(time.toInstant());
        this.zone = gm.getTimeZone();
        this.dateTextFormatter = new TextFormatter<>(new DateTimeStringConverter(dateFormat), date);
        dateText.setTextFormatter(dateTextFormatter);
        val timeFormat = new SimpleDateFormat("HH:mm:ss");
        this.timeTextFormatter = new TextFormatter<>(new DateTimeStringConverter(timeFormat), date);
        timeText.setTextFormatter(timeTextFormatter);
    }

    private void setButtonImage(ButtonBase b, String name, Locale locale, Images images, IconSize iconSize) {
        b.setGraphic(getImageView(images, name, locale, iconSize));
        b.setText(null);
    }

    public void initListeners(GlobalKeys globalKeys, Map<String, KeyMapping> keyMappings) {
        timeSetButton.setOnAction(e -> {
            Date date = dateTextFormatter.getValue();
            val localDate = date.toInstant().atZone(zone);
            Date time = timeTextFormatter.getValue();
            @SuppressWarnings("deprecation")
            val localTime = localDate.withHour(time.getHours()).withMinute(time.getMinutes())
                    .withSecond(time.getSeconds());
            saveTime.ifPresent(c -> c.accept(localTime));
        });
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
