/*
 * dwarfhustle-gamemap-console - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.console.actor;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import jakarta.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class ConsoleActor {

	public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, ConsoleActor.class.getSimpleName());

	public static final String NAME = ConsoleActor.class.getSimpleName();

	public static final int ID = KEY.hashCode();

	/**
	 * Factory to create {@link ConsoleActor}.
	 *
	 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
	 */
	public interface ConsoleActorFactory {

		ConsoleActor create(ActorContext<Message> context);
	}

	public static Behavior<Message> create(Injector injector) {
		return Behaviors.setup((context) -> {
			return injector.getInstance(ConsoleActorFactory.class).create(context).start();
		});
	}

	/**
	 * Creates the {@link ConsoleActor}.
	 */
	public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
		var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
		return createNamedActor(system, timeout, ID, KEY, NAME, create(injector));
	}

	@Inject
	@Assisted
	private ActorContext<Message> context;

	@Inject
	private ConsoleProcessor processor;

	@Inject
	private GameSettingsProvider gsp;

	/**
	 * Initial behavior. Returns a behavior for the messages from
	 * {@link #getInitialBehavior()}.
	 */
	public Behavior<Message> start() {
		return getInitialBehavior().build();
	}

	/**
	 * Reacts to {@link LineMessage}. Returns a behavior for the messages from
	 * {@link #getInitialBehavior()}.
	 */
	private Behavior<Message> onLine(LineMessage m) {
		log.debug("onLine {}", m);
		processor.process(m.line);
		return Behaviors.same();
	}

	/**
	 * Reacts to {@link ParsedLineMessage}. Returns a behavior for the messages from
	 * {@link #getInitialBehavior()}.
	 */
	private Behavior<Message> onParsedLine(ParsedLineMessage m) {
		log.debug("onParsedLine {}", m);
		return Behaviors.same();
	}

	/**
	 * Reacts to {@link UnknownLineMessage}. Returns a behavior for the messages
	 * from {@link #getInitialBehavior()}.
	 */
	private Behavior<Message> onUnknownLine(UnknownLineMessage m) {
		log.debug("onUnknownLine {}", m);
		return Behaviors.same();
	}

	/**
	 * Returns a behavior for the messages:
	 *
	 * <ul>
	 * <li>{@link LineMessage}
	 * <li>{@link ParsedLineMessage}
	 * <li>{@link UnknownLineMessage}
	 * </ul>
	 */
	private BehaviorBuilder<Message> getInitialBehavior() {
		return Behaviors.receive(Message.class)//
				.onMessage(LineMessage.class, this::onLine)//
				.onMessage(ParsedLineMessage.class, this::onParsedLine)//
				.onMessage(UnknownLineMessage.class, this::onUnknownLine)//
		;
	}
}
