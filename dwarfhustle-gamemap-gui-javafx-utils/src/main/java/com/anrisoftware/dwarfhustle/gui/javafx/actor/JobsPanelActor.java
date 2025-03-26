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

import static com.anrisoftware.dwarfhustle.gui.javafx.actor.AdditionalCss.ADDITIONAL_CSS;
import static com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil.runFxThread;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameMap.getGameMap;

import java.awt.MouseInfo;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.primitive.IntObjectMap;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.GameTickMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapTileEmptyUnderCursorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapTileUnderCursorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MouseEnteredGuiMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MouseExitedGuiMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetSelectedObjectMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.PanelControllerBuild.PanelControllerResult;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GameMapObjectInfoPaneItem;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.JobsPaneController;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.MapBlockItemWidgetController;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameQuitMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.MainWindowResizedMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.anrisoftware.resources.texts.external.Texts;
import com.anrisoftware.resources.texts.external.TextsFactory;
import com.google.inject.Injector;
import com.jayfella.jme.jfx.JavaFxUI;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import javafx.scene.input.MouseEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Jobs pane.
 *
 * @author Erwin Müller
 */
@Slf4j
public class JobsPanelActor extends AbstractPaneActor<JobsPaneController> {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            JobsPanelActor.class.getSimpleName());

    public static final String NAME = JobsPanelActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    private static final Map<String, PanelActorCreator> panelActors = Maps.mutable.empty();

    static {
    }

    public interface JobsPanelActorFactory extends AbstractPaneActorFactory<JobsPaneController> {
    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        return AbstractPaneActor.create(injector, timeout, ID, KEY, NAME, JobsPanelActorFactory.class,
                "/work_jobs_pane_ui.fxml", panelActors, PanelControllerBuild.class, ADDITIONAL_CSS);
    }

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    @Named("type-gameMapObjectInfoPaneItems")
    private IntObjectMap<GameMapObjectInfoPaneItem> gameMapObjectInfoPaneItems;

    private Texts texts;

    private JobsPaneController controller;

    private PanelControllerResult<MapBlockItemWidgetController> mapTileItemWidget;

    private ObjectsGetter og;

    private ObjectsGetter cg;

    private ObjectsGetter mg;

    private long currentMap;

    private KnowledgeGetter kg;

    @Inject
    public void setTextsFactory(TextsFactory texts) {
        this.texts = texts.create("InfoPanelActor_Texts");
    }

    @Override
    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        this.og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
        this.cg = actor.getObjectGetterAsyncNow(MapChunksJcsCacheActor.ID);
        this.mg = actor.getObjectGetterAsyncNow(MapObjectsJcsCacheActor.ID);
        this.kg = actor.getKnowledgeGetterAsyncNow(PowerLoomKnowledgeActor.ID);
        this.controller = is.controller;
        this.currentMap = gs.get().currentMap.get();
        gs.get().currentMap.addListener((o, ov, nv) -> {
            currentMap = nv.longValue();
        });
        runFxThread(() -> {
            final var controller = is.controller;
        });
        final var builder = injector.getInstance(PanelControllerBuild.class);
        builder.<MapBlockItemWidgetController>loadFxml(injector, context.getExecutionContext(),
                "/map_tile_item_widget_ui.fxml", ADDITIONAL_CSS).whenComplete((res, err) -> {
                    if (err == null) {
                        mapTileItemWidget = res;
                        final var controller = is.controller;
                        controller.setup();
                    }
                });
        return getDefaultBehavior()//
        ;
    }

    private Behavior<Message> onMapTileUnderCursor(MapTileUnderCursorMessage m) {
        log.trace("onMapTileUnderCursor {}", m);
        updateInfoPane();
        return Behaviors.same();
    }

    private Behavior<Message> onMapTileEmptyUnderCursor(MapTileEmptyUnderCursorMessage m) {
        log.trace("onMapTileEmptyUnderCursor {}", m);
        clearInfoPane();
        return Behaviors.same();
    }

    private Behavior<Message> onMouseEnteredGui(MouseEnteredGuiMessage m) {
        log.trace("onMouseEnteredGui {}", m);
        runFxThread(() -> {
            // controller.infoPane.setVisible(false);
        });
        return Behaviors.same();
    }

    private Behavior<Message> onMouseExitedGui(MouseExitedGuiMessage m) {
        log.trace("onMouseExitedGui {}", m);
        runFxThread(() -> {
            // controller.infoPane.setVisible(true);
        });
        return Behaviors.same();
    }

    @Override
    protected Behavior<Message> onGameTick(GameTickMessage m) {
        // log.trace("onGameTick {}", m);
        updateInfoPane();
        return Behaviors.same();
    }

    private Behavior<Message> onSetSelectedObject(SetSelectedObjectMessage m) {
        log.trace("onSetSelectedObject {}", m);
        updateInfoPane();
        return Behaviors.same();
    }

    private BehaviorBuilder<Message> getDefaultBehavior() {
        return super.getBehaviorAfterAttachGui()//
                .onMessage(MouseEnteredGuiMessage.class, this::onMouseEnteredGui)//
                .onMessage(MouseExitedGuiMessage.class, this::onMouseExitedGui)//
                .onMessage(MapTileUnderCursorMessage.class, this::onMapTileUnderCursor)//
                .onMessage(MapTileEmptyUnderCursorMessage.class, this::onMapTileEmptyUnderCursor)//
                .onMessage(GameTickMessage.class, this::onGameTick)//
                .onMessage(SetSelectedObjectMessage.class, this::onSetSelectedObject)//
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
        final var pane = is.root;
        final var p = MouseInfo.getPointerInfo().getLocation();
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
        final var pane = is.root;
        pane.setLayoutX(e.getSceneX() + 20);
        pane.setLayoutY(e.getSceneY() + 20);
    }

    @SneakyThrows
    private void updateInfoPane() {
        final var gm = getGameMap(og, currentMap);
        runFxThread(() -> {
        });
    }

    private void clearInfoPane() {
        runFxThread(() -> {
        });
    }

}
