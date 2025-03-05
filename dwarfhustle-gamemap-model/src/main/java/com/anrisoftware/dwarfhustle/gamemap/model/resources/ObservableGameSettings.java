/*
 * dwarfhustle-gamemap-model - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.model.resources;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.anrisoftware.resources.images.external.IconSize;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanLongPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;

/**
 * Settings that apply to all games and can be changed in the game settings
 * menu.
 *
 * @author Erwin Müller
 */
public class ObservableGameSettings {

    /**
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @Data
    public static class GameSettings {

        public Locale locale = Locale.US;

        @JsonIgnore
        public DateTimeFormatter gameTimeFormat = DateTimeFormatter.ofPattern("yyyy MMMM dd - HH:mm");

        public Duration gameTickDuration = Duration.ofMillis(250);

        public boolean gameTickPaused = false;

        public long gameSpeedAmountToAddMillis = 2000;

        public Duration gameTickNormalDuration = Duration.ofMillis(250);

        public Duration gameTickFastDuration = Duration.ofMillis(125);

        public Duration terrainUpdateDuration = Duration.ofMillis(100);

        public boolean windowFullscreen = false;

        public int windowWidth = 1024;

        public int windowHeight = 768;

        public IconSize iconSize = IconSize.MEDIUM;

        public TextPosition textPosition = TextPosition.RIGHT;

        public double commandsSplitPosition = 0.71;

        public String lastCommand = "";

        public int visibleDepthLayers = 1;

        public float timeUpdateInterval = 1 / 30f;

        public boolean hideUndiscovered = true;

    }

    /**
     * Command item with a time stamp.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @ToString(onlyExplicitlyIncluded = true)
    public static class CommandItem implements Comparable<CommandItem> {

        @EqualsAndHashCode.Include
        public LocalTime time;

        @ToString.Include
        public String line;

        @Override
        public int compareTo(CommandItem o) {
            return time.compareTo(o.time);
        }
    }

    public final ObjectProperty<Locale> locale;

    public final ObjectProperty<DateTimeFormatter> gameTimeFormat;

    public final ObjectProperty<Duration> gameTickDuration;

    public final BooleanProperty gameTickPaused;

    public final LongProperty gameSpeedAmountToAddMillis;

    public final ObjectProperty<Duration> gameTickNormalDuration;

    public final ObjectProperty<Duration> gameTickFastDuration;

    public final ObjectProperty<Duration> terrainUpdateDuration;

    public final BooleanProperty windowFullscreen;

    public final IntegerProperty windowWidth;

    public final IntegerProperty windowHeight;

    public final ObjectProperty<IconSize> iconSize;

    public final ObjectProperty<TextPosition> textPosition;

    public final DoubleProperty commandsSplitPosition;

    public final ObjectProperty<String> lastCommand;

    public final LongProperty currentWorld;

    public final LongProperty currentMap;

    public final IntegerProperty visibleDepthLayers;

    public final FloatProperty timeUpdateInterval;

    public final BooleanProperty hideUndiscovered;

    public final SimpleBooleanProperty mouseEnteredGui;

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public ObservableGameSettings(GameSettings p) {
        this.locale = JavaBeanObjectPropertyBuilder.create().bean(p).name("locale").build();
        this.gameTimeFormat = JavaBeanObjectPropertyBuilder.create().bean(p).name("gameTimeFormat").build();
        this.gameTickDuration = JavaBeanObjectPropertyBuilder.create().bean(p).name("gameTickDuration").build();
        this.gameTickPaused = JavaBeanBooleanPropertyBuilder.create().bean(p).name("gameTickPaused").build();
        this.gameSpeedAmountToAddMillis = JavaBeanLongPropertyBuilder.create().bean(p)
                .name("gameSpeedAmountToAddMillis").build();
        this.gameTickNormalDuration = JavaBeanObjectPropertyBuilder.create().bean(p).name("gameTickNormalDuration")
                .build();
        this.gameTickFastDuration = JavaBeanObjectPropertyBuilder.create().bean(p).name("gameTickFastDuration").build();
        this.terrainUpdateDuration = JavaBeanObjectPropertyBuilder.create().bean(p).name("terrainUpdateDuration")
                .build();
        this.windowFullscreen = JavaBeanBooleanPropertyBuilder.create().bean(p).name("windowFullscreen").build();
        this.windowWidth = JavaBeanIntegerPropertyBuilder.create().bean(p).name("windowWidth").build();
        this.windowHeight = JavaBeanIntegerPropertyBuilder.create().bean(p).name("windowHeight").build();
        this.iconSize = JavaBeanObjectPropertyBuilder.create().bean(p).name("iconSize").build();
        this.textPosition = JavaBeanObjectPropertyBuilder.create().bean(p).name("textPosition").build();
        this.commandsSplitPosition = JavaBeanDoublePropertyBuilder.create().bean(p).name("commandsSplitPosition")
                .build();
        this.lastCommand = JavaBeanObjectPropertyBuilder.create().bean(p).name("lastCommand").build();
        this.visibleDepthLayers = JavaBeanIntegerPropertyBuilder.create().bean(p).name("visibleDepthLayers").build();
        this.timeUpdateInterval = JavaBeanFloatPropertyBuilder.create().bean(p).name("timeUpdateInterval").build();
        this.hideUndiscovered = JavaBeanBooleanPropertyBuilder.create().bean(p).name("hideUndiscovered").build();
        this.currentWorld = new SimpleLongProperty();
        this.currentMap = new SimpleLongProperty();
        this.mouseEnteredGui = new SimpleBooleanProperty(false);
    }

    public void copy(GameSettings other) {
        locale.set(other.locale);
        gameTimeFormat.set(other.gameTimeFormat);
        gameTickDuration.set(other.gameTickDuration);
        gameTickPaused.set(other.gameTickPaused);
        gameSpeedAmountToAddMillis.set(other.gameSpeedAmountToAddMillis);
        gameTickNormalDuration.set(other.gameTickNormalDuration);
        gameTickFastDuration.set(other.gameTickFastDuration);
        terrainUpdateDuration.set(other.terrainUpdateDuration);
        windowFullscreen.set(other.windowFullscreen);
        windowWidth.set(other.windowWidth);
        windowHeight.set(other.windowHeight);
        iconSize.set(other.iconSize);
        iconSize.set(other.iconSize);
        commandsSplitPosition.set(other.commandsSplitPosition);
        lastCommand.set(other.lastCommand);
        visibleDepthLayers.set(other.visibleDepthLayers);
        timeUpdateInterval.set(other.timeUpdateInterval);
        hideUndiscovered.set(other.hideUndiscovered);
    }
}
