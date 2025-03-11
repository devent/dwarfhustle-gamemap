/*
 * dwarfhustle-gamemap-tester-gui-javafx - Game map.
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
import static org.dwarfhustle.gamemap.tester.gui.javafx.actor.AdditionalCss.ADDITIONAL_CSS;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.dwarfhustle.gamemap.tester.gui.javafx.controllers.MaterialsButtonsController;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialsButtonsCloseMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.MaterialsButtonsOpenMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsCloseMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsOpenMessage;
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
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
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
import lombok.extern.slf4j.Slf4j;

/**
 * Materials buttons loaded from {@code tester_materials_buttons.fxml}.
 *
 * @author Erwin Müller
 */
@Slf4j
public class MaterialsButtonsActor extends AbstractPaneActor<MaterialsButtonsController> {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            MaterialsButtonsActor.class.getSimpleName());

    public static final String NAME = MaterialsButtonsActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    private static final Map<String, PanelActorCreator> panelActors = Maps.mutable.empty();

    static {
    }

    /**
     *
     * @author Erwin Müller <erwin@muellerpublic.de>
     */
    public interface MaterialsButtonsActorFactory extends AbstractPaneActorFactory<MaterialsButtonsController> {
    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        return AbstractPaneActor.create(injector, timeout, ID, KEY, NAME, MaterialsButtonsActorFactory.class,
                "/tester_materials_buttons.fxml", panelActors, PanelControllerBuild.class, ADDITIONAL_CSS);
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

    private MaterialsButtonsController controller;

    @Override
    protected Behavior<Message> onAttachGui(AttachGuiMessage m) {
        log.debug("onAttachGui {}", m);
        return getBehaviorAfterAttachGui().build();
    }

    /**
     * Processing {@link MaterialsButtonsOpenMessage}.
     */
    private Behavior<Message> onMaterialsButtonsOpen(MaterialsButtonsOpenMessage m) {
        log.debug("onMaterialsButtonsOpen {}", m);
        runFxThread(() -> {
            m.testerButtonsBox.getChildren().add(0, controller.materialsBox);
            m.testerButtonsBox.requestLayout();
        });
        return Behaviors.same();
    }

    /**
     * Processing {@link MaterialsButtonsCloseMessage}.
     */
    private Behavior<Message> onMaterialsButtonsClose(MaterialsButtonsCloseMessage m) {
        log.debug("onMaterialsButtonsClose {}", m);
        runFxThread(() -> {
            m.testerButtonsBox.getChildren().remove(controller.materialsBox);
            m.testerButtonsBox.requestLayout();
        });
        return Behaviors.same();
    }

    /**
     * Processing {@link ObjectsButtonsOpenMessage}.
     */
    private Behavior<Message> onObjectsButtonsOpen(ObjectsButtonsOpenMessage m) {
        log.debug("onObjectsButtonsOpen {}", m);
        runFxThread(() -> {
            m.testerButtonsBox.requestLayout();
        });
        return Behaviors.same();
    }

    /**
     * Processing {@link ObjectsButtonsCloseMessage}.
     */
    private Behavior<Message> onObjectsButtonsClose(ObjectsButtonsCloseMessage m) {
        log.debug("onObjectsButtonsClose {}", m);
        runFxThread(() -> {
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
            c.setOnMouseEnteredGui((entered) -> {
                gs.get().mouseEnteredGui.set(entered);
            });
        });
        return super.getBehaviorAfterAttachGui()//
                .onMessage(MaterialsButtonsOpenMessage.class, this::onMaterialsButtonsOpen)//
                .onMessage(MaterialsButtonsCloseMessage.class, this::onMaterialsButtonsClose)//
                .onMessage(ObjectsButtonsOpenMessage.class, this::onObjectsButtonsOpen)//
                .onMessage(ObjectsButtonsCloseMessage.class, this::onObjectsButtonsClose)//
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
