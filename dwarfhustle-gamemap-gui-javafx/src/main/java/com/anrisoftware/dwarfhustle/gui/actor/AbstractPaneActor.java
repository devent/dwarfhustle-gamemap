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

import static com.anrisoftware.dwarfhustle.gui.controllers.JavaFxUtil.runFxThread;
import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.GameTickMessage;
import com.anrisoftware.dwarfhustle.gui.actor.PanelControllerBuild.PanelControllerInitializeFxBuild;
import com.anrisoftware.dwarfhustle.gui.actor.PanelControllerBuild.PanelControllerResult;
import com.anrisoftware.dwarfhustle.gui.messages.AttachGuiMessage;
import com.anrisoftware.dwarfhustle.gui.messages.AttachGuiMessage.AttachGuiFinishedMessage;
import com.anrisoftware.dwarfhustle.gui.messages.GameQuitMessage;
import com.anrisoftware.dwarfhustle.gui.messages.MainWindowResizedMessage;
import com.anrisoftware.dwarfhustle.gui.states.MainPanelState;
import com.anrisoftware.dwarfhustle.gui.states.PanelComponent;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.badlogic.ashley.core.Engine;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jayfella.jme.jfx.JavaFxUI;
import com.jme3.app.Application;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.javadsl.StashOverflowException;
import akka.actor.typed.receptionist.ServiceKey;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Main panel actor.
 *
 * @author Erwin Müller
 */
@Slf4j
public abstract class AbstractPaneActor<T> {

    public interface AbstractPaneActorFactory<T> {

        AbstractPaneActor<? extends T> create(ActorContext<Message> context, StashBuffer<Message> buffer);
    }

    @RequiredArgsConstructor
    @ToString
    private static class SetupUiErrorMessage extends Message {

        public final Throwable cause;

    }

