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

import static com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil.runFxThread;
import static java.time.Duration.ofSeconds;
import static org.dwarfhustle.gamemap.tester.gui.javafx.actor.AdditionalCss.ADDITIONAL_CSS;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import org.dwarfhustle.gamemap.tester.gui.javafx.actor.ObjectDeleteActor.StartDeleteObjectMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.actor.ObjectDeleteActor.StopDeleteObjectMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.actor.ObjectInsertActor.StartInsertObjectMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.actor.ObjectInsertActor.StopInsertObjectMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.actor.PaintTerrainActor.StartPaintTerrainMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.actor.PaintTerrainActor.StopPaintTerrainMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.controllers.TesterMainPaneController;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.DeleteButtonsCloseTriggeredMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.DeleteButtonsOpenTriggeredMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialsButtonsCloseMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialsButtonsCloseTriggeredMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialsButtonsOpenMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialsButtonsOpenTriggeredMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsCloseMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsCloseTriggeredMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsOpenMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsOpenTriggeredMessage;
import org.eclipse.collections.impl.factory.Maps;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapCursorUpdateMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.AbstractPaneActor;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.InfoPanelActor;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.PanelActorCreator;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AboutDialogMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AboutDialogMessage.AboutDialogOpenTriggeredMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AttachGuiMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AttachGuiMessage.AttachGuiFinishedMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.SettingsDialogMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.SettingsDialogMessage.SettingsDialogOpenTriggeredMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMapping;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;
import com.google.inject.Injector;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Game map tester main panel actor.
 *
 * @author Erwin Müller
 */
