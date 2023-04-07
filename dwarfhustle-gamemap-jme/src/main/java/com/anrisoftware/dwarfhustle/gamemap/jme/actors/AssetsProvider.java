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
package com.anrisoftware.dwarfhustle.gamemap.jme.actors;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Provider;

import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObjects;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheRetrieveMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheRetrieveMessage.CacheRetrieveResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.ObjectsJcsCacheActor;

import akka.actor.typed.javadsl.AskPattern;
import lombok.SneakyThrows;

/**
 * Provides the {@link GameObjects} for id := {@link GameObject} backend.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class AssetsProvider implements Provider<GameObjects> {

    @Inject
    private ActorSystemProvider actor;

    private GameObjects objects;

    @Override
    public GameObjects get() {
        if (objects == null) {
            this.objects = retrieveCache();
        }
        return objects;
    }

    @SneakyThrows
    private GameObjects retrieveCache() {
        var timeout = Duration.ofSeconds(1);
        CompletionStage<CacheRetrieveResponseMessage> result = AskPattern.ask(actor.get(),
                replyTo -> new CacheRetrieveMessage(replyTo, ObjectsJcsCacheActor.ID), timeout,
                actor.getActorSystem().scheduler());
        var ret = result.toCompletableFuture().get();
        return new GameObjects() {

            @Override
            public GameObject get(long key) {
                return ret.cache.get(key);
            }
        };
    }

}
