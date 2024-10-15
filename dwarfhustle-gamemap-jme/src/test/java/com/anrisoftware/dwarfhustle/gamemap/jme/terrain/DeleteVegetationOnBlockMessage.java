package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import org.eclipse.collections.api.set.ImmutableSet;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;

import lombok.RequiredArgsConstructor;

/**
 * 
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
public class DeleteVegetationOnBlockMessage extends Message {

    public final GameBlockPos cursor;

    public final ImmutableSet<String> types;

}
