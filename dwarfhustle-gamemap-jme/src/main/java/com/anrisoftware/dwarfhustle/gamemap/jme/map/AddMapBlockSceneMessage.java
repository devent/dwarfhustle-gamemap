package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Message to add {@link MapBlock} to the scene.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@RequiredArgsConstructor
public class AddMapBlockSceneMessage extends Message {

    public final MapBlock mb;
}
