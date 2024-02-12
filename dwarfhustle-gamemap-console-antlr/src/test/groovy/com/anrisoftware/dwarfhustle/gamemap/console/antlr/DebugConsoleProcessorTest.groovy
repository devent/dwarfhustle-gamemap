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

import static org.junit.jupiter.api.Assertions.*
import static org.junit.jupiter.params.provider.Arguments.of
import static org.mockito.Mockito.*

import java.util.stream.Stream

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import com.anrisoftware.dwarfhustle.gamemap.console.actor.OpenSceneMessage
import com.anrisoftware.dwarfhustle.gamemap.console.actor.ParsedLineMessage
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetVisibleDepthLayersMessage
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetTimeWorldMessage
import com.anrisoftware.dwarfhustle.gamemap.console.actor.UnknownLineMessage
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.TypeLiteral

import akka.actor.typed.ActorRef


/**
 * @see DebugConsoleProcessor
 *
 * @author Erwin Müller
 */
class DebugConsoleProcessorTest {

    static Stream parseExpressionsSource() {
        Stream.of(
                //
                of('open scene', { List it ->
                    assertEquals it.size(), 2
                    assertEquals it[0].class, ParsedLineMessage
                    assertEquals it[1].class, OpenSceneMessage
                }),
                //
                of('set time 23:59:59 to world', { List it ->
                    assertEquals it.size(), 2
                    assertEquals it[0].class, ParsedLineMessage
                    assertEquals it[1].class, SetTimeWorldMessage
                    assertEquals it[1].time.hour, 23
                    assertEquals it[1].time.minute, 59
                    assertEquals it[1].time.second, 59
                }),
                //
                of('set xxxx 4 to terrain', { List it ->
                    assertEquals it.size(), 1
                    assertEquals it[0].class, UnknownLineMessage
                }),
                //
                of('set layers 4 to terrain', { List it ->
                    assertEquals it.size(), 2
                    assertEquals it[0].class, ParsedLineMessage
                    assertEquals it[1].class, SetVisibleDepthLayersMessage
                    assertEquals it[1].layers, 4
                }),
                //
                )
    }

    @ParameterizedTest
    @MethodSource("parseExpressionsSource")
    void process_expressions_to_message(String str, def checkMessage) {
        def actor =  mock(ActorRef.class)
        def messages = []
        when(actor.tell(any())).then({
            messages << it.getArgument(0)
        })
        def injector = Guice.createInjector(new GamemapConsoleAntlrModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(new TypeLiteral<ActorRef<Message>>() {
                                }).toInstance(actor)
                    }
                })
        def service = injector.getInstance(DebugConsoleProcessor)
        service.process(str)
        checkMessage(messages)
    }
}
