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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.resources.images.external.IconSize;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
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
		public DateTimeFormatter gameTimeFormat = DateTimeFormatter.ISO_TIME;

		public float tickLength = 1 / 30f;

		public float tickLongLength = 1 / 15f;

		public boolean windowFullscreen = false;

		public int windowWidth = 1024;

		public int windowHeight = 768;

		public IconSize iconSize = IconSize.MEDIUM;

		public TextPosition textPosition = TextPosition.RIGHT;

		public double commandsSplitPosition = 0.71;

		public String lastCommand = "";

        public int visibleDepthLayers = 1;

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

	public final FloatProperty tickLength;

	public final FloatProperty tickLongLength;

	public final BooleanProperty windowFullscreen;

	public final IntegerProperty windowWidth;

	public final IntegerProperty windowHeight;

	public final ObjectProperty<IconSize> iconSize;

	public final ObjectProperty<TextPosition> textPosition;

	public final DoubleProperty commandsSplitPosition;

	public final ObjectProperty<String> lastCommand;

	public final ObjectProperty<WorldMap> currentWorld;

	public final ObjectProperty<GameMap> currentMap;

    public final IntegerProperty visibleDepthLayers;

	@SuppressWarnings("unchecked")
	@SneakyThrows
	public ObservableGameSettings(GameSettings p) {
		this.locale = JavaBeanObjectPropertyBuilder.create().bean(p).name("locale").build();
		this.gameTimeFormat = JavaBeanObjectPropertyBuilder.create().bean(p).name("gameTimeFormat").build();
		this.tickLength = JavaBeanFloatPropertyBuilder.create().bean(p).name("tickLength").build();
		this.tickLongLength = JavaBeanFloatPropertyBuilder.create().bean(p).name("tickLongLength").build();
		this.windowFullscreen = JavaBeanBooleanPropertyBuilder.create().bean(p).name("windowFullscreen").build();
		this.windowWidth = JavaBeanIntegerPropertyBuilder.create().bean(p).name("windowWidth").build();
		this.windowHeight = JavaBeanIntegerPropertyBuilder.create().bean(p).name("windowHeight").build();
		this.iconSize = JavaBeanObjectPropertyBuilder.create().bean(p).name("iconSize").build();
		this.textPosition = JavaBeanObjectPropertyBuilder.create().bean(p).name("textPosition").build();
		this.commandsSplitPosition = JavaBeanDoublePropertyBuilder.create().bean(p).name("commandsSplitPosition")
				.build();
		this.lastCommand = JavaBeanObjectPropertyBuilder.create().bean(p).name("lastCommand").build();
        this.visibleDepthLayers = JavaBeanIntegerPropertyBuilder.create().bean(p).name("visibleDepthLayers").build();
		this.currentWorld = new SimpleObjectProperty<>();
		this.currentMap = new SimpleObjectProperty<>();
	}

	public void copy(GameSettings other) {
		locale.set(other.locale);
		gameTimeFormat.set(other.gameTimeFormat);
		tickLength.set(other.tickLength);
		tickLongLength.set(other.tickLongLength);
		windowFullscreen.set(other.windowFullscreen);
		windowWidth.set(other.windowWidth);
		windowHeight.set(other.windowHeight);
		iconSize.set(other.iconSize);
		iconSize.set(other.iconSize);
		commandsSplitPosition.set(other.commandsSplitPosition);
		lastCommand.set(other.lastCommand);
        visibleDepthLayers.set(other.visibleDepthLayers);
	}
}
