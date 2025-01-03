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

import static org.dwarfhustle.gamemap.tester.gui.javafx.actor.AdditionalCss.ADDITIONAL_CSS;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.dwarfhustle.gamemap.tester.gui.javafx.controllers.TerrainMaterialButtonsController;
import org.eclipse.collections.api.factory.Maps;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.ObservableGameSettings.GameSettings;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.AbstractPaneActor;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.PanelActorCreator;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.PanelControllerBuild;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.PanelControllerBuild.PanelControllerResult;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.MapTileItemWidgetController;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AttachGuiMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameQuitMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.MainWindowResizedMessage;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.resources.texts.external.Texts;
import com.anrisoftware.resources.texts.external.TextsFactory;
import com.google.inject.Injector;
import com.jme3.app.Application;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Shows the map and tile information.
 *
 * @author Erwin Müller
 */
@Slf4j
public class TerrainMaterialButtonsActor extends AbstractPaneActor<TerrainMaterialButtonsController> {

	public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
			TerrainMaterialButtonsActor.class.getSimpleName());

	public static final String NAME = TerrainMaterialButtonsActor.class.getSimpleName();

	public static final int ID = KEY.hashCode();

	private static final Map<String, PanelActorCreator> panelActors = Maps.mutable.empty();

	static {
	}

	/**
	 * 
	 * @author Erwin Müller <erwin@muellerpublic.de>
	 */
	public interface TerrainMaterialButtonsActorFactory
			extends AbstractPaneActorFactory<TerrainMaterialButtonsController> {
	}

	public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
			CompletionStage<ObjectsGetter> og) {
		return AbstractPaneActor.create(injector, timeout, ID, KEY, NAME, og, TerrainMaterialButtonsActorFactory.class,
				"/tester_terrain_materials_buttons.fxml", panelActors, PanelControllerBuild.class, ADDITIONAL_CSS);
	}

	@Inject
	private Application app;

	@Inject
	private GameSettings gs;

	private Texts texts;

	private TerrainMaterialButtonsController controller;

	private PanelControllerResult<MapTileItemWidgetController> mapTileItemWidget;

	@Inject
	public void setTextsFactory(TextsFactory texts) {
		this.texts = texts.create("InfoPanelActor_Texts");
	}

	@Override
	protected Behavior<Message> onAttachGui(AttachGuiMessage m) {
		log.debug("onAttachGui {}", m);
		return getBehaviorAfterAttachGui().build();
	}

	@Override
	protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
		this.controller = initial.controller;
		return super.getBehaviorAfterAttachGui()//
		;
	}

	@Override
	protected Behavior<Message> onShutdown(ShutdownMessage m) {
		return Behaviors.stopped();
	}

	@Override
	protected Behavior<Message> onGameQuit(GameQuitMessage m) {
		return Behaviors.same();
	}

	@Override
	protected Behavior<Message> onMainWindowResized(MainWindowResizedMessage m) {
		return Behaviors.same();
	}
}
