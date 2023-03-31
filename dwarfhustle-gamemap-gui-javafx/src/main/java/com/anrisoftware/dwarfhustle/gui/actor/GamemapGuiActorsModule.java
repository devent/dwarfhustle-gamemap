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
package com.anrisoftware.dwarfhustle.gui.actor;

import com.anrisoftware.dwarfhustle.gui.actor.GameMainPanelActor.GameMainPanelActorFactory;
import com.anrisoftware.dwarfhustle.gui.actor.InfoPanelActor.InfoPanelActorFactory;
import com.anrisoftware.dwarfhustle.gui.actor.StatusActor.StatusActorFactory;
import com.anrisoftware.dwarfhustle.gui.controllers.InfoPaneController;
import com.anrisoftware.dwarfhustle.gui.controllers.MainPaneController;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class GamemapGuiActorsModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(new TypeLiteral<AbstractPaneActor<? extends MainPaneController>>() {
                }, GameMainPanelActor.class).build(GameMainPanelActorFactory.class));
        install(new FactoryModuleBuilder().implement(StatusActor.class, StatusActor.class)
                .build(StatusActorFactory.class));
        install(new FactoryModuleBuilder()
                .implement(new TypeLiteral<AbstractPaneActor<? extends InfoPaneController>>() {
                }, InfoPanelActor.class).build(InfoPanelActorFactory.class));
    }
}
