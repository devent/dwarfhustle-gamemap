package com.anrisoftware.dwarfhustle.gamemap.model.messages;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Message that the map tile is under the cursor.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
@ToString(callSuper = true)
public class MapTileUnderCursorMessage extends Message {

    public final int level;

    public final int y;

    public final int x;
}
