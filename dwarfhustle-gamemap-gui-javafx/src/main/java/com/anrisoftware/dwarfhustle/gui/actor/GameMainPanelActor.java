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

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.collections.impl.factory.Maps;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.SetGameMapMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
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
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;
import com.google.inject.Injector;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Noise main panel actor.
 *
 * @author Erwin Müller
 */
@Slf4j
public class GameMainPanelActor extends AbstractPaneActor {

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

    public interface GameMainPanelActorFactory extends AbstractPaneActorFactory {

    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        return AbstractPaneActor.create(injector, timeout, ID, KEY, NAME, GameMainPanelActorFactory.class,
                "/main_ui.fxml", panelActors, ADDITIONAL_CSS);
    }

    @Inject
    @Named("AppTexts")
    private Texts appTexts;

    @Inject
    @Named("AppIcons")
    private Images appIcons;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameSettingsProvider gsp;

    @Inject
    private GlobalKeys globalKeys;

    @Inject
    @Named("keyMappings")
    private Map<String, KeyMapping> keyMappings;

    @Override
    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        StatusActor.create(injector, Duration.ofSeconds(1), (MainPaneController) initial.controller);
        InfoPanelActor.create(injector, Duration.ofSeconds(1)).whenComplete((v, err) -> {
            if (err == null) {
                v.tell(new AttachGuiMessage(null));
            }
        });
        runFxThread(() -> {
            var controller = (MainPaneController) initial.controller;
            controller.updateLocale(Locale.US, appTexts, appIcons, IconSize.SMALL, gsp.get());
            controller.initListeners(actor.get(), gsp.get());
            controller.initButtons(globalKeys, keyMappings, gsp.get());
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
            var controller = (MainPaneController) initial.controller;
            controller.setGameMap(m.gm);
        });
        return Behaviors.same();
    }

    private BehaviorBuilder<Message> getDefaultBehavior() {
        return super.getBehaviorAfterAttachGui()//
                .onMessage(SettingsDialogOpenTriggeredMessage.class, this::onSettingsDialogOpenTriggered)//
                .onMessage(AboutDialogOpenTriggeredMessage.class, this::onAboutDialogOpenTriggered)//
                .onMessage(SetGameMapMessage.class, this::onSetGameMap)//
        ;
    }

}
