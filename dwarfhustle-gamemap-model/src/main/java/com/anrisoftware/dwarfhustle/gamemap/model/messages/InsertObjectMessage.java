/*
 * dwarfhustle-gamemap-model - Game map.
 * Copyright © 2023 Erwin Müller (erwin.mueller@anrisoftware.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.anrisoftware.dwarfhustle.gamemap.model.messages;

import java.util.function.Consumer;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.InsertObjectMessage.InsertObjectSuccessMessage;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject;

import akka.actor.typed.ActorRef;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Inserts the {@link GameMapObject} into the {@link GameMap}.
 *
 * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
 */
@ToString(callSuper = true)
public class InsertObjectMessage<T extends InsertObjectSuccessMessage> extends Message {

    /**
     * 
     * 
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @RequiredArgsConstructor
    @ToString(callSuper = true)
    public static class InsertObjectSuccessMessage extends Message {

        public final GameMapObject go;
    }

    private static final Consumer<GameMapObject> NOP_CONSUMER = (go) -> {
    };

    private final static Runnable NOP = () -> {
    };

    /**
     * Reply to {@link ActorRef}.
     */
    @ToString.Exclude
    public final ActorRef<T> replyTo;

    public final GameMap gm;

    public final int cid;

    public final KnowledgeObject ko;

    public final GameBlockPos pos;

    public final Consumer<GameMapObject> consumer;

    public final Runnable onInserted;

    public InsertObjectMessage(ActorRef<T> replyTo, GameMap gm, int cid, KnowledgeObject ko, GameBlockPos pos,
            Consumer<GameMapObject> consumer, Runnable onInserted) {
        this.replyTo = replyTo;
        this.gm = gm;
        this.cid = cid;
        this.ko = ko;
        this.pos = pos;
        this.consumer = consumer;
        this.onInserted = onInserted;
    }

    public InsertObjectMessage(ActorRef<T> replyTo, GameMap gm, int cid, KnowledgeObject ko, GameBlockPos pos,
            Runnable onInserted) {
        this(replyTo, gm, cid, ko, pos, NOP_CONSUMER, onInserted);
    }

    public InsertObjectMessage(ActorRef<T> replyTo, GameMap gm, int cid, KnowledgeObject ko, GameBlockPos pos,
            Consumer<GameMapObject> consumer) {
        this(replyTo, gm, cid, ko, pos, consumer, NOP);
    }

    public InsertObjectMessage(ActorRef<T> replyTo, GameMap gm, int cid, KnowledgeObject ko, GameBlockPos pos) {
        this(replyTo, gm, cid, ko, pos, NOP_CONSUMER, NOP);
    }
}
