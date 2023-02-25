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

import java.util.Optional;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.console.actor.AddModelObjectHereMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.AddModelObjectMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.ApplyImpulseModelMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.ConsoleProcessor;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.ParsedLineMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetCameraPositionMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetLayersTerrainMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetMarkCoordinatesMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetMarkScaleMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectCoordinatesMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectPanningVelocityMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectPositionMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectRotationMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectScaleMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetTilesRotationMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.UnknownLineMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParserService.DebugConsoleParserServiceFactory;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParserService.TupelXyz;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;

import akka.actor.typed.ActorRef;
import lombok.RequiredArgsConstructor;

/**
 * Processes one line in the language parser and creates messages.
 *
 * @author Erwin Müller
 */
public class DebugConsoleProcessor implements ConsoleProcessor {

    @Inject
    private DebugConsoleParserServiceFactory parserFactory;

    @Inject
    private ActorRef<Message> actor;

    /**
     * Process the line in the language parser and creates messages.
     */
    @Override
    public void process(String line) {
        var parser = parserFactory.create();
        parser.parse(line);
        new ParseProcess(parser).parseVerb().ifPresentOrElse(m -> {
            actor.tell(new ParsedLineMessage(line));
            actor.tell(m);
        }, () -> {
            actor.tell(new UnknownLineMessage(line));
        });
    }

    @RequiredArgsConstructor
    private static class ParseProcess {

        private final DebugConsoleParserService parser;

        private Optional<Message> parseVerb() {
            return parser.verb.map(this::parseVerb);
        }

        private Message parseVerb(String verb) {
            switch (verb) {
            case "set":
                return parser.object.map(this::parseSetObject).orElse(null);
            case "add":
                return parser.object.map(this::parseAddForName).orElse(null);
            case "apply":
                return parser.object.map(this::parseApply).orElse(null);
            default:
                return null;
            }
        }

        private Message parseSetObject(String object) {
            switch (object) {
            case "tiles":
                return parseSetForTiles(object);
            case "mark":
                return parseSetForMark(object);
            default:
                return parseSetForObject(object);
            }
        }

        private Message parseApply(String object) {
            return parser.physics.map(this::parseApplyPhysics).orElse(null);
        }

        private Message parseApplyPhysics(String physics) {
            switch (physics) {
            case "impulse":
                return parseApplyImpulse();
            default:
                return null;
            }
        }

        private Message parseApplyImpulse() {
            return parser.getTupelXyzVxVyVz().map(t -> new ApplyImpulseModelMessage(parser.objectType.get(),
                    parser.id.get(), t.vx, t.vy, t.vz, t.x, t.y, t.z)).orElse(null);
        }

        private Message parseAddForName(String object) {
            return parser.objectType.map(this::parseAddForObjectType).orElse(null);
        }

        Message parseAddForObjectType(String objectType) {
            Optional<TupelXyz> t = parser.getTupelXyz();
            if (t.isEmpty()) {
                return new AddModelObjectHereMessage(objectType);
            } else {
                return new AddModelObjectMessage(objectType, (int) t.get().x, (int) t.get().y, (int) t.get().z);
            }
        }

        private Message parseSetForObject(String object) {
            return parser.property.map(this::parseSetForObjectProperty).orElse(null);
        }

        Message parseSetForObjectProperty(String property) {
            switch (property) {
            case "coordinates":
                switch (parser.object.get()) {
                case "tile":
                    return null;
                default:
                    return parser.getTupelXyzXxYyZz().map(t -> new SetObjectCoordinatesMessage(parser.object.get(),
                            parser.id.get(), (int) t.x, (int) t.y, (int) t.z, t.xx, t.yy, t.zz)).orElse(null);
                }
            case "rotation":
                switch (parser.object.get()) {
                case "tile":
                    return null;
                default:
                    return parser.getTupelObjectIdXyz()
                            .map(t -> SetObjectRotationMessage.fromAngles(t.object, t.id, t.x, t.y, t.z)).orElse(null);
                }
            case "scale":
                switch (parser.object.get()) {
                case "tile":
                    return null;
                default:
                    return parser.getTupelXyz()
                            .map(t -> new SetObjectScaleMessage(parser.object.get(), parser.id.get(), t.x, t.y, t.z))
                            .orElse(null);
                }
            case "position":
                switch (parser.object.get()) {
                case "tile":
                    return null;
                case "camera":
                    return parser.getTupelXyz().map(t -> new SetCameraPositionMessage(t.x, t.y, t.z)).orElse(null);
                default:
                    return parser.getTupelObjectIdXyz()
                            .map(t -> new SetObjectPositionMessage(t.object, t.id, t.x, t.y, t.z)).orElse(null);
                }
            case "panningVelocity":
                switch (parser.object.get()) {
                case "tile":
                    return null;
                default:
                    return parser.getTupelObjectIdXyz()
                            .map(t -> new SetObjectPanningVelocityMessage(t.object, t.id, t.x, t.y, t.z)).orElse(null);
                }
            case "layers":
                switch (parser.object.get()) {
                case "terrain":
                    return parser.layers.map(SetLayersTerrainMessage::new).orElse(null);
                default:
                    return null;
                }
            default:
                return null;
            }
        }

        private Message parseSetForTiles(String object) {
            switch (parser.property.get()) {
            case "coordinates":
                return null;
            case "rotation":
                switch (object) {
                case "tile":
                    return parser.getTupelXyz().map(t -> SetTilesRotationMessage.fromAngles(t.x, t.y, t.z))
                            .orElse(null);
                default:
                    return null;
                }
            default:
                return null;
            }
        }

        private Message parseSetForMark(String object) {
            switch (parser.property.get()) {
            case "coordinates":
                return parser.getTupelXyz().map(t -> new SetMarkCoordinatesMessage(t.x, t.y, t.z)).orElse(null);
            case "scale":
                return parser.getTupelXyz().map(t -> new SetMarkScaleMessage(t.x, t.y, t.z)).orElse(null);
            default:
                return null;
            }
        }

    }

}