@Slf4j
public class TesterMainPanelActor extends AbstractPaneActor<TesterMainPaneController> {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            TesterMainPanelActor.class.getSimpleName());

    public static final String NAME = TesterMainPanelActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    private static final Map<String, PanelActorCreator> panelActors = Maps.mutable.empty();

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedGuiResponse extends Message {
        private final AttachGuiFinishedMessage response;
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class WrappedCacheResponse extends Message {
        private final CacheResponseMessage<?> response;
    }

    /**
     *
     * @author Erwin Müller <erwin@muellerpublic.de>
     */
    public interface TesterMainPanelActorFactory extends AbstractPaneActorFactory<TesterMainPaneController> {

    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        return AbstractPaneActor.create(injector, timeout, ID, KEY, NAME, TesterMainPanelActorFactory.class,
                "/tester_main_ui.fxml", panelActors, ADDITIONAL_CSS);
    }

    @Inject
    @Named("AppTexts")
    private Texts appTexts;

    @Inject
    @Named("AppIcons")
    private Images appIcons;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GlobalKeys globalKeys;

    @Inject
    @Named("keyMappings")
    private Map<String, KeyMapping> keyMappings;

    private ObjectsGetter og;

    private ObjectsSetter os;

    private Optional<ActorRef<Message>> paintTerrainActor = Optional.empty();

    private Optional<ActorRef<Message>> objectInsertActor = Optional.empty();

    private Optional<ActorRef<Message>> objectDeleteActor = Optional.empty();

    @SneakyThrows
    @Override
    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        this.og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
        this.os = actor.getObjectSetterAsyncNow(StoredObjectsJcsCacheActor.ID);
        TesterStatusActor.create(injector, ofSeconds(1), initial.controller).whenComplete((v, err) -> {
            log.debug("TesterStatusActor {} {}", v, err);
        });
        InfoPanelActor.create(injector, ofSeconds(1)).whenComplete((v, err) -> {
            log.debug("InfoPanelActor {} {}", v, err);
            if (err == null) {
                v.tell(new AttachGuiMessage(null));
            }
        });
        MaterialsButtonsActor.create(injector, ofSeconds(1)).whenComplete((v, err) -> {
            log.debug("MaterialsButtonsActor {} {}", v, err);
            if (err == null) {
                v.tell(new AttachGuiMessage(null));
            }
        });
        ObjectsButtonsActor.create(injector, ofSeconds(1)).whenComplete((v, err) -> {
            log.debug("ObjectsButtonsActor {} {}", v, err);
            if (err == null) {
                v.tell(new AttachGuiMessage(null));
            }
        });
        runFxThread(() -> {
            var controller = initial.controller;
            controller.updateLocale(Locale.US, appTexts, appIcons, IconSize.SMALL);
            controller.initButtons(globalKeys, keyMappings);
            controller.setOnMouseEnteredGui((entered) -> {
                gs.get().mouseEnteredGui.set(entered);
            });
        });
        return getDefaultBehavior()//
        ;
    }

    /**
     * Processing {@link SettingsDialogOpenTriggeredMessage}.
     * <p>
     * This message is send after if user wants to open the settings dialog by
     * either clicking on the settings button or using a shortcut key binding.
     * <p>
     * Returns a behavior that reacts to the following messages:
     * <ul>
     * <li>{@link #getBehaviorAfterAttachGui()}
     * <li>{@link SettingsDialogMessage}
     * </ul>
     */
    private Behavior<Message> onSettingsDialogOpenTriggered(SettingsDialogOpenTriggeredMessage m) {
        log.debug("onSettingsDialogOpenTriggered {}", m);
        return Behaviors.same();
    }

    private Behavior<Message> onSettingsDialogClosed(SettingsDialogMessage m) {
        log.debug("onSettingsDialogClosed {}", m);
        return getDefaultBehavior().build();
    }

    /**
     * Processing {@link AboutDialogOpenTriggeredMessage}.
     * <p>
     * This message is send after if user wants to open the about dialog by either
     * clicking on the About button or using a shortcut key binding.
     * <p>
     * Returns a behavior that reacts to the following messages:
     * <ul>
     * <li>{@link #getBehaviorAfterAttachGui()}
     * <li>{@link AboutDialogMessage}
     * </ul>
     */
    private Behavior<Message> onAboutDialogOpenTriggered(AboutDialogOpenTriggeredMessage m) {
        log.debug("onAboutDialogOpenTriggered {}", m);
        return Behaviors.same();
    }

    private Behavior<Message> onAboutDialog(AboutDialogMessage m) {
        log.debug("onAboutDialog {}", m);
        return getDefaultBehavior().build();
    }

    private void forwardMessage(Message m) {
        initial.actors.forEach(a -> {
            a.tell(m);
        });
    }

    /**
     * Processing {@link SetGameMapMessage}.
     * <p>
     * Updates the fortress name.
     * <p>
     * Returns a behavior that reacts to the following messages:
     * <ul>
     * <li>{@link #getBehaviorAfterAttachGui()}
     * </ul>
     */
    private Behavior<Message> onSetGameMap(SetGameMapMessage m) {
        log.debug("onSetGameMap {}", m);
        runFxThread(() -> {
            var controller = initial.controller;
            var gm = GameMap.getGameMap(og, m.gm);
            var wm = WorldMap.getWorldMap(og, gm.world);
            controller.setMap(wm, gm);
            controller.initListeners(() -> gm, this::saveGameMap);
        });
        return Behaviors.same();
    }

    /**
     * Processing {@link MapCursorUpdateMessage}.
     * <p>
     * Update the value and text of the level scroll bar and level label.
     * <p>
     * Returns a behavior that reacts to the following messages:
     * <ul>
     * <li>{@link #getBehaviorAfterAttachGui()}
     * </ul>
     */
    private Behavior<Message> onMapCursorUpdate(MapCursorUpdateMessage m) {
        log.debug("onMapCursorUpdate {}", m);
        runFxThread(() -> {
            initial.controller.updateMapCursor(m.cursor);
        });
        return Behaviors.same();
    }

    /**
     * Processing {@link MaterialsButtonsOpenTriggeredMessage}.
     */
    private Behavior<Message> onMaterialsButtonsOpenTriggered(MaterialsButtonsOpenTriggeredMessage m) {
        log.debug("onMaterialsButtonsOpenTriggered {}", m);
        actor.tell(new ObjectsButtonsCloseMessage(initial.controller.testerButtonsBox));
        actor.tell(new MaterialsButtonsOpenMessage(initial.controller.testerButtonsBox));
        stopObjectInsertActor();
        stopObjectDeleteActor();
        createPaintTerrainActor();
        return Behaviors.same();
    }

    /**
     * Processing {@link MaterialsButtonsCloseTriggeredMessage}.
     */
    private Behavior<Message> onMaterialsButtonsCloseTriggered(MaterialsButtonsCloseTriggeredMessage m) {
        log.debug("onMaterialsButtonsCloseTriggered {}", m);
        actor.tell(new MaterialsButtonsCloseMessage(initial.controller.testerButtonsBox));
        stopPaintTerrainActor();
        return Behaviors.same();
    }

    /**
     * Processing {@link ObjectsButtonsOpenTriggeredMessage}.
     */
    private Behavior<Message> onObjectsButtonsOpenTriggered(ObjectsButtonsOpenTriggeredMessage m) {
        log.debug("onObjectsButtonsOpenTriggered {}", m);
        actor.tell(new MaterialsButtonsCloseMessage(initial.controller.testerButtonsBox));
        actor.tell(new ObjectsButtonsOpenMessage(initial.controller.testerButtonsBox));
        stopPaintTerrainActor();
        stopObjectDeleteActor();
        createObjectInsertActor();
        return Behaviors.same();
    }

    /**
     * Processing {@link ObjectsButtonsCloseTriggeredMessage}.
     */
    private Behavior<Message> onObjectsButtonsCloseTriggered(ObjectsButtonsCloseTriggeredMessage m) {
        log.debug("onObjectsButtonsCloseTriggered {}", m);
        actor.tell(new ObjectsButtonsCloseMessage(initial.controller.testerButtonsBox));
        stopObjectInsertActor();
        return Behaviors.same();
    }

    /**
     * Processing {@link DeleteButtonsOpenTriggeredMessage}.
     */
    private Behavior<Message> onDeleteButtonsOpenTriggered(DeleteButtonsOpenTriggeredMessage m) {
        log.debug("onDeleteButtonsOpenTriggered {}", m);
        actor.tell(new ObjectsButtonsCloseMessage(initial.controller.testerButtonsBox));
        actor.tell(new MaterialsButtonsCloseMessage(initial.controller.testerButtonsBox));
        stopObjectInsertActor();
        stopPaintTerrainActor();
        createObjectDeleteActor();
        return Behaviors.same();
    }

    /**
     * Processing {@link DeleteButtonsCloseTriggeredMessage}.
     */
    private Behavior<Message> onDeleteButtonsCloseTriggered(DeleteButtonsCloseTriggeredMessage m) {
        log.debug("onDeleteButtonsCloseTriggered {}", m);
        stopObjectDeleteActor();
        return Behaviors.same();
    }

    private void createPaintTerrainActor() {
        PaintTerrainActor.create(injector, Duration.ofSeconds(1)).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("PaintTerrainActor", ex);
            } else {
                paintTerrainActor = Optional.of(res);
                res.tell(new StartPaintTerrainMessage());
            }
        });
    }

    private void stopPaintTerrainActor() {
        paintTerrainActor.ifPresent(a -> a.tell(new StopPaintTerrainMessage()));
        paintTerrainActor = Optional.empty();
    }

    private void createObjectInsertActor() {
        ObjectInsertActor.create(injector, Duration.ofSeconds(1)).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("ObjectInsertActor", ex);
            } else {
                objectInsertActor = Optional.of(res);
                res.tell(new StartInsertObjectMessage());
            }
        });
    }

    private void stopObjectInsertActor() {
        objectInsertActor.ifPresent(a -> a.tell(new StopInsertObjectMessage()));
        objectInsertActor = Optional.empty();
    }

    private void createObjectDeleteActor() {
        ObjectDeleteActor.create(injector, Duration.ofSeconds(1)).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("ObjectDeleteActor", ex);
            } else {
                objectDeleteActor = Optional.of(res);
                res.tell(new StartDeleteObjectMessage());
            }
        });
    }

    private void stopObjectDeleteActor() {
        objectDeleteActor.ifPresent(a -> a.tell(new StopDeleteObjectMessage()));
        objectDeleteActor = Optional.empty();
    }

    private void saveGameMap(GameMap gm) {
        os.set(gm.getObjectType(), gm);
    }

    private BehaviorBuilder<Message> getDefaultBehavior() {
        return super.getBehaviorAfterAttachGui()//
                .onMessage(SettingsDialogOpenTriggeredMessage.class, this::onSettingsDialogOpenTriggered)//
                .onMessage(AboutDialogOpenTriggeredMessage.class, this::onAboutDialogOpenTriggered)//
                .onMessage(SetGameMapMessage.class, this::onSetGameMap)//
                .onMessage(MapCursorUpdateMessage.class, this::onMapCursorUpdate)//
                .onMessage(MaterialsButtonsOpenTriggeredMessage.class, this::onMaterialsButtonsOpenTriggered)//
                .onMessage(MaterialsButtonsCloseTriggeredMessage.class, this::onMaterialsButtonsCloseTriggered)//
                .onMessage(ObjectsButtonsOpenTriggeredMessage.class, this::onObjectsButtonsOpenTriggered)//
                .onMessage(ObjectsButtonsCloseTriggeredMessage.class, this::onObjectsButtonsCloseTriggered)//
                .onMessage(DeleteButtonsOpenTriggeredMessage.class, this::onDeleteButtonsOpenTriggered)//
                .onMessage(DeleteButtonsCloseTriggeredMessage.class, this::onDeleteButtonsCloseTriggered)//
        ;
    }

}
