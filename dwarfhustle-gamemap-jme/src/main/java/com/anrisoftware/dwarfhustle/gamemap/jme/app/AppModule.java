/*
 * Dwarf Hustle Game Map - Game map.
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

import com.anrisoftware.dwarfhustle.gamemap.console.actor.GamemapConsoleActorModule;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.GamemapConsoleAntlrModule;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GamemapModelModule;
import com.anrisoftware.dwarfhustle.model.actor.ModelActorsModule;
import com.anrisoftware.dwarfhustle.model.api.objects.ApiModule;
import com.anrisoftware.dwarfhustle.model.db.cache.JcsCacheModule;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.OrientDbModule;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.ObjectsDbModule;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerloomModule;
import com.anrisoftware.resources.binary.internal.binaries.BinariesResourcesModule;
import com.anrisoftware.resources.binary.internal.maps.BinariesDefaultMapsModule;
import com.anrisoftware.resources.images.internal.images.ImagesResourcesModule;
import com.anrisoftware.resources.images.internal.mapcached.ResourcesImagesCachedMapModule;
import com.anrisoftware.resources.images.internal.scaling.ResourcesSmoothScalingModule;
import com.anrisoftware.resources.texts.internal.texts.TextsResourcesDefaultModule;
import com.badlogic.ashley.core.Engine;
import com.google.inject.AbstractModule;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class AppModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new ModelActorsModule());
		install(new GamemapConsoleActorModule());
		install(new GamemapConsoleAntlrModule());
		bind(Engine.class).asEagerSingleton();
		install(new GamemapModelModule());
		// Model Modules
		install(new ModelActorsModule());
		install(new ObjectsDbModule());
		install(new PowerloomModule());
		install(new OrientDbModule());
		install(new ApiModule());
		install(new JcsCacheModule());
		// Resources
		install(new ImagesResourcesModule());
		install(new ResourcesImagesCachedMapModule());
		install(new ResourcesSmoothScalingModule());
		install(new TextsResourcesDefaultModule());
		install(new BinariesResourcesModule());
		install(new BinariesDefaultMapsModule());
	}

}
