/*
 * dwarfhustle-gamemap-console-antlr - Console debug commands defined in ANTLR 4.
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
package com.anrisoftware.dwarfhustle.gamemap.console.antlr;

import com.anrisoftware.dwarfhustle.gamemap.console.actor.ConsoleProcessor;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParserService.DebugConsoleParserServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @see DebugConsoleProcessor
 * @see DebugConsoleParserServiceFactory
 *
 * @author Erwin Müller
 */
public class GamemapConsoleAntlrModule extends AbstractModule {

    @Override
    protected void configure() {
		bind(ConsoleProcessor.class).to(DebugConsoleProcessor.class);
        install(new FactoryModuleBuilder().implement(DebugConsoleParserService.class, DebugConsoleParserService.class)
                .build(DebugConsoleParserServiceFactory.class));
    }
}
