package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import java.util.function.Function;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;

import lombok.RequiredArgsConstructor;

/**
 * 
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
public class AddObjectOnBlockMessage extends Message {

    public final GameBlockPos cursor;

    public final String type;

    public final String name;

    public final Function<MapBlock, Boolean> validBlock;
}
