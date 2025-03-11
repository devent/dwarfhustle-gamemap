/*
 * dwarfhustle-gamemap-gui-javafx-utils - Game map.
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
package com.anrisoftware.dwarfhustle.gui.javafx.actor;

import static com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil.runFxThread;
import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.StartTerrainForGameMapMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.resources.texts.external.Texts;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class AbstractStatusActor {

	/**
	 * Creates the {@link AbstractStatusActor}.
	 */
	public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
			AbstractStatusController controller, int id, ServiceKey<Message> key, String name,
			Behavior<Message> actor) {
		var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
		return createNamedActor(system, timeout, id, key, name, actor);
	}

	@Inject
	@Assisted
	protected ActorContext<Message> context;

	@Inject
	@Assisted
	protected AbstractStatusController controller;

	@Inject
	@Named("AppTexts")
	protected Texts appTexts;

	/**
	 * Returns a behavior for the messages from {@link #getInitialBehavior()}
	 */
	public Behavior<Message> start() {
		return getInitialBehavior()//
				.build();
	}

	/**
	 * Processing {@link SetGameMapMessage}.
	 * <p>
	 * Sets the status text that the world and game map is loading.
	 * <p>
	 * Returns a behavior that reacts to the messages from
	 * {@link #getInitialBehavior()}.
	 */
	private Behavior<Message> onSetGameMap(SetGameMapMessage m) {
		log.debug("onLoadMapTiles {}", m);
		runFxThread(() -> {
			controller.getStatusLabel().setText("Loading game map...");
		});
		return Behaviors.same();
	}

	/**
	 * Processing {@link StartTerrainForGameMapMessage}.
	 * <p>
	 * Sets the status text that the world and game map is finished loading.
	 * <p>
	 * Returns a behavior that reacts to the messages from
	 * {@link #getInitialBehavior()}.
	 */
	private Behavior<Message> onGameMapCached(StartTerrainForGameMapMessage m) {
		log.debug("GameMapCached {}", m);
		runFxThread(() -> {
			controller.getStatusLabel().setText("Game map loaded.");
		});
		return Behaviors.same();
	}

	/**
	 * Returns a behavior for the messages:
	 *
	 * <ul>
	 * <li>{@link SetGameMapMessage}
	 * <li>{@link StartTerrainForGameMapMessage}
	 * </ul>
	 */
	protected BehaviorBuilder<Message> getInitialBehavior() {
		return Behaviors.receive(Message.class)//
				.onMessage(SetGameMapMessage.class, this::onSetGameMap)//
				.onMessage(StartTerrainForGameMapMessage.class, this::onGameMapCached)//
		;
	}

}
