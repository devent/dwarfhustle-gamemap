package com.anrisoftware.dwarfhustle.gamemap.model.messages;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Message that the {@link MapBlock} was loaded.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
@ToString(callSuper = true)
public class MapBlockLoadedMessage extends Message {

	public final MapBlock mb;
}
