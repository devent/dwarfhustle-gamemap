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

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.ObservableGameSettings.GameSettings;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides the {@link GameSettings} and saves/loads the properties from/to
 * file.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class GameSettingsProvider implements Provider<ObservableGameSettings> {

	private final static String GAME_SETTINGS_FILE = GameSettingsProvider.class.getPackageName()
			+ ".game_settings_file";

	private static final File DEFAULT_GAME_SETTINGS_FILE = new File(
			System.getProperty("user.home") + "/.dwarfhustle-gamemap-settings.yaml");

	private final GameSettings gs;

	private final ObservableGameSettings ogs;

	private File file;

	@Inject
	private ObjectMapper mapper;

	public GameSettingsProvider() {
		this(new GameSettings());
	}

	public GameSettingsProvider(GameSettings gs) {
		this.gs = gs;
		this.ogs = new ObservableGameSettings(gs);
		this.file = getFile();
	}

	private static File getFile() {
		var argsFile = System.getProperty(GAME_SETTINGS_FILE);
		if (argsFile != null) {
			return new File(argsFile);
		}
		return DEFAULT_GAME_SETTINGS_FILE;
	}

	@Override
	public ObservableGameSettings get() {
		return ogs;
	}

	public void save() throws IOException {
		save(file);
	}

	public void save(File file) throws IOException {
		log.debug("Save settings to {}", file);
		mapper.writeValue(file, gs);
	}

	public String saveAsString() throws IOException {
		return mapper.writeValueAsString(gs);
	}

	public void load() throws IOException {
		load(file);
	}

	public void load(File file) throws IOException {
		if (file.exists()) {
			log.debug("Load settings from {}", file);
			var p = mapper.readValue(file, GameSettings.class);
			ogs.copy(p);
		}
	}

	public void loadFromString(String s) throws IOException {
		var p = mapper.readValue(s, GameSettings.class);
		ogs.copy(p);
	}

}
