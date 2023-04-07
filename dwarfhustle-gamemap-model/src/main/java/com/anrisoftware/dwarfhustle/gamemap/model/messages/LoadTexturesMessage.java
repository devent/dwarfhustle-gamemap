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

import akka.actor.typed.ActorRef;
import lombok.ToString;

/**
 * Message to retrieve textures.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString(callSuper = true)
public class LoadTexturesMessage<T extends AssetsResponseMessage<?>> extends AssetsMessage<T> {

    /**
     * Response message that textures are loaded.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @ToString
    public static class LoadTexturesSuccessMessage<T extends AssetsMessage<?>> extends AssetsResponseMessage<T> {

        public LoadTexturesSuccessMessage(LoadTexturesMessage<?> om) {
            super(om);
        }
    }

    /**
     * Response message that there was an error loading textures.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @ToString
    public static class LoadTexturesErrorMessage<T extends AssetsMessage<?>> extends AssetsResponseMessage<T> {

        public final Throwable e;

        public LoadTexturesErrorMessage(LoadTexturesMessage<?> om, Throwable e) {
            super(om);
            this.e = e;
        }
    }

    public LoadTexturesMessage(ActorRef<T> replyTo) {
        super(replyTo);
    }

}
