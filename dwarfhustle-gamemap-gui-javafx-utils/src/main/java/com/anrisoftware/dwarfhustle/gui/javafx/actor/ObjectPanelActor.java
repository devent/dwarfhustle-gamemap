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

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.primitive.IntObjectMaps;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.GameTickMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetSelectedObjectMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.ObjectPane;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.ObjectPaneController;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameQuitMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.MainWindowResizedMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Shows object information.
 *
 * @author Erwin Müller
 */
@Slf4j
public class ObjectPanelActor extends AbstractPaneActor<ObjectPaneController> {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            ObjectPanelActor.class.getSimpleName());

    public static final String NAME = ObjectPanelActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    private static final Map<String, PanelActorCreator> panelActors = Maps.mutable.empty();

    static {
    }

    public interface ObjectPanelActorFactory extends AbstractPaneActorFactory<ObjectPaneController> {
    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        return AbstractPaneActor.create(injector, timeout, ID, KEY, NAME, ObjectPanelActorFactory.class,
                "/object_pane_ui.fxml", panelActors, PanelControllerBuild.class, ADDITIONAL_CSS);
    }

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    @Named("type-objectPropertiesPanes")
    private IntObjectMap<ObjectPane> objectPropertiesPanes;

    private Texts texts;

    private ObjectPaneController controller;

    private ObjectsGetter og;

    private ObjectsGetter cg;

    private ObjectsGetter mg;

    private long currentMap;

    private KnowledgeGetter kg;

    private long oldSelectedObject = 0;

    private ObjectPane currentObjectPane;

    private MutableIntObjectMap<ObjectPane> createdObjectPanes;

    @Inject
    public void setTextsFactory(TextsFactory texts) {
        this.texts = texts.create("ObjectPanelActor_Texts");
    }

    @Override
    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        this.og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
        this.cg = actor.getObjectGetterAsyncNow(MapChunksJcsCacheActor.ID);
        this.mg = actor.getObjectGetterAsyncNow(MapObjectsJcsCacheActor.ID);
        this.kg = actor.getKnowledgeGetterAsyncNow(PowerLoomKnowledgeActor.ID);
        this.controller = is.controller;
        this.currentMap = gs.get().currentMap.get();
        this.createdObjectPanes = IntObjectMaps.mutable.empty();
        gs.get().currentMap.addListener((o, ov, nv) -> {
            currentMap = nv.longValue();
        });
        runFxThread(() -> {
            final var controller = is.controller;
        });
        return getDefaultBehavior()//
        ;
    }

    @Override
    protected Behavior<Message> onGameTick(GameTickMessage m) {
        // log.trace("onGameTick {}", m);
        updateObjectPane();
        return Behaviors.same();
    }

    private Behavior<Message> onSetSelectedObject(SetSelectedObjectMessage m) {
        log.trace("onSetSelectedObject {}", m);
        updateObjectPane();
        return Behaviors.same();
    }

    private BehaviorBuilder<Message> getDefaultBehavior() {
        return super.getBehaviorAfterAttachGui()//
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
        val container = JavaFxUI.getInstance().getJmeFxContainer();
        val anchor = (AnchorPane) container.getRootNode().getChildren().get(0);
        val main = (BorderPane) anchor.getChildren().filtered(n -> n.getId().equalsIgnoreCase("mainPanel")).getFirst();
        val pane = (GridPane) main.getChildren().filtered(n -> n.getId().equalsIgnoreCase("gameMapPane")).getFirst();
        is.controller.setup(injector);
        pane.add(is.root, 1, 1, 1, 2);
        is.root.setVisible(false);
    }

    @Override
    protected Behavior<Message> onGameQuit(GameQuitMessage m) {
        return Behaviors.same();
    }

    @Override
    protected Behavior<Message> onMainWindowResized(MainWindowResizedMessage m) {
        return Behaviors.same();
    }

    @SneakyThrows
    private void updateObjectPane() {
        val gm = getGameMap(og, currentMap);
        val id = gm.getSelectedObjectId();
        if (oldSelectedObject != id) {
            this.currentObjectPane = lazyCreateObjectPane(gm, id);
        }
        runFxThread(() -> {
            if (id != 0) {
                currentObjectPane.update(id, controller);
                is.root.setVisible(true);
            } else {
                is.root.setVisible(false);
            }
        });
    }

    private ObjectPane lazyCreateObjectPane(final GameMap gm, final long id) {
        this.oldSelectedObject = id;
        if (id != 0) {
            final int type = gm.getSelectedObjectType();
            return createdObjectPanes.getIfAbsentPut(type, () -> {
                val go = og.get(type, id);
                val objectPane = objectPropertiesPanes.get(go.getObjectType());
                val p = objectPane.create(go.getObjectType(), actor);
                return p;
            });
        }
        return null;
    }
}
