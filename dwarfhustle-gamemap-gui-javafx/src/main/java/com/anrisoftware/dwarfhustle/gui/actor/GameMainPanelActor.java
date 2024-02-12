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
package com.anrisoftware.dwarfhustle.gui.actor;

import static com.anrisoftware.dwarfhustle.gui.actor.AdditionalCss.ADDITIONAL_CSS;
import static com.anrisoftware.dwarfhustle.gui.controllers.JavaFxUtil.runFxThread;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import org.eclipse.collections.impl.factory.Maps;
import org.scenicview.ScenicView;

import com.anrisoftware.dwarfhustle.gamemap.console.actor.OpenSceneMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapCursorUpdateMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gui.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.controllers.MainPaneController;
import com.anrisoftware.dwarfhustle.gui.messages.AboutDialogMessage;
import com.anrisoftware.dwarfhustle.gui.messages.AboutDialogMessage.AboutDialogOpenTriggeredMessage;
import com.anrisoftware.dwarfhustle.gui.messages.AttachGuiMessage;
import com.anrisoftware.dwarfhustle.gui.messages.AttachGuiMessage.AttachGuiFinishedMessage;
import com.anrisoftware.dwarfhustle.gui.messages.SettingsDialogMessage;
import com.anrisoftware.dwarfhustle.gui.messages.SettingsDialogMessage.SettingsDialogOpenTriggeredMessage;
import com.anrisoftware.dwarfhustle.gui.states.KeyMapping;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
import com.anrisoftware.dwarfhustle.model.db.cache.CachePutMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheResponseMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;
import com.google.inject.Injector;
import com.jayfella.jme.jfx.JavaFxUI;
import com.jme3.app.Application;

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
 * Noise main panel actor.
 *
 * @author Erwin Müller
 */
@Slf4j
public class GameMainPanelActor extends AbstractPaneActor<MainPaneController> {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            GameMainPanelActor.class.getSimpleName());

    public static final String NAME = GameMainPanelActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    private static final Map<String, PanelActorCreator> panelActors = Maps.mutable.empty();

    static {
    }

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

    public interface GameMainPanelActorFactory extends AbstractPaneActorFactory<MainPaneController> {

    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout,
            CompletionStage<ObjectsGetter> og) {
        return AbstractPaneActor.create(injector, timeout, ID, KEY, NAME, og, GameMainPanelActorFactory.class,
                "/main_ui.fxml", panelActors, ADDITIONAL_CSS);
    }

    @Inject
    @Named("AppTexts")
    private Texts appTexts;

    @Inject
    @Named("AppIcons")
    private Images appIcons;

    @Inject
    private Application app;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GlobalKeys globalKeys;

    @Inject
    @Named("keyMappings")
    private Map<String, KeyMapping> keyMappings;

    private boolean scenicViewShown = false;

    @SuppressWarnings("rawtypes")
    private ActorRef<CacheResponseMessage> cacheResponseAdapter;

    private ActorRef<Message> objectsActor;

    @SneakyThrows
    @Override
    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        this.cacheResponseAdapter = context.messageAdapter(CacheResponseMessage.class, WrappedCacheResponse::new);
        this.objectsActor = actor.getActorAsync(StoredObjectsJcsCacheActor.ID).toCompletableFuture().get(1,
                TimeUnit.SECONDS);
        StatusActor.create(injector, Duration.ofSeconds(1), initial.controller);
        InfoPanelActor.create(injector, Duration.ofSeconds(1), supplyAsync(() -> og)).whenComplete((v, err) -> {
            if (err == null) {
                v.tell(new AttachGuiMessage(null));
            }
        });
        runFxThread(() -> {
            var controller = initial.controller;
            controller.updateLocale(Locale.US, appTexts, appIcons, IconSize.SMALL);
            controller.initListeners(this::saveGameMap);
            controller.initButtons(globalKeys, keyMappings);
        });
        return getDefaultBehavior()//
        ;
    }

    private void saveGameMap(GameMap gm) {
        objectsActor.tell(new CachePutMessage<>(cacheResponseAdapter, gm.getId(), gm));
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
            var wm = og.get(WorldMap.class, WorldMap.OBJECT_TYPE, m.gm.world);
            controller.setMap(wm, m.gm);
        });
        return Behaviors.same();
    }

    /**
     * Processing {@link OpenSceneMessage}.
     * <p>
     * Opens the JavaFX scene viewer.
     * <p>
     * Returns a behavior that reacts to the following messages:
     * <ul>
     * <li>{@link #getBehaviorAfterAttachGui()}
     * </ul>
     */
    private Behavior<Message> onOpenScene(OpenSceneMessage m) {
        log.debug("onOpenScene {}", m);
        runFxThread(() -> {
            if (scenicViewShown) {
                return;
            }
            ScenicView.show(JavaFxUI.getInstance().getScene());
            app.setPauseOnLostFocus(false);
            scenicViewShown = true;
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

    private BehaviorBuilder<Message> getDefaultBehavior() {
        return super.getBehaviorAfterAttachGui()//
                .onMessage(SettingsDialogOpenTriggeredMessage.class, this::onSettingsDialogOpenTriggered)//
                .onMessage(AboutDialogOpenTriggeredMessage.class, this::onAboutDialogOpenTriggered)//
                .onMessage(SetGameMapMessage.class, this::onSetGameMap)//
                .onMessage(OpenSceneMessage.class, this::onOpenScene)//
                .onMessage(MapCursorUpdateMessage.class, this::onMapCursorUpdate)//
        ;
    }

}
