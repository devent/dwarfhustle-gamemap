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
package com.anrisoftware.dwarfhustle.gui.actor;

import static com.anrisoftware.dwarfhustle.gui.controllers.JavaFxUtil.runFxThread;
import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadMapTilesMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapBlockLoadedMessage;
import com.anrisoftware.dwarfhustle.gui.controllers.MainPaneController;
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
 * Acts on the messages:
 * <ul>
 * <li>{@link CreateSchemasMessage}</li>
 * </ul>
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class StatusActor {

	public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
			StatusActor.class.getSimpleName());

	public static final String NAME = StatusActor.class.getSimpleName();

	public static final int ID = KEY.hashCode();

	/**
	 * Factory to create {@link StatusActor}.
	 *
	 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
	 */
	public interface StatusActorFactory {
		StatusActor create(ActorContext<Message> context, MainPaneController controller);
	}

	/**
	 * Creates the {@link StatusActor}.
	 */
	public static Behavior<Message> create(Injector injector, MainPaneController controller) {
		return Behaviors
				.setup(context -> injector.getInstance(StatusActorFactory.class).create(context, controller).start());
	}

	/**
	 * Creates the {@link StatusActor}.
	 */
	public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
			MainPaneController controller) {
		var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
		return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, controller));
	}

	@Inject
	@Assisted
	private ActorContext<Message> context;

	@Inject
	@Assisted
	private MainPaneController controller;

	/**
	 * Returns a behavior for the messages from {@link #getInitialBehavior()}
	 */
	public Behavior<Message> start() {
		return getInitialBehavior()//
				.build();
	}

	/**
	 * Processing {@link LoadMapTilesMessage}.
	 * <p>
	 * Sets the status text that the world and game map is loading.
	 * <p>
	 * Returns a behavior that reacts to the messages from
	 * {@link #getInitialBehavior()}.
	 */
	private Behavior<Message> onLoadMapTiles(LoadMapTilesMessage m) {
		log.debug("onLoadMapTiles {}", m);
		runFxThread(() -> {
			controller.statusLabel.setText("Loading game map...");
		});
		return Behaviors.same();
	}

	/**
	 * Processing {@link MapBlockLoadedMessage}.
	 * <p>
	 * Sets the status text that the world and game map is finished loading.
	 * <p>
	 * Returns a behavior that reacts to the messages from
	 * {@link #getInitialBehavior()}.
	 */
	private Behavior<Message> onMapBlockLoaded(MapBlockLoadedMessage m) {
		log.debug("onMapBlockLoaded {}", m);
		runFxThread(() -> {
			controller.statusLabel.setText("Game map loaded.");
		});
		return Behaviors.same();
	}

	/**
	 * Returns a behavior for the messages:
	 *
	 * <ul>
	 * <li>{@link LoadGameObjectMessage}
	 * <li>{@link MapBlockLoadedMessage}
	 * </ul>
	 */
	private BehaviorBuilder<Message> getInitialBehavior() {
		return Behaviors.receive(Message.class)//
				.onMessage(LoadMapTilesMessage.class, this::onLoadMapTiles)//
				.onMessage(MapBlockLoadedMessage.class, this::onMapBlockLoaded)//
		;
	}

}
