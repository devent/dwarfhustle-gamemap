/*
 * dwarfhustle-gamemap-jme - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.jme.app;

import com.anrisoftware.dwarfhustle.gamemap.jme.app.GameTickActor.GameTickActorFactory;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.GameTimeActor.GameTimeActorFactory;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.MaterialAssetsCacheActor.MaterialAssetsJcsCacheActorFactory;
import com.anrisoftware.dwarfhustle.gamemap.jme.app.ModelsAssetsCacheActor.ModelsAssetsJcsCacheActorFactory;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class DwarfhustleGamemapJmeAppModule extends AbstractModule {

    @Override
    protected void configure() {
        // install(new FactoryModuleBuilder().implement(AppActor.class,
        // AppActor.class).build(AppActorFactory.class));
        install(new FactoryModuleBuilder().implement(MaterialAssetsCacheActor.class, MaterialAssetsCacheActor.class)
                .build(MaterialAssetsJcsCacheActorFactory.class));
        install(new FactoryModuleBuilder().implement(ModelsAssetsCacheActor.class, ModelsAssetsCacheActor.class)
                .build(ModelsAssetsJcsCacheActorFactory.class));
        install(new FactoryModuleBuilder().implement(GameTickActor.class, GameTickActor.class)
                .build(GameTickActorFactory.class));
        install(new FactoryModuleBuilder().implement(GameTimeActor.class, GameTimeActor.class)
                .build(GameTimeActorFactory.class));
        // install(new FactoryModuleBuilder().implement(LoadGameMapActor.class,
        // LoadGameMapActor.class)
        // .build(LoadGameMapActorFactory.class));
    }
}
