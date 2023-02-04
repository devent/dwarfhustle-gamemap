package com.anrisoftware.dwarfhustle.gamemap.model.messages;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Message to set the {@link GameMap}.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
@ToString(callSuper = true)
public class SetGameMapMessage extends Message {

	public final GameMap gm;
}
