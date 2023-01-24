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
package com.anrisoftware.dwarfhustle.gamemap.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.anrisoftware.resources.images.external.IconSize;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
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
		public DateTimeFormatter gameTimeFormat = DateTimeFormatter.RFC_1123_DATE_TIME;

		public float tickLength = 1 / 30f;

		public float tickLongLength = 1 / 15f;

		public boolean windowFullscreen = false;

		public int windowWidth = 1024;

		public int windowHeight = 768;

		public IconSize iconSize = IconSize.MEDIUM;

		public TextPosition textPosition = TextPosition.RIGHT;

		public double commandsSplitPosition = 0.71;

		public float cameraPosX = 0.002901543f;

		public float cameraPosY = -0.013370683f;

		public float cameraPosZ = 28.217747f;

		public float cameraRotX = -4.8154507E-6f;

		public float cameraRotY = 0.9999911f;

		public float cameraRotZ = 0.0012241602f;

		public float cameraRotW = 0.004027171f;

		public String lastCommand = "";
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

	public final FloatProperty cameraPosX;

	public final FloatProperty cameraPosY;

	public final FloatProperty cameraPosZ;

	public final FloatProperty cameraRotX;

	public final FloatProperty cameraRotY;

	public final FloatProperty cameraRotZ;

	public final FloatProperty cameraRotW;

	public final ObjectProperty<String> lastCommand;

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
		this.cameraPosX = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraPosX").build();
		this.cameraPosY = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraPosY").build();
		this.cameraPosZ = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraPosZ").build();
		this.cameraRotX = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraRotX").build();
		this.cameraRotY = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraRotY").build();
		this.cameraRotZ = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraRotZ").build();
		this.cameraRotW = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraRotW").build();
		this.lastCommand = JavaBeanObjectPropertyBuilder.create().bean(p).name("lastCommand").build();
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
		cameraPosX.set(other.cameraPosX);
		cameraPosY.set(other.cameraPosY);
		cameraPosZ.set(other.cameraPosZ);
		cameraRotX.set(other.cameraRotX);
		cameraRotY.set(other.cameraRotY);
		cameraRotZ.set(other.cameraRotZ);
		cameraRotW.set(other.cameraRotW);
		lastCommand.set(other.lastCommand);
	}

	public Vector3f getCameraPos() {
		return new Vector3f(cameraPosX.get(), cameraPosY.get(), cameraPosZ.get());
	}

	public Quaternion getCameraRot() {
		return new Quaternion(cameraRotX.get(), cameraRotY.get(), cameraRotZ.get(), cameraRotW.get());
	}

	public void setCameraPos(Vector3f l) {
		cameraPosX.set(l.x);
		cameraPosY.set(l.y);
		cameraPosZ.set(l.z);
	}

	public void setCameraRot(Quaternion r) {
		cameraRotX.set(r.getX());
		cameraRotY.set(r.getY());
		cameraRotZ.set(r.getZ());
		cameraRotW.set(r.getW());
	}
}
