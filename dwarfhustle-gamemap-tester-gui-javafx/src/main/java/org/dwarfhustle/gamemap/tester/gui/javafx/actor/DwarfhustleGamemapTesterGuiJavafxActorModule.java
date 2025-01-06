/*
 * dwarfhustle-gamemap-gui-javafx - GUI in Javafx.
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
package org.dwarfhustle.gamemap.tester.gui.javafx.actor;

import java.util.Map;

import org.dwarfhustle.gamemap.tester.gui.javafx.actor.MaterialsButtonsActor.MaterialsButtonsActorFactory;
import org.dwarfhustle.gamemap.tester.gui.javafx.actor.TesterMainPanelActor.TesterMainPanelActorFactory;
import org.dwarfhustle.gamemap.tester.gui.javafx.actor.TesterStatusActor.TesterStatusActorFactory;
import org.dwarfhustle.gamemap.tester.gui.javafx.controllers.MaterialsButtonsController;
import org.dwarfhustle.gamemap.tester.gui.javafx.controllers.TesterMainPaneController;
import org.dwarfhustle.gamemap.tester.gui.javafx.states.TesterKeyMappingProvider;

import com.anrisoftware.dwarfhustle.gui.javafx.actor.AbstractPaneActor;
import com.anrisoftware.dwarfhustle.gui.javafx.states.JmeMapping;
import com.anrisoftware.dwarfhustle.gui.javafx.states.JmeMappingsProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMapping;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

/**
 * @see TesterMainPanelActorFactory
 * @see TesterStatusActorFactory
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class DwarfhustleGamemapTesterGuiJavafxActorModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder()
				.implement(new TypeLiteral<AbstractPaneActor<? extends TesterMainPaneController>>() {
				}, TesterMainPanelActor.class).build(TesterMainPanelActorFactory.class));
		install(new FactoryModuleBuilder().implement(TesterStatusActor.class, TesterStatusActor.class)
				.build(TesterStatusActorFactory.class));
		install(new FactoryModuleBuilder()
				.implement(new TypeLiteral<AbstractPaneActor<? extends MaterialsButtonsController>>() {
				}, MaterialsButtonsActor.class).build(MaterialsButtonsActorFactory.class));
		bind(new TypeLiteral<Map<String, KeyMapping>>() {
		}).annotatedWith(Names.named("keyMappings")).toProvider(TesterKeyMappingProvider.class).asEagerSingleton();
		bind(new TypeLiteral<Map<String, JmeMapping>>() {
		}).annotatedWith(Names.named("jmeMappings")).toProvider(JmeMappingsProvider.class).asEagerSingleton();
	}

}
