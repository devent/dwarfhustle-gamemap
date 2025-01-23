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
package com.anrisoftware.dwarfhustle.gamemap.model.cache

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.notNullValue

import java.time.Duration

import org.apache.commons.jcs3.JCS

import com.anrisoftware.dwarfhustle.model.db.cache.JcsCacheConfig

/**
 * Creates application caches configuration based on parameters.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class AppCachesConfig {

    /**
     * Creates the caches configuration.
     */
    def create(File parentDir) {
        assertThat(parentDir, notNullValue())
        def config = new Properties()
        def params = [:]
        params.stored_objects = [:]
        params.stored_objects.cache_name = "stored_objects"
        params.stored_objects.max_objects = 10000
        params.stored_objects.is_eternal = false
        params.stored_objects.max_idle = Duration.ofHours(24).seconds
        params.stored_objects.max_life = Duration.ofHours(24).seconds
        params.stored_objects.max_key_size = 10000
        params.stored_objects.have_file_aux = true
        params.stored_objects.parent_dir = parentDir
        //
        params.map_objects = [:]
        params.map_objects.cache_name = "map_objects"
        params.map_objects.max_objects = 10000
        params.map_objects.is_eternal = false
        params.map_objects.max_idle = Duration.ofHours(24).seconds
        params.map_objects.max_life = Duration.ofHours(24).seconds
        params.map_objects.max_key_size = 10000
        params.map_objects.have_file_aux = true
        params.map_objects.parent_dir = parentDir
        //
        params.knowledge = [:]
        params.knowledge.cache_name = "knowledge"
        params.knowledge.max_objects = 10000
        params.knowledge.is_eternal = true
        params.knowledge.max_idle = Duration.ofHours(24).seconds
        params.knowledge.max_life = Duration.ofHours(24).seconds
        params.knowledge.max_key_size = 10000
        params.knowledge.have_file_aux = true
        params.knowledge.parent_dir = parentDir
        //
        params.chunks = [:]
        params.chunks.cache_name = "chunks"
        params.chunks.max_objects = 108250
        params.chunks.is_eternal = true
        params.chunks.max_idle = Duration.ofHours(24).seconds
        params.chunks.max_life = Duration.ofHours(24).seconds
        params.chunks.max_key_size = 10000
        params.chunks.have_file_aux = false
        params.chunks.parent_dir = parentDir
        //
        JcsCacheConfig.createCaches(config, params)
        JCS.setConfigProperties(config);
    }
}
