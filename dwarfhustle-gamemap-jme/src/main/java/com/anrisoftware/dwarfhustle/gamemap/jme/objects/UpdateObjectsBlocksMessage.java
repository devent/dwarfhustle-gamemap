package com.anrisoftware.dwarfhustle.gamemap.jme.objects;

import org.eclipse.collections.api.multimap.Multimap;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateObjectsBlocksMessage extends Message {

    public final GameMap gm;

    public final Multimap<Long, Integer> blocks;

}
