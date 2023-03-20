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
public class ObjectsProvider implements Provider<GameObjects<Long, GameObject>> {

    @Inject
    private ActorSystemProvider actor;

    private GameObjects<Long, GameObject> objects;

    @Override
    public GameObjects<Long, GameObject> get() {
        if (objects == null) {
            this.objects = retrieveCache();
        }
        return objects;
    }

    @SneakyThrows
    private GameObjects<Long, GameObject> retrieveCache() {
        var timeout = Duration.ofSeconds(1);
        CompletionStage<CacheRetrieveResponseMessage> result = AskPattern.ask(actor.get(),
                replyTo -> new CacheRetrieveMessage(replyTo, ObjectsJcsCacheActor.ID), timeout,
                actor.getActorSystem().scheduler());
        var ret = result.toCompletableFuture().get();
        return new GameObjects<>() {

            @Override
            public GameObject get(Long key) {
                return ret.cache.get(key);
            }
        };
    }

}
