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

import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;

import akka.actor.typed.ActorRef;
import lombok.ToString;

/**
 * Message to retrieve a {@link TextureCacheObject} with a {@link AssetCacheKey}
 * from the cache.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString(callSuper = true)
public class GetTextureMessage<T extends AssetsResponseMessage<?>> extends AssetsMessage<T> {

    /**
     * Response message that the {@link TextureCacheObject} was retrieved.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @ToString
    public static class GetTextureSuccessMessage<T extends AssetsMessage<?>> extends AssetsResponseMessage<T> {

        public final TextureCacheObject to;

        public GetTextureSuccessMessage(GetTextureMessage<?> om, TextureCacheObject to) {
            super(om);
            this.to = to;
        }
    }

    /**
     * Response message that there was an error retrieving a
     * {@link TextureCacheObject}.
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

    public final long key;

    public final Consumer<GameObject> consumer;

    public GetTextureMessage(ActorRef<T> replyTo, long key) {
        this(replyTo, key, EMPTY_CONSUMER);
    }

    public GetTextureMessage(ActorRef<T> replyTo, long key, Consumer<GameObject> consumer) {
        super(replyTo);
        this.key = key;
        this.consumer = consumer;
    }

}
