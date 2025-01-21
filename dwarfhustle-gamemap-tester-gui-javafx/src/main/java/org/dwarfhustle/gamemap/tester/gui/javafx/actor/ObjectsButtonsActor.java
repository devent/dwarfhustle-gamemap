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
import static org.dwarfhustle.gamemap.tester.gui.javafx.actor.AdditionalCss.ADDITIONAL_CSS;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.dwarfhustle.gamemap.tester.gui.javafx.controllers.ObjectsButtonsController;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsCloseMessage;
import org.dwarfhustle.gamemap.tester.gui.javafx.messages.ObjectsButtonsOpenMessage;
import org.eclipse.collections.api.factory.Lists;
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
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.vegetations.Grass;
import com.anrisoftware.dwarfhustle.model.api.vegetations.Shrub;
import com.anrisoftware.dwarfhustle.model.api.vegetations.Tree;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
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
 * Objects buttons loaded from {@code tester_objects_buttons.fxml}.
 *
 * @author Erwin Müller
 */
@Slf4j
public class ObjectsButtonsActor extends AbstractPaneActor<ObjectsButtonsController> {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            ObjectsButtonsActor.class.getSimpleName());

    public static final String NAME = ObjectsButtonsActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    private static final Map<String, PanelActorCreator> panelActors = Maps.mutable.empty();

    static {
    }

    /**
     *
     * @author Erwin Müller <erwin@muellerpublic.de>
     */
    public interface ObjectsButtonsActorFactory extends AbstractPaneActorFactory<ObjectsButtonsController> {
    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        return AbstractPaneActor.create(injector, timeout, ID, KEY, NAME, ObjectsButtonsActorFactory.class,
                "/tester_objects_buttons.fxml", panelActors, PanelControllerBuild.class, ADDITIONAL_CSS);
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

    private ObjectsButtonsController controller;

    @Override
    protected Behavior<Message> onAttachGui(AttachGuiMessage m) {
        log.debug("onAttachGui {}", m);
        return getBehaviorAfterAttachGui().build();
    }

    /**
     * Processing {@link ObjectsButtonsOpenMessage}.
     */
    private Behavior<Message> onObjectsButtonsOpen(ObjectsButtonsOpenMessage m) {
        log.debug("onObjectsButtonsOpen {}", m);
        runFxThread(() -> {
            m.testerButtonsBox.getChildren().add(0, controller.objectsBox);
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
            m.testerButtonsBox.getChildren().remove(controller.objectsBox);
            m.testerButtonsBox.requestLayout();
        });
        return Behaviors.same();
    }

    @Override
    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        var kg = actor.getKnowledgeGetterAsyncNow(PowerLoomKnowledgeActor.ID);
        Map<String, List<String>> tabsButtons = Maps.mutable.empty();
        final var c = initial.controller;
        this.controller = c;
        collectObjects(tabsButtons, kg, "grass", Grass.OBJECT_TYPE);
        collectObjects(tabsButtons, kg, "shrub", Shrub.OBJECT_TYPE);
        collectObjects(tabsButtons, kg, "tree", Tree.OBJECT_TYPE);
        runFxThread(() -> {
            c.createButtons(tabsButtons);
            c.updateLocale(Locale.US, appTexts, appIcons, IconSize.SMALL);
            c.initListeners(globalKeys, keyMappings);
            c.setOnMouseEnteredGui((entered) -> {
                gs.get().mouseEnteredGui.set(entered);
            });
        });
        return super.getBehaviorAfterAttachGui()//
                .onMessage(ObjectsButtonsOpenMessage.class, this::onObjectsButtonsOpen)//
                .onMessage(ObjectsButtonsCloseMessage.class, this::onObjectsButtonsClose)//
        ;
    }

    private void collectObjects(Map<String, List<String>> tabsButtons, KnowledgeGetter kg, String name, int type) {
        var objects = kg.get(type);
        List<String> buttons = Lists.mutable.withInitialCapacity(objects.objects.size());
        tabsButtons.put(name, buttons);
        for (var ko : objects.objects) {
            buttons.add(ko.name);
        }
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