    public static <T> Behavior<Message> create(Injector injector,
            Class<? extends AbstractPaneActorFactory<T>> paneActorFactoryType, String mainUiResource,
            Map<String, PanelActorCreator> panelActors, Class<? extends PanelControllerBuild> panelControllerBuildClass,
            String... additionalCss) {
        return Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            startJavafxBuild(injector, context, mainUiResource, panelActors, panelControllerBuildClass, additionalCss);
            return injector.getInstance(paneActorFactoryType).create(context, stash).start(injector);
        }));
    }

    public static <T> Behavior<Message> create(Injector injector,
            Class<? extends AbstractPaneActorFactory<T>> paneActorFactoryType, String mainUiResource,
            Map<String, PanelActorCreator> panelActors, String... additionalCss) {
        return Behaviors.withStash(100, stash -> Behaviors.setup(context -> {
            startJavafxBuild(injector, context, mainUiResource, panelActors, additionalCss);
            return injector.getInstance(paneActorFactoryType).create(context, stash).start(injector);
        }));
    }

    private static void startJavafxBuild(Injector injector, ActorContext<Message> context, String mainUiResource,
            Map<String, PanelActorCreator> panelActors, Class<? extends PanelControllerBuild> panelControllerBuildClass,
            String... additionalCss) {
        startJavafxBuild0(injector, context, mainUiResource, panelActors, panelControllerBuildClass, additionalCss);
    }

    private static void startJavafxBuild(Injector injector, ActorContext<Message> context, String mainUiResource,
            Map<String, PanelActorCreator> panelActors, String... additionalCss) {
        startJavafxBuild0(injector, context, mainUiResource, panelActors, PanelControllerInitializeFxBuild.class,
                additionalCss);
    }

    private static <T> void startJavafxBuild0(Injector injector, ActorContext<Message> context, String mainUiResource,
            Map<String, PanelActorCreator> panelActors, Class<? extends PanelControllerBuild> panelControllerBuildClass,
            String... additionalCss) {
        var build = injector.getInstance(panelControllerBuildClass);
        context.pipeToSelf(build.loadFxml(injector, context.getExecutionContext(), mainUiResource, additionalCss),
                (result, cause) -> {
                    if (cause == null) {
                        var actors = spawnPanelActors(injector, context, panelActors, result);
                        log.debug("build.loadFxml done");
                        return new InitialStateMessage<>(result.controller, result.root, actors);
                    } else {
                        log.error("build.loadFxml", cause);
                        return new SetupUiErrorMessage(cause);
                    }
                });
    }

    private static <T> ImmutableMap<String, ActorRef<Message>> spawnPanelActors(Injector injector,
            ActorContext<Message> context, Map<String, PanelActorCreator> panelActors,
            PanelControllerResult<T> result) {
        MutableMap<String, ActorRef<Message>> actors = Maps.mutable.empty();
        panelActors.forEach((name, a) -> {
            var actor = context.spawn(a.create(injector), name);
            actors.put(name, actor);
        });
        return actors.toImmutable();
    }

    public static <T> CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout, int id,
            ServiceKey<Message> key, String name,
            Class<? extends AbstractPaneActorFactory<T>> mainPanelActorFactoryType, String mainUiResource,
            Map<String, PanelActorCreator> panelActors, String... additionalCss) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, id, key, name,
                create(injector, mainPanelActorFactoryType, mainUiResource, panelActors, additionalCss));
    }

    public static <T> CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout, int id,
            ServiceKey<Message> key, String name,
            Class<? extends AbstractPaneActorFactory<T>> mainPanelActorFactoryType, String mainUiResource,
            Map<String, PanelActorCreator> panelActors, Class<? extends PanelControllerBuild> panelControllerBuildClass,
            String... additionalCss) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, id, key, name, create(injector, mainPanelActorFactoryType,
                mainUiResource, panelActors, panelControllerBuildClass, additionalCss));
    }

    @Inject
    @Assisted
    protected ActorContext<Message> context;

    @Inject
    @Assisted
    private StashBuffer<Message> buffer;

    @Inject
    protected Application app;

    @Inject
    private MainPanelState mainPanelState;

    @Inject
    protected Engine engine;

    protected InitialStateMessage<T> initial;

    protected Injector injector;

    public Behavior<Message> start(Injector injector) {
        this.injector = injector;
        return Behaviors.receive(Message.class)//
                .onMessage(InitialStateMessage.class, this::onInitialState)//
                .onMessage(SetupUiErrorMessage.class, this::onSetupUiError)//
                .onMessage(GameTickMessage.class, this::onIgnore)//
                .onMessage(Message.class, this::stashOtherCommand)//
                .build();
    }

    /**
     * Ignores the message.
     */
    private Behavior<Message> onIgnore(Message m) {
        return Behaviors.same();
    }

    /**
     * Unstash all messages in the buffer.
     */
    private Behavior<Message> onInitialState(InitialStateMessage<T> m) {
        log.debug("onInitialState");
        this.initial = m;
        initial.actors.forEachValue(a -> a.tell(m));
        return buffer.unstashAll(Behaviors.receive(Message.class)//
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(AttachGuiMessage.class, this::onAttachGui)//
                .build());
    }

    /**
     * Throws setup errors.
     */
    @SneakyThrows
    private Behavior<Message> onSetupUiError(SetupUiErrorMessage m) {
        log.error("onSetupUiError", m);
        throw m.cause;
    }

    private Behavior<Message> stashOtherCommand(Message m) {
        log.debug("stashOtherCommand: {}", m);
        try {
            buffer.stash(m);
        } catch (StashOverflowException e) {
        }
        return Behaviors.same();
    }

    protected Behavior<Message> onShutdown(ShutdownMessage m) {
        log.debug("onShutdown {}", m);
        Platform.exit();
        return Behaviors.stopped();
    }

    protected Behavior<Message> onAttachGui(AttachGuiMessage m) {
        log.debug("onAttachGui {}", m);
        runFxThread(() -> {
            setupUi();
            initial.actors.forEachValue(a -> a.tell(m));
        });
        app.enqueue(() -> {
            attachPaneState();
        });
        if (m.replyTo != null) {
            m.replyTo.tell(new AttachGuiFinishedMessage());
        }
        return getBehaviorAfterAttachGui().build();
    }

    protected void attachPaneState() {
        app.getStateManager().attach(mainPanelState);
        var entity = engine.createEntity();
        entity.add(new PanelComponent(context.getSelf()));
        engine.addEntity(entity);
    }

    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        return Behaviors.receive(Message.class)//
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(GameQuitMessage.class, this::onGameQuit)//
                .onMessage(MainWindowResizedMessage.class, this::onMainWindowResized)//
        ;
    }

    protected void setupUi() {
        var pane = initial.root;
        pane.setPrefSize(app.getCamera().getWidth(), app.getCamera().getHeight());
        JavaFxUI.getInstance().attachChild(pane);
    }

    protected Behavior<Message> onGameQuit(GameQuitMessage m) {
        log.debug("onGameQuit {}", m);
        app.enqueue(() -> app.stop());
        return Behaviors.same();
    }

    protected Behavior<Message> onMainWindowResized(MainWindowResizedMessage m) {
        log.debug("onMainWindowResized {}", m);
        runFxThread(() -> {
            initial.root.setPrefSize(m.width, m.height);
        });
        return Behaviors.same();
    }

}
