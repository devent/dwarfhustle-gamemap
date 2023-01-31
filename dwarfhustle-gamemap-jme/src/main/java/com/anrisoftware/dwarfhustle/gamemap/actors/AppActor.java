/*
 * dwarfhustle-model-db - Manages the compile dependencies for the model.
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
package com.anrisoftware.dwarfhustle.gamemap.actors;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.AbstractLoadObjectMessage.LoadObjectErrorMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.AbstractLoadObjectMessage.LoadObjectSuccessMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.AbstractObjectsReplyMessage.ObjectsResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.CreateSchemasMessage;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.GameObjectSchema;
import com.anrisoftware.dwarfhustle.model.db.orientdb.objects.LoadWorldMapMessage;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Acts on the messages:
 * <ul>
 * <li>{@link LoadWorldMessage}</li>
 * </ul>
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class AppActor {

	public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, AppActor.class.getSimpleName());

	public static final String NAME = AppActor.class.getSimpleName();

	public static final int ID = KEY.hashCode();

	@RequiredArgsConstructor
	@ToString(callSuper = true)
	private static class WrappedObjectsResponse extends Message {
		private final ObjectsResponseMessage response;
	}

	/**
	 * Factory to create {@link AppActor}.
	 *
	 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
	 */
	public interface AppActorFactory {
		AppActor create(ActorContext<Message> context, ActorRef<Message> objects);
	}

	/**
	 * Creates the {@link AppActor}.
	 */
	public static Behavior<Message> create(Injector injector, ActorRef<Message> objects) {
		return Behaviors.setup((context) -> {
			return injector.getInstance(AppActorFactory.class).create(context, objects).start();
		});
	}

	/**
	 * Creates the {@link AppActor}.
	 */
	public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
			ActorRef<Message> objects) {
		var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
		return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, objects));
	}

	@Inject
	@Assisted
	private ActorContext<Message> context;

	@Inject
	@Assisted
	private ActorRef<Message> objects;

	@Inject
	private List<GameObjectSchema> schemas;

	private ActorRef<ObjectsResponseMessage> objectsResponseAdapter;

	private Optional<CreateSchemasMessage> createSchemasMessage = Optional.empty();

	/**
	 * Returns a behavior for the messages from {@link #getInitialBehavior()}
	 */
	public Behavior<Message> start() {
		this.objectsResponseAdapter = context.messageAdapter(ObjectsResponseMessage.class, WrappedObjectsResponse::new);
		return getInitialBehavior()//
				.build();
	}

	/**
	 * Returns a behavior for the messages from {@link #getInitialBehavior()}
	 */
	private Behavior<Message> onLoadWorld(LoadWorldMessage m) {
		log.debug("onLoadWorld {}", m);
		objects.tell(new LoadWorldMapMessage(objectsResponseAdapter));
		return Behaviors.same();
	}

	/**
	 * <ul>
	 * <li>
	 * </ul>
	 */
	private Behavior<Message> onWrappedObjectsResponse(WrappedObjectsResponse m) {
		log.debug("onWrappedObjectsResponse {}", m);
		var response = m.response;
		var om = createSchemasMessage.get();
		if (response instanceof LoadObjectErrorMessage) {
			var rm = (LoadObjectErrorMessage) response;
			log.error("Objects error", rm);
			om.replyTo.tell(rm);
			return Behaviors.stopped();
		} else if (response instanceof LoadObjectSuccessMessage) {
			om.replyTo.tell(response);
		}
		return Behaviors.same();
	}

	/**
	 * Returns a behavior for the messages:
	 *
	 * <ul>
	 * <li>{@link LoadWorldMessage}
	 * <li>{@link WrappedObjectsResponse}
	 * </ul>
	 */
	private BehaviorBuilder<Message> getInitialBehavior() {
		return Behaviors.receive(Message.class)//
				.onMessage(LoadWorldMessage.class, this::onLoadWorld)//
				.onMessage(WrappedObjectsResponse.class, this::onWrappedObjectsResponse)//
		;
	}
}
