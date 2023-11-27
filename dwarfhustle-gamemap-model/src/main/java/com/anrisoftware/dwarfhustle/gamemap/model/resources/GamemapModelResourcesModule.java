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

import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Binds the properties and settings providers as singletons. Loads also the
 * application resources from
 * <ul>
 * <li>{@code AppIcons.properties}
 * <li>{@code AppImages.properties}
 * <li>{@code AppTexts.properties}
 * </ul>
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class GamemapModelResourcesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GameSettingsProvider.class).asEagerSingleton();
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).asEagerSingleton();
        bind(Images.class).annotatedWith(Names.named("AppIcons")).toProvider(AppIconsProvider.class).asEagerSingleton();
        bind(Images.class).annotatedWith(Names.named("AppImages")).toProvider(AppImagesProvider.class)
                .asEagerSingleton();
        bind(Texts.class).annotatedWith(Names.named("AppTexts")).toProvider(AppTextsProvider.class).asEagerSingleton();
    }

}
