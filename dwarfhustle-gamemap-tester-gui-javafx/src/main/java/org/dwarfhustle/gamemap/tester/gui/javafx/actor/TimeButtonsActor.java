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
import static com.anrisoftware.dwarfhustle.model.api.objects.GameMap.getGameMap;
import static com.anrisoftware.dwarfhustle.model.api.objects.WorldMap.getWorldMap;
import static org.dwarfhustle.gamemap.tester.gui.javafx.actor.AdditionalCss.ADDITIONAL_CSS;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.dwarfhustle.gamemap.tester.gui.javafx.controllers.TimeButtonsController;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.TimeButtonsCloseMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.TimeButtonsOpenMessage;
import org.eclipse.collections.api.factory.Maps;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.AbstractPaneActor;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.PanelActorCreator;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.PanelControllerBuild;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AttachGuiMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameQuitMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.MainWindowResizedMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.states.KeyMapping;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.api.objects.WorldMap;
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
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Time buttons loaded from {@code tester_time_buttons.fxml}.
 *
 * @author Erwin Müller
 */
@Slf4j
public class TimeButtonsActor extends AbstractPaneActor<TimeButtonsController> {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            TimeButtonsActor.class.getSimpleName());

    public static final String NAME = TimeButtonsActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    private static final Map<String, PanelActorCreator> panelActors = Maps.mutable.empty();

    static {
    }

    /**
     *
     * @author Erwin Müller <erwin@muellerpublic.de>
     */
    public interface TimeButtonsActorFactory extends AbstractPaneActorFactory<TimeButtonsController> {
    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        return AbstractPaneActor.create(injector, timeout, ID, KEY, NAME, TimeButtonsActorFactory.class,
                "/tester_time_buttons.fxml", panelActors, PanelControllerBuild.class, ADDITIONAL_CSS);
    }

    @Inject
    private GameSettingsProvider gs;

    @Inject
    private GlobalKeys globalKeys;

    @Inject
    @Named("keyMappings")
    private Map<String, KeyMapping> keyMappings;

    @Inject
    @Named("AppTexts")
    private Texts appTexts;

    @Inject
    @Named("AppIcons")
    private Images appIcons;

    @Inject
    private ActorSystemProvider actor;

    private TimeButtonsController controller;

    private ObjectsGetter og;

    private ObjectsSetter os;

    /**
     * @see AttachGuiMessage
     */
    @Override
    protected Behavior<Message> onAttachGui(AttachGuiMessage m) {
        log.debug("onAttachGui {}", m);
        this.og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
        this.os = actor.getObjectSetterAsyncNow(StoredObjectsJcsCacheActor.ID);
        return getBehaviorAfterAttachGui().build();
    }

    @Override
    protected void currentMapSetup() {
        runFxThread(() -> {
            val gm = getGameMap(og, currentMap);
            val wm = getWorldMap(og, gm.getWorld());
            is.controller.setTime(wm, gm, gs);
            is.controller.setSaveTime(TimeButtonsActor.this::saveTime);
        });
    }

    /**
     * Processing {@link TimeButtonsOpenMessage}.
     */
    private Behavior<Message> onTimeButtonsOpen(TimeButtonsOpenMessage m) {
        log.debug("onTimeButtonsOpen {}", m);
        runFxThread(() -> {
            m.testerButtonsBox.getChildren().add(0, controller.timeBox);
            m.testerButtonsBox.requestLayout();
        });
        return Behaviors.same();
    }

    /**
     * Processing {@link TimeButtonsCloseMessage}.
     */
    private Behavior<Message> onTimeButtonsClose(TimeButtonsCloseMessage m) {
        log.debug("onTimeButtonsClose {}", m);
        runFxThread(() -> {
            m.testerButtonsBox.getChildren().remove(controller.timeBox);
            m.testerButtonsBox.requestLayout();
        });
        return Behaviors.same();
    }

    @Override
    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        final var c = is.controller;
        this.controller = c;
        runFxThread(() -> {
            c.updateLocale(Locale.US, appTexts, appIcons, IconSize.SMALL);
            c.initListeners(globalKeys, keyMappings);
            c.setOnMouseEnteredGui(entered -> {
                gs.get().mouseEnteredGui.set(entered);
            });
        });
        return super.getBehaviorAfterAttachGui()//
                .onMessage(TimeButtonsOpenMessage.class, this::onTimeButtonsOpen)//
                .onMessage(TimeButtonsCloseMessage.class, this::onTimeButtonsClose)//
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

    private void saveTime(ZonedDateTime time) {
        val gm = getGameMap(og, currentMap);
        val wm = getWorldMap(og, gm.getWorld());
        wm.setTime(LocalDateTime.ofInstant(time.toInstant(), ZoneId.of("UTC+00:00")));
        os.set(WorldMap.OBJECT_TYPE, wm);
    }
}
