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
package com.anrisoftware.dwarfhustle.gamemap.model.objects;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;
import static com.anrisoftware.dwarfhustle.model.db.cache.MapObject.getMapObject;
import static com.anrisoftware.dwarfhustle.model.db.cache.MapObject.setMapObject;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.lable.oss.uniqueid.IDGenerator;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.DeleteObjectMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.DeleteObjectMessage.DeleteObjectSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.InsertObjectMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.InsertObjectMessage.InsertObjectSuccessMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.IdsObjectsProvider.IdsObjects;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObject;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @see InsertObjectMessage
 * @see DeleteObjectMessage
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class ObjectsActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, ObjectsActor.class.getSimpleName());

    public static final String NAME = ObjectsActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {
        public final ObjectsGetter og;
        public final ObjectsSetter os;
        public final ObjectsGetter chunks;
        public final ObjectsGetter mg;
        public final ObjectsSetter ms;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class SetupErrorMessage extends Message {
        public final Throwable cause;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedCacheResponse extends Message {
        public final CacheResponseMessage<?> res;
    }

    /**
     * Factory to create {@link ObjectsActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface ObjectsActorFactory {
        ObjectsActor create(ActorContext<Message> context, StashBuffer<Message> stash);
    }

    /**
     * Creates the {@link ObjectsActor}.
     */
    private static Behavior<Message> create(Injector injector, ActorSystemProvider actor) {
        return Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            context.pipeToSelf(CompletableFuture.supplyAsync(() -> returnInitialState(injector, actor)),
                    (result, cause) -> {
                        if (cause == null) {
                            return result;
                        } else {
                            return new SetupErrorMessage(cause);
                        }
                    });
            return injector.getInstance(ObjectsActorFactory.class).create(context, stash).start(injector);
        }));
    }

    /**
     * Creates the {@link ObjectsActor}.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        var actor = injector.getInstance(ActorSystemProvider.class);
        return createNamedActor(actor.getActorSystem(), timeout, ID, KEY, NAME, create(injector, actor));
    }

    private static Message returnInitialState(Injector injector, ActorSystemProvider actor) {
        try {
            var og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            var os = actor.getObjectSetterAsyncNow(StoredObjectsJcsCacheActor.ID);
            var chunks = actor.getObjectGetterAsyncNow(MapChunksJcsCacheActor.ID);
            var mg = actor.getObjectGetterAsyncNow(MapObjectsJcsCacheActor.ID);
            var ms = actor.getObjectSetterAsyncNow(MapObjectsJcsCacheActor.ID);
            return new InitialStateMessage(og, os, chunks, mg, ms);
        } catch (Exception ex) {
            return new SetupErrorMessage(ex);
        }
    }

    @Inject
    @Assisted
    private ActorContext<Message> context;

    @Inject
    @Assisted
    private StashBuffer<Message> buffer;

    @Inject
    @IdsObjects
    private IDGenerator ids;

    private InitialStateMessage is;

    /**
     * Stash behavior. Returns a behavior for the messages:
     *
     * <ul>
     * <li>{@link InitialStateMessage}
     * <li>{@link SetupErrorMessage}
     * <li>{@link Message}
     * </ul>
     */
    @SneakyThrows
    public Behavior<Message> start(Injector injector) {
        return Behaviors.receive(Message.class)//
                .onMessage(InitialStateMessage.class, this::onInitialState)//
                .onMessage(SetupErrorMessage.class, this::onSetupError)//
                .onMessage(Message.class, this::stashOtherCommand)//
                .build();
    }

    private Behavior<Message> stashOtherCommand(Message m) {
        log.debug("stashOtherCommand: {}", m);
        buffer.stash(m);
        return Behaviors.same();
    }

    private Behavior<Message> onSetupError(SetupErrorMessage m) {
        log.debug("onSetupError: {}", m);
        return Behaviors.stopped();
    }

    /**
     * Returns a behavior for the messages from {@link #getInitialBehavior()}
     */
    private Behavior<Message> onInitialState(InitialStateMessage m) {
        log.debug("onInitialState");
        this.is = m;
        return buffer.unstashAll(getInitialBehavior()//
                .build());
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    private Behavior<Message> onShutdown(ShutdownMessage m) {
        log.debug("onShutdown {}", m);
        return Behaviors.stopped();
    }

    @SneakyThrows
    private Behavior<Message> onInsertObject(InsertObjectMessage<? super InsertObjectSuccessMessage> m) {
        final GameMapObject go = m.ko.createObject(ids.generate());
        go.setMap(m.gm.getId());
        go.setPos(m.pos);
        go.setKid(m.ko.getKid());
        go.setOid(m.ko.getKnowledgeType().hashCode());
        go.setVisible(true);
        m.consumer.accept(go);
        is.os.set(go.getObjectType(), go);
        final var mo = getMapObject(is.mg, m.gm, go.getPos());
        mo.setCid(m.cid);
        mo.addObject(go.getObjectType(), go.getId());
        setMapObject(is.ms, mo);
        m.gm.addFilledBlock(mo.getCid(), mo.getIndex());
        is.os.set(m.gm.getObjectType(), m.gm);
        m.onInserted.run();
        m.replyTo.tell(new InsertObjectSuccessMessage(go));
        return Behaviors.same();
    }

    @SneakyThrows
    private Behavior<Message> onDeleteObject(DeleteObjectMessage<? super DeleteObjectSuccessMessage> m) {
        if (!m.mo.getOids().isEmpty()) {
            final int type = m.mo.getOids().get(m.id);
            final GameMapObject go = is.og.get(type, m.id);
            m.mo.removeObject(m.id);
            is.ms.set(m.mo.getObjectType(), m.mo);
            is.os.remove(go.getObjectType(), go);
            if (m.mo.isEmpty()) {
                m.gm.removeFilledBlock(m.mo.cid, m.mo.getIndex());
                is.os.set(m.gm.getObjectType(), m.gm);
                is.ms.remove(MapObject.OBJECT_TYPE, m.mo);
            }
        }
        m.onDeleted.run();
        m.replyTo.tell(new DeleteObjectSuccessMessage());
        return Behaviors.same();
    }

    /**
     * Returns a behavior for the messages:
     *
     * <ul>
     * <li>{@link ShutdownMessage}
     * </ul>
     */
    private BehaviorBuilder<Message> getInitialBehavior() {
        return Behaviors.receive(Message.class)//
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(InsertObjectMessage.class, this::onInsertObject)//
                .onMessage(DeleteObjectMessage.class, this::onDeleteObject)//
        ;
    }

}
