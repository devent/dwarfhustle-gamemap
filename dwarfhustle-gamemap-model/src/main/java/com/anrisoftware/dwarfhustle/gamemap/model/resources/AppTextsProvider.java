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

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import com.anrisoftware.resources.texts.external.Texts;
import com.anrisoftware.resources.texts.external.TextsFactory;

/**
 * Provides the {@link Texts} from {@code AppTexts.properties}
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class AppTextsProvider implements Provider<Texts> {

    private final Texts texts;

    @Inject
    public AppTextsProvider(TextsFactory images) {
        this.texts = images.create("AppTexts");
    }

    @Override
    public Texts get() {
        return texts;
    }

}
