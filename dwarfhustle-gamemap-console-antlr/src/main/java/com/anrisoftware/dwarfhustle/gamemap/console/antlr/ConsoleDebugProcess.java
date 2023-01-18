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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Scanner;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.anrisoftware.dwarfhustle.gamemap.console.actor.AddModelObjectHereMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.AddModelObjectMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.ApplyImpulseModelMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetMarkCoordinatesMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetMarkScaleMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectCoordinatesMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectPanningVelocityMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectPositionMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectRotationMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetObjectScaleMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.actor.SetTilesRotationMessage;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParserService.DebugParserServiceFactory;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.google.inject.assistedinject.Assisted;
import com.jme3.math.Quaternion;

import akka.actor.typed.ActorRef;
import lombok.SneakyThrows;

/**
 * Waits for user input and sends debug console events.
 *
 * @author Erwin Müller
 */
public class ConsoleDebugProcess implements Runnable {

	@Inject
	private DebugParserServiceFactory parserFactory;

	@Inject
	@Assisted
	private ActorRef<Message> actor;

	private boolean running;

	private DebugConsoleParserService parser;

	public ConsoleDebugProcess() {
		this.running = true;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	@SneakyThrows
	public void run() {
		var br = new BufferedReader(new InputStreamReader(System.in));
		var scan = new Scanner(br);
		try {
			while (running) {
				while (!br.ready()) {
					if (!running) {
						return;
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						running = false;
					}
				}
				String readLine = scan.nextLine();
				this.parser = parserFactory.create();
				parser.parse(readLine);
				parseVerb(Optional.empty()).ifPresent(m -> actor.tell(m));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scan.close();
		}
	}

	private Optional<Message> parseVerb(Optional<Message> o) {
		if (parser.verb == null) {
			return o;
		}
		switch (parser.verb) {
		case "set":
			switch (parser.object) {
			case "tiles":
				return parseSetForTiles(o);
			case "mark":
				return parseSetForMark(o);
			default:
				return parseSetForObject(o);
			}
		case "add":
			return parseAddForName(o);
		case "apply":
			return parseApply(o);
		}
		return o;
	}

	private Optional<Message> parseApply(Optional<Message> o) {
		if (StringUtils.isBlank(parser.physics)) {
			return o;
		}
		switch (parser.physics) {
		case "impulse":
			return parseApplyImpulse(o);
		default:
			return o;
		}
	}

	private Optional<Message> parseApplyImpulse(Optional<Message> o) {
		if (parser.zz == null || parser.yy == null || parser.xx == null) {
			parser.xx = 0f;
			parser.yy = 0f;
			parser.zz = 0f;
		}
		return of(new ApplyImpulseModelMessage(parser.objectType, parser.id, parser.vx, parser.vy, parser.vz, parser.xx,
				parser.yy, parser.zz));
	}

	private Optional<Message> parseAddForName(Optional<Message> o) {
		if (StringUtils.isBlank(parser.objectType)) {
			return o;
		}
		if (parser.z == null || parser.y == null || parser.x == null) {
			return of(new AddModelObjectHereMessage(parser.objectType));
		} else {
			return of(new AddModelObjectMessage(parser.objectType, parser.z, parser.y, parser.x));
		}
	}

	private Optional<Message> parseSetForObject(Optional<Message> o) {
		switch (parser.property) {
		case "coordinates":
			switch (parser.object) {
			case "tile":
				return o;
			default:
				return of(new SetObjectCoordinatesMessage(parser.object, parser.id, parser.z, parser.y, parser.x,
						parser.zz, parser.yy, parser.xx));
			}
		case "rotation":
			switch (parser.object) {
			case "tile":
				return o;
			default:
				return parseRotationObject();
			}
		case "scale":
			switch (parser.object) {
			case "tile":
				return o;
			default:
				if (parser.xx != null || parser.yy != null || parser.zz != null) {
					return of(new SetObjectScaleMessage(parser.object, parser.id, parser.xx, parser.yy, parser.zz));
				}
			}
		case "position":
			switch (parser.object) {
			case "tile":
				return o;
			default:
				if (parser.xx != null || parser.yy != null || parser.zz != null) {
					return of(new SetObjectPositionMessage(parser.object, parser.id, parser.xx, parser.yy, parser.zz));
				}
			}
		case "panningVelocity":
			switch (parser.object) {
			case "tile":
				return o;
			default:
				if (parser.xx != null || parser.yy != null || parser.zz != null) {
					return of(new SetObjectPanningVelocityMessage(parser.object, parser.id, parser.xx, parser.yy,
							parser.zz));
				}
			}
		}
		return o;
	}

	private Optional<Message> parseRotationObject() {
		var q = new Quaternion().fromAngles(parser.xx * DEG_TO_RAD, parser.yy * DEG_TO_RAD, parser.zz * DEG_TO_RAD);
		return of(new SetObjectRotationMessage(parser.object, parser.id, q.getX(), q.getY(), q.getZ(), q.getW()));
	}

	private Optional<Message> parseSetForTiles(Optional<Message> o) {
		switch (parser.property) {
		case "coordinates":
			return o;
		case "rotation":
			switch (parser.object) {
			case "tile":
				return of(new SetTilesRotationMessage(parser.xx * DEG_TO_RAD, parser.yy * DEG_TO_RAD,
						parser.zz * DEG_TO_RAD));
			default:
				return o;
			}
		}
		return o;
	}

	private Optional<Message> parseSetForMark(Optional<Message> o) {
		switch (parser.property) {
		case "coordinates":
			return of(new SetMarkCoordinatesMessage(parser.xx, parser.yy, parser.zz));
		case "scale":
			return of(new SetMarkScaleMessage(parser.xx, parser.yy, parser.zz));
		}
		return o;
	}

}
