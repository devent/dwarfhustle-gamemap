package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;

import lombok.RequiredArgsConstructor;

/**
 * 
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
public class VegetationAddGrowMessage extends Message {

    public final GameBlockPos cursor;
}
