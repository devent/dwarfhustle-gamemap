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

import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetCacheKey;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetCacheObject;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheRetrieveMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheRetrieveMessage.CacheRetrieveResponseMessage;

import akka.actor.typed.javadsl.AskPattern;
import lombok.SneakyThrows;

/**
 * Provides {@link GameAssets}.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class GameAssetsProvider implements Provider<GameAssets> {

    @Inject
    private ActorSystemProvider actor;

    private GameAssets assets;

    @Override
    public GameAssets get() {
        if (assets == null) {
            this.assets = retrieveCache();
        }
        return assets;
    }

    @SneakyThrows
    private GameAssets retrieveCache() {
        var timeout = Duration.ofSeconds(30);
        CompletionStage<CacheRetrieveResponseMessage> result = AskPattern.ask(actor.get(),
                replyTo -> new CacheRetrieveMessage(replyTo, AssetsJcsCacheActor.ID), timeout,
                actor.getActorSystem().scheduler());
        var ret = result.toCompletableFuture().get();
        return new GameAssets() {

            @Override
            public AssetCacheObject get(AssetCacheKey<?> key) {
                return (AssetCacheObject) ret.cache.get(key);
            }
        };
    }

}
