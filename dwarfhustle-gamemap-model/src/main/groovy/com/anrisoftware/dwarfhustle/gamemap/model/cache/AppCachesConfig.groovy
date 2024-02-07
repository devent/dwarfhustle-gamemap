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
        def params = [objects: [:], assets_material: [:], assets_models: [:], knowledge: [:]]
        params.objects.cache_name = "objects"
        params.objects.max_objects = 10000
        params.objects.is_eternal = false
        params.objects.max_idle = Duration.ofHours(24).seconds
        params.objects.max_life = Duration.ofHours(24).seconds
        params.objects.max_key_size = 10000
        params.objects.have_file_aux = true
        params.objects.parent_dir = parentDir
        //
        params.assets_material.cache_name = "assets-material"
        params.assets_material.max_objects = 1000
        params.assets_material.is_eternal = false
        params.assets_material.max_idle = Duration.ofHours(24).seconds
        params.assets_material.max_life = Duration.ofHours(24).seconds
        params.assets_material.max_key_size = 1000
        params.assets_material.have_file_aux = true
        params.assets_material.parent_dir = parentDir
        //
        params.assets_models.cache_name = "assets-models"
        params.assets_models.max_objects = 1000
        params.assets_models.is_eternal = false
        params.assets_models.max_idle = Duration.ofHours(24).seconds
        params.assets_models.max_life = Duration.ofHours(24).seconds
        params.assets_models.max_key_size = 1000
        params.assets_models.have_file_aux = true
        params.assets_models.parent_dir = parentDir
        //
        params.knowledge.cache_name = "knowledge"
        params.knowledge.max_objects = 1000
        params.knowledge.is_eternal = false
        params.knowledge.max_idle = Duration.ofHours(24).seconds
        params.knowledge.max_life = Duration.ofHours(24).seconds
        params.knowledge.max_key_size = 1000
        params.knowledge.have_file_aux = true
        params.knowledge.parent_dir = parentDir
        //
        JcsCacheConfig.createCaches(config, params)
        JCS.setConfigProperties(config);
    }
}
