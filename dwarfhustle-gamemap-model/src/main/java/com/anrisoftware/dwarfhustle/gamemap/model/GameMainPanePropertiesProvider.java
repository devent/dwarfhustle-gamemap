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

import com.anrisoftware.dwarfhustle.gamemap.model.ObservableGameMainPaneProperties.GameMainPaneProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides the {@link GameMainPaneProperties} and saves/loads the properties
 * from/to file.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class GameMainPanePropertiesProvider implements Provider<ObservableGameMainPaneProperties> {

    private final static String LAST_MAIN_PANE_PROPERTIES_FILE = GameMainPanePropertiesProvider.class.getPackageName()
            + ".last_main_pane_properties_file";

    private final static File DEFAULT_LAST_MAIN_PANE_PROPERTIES_FILE = new File(
			System.getProperty("user.home") + "/.dwarfhustle-gamemap-last.yaml");

    private final GameMainPaneProperties p;

    private final ObservableGameMainPaneProperties op;

    @Inject
    private ObjectMapper mapper;

    public GameMainPanePropertiesProvider() throws IOException {
        this.p = new GameMainPaneProperties();
        this.op = new ObservableGameMainPaneProperties(p);
    }

    @Override
    public ObservableGameMainPaneProperties get() {
        return op;
    }

    @SneakyThrows
    public void save() {
        File file = getFile();
        log.debug("Save properties to {}", file);
        mapper.writeValue(file, p);
    }

    @SneakyThrows
    public void load() {
        var file = getFile();
        if (file.exists()) {
            log.debug("Load properties from {}", file);
            var p = mapper.readValue(file, GameMainPaneProperties.class);
            op.copy(p);
        }
    }

    private File getFile() {
        var argsFile = System.getProperty(LAST_MAIN_PANE_PROPERTIES_FILE);
        if (argsFile != null) {
            return new File(argsFile);
        }
        return DEFAULT_LAST_MAIN_PANE_PROPERTIES_FILE;
    }
}
