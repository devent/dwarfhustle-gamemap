/*
 * Dwarf Hustle Game Map - Game map.
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

import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetKey;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureObject;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;

import akka.actor.typed.ActorRef;
import lombok.ToString;

/**
 * Message to retrieve a {@link TextureObject} with a {@link AssetKey} from
 * the cache.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString(callSuper = true)
public class GetTextureMessage<T extends AssetsResponseMessage<?>> extends AssetsMessage<T> {

    /**
     * Response message that the {@link TextureObject} with the {@link AssetKey}
     * was retrieved.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @ToString
    public static class GetTextureSuccessMessage<T extends AssetsMessage<?>> extends AssetsResponseMessage<T> {

        public final TextureObject to;

        public GetTextureSuccessMessage(GetTextureMessage<?> om, TextureObject to) {
            super(om);
            this.to = to;
        }
    }

    /**
     * Response message that there was an error retrieving a {@link TextureObject}
     * with the {@link AssetKey}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @ToString
    public static class GetTextureErrorMessage<T extends AssetsMessage<?>> extends AssetsResponseMessage<T> {

        private final Throwable e;

        public GetTextureErrorMessage(GetTextureMessage<?> om, Throwable e) {
            super(om);
            this.e = e;
        }
    }

    private final static Consumer<GameObject> EMPTY_CONSUMER = go -> {
    };

    public final AssetKey<?> key;

    public final Consumer<GameObject> consumer;

    public GetTextureMessage(ActorRef<T> replyTo, AssetKey<?> key) {
        this(replyTo, key, EMPTY_CONSUMER);
    }

    public GetTextureMessage(ActorRef<T> replyTo, AssetKey<?> key, Consumer<GameObject> consumer) {
        super(replyTo);
        this.key = key;
        this.consumer = consumer;
    }

}