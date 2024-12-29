/*
 * dwarfhustle-gamemap-gui-javafx - GUI in Javafx.
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
package org.dwarfhustle.gamemap.tester.gui.javafx.actor;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import org.dwarfhustle.gamemap.tester.gui.javafx.controllers.TesterMainPaneController;

import com.anrisoftware.dwarfhustle.gui.javafx.actor.AbstractStatusActor;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.AbstractStatusController;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.google.inject.Injector;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TesterStatusActor extends AbstractStatusActor {

	public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
			TesterStatusActor.class.getSimpleName());

	public static final String NAME = TesterStatusActor.class.getSimpleName();

	public static final int ID = KEY.hashCode();

	/**
	 * Factory to create {@link TesterStatusActor}.
	 *
	 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
	 */
	public interface TesterStatusActorFactory {
		TesterStatusActor create(ActorContext<Message> context, AbstractStatusController controller);
	}

	/**
	 * Creates the {@link TesterStatusActor}.
	 */
	public static Behavior<Message> create(Injector injector, TesterMainPaneController controller) {
		return Behaviors
				.setup(context -> injector.getInstance(TesterStatusActorFactory.class).create(context, controller)
						.start());
	}

	/**
	 * Creates the {@link TesterStatusActor}.
	 */
	public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
			TesterMainPaneController controller) {
		var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
		return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, controller));
	}

}
