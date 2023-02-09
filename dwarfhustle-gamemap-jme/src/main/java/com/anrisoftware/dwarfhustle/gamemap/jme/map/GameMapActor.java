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
package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapBlockLoadedMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.badlogic.ashley.core.Engine;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Acts on the messages:
 * <ul>
 * <li>{@link }</li>
 * </ul>
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class GameMapActor {

	public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, GameMapActor.class.getSimpleName());

	public static final String NAME = GameMapActor.class.getSimpleName();

	public static final int ID = KEY.hashCode();

	@RequiredArgsConstructor
	@ToString(callSuper = true)
	private static class InitialStateMessage extends Message {
		public final GameMapState state;
	}

	@RequiredArgsConstructor
	@ToString(callSuper = true)
	private static class SetupErrorMessage extends Message {
		public final Throwable cause;
	}

	/**
	 * Factory to create {@link GameMapActor}.
	 *
	 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
	 */
	public interface GameMapActorFactory {
		GameMapActor create(ActorContext<Message> context, StashBuffer<Message> stash);
	}

	/**
	 * Creates the {@link GameMapActor}.
	 */
	public static Behavior<Message> create(Injector injector) {
		return Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
			context.pipeToSelf(createState(injector, context), (result, cause) -> {
				if (cause == null) {
					return result;
				} else {
					return new SetupErrorMessage(cause);
				}
			});
			return injector.getInstance(GameMapActorFactory.class).create(context, stash).start(injector);
		}));
	}

	/**
	 * Creates the {@link GameMapActor}.
	 */
	public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
		var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
		return createNamedActor(system, timeout, ID, KEY, NAME, create(injector));
	}

	private static CompletionStage<Message> createState(Injector injector, ActorContext<Message> context) {
		return CompletableFuture.supplyAsync(() -> attachState(injector));
	}

	private static Message attachState(Injector injector) {
		var app = injector.getInstance(Application.class);
		var gameMapState = injector.getInstance(GameMapState.class);
		var cameraState = injector.getInstance(CameraPanningState.class);
		try {
			var f = app.enqueue(() -> {
				app.getStateManager().attach(gameMapState);
				app.getStateManager().attach(cameraState);
				var mapRenderSystem = gameMapState.getMapRenderSystem();
				cameraState.setMapRenderSystem(mapRenderSystem);
				return new InitialStateMessage(gameMapState);
			});
			return f.get();
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
	private ActorSystemProvider actor;

	@Inject
	private Engine engine;

	@Inject
	private Application app;

	private Injector injector;

	private GameMapState state;

	/**
	 * Stash behavior. Returns a behavior for the messages:
	 *
	 * <ul>
	 * <li>{@link InitialStateMessage}
	 * <li>{@link SetupErrorMessage}
	 * <li>{@link Message}
	 * </ul>
	 */
	public Behavior<Message> start(Injector injector) {
		this.injector = injector;
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
		this.state = m.state;
		return buffer.unstashAll(getInitialBehavior()//
				.build());
	}

	/**
	 * Reacts to the {@link MapBlockLoadedMessage} message. Creates the
	 * {@link MapBlockComponent} for the render to construct map blocks and map
	 * tiles of the game map.
	 */
	private Behavior<Message> onMapBlockLoaded(MapBlockLoadedMessage m) {
		log.debug("onMapBlockLoaded {}", m);
		app.enqueue(() -> {
			var e = engine.createEntity();
			e.add(new MapBlockComponent(m.mb));
			engine.addEntity(e);
		});
		return Behaviors.same();
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
				.onMessage(MapBlockLoadedMessage.class, this::onMapBlockLoaded)//
		;
	}
}
