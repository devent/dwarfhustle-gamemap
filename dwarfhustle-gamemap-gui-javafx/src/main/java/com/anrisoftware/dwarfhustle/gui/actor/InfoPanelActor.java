/*
 * Dwarf Hustle Game Map - Game map.
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

import static com.anrisoftware.dwarfhustle.gui.actor.AdditionalCss.ADDITIONAL_CSS;
import static com.anrisoftware.dwarfhustle.gui.controllers.JavaFxUtil.runFxThread;

import java.awt.MouseInfo;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.eclipse.collections.impl.factory.Maps;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapTileEmptyUnderCursorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapTileUnderCursorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.ObservableGameSettings.GameSettings;
import com.anrisoftware.dwarfhustle.gui.actor.PanelControllerBuild.PanelControllerResult;
import com.anrisoftware.dwarfhustle.gui.controllers.InfoPaneController;
import com.anrisoftware.dwarfhustle.gui.controllers.MapTileItem;
import com.anrisoftware.dwarfhustle.gui.controllers.MapTileItemWidgetController;
import com.anrisoftware.dwarfhustle.gui.messages.GameQuitMessage;
import com.anrisoftware.dwarfhustle.gui.messages.MainWindowResizedMessage;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapPos;
import com.anrisoftware.dwarfhustle.model.api.objects.MapTile;
import com.anrisoftware.dwarfhustle.model.api.objects.Person;
import com.anrisoftware.resources.texts.external.Texts;
import com.anrisoftware.resources.texts.external.TextsFactory;
import com.google.inject.Injector;
import com.jayfella.jme.jfx.JavaFxUI;
import com.jme3.app.Application;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import lombok.extern.slf4j.Slf4j;

/**
 * Shows the map and tile information.
 *
 * @author Erwin Müller
 */
@Slf4j
public class InfoPanelActor extends AbstractPaneActor<InfoPaneController> {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            InfoPanelActor.class.getSimpleName());

    public static final String NAME = InfoPanelActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    private static final Map<String, PanelActorCreator> panelActors = Maps.mutable.empty();

    static {
    }

    public interface InfoPanelActorFactory extends AbstractPaneActorFactory<InfoPaneController> {
    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        return AbstractPaneActor.create(injector, timeout, ID, KEY, NAME, InfoPanelActorFactory.class,
                "/info_pane_ui.fxml", panelActors, PanelControllerBuild.class, ADDITIONAL_CSS);
    }

    @Inject
    private Application app;

    @Inject
    private GameSettings gs;

    private Texts texts;

    private InfoPaneController controller;

    private PanelControllerResult<MapTileItemWidgetController> mapTileItemWidget;

    @Inject
    public void setTextsFactory(TextsFactory texts) {
        this.texts = texts.create("InfoPanelActor_Texts");
    }

    @Override
    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        this.controller = initial.controller;
        runFxThread(() -> {
            var controller = initial.controller;
        });
        var builder = injector.getInstance(PanelControllerBuild.class);
        builder.<MapTileItemWidgetController>loadFxml(injector, context.getExecutionContext(),
                "/map_tile_item_widget_ui.fxml", ADDITIONAL_CSS).whenComplete((res, err) -> {
                    if (err == null) {
                        mapTileItemWidget = res;
                        var controller = initial.controller;
                        controller.setup(injector);
                    }
                });
        return getDefaultBehavior()//
        ;
    }

    private Behavior<Message> onMapTileUnderCursor(MapTileUnderCursorMessage m) {
        // log.debug("onMapTileUnderCursor {}", m);
        var mt = new MapTile(1);
        mt.setPos(new GameMapPos(1, 5, 5, 5));
        var p = new Person(1);
        p.setFirstName("Gorbir");
        p.setLastName("Shatterfeet");
        runFxThread(() -> {
            controller.items.clear();
            controller.items.add(new MapTileItem(mt));
            controller.items.add(new MapTileItem(p));
            controller.infoPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            controller.infoPane.setVisible(true);
        });
        return Behaviors.same();
    }

    private Behavior<Message> onMapTileEmptyUnderCursor(MapTileEmptyUnderCursorMessage m) {
        // log.debug("onMapTileEmptyUnderCursor {}", m);
        runFxThread(() -> {
            if (controller.items != null) {
                controller.items.clear();
            }
            controller.infoPane.setVisible(false);
        });
        return Behaviors.same();
    }

    private BehaviorBuilder<Message> getDefaultBehavior() {
        return super.getBehaviorAfterAttachGui()//
                .onMessage(MapTileUnderCursorMessage.class, this::onMapTileUnderCursor)//
                .onMessage(MapTileEmptyUnderCursorMessage.class, this::onMapTileEmptyUnderCursor)//
        ;
    }

    @Override
    protected Behavior<Message> onShutdown(ShutdownMessage m) {
        return Behaviors.stopped();
    }

    @Override
    protected void attachPaneState() {
        // nothing
    }

    @Override
    protected void setupUi() {
        var pane = initial.root;
        var p = MouseInfo.getPointerInfo().getLocation();
        pane.setLayoutX(p.x);
        pane.setLayoutY(p.y);
        pane.setPrefSize(100, 100);
        JavaFxUI.getInstance().attachChild(pane);
        JavaFxUI.getInstance().getScene().setOnMouseMoved(this::onMouseMoved);
    }

    @Override
    protected Behavior<Message> onGameQuit(GameQuitMessage m) {
        return Behaviors.same();
    }

    @Override
    protected Behavior<Message> onMainWindowResized(MainWindowResizedMessage m) {
        return Behaviors.same();
    }

    private void onMouseMoved(MouseEvent e) {
        var pane = initial.root;
        pane.setLayoutX(e.getSceneX() + 20);
        pane.setLayoutY(e.getSceneY() + 20);
    }
}
