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
package com.anrisoftware.dwarfhustle.gamemap.console.actor;

import static com.jme3.math.FastMath.DEG_TO_RAD;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.jme3.math.Quaternion;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Event that the rotation of a game object should be updated.
 *
 * @author Erwin Müller
 */
@RequiredArgsConstructor
@ToString(callSuper = true)
public class SetObjectRotationMessage extends Message {

    public static SetObjectRotationMessage fromAngles(String object, long id, float x, float y, float z) {
        var q = new Quaternion().fromAngles(x * DEG_TO_RAD, y * DEG_TO_RAD, z * DEG_TO_RAD);
        return new SetObjectRotationMessage(object, id, q.getX(), q.getY(), q.getZ(), q.getW());
    }

    public final String objectType;

    public final long id;

    public final float x;

    public final float y;

    public final float z;

    public final float w;

}
