/*
 * dwarfhustle-gamemap-jme - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.jme.app;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;

import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameChunkPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage.CacheErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DbMessage.DbErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.DbMessage.DbResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.LoadObjectMessage.LoadObjectNotFoundMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.LoadObjectsMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.LoadObjectsMessage.LoadObjectsEmptyMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.LoadObjectsMessage.LoadObjectsSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.actor.OrientDbActor;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Loads the entire {@link GameMap}, the {@link MapChunk}s and {@link MapBlock}s
 * from the database and puts them into the objects cache.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class LoadGameMapActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            LoadGameMapActor.class.getSimpleName());

    public static final String NAME = LoadGameMapActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    public static class LoadGameMapToCacheMessage<T extends LoadGameMapToCacheResponseMessage> extends Message {
        public final ActorRef<T> replyTo;
        public final GameMap gm;
        public final MapChunk chunk;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    public static class LoadGameMapToCacheResponseMessage extends Message {
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    public static class LoadGameMapToCacheFinishedMessage extends LoadGameMapToCacheResponseMessage {
    }

    @RequiredArgsConstructor
    @ToString
    private static class WrappedDbResponse extends Message {
        private final DbResponseMessage<?> response;
    }

    @RequiredArgsConstructor
    @ToString
    private static class WrappedCacheResponse extends Message {
        private final CacheResponseMessage<?> response;
    }

    /**
     * Factory to create {@link LoadGameMapActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface LoadGameMapActorFactory {
        LoadGameMapActor create(ActorContext<Message> context, ObjectsSetter os);
    }

    /**
     * Creates the {@link LoadGameMapActor}.
     *
     * @param os
     */
    @SuppressWarnings("unchecked")
    public static Behavior<Message> create(Injector injector, CompletionStage<ObjectsSetter> os) {
        return Behaviors.setup(context -> {
            return (Behavior<Message>) injector.getInstance(LoadGameMapActorFactory.class)
                    .create(context, os.toCompletableFuture().get()).start(injector);
        });
    }

    /**
     * Creates the {@link LoadGameMapActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
            CompletionStage<ObjectsSetter> os) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, os));
    }

    @Inject
    @Assisted
    private ActorContext<Message> context;

    @Inject
    @Assisted
    private ObjectsSetter os;

    @Inject
    private ActorSystemProvider a;

    private ActorRef<Message> dbActor;

    @SuppressWarnings("rawtypes")
    private ActorRef<DbResponseMessage> dbAdapter;

    private AtomicInteger currentChunksLoaded = new AtomicInteger(0);

    private int chunksCount;

    private ActorRef<LoadGameMapToCacheResponseMessage> replyTo;

    private MutableList<GameObject> toCache;

    /**
     * @return {@link Behavior} from {@link #getInitialBehavior()}
     */
    @SneakyThrows
    public Behavior<? extends Message> start(Injector injector) {
        this.toCache = Lists.mutable.empty();
        this.toCache = this.toCache.asSynchronized();
        this.dbActor = a.getActorAsync(OrientDbActor.ID).toCompletableFuture().get(30, SECONDS);
        this.dbAdapter = context.messageAdapter(DbResponseMessage.class, WrappedDbResponse::new);
        return getInitialBehavior() //
                .build();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Behavior<Message> onLoadGameMapToCache(LoadGameMapToCacheMessage m) {
        this.chunksCount = m.gm.chunksCount + m.gm.blocksCount - 1;
        this.replyTo = m.replyTo;
        if (currentChunksLoaded.get() == chunksCount) {
            m.replyTo.tell(new LoadGameMapToCacheFinishedMessage());
            return Behaviors.stopped();
        } else {
            retrieveChunksBlocks(m);
            return Behaviors.same();
        }
    }

    private void retrieveChunksBlocks(LoadGameMapToCacheMessage<?> m) {
        int w = m.chunk.getPos().getSizeX() / 2;
        int h = m.chunk.getPos().getSizeY() / 2;
        int d = m.chunk.getPos().getSizeZ() / 2;
        if (m.chunk.blocks.isEmpty()) {
            dbActor.tell(new LoadObjectsMessage<>(dbAdapter, MapChunk.class, MapChunk.OBJECT_TYPE, go -> {
                currentChunksLoaded.incrementAndGet();
                context.getSelf().tell(new LoadGameMapToCacheMessage<>(m.replyTo, m.gm, (MapChunk) go));
            }, db -> {
                return createQueryChunks(db, m.chunk.map, m.chunk.getPos(), w, h, d);
            }));
        } else {
            dbActor.tell(new LoadObjectsMessage<>(dbAdapter, MapBlock.class, MapBlock.OBJECT_TYPE, go -> {
                currentChunksLoaded.incrementAndGet();
            }, db -> createQueryBlocks(db, m.chunk.id)));
        }
    }

    private OResultSet createQueryChunks(ODatabaseDocument db, long map, GameChunkPos p, int w, int h, int d) {
        var query = "SELECT * from ? where map = ? and sx >= ? and sy >= ? and sz >= ? and ex <= ? and ey <= ? and ez <= ? and (ex - sx = ?) and (ey - sy = ?) and (ez - sz = ?)";
        return db.query(query, MapChunk.OBJECT_TYPE, map, p.x, p.y, p.z, p.ep.x, p.ep.y, p.ep.z, w, h, d);
    }

    private OResultSet createQueryBlocks(ODatabaseDocument db, long chunk) {
        var query = "SELECT * from ? where chunk = ?";
        return db.query(query, MapBlock.OBJECT_TYPE, chunk);
    }

    /**
     * Reacts to the {@link WrappedObjectsResponse} message from the objects actor.
     */
    @SneakyThrows
    private Behavior<Message> onWrappedDbResponse(WrappedDbResponse m) {
        var response = m.response;
        if (response instanceof DbErrorMessage<?> rm) {
            log.error("Db error", rm);
            return Behaviors.stopped();
        } else if (response instanceof LoadObjectNotFoundMessage rm) {
            log.error("Object not found", rm);
            return Behaviors.stopped();
        } else if (response instanceof LoadObjectsEmptyMessage rm) {
            log.error("Objects not found", rm);
            return Behaviors.stopped();
        } else if (response instanceof LoadObjectsSuccessMessage rm) {
            if (currentChunksLoaded.get() == chunksCount) {
                replyTo.tell(new LoadGameMapToCacheFinishedMessage());
                return Behaviors.stopped();
            }
        }
        return Behaviors.same();
    }

    /**
     * Reacts to the {@link WrappedCacheResponse} message from the objects actor.
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onWrappedCacheResponse(WrappedCacheResponse m) {
        var response = m.response;
        if (response instanceof CacheErrorMessage<?> rm) {
            log.error("Cache error", rm);
            return Behaviors.stopped();
        }
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onShutdown(ShutdownMessage m) {
        return Behaviors.stopped();
    }

    /**
     * Returns a behavior for the messages:
     *
     * <ul>
     * <li>{@link LoadGameMapToCacheMessage}
     * <li>{@link ShutdownMessage}
     * <li>{@link WrappedDbResponse}
     * <li>{@link WrappedCacheResponse}
     * </ul>
     */
    private BehaviorBuilder<Message> getInitialBehavior() {
        return Behaviors.receive(Message.class)//
                .onMessage(LoadGameMapToCacheMessage.class, this::onLoadGameMapToCache)//
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(WrappedDbResponse.class, this::onWrappedDbResponse)//
                .onMessage(WrappedCacheResponse.class, this::onWrappedCacheResponse)//
        ;
    }

}
