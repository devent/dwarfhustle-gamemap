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

import javax.inject.Provider;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ObjectMapperProvider implements Provider<ObjectMapper> {

    private final ObjectMapper mapper;

    public ObjectMapperProvider() {
        this.mapper = createObjectMapper();
    }

    @Override
    public ObjectMapper get() {
        return mapper;
    }

    private ObjectMapper createObjectMapper() {
        @SuppressWarnings("deprecation")
        var module = new SimpleModule("customerSerializationModule", new Version(1, 0, 0, "static version"));
        addCustomDeserializersTo(module);
        addCustomSerializersTo(module);
        var objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.registerModule(module);
        return objectMapper;
    }

    private void addCustomSerializersTo(SimpleModule module) {
    }

    private void addCustomDeserializersTo(SimpleModule module) {
    }

}
