/*
 * dwarfhustle-gamemap-model - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.model.messages;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Changes the material for the block.
 *
 * @author Erwin Müller {@literal <erwin@mullerlpublic.de>}
 */
@RequiredArgsConstructor
@ToString(callSuper = true)
public class SetMaterialBlockMessage extends Message {

    public final KnowledgeObject ko;

    public final GameBlockPos[] pos;

}
