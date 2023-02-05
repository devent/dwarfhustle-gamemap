package com.anrisoftware.dwarfhustle.gamemap.model.messages;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapTile;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Message to load the {@link MapBlock} map blocks and {@link MapTile} map
 * tiles.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
@ToString(callSuper = true)
public class LoadMapTilesMessage extends Message {

	public final GameMap gm;
}
