package com.anrisoftware.dwarfhustle.gamemap.model.messages;

import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Message is send to all actors every game tick.
 *
 * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
 */
@RequiredArgsConstructor
@ToString
public class GameTickMessage extends Message {

    public final long tick;

}
