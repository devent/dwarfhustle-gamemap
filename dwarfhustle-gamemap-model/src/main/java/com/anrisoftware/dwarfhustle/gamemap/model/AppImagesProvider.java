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

import javax.inject.Inject;
import javax.inject.Provider;

import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.images.external.ImagesFactory;

/**
 * Provides the {@link Images} from {@code AppImages.properties}
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class AppImagesProvider implements Provider<Images> {

    private final Images images;

    @Inject
    public AppImagesProvider(ImagesFactory images) {
        this.images = images.create("AppImages");
    }

    @Override
    public Images get() {
        return images;
    }

}
