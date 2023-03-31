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

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.GameTickMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.badlogic.ashley.systems.IntervalSystem;

import akka.actor.typed.ActorRef;

/**
 * Sends the GameTickMessage in regular intervals.
 *
 * @author Erwin Müller
 */
public class GameTickSystem extends IntervalSystem {

    @Inject
    private ActorRef<Message> actor;

    private long tick = 0;

    @Inject
    public GameTickSystem(GameSettingsProvider gs) {
        super(gs.get().timeUpdateInterval.get());
    }

    @Override
    protected void updateInterval() {
        actor.tell(new GameTickMessage(tick++));
    }

}
