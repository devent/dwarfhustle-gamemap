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

import static com.jme3.math.FastMath.DEG_TO_RAD;
import static java.util.Optional.of;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.anrisoftware.dwarfhustle.gamemap.console.actor.AddModelObjectHereMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.AddModelObjectMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.ApplyImpulseModelMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.ConsoleProcessor;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.ParsedLineMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetMarkCoordinatesMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetMarkScaleMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectCoordinatesMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectPanningVelocityMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectPositionMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectRotationMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectScaleMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetTilesRotationMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.UnknownLineMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParserService.DebugParserServiceFactory;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.jme3.math.Quaternion;

import akka.actor.typed.ActorRef;

/**
 * Processes one line in the language parser and creates messages.
 *
 * @author Erwin Müller
 */
public class DebugConsoleProcessor implements ConsoleProcessor {

    @Inject
    private DebugParserServiceFactory parserFactory;

    @Inject
    private ActorRef<Message> actor;

    /**
     * Process the line in the language parser and creates messages.
     */
    @Override
    public void process(String line) {
        var parser = parserFactory.create();
        parser.parse(line);
        parseVerb(parser, Optional.empty()).ifPresentOrElse(m -> {
            actor.tell(new ParsedLineMessage(line));
            actor.tell(m);
        }, () -> {
            actor.tell(new UnknownLineMessage(line));
        });
    }

    private Optional<Message> parseVerb(DebugConsoleParserService parser, Optional<Message> o) {
        if (parser.verb == null) {
            return o;
        }
        switch (parser.verb) {
        case "set":
            switch (parser.object) {
            case "tiles":
                return parseSetForTiles(parser, o);
            case "mark":
                return parseSetForMark(parser, o);
            default:
                return parseSetForObject(parser, o);
            }
        case "add":
            return parseAddForName(parser, o);
        case "apply":
            return parseApply(parser, o);
        }
        return o;
    }

    private Optional<Message> parseApply(DebugConsoleParserService parser, Optional<Message> o) {
        if (StringUtils.isBlank(parser.physics)) {
            return o;
        }
        switch (parser.physics) {
        case "impulse":
            return parseApplyImpulse(parser, o);
        default:
            return o;
        }
    }

    private Optional<Message> parseApplyImpulse(DebugConsoleParserService parser, Optional<Message> o) {
        if (parser.z == null || parser.y == null || parser.x == null) {
            parser.x = 0f;
            parser.y = 0f;
            parser.z = 0f;
        }
        return of(new ApplyImpulseModelMessage(parser.objectType, parser.id, parser.vx, parser.vy, parser.vz, parser.x,
                parser.y, parser.z));
    }

    private Optional<Message> parseAddForName(DebugConsoleParserService parser, Optional<Message> o) {
        if (StringUtils.isBlank(parser.objectType)) {
            return o;
        }
        if (parser.z == null || parser.y == null || parser.x == null) {
            return of(new AddModelObjectHereMessage(parser.objectType));
        } else {
            return of(new AddModelObjectMessage(parser.objectType, parser.z.intValue(), parser.y.intValue(),
                    parser.x.intValue()));
        }
    }

    private Optional<Message> parseSetForObject(DebugConsoleParserService parser, Optional<Message> o) {
        switch (parser.property) {
        case "coordinates":
            switch (parser.object) {
            case "tile":
                return o;
            default:
                return of(new SetObjectCoordinatesMessage(parser.object, parser.id, parser.z.intValue(),
                        parser.y.intValue(), parser.x.intValue(), parser.z, parser.y, parser.x));
            }
        case "rotation":
            switch (parser.object) {
            case "tile":
                return o;
            default:
                return parseRotationObject(parser);
            }
        case "scale":
            switch (parser.object) {
            case "tile":
                return o;
            default:
                if (parser.x != null || parser.y != null || parser.z != null) {
                    return of(new SetObjectScaleMessage(parser.object, parser.id, parser.x, parser.y, parser.z));
                }
            }
        case "position":
            switch (parser.object) {
            case "tile":
                return o;
            default:
                if (parser.x != null || parser.y != null || parser.z != null) {
                    return of(new SetObjectPositionMessage(parser.object, parser.id, parser.x, parser.y, parser.z));
                }
            }
        case "panningVelocity":
            switch (parser.object) {
            case "tile":
                return o;
            default:
                if (parser.x != null || parser.y != null || parser.z != null) {
                    return of(new SetObjectPanningVelocityMessage(parser.object, parser.id, parser.x, parser.y,
                            parser.z));
                }
            }
        }
        return o;
    }

    private Optional<Message> parseRotationObject(DebugConsoleParserService parser) {
        var q = new Quaternion().fromAngles(parser.x * DEG_TO_RAD, parser.y * DEG_TO_RAD, parser.z * DEG_TO_RAD);
        return of(new SetObjectRotationMessage(parser.object, parser.id, q.getX(), q.getY(), q.getZ(), q.getW()));
    }

    private Optional<Message> parseSetForTiles(DebugConsoleParserService parser, Optional<Message> o) {
        switch (parser.property) {
        case "coordinates":
            return o;
        case "rotation":
            switch (parser.object) {
            case "tile":
                return of(new SetTilesRotationMessage(parser.x * DEG_TO_RAD, parser.y * DEG_TO_RAD,
                        parser.z * DEG_TO_RAD));
            default:
                return o;
            }
        }
        return o;
    }

    private Optional<Message> parseSetForMark(DebugConsoleParserService parser, Optional<Message> o) {
        switch (parser.property) {
        case "coordinates":
            return of(new SetMarkCoordinatesMessage(parser.x, parser.y, parser.z));
        case "scale":
            return of(new SetMarkScaleMessage(parser.x, parser.y, parser.z));
        }
        return o;
    }

}
