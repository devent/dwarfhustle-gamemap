package com.anrisoftware.dwarfhustle.gamemap.model.messages;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Message to set the {@link WorldMap}.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
@ToString(callSuper = true)
public class SetWorldMapMessage extends Message {

	public final WorldMap wm;
}
