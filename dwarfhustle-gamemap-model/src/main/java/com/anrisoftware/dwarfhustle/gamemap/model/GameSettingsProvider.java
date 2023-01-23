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

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;

import com.anrisoftware.dwarfhustle.gamemap.model.ObservableGameSettings.GameSettings;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
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

	private final GameSettings p;

	private final ObservableGameSettings op;

	@Inject
	private ObjectMapper mapper;

	public GameSettingsProvider() throws IOException {
		this.p = new GameSettings();
		this.op = new ObservableGameSettings(p);
	}

	@Override
	public ObservableGameSettings get() {
		return op;
	}

	@SneakyThrows
	public void save() {
		File file = getFile();
		log.debug("Save settings to {}", file);
		mapper.writeValue(file, p);
	}

	@SneakyThrows
	public void load() {
		var file = getFile();
		if (file.exists()) {
			log.debug("Load settings from {}", file);
			var p = mapper.readValue(file, GameSettings.class);
			p.commandsSet = new TreeSortedSet<>(p.commandsSet);
			op.copy(p);
		}
	}

	private File getFile() {
		var argsFile = System.getProperty(GAME_SETTINGS_FILE);
		if (argsFile != null) {
			return new File(argsFile);
		}
		return DEFAULT_GAME_SETTINGS_FILE;
	}
}
