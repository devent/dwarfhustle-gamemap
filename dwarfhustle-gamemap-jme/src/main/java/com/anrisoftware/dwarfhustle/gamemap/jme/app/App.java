/*
 * dwarfhustle-gamemap-jme - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.jme.app;

import static akka.actor.typed.javadsl.AskPattern.ask;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletionStage;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import com.anrisoftware.dwarfhustle.gamemap.jme.terrain.DebugCoordinateAxesState;
import com.anrisoftware.dwarfhustle.gamemap.jme.terrain.TerrainActor;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppCommand;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.AppErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gui.actor.GameMainPanelActor;
import com.anrisoftware.dwarfhustle.gui.messages.AttachGuiMessage;
import com.anrisoftware.dwarfhustle.gui.messages.AttachGuiMessage.AttachGuiFinishedMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.badlogic.ashley.core.Engine;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.LostFocusBehavior;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ConstantVerifierState;
import com.jme3.system.AppSettings;

import akka.actor.typed.ActorRef;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class App extends SimpleApplication {

    @Command(name = "dwarf-hustle game-map", mixinStandardHelpOptions = true)
    @Getter
    static class AppCommandImpl implements AppCommand, Runnable {

        @Option(names = { "-dir" }, paramLabel = "GAME-DIR", description = "the game directory")
        private File gamedir;

        @Option(names = { "-skip" }, paramLabel = "SKIP-LOAD", description = "skip loading of the world")
        private final boolean skipLoad = false;

        @Option(names = { "-db-server" }, paramLabel = "DB-SERVER", description = "the database server host")
        private final String dbServer = "";

        @Option(names = { "-db-user" }, paramLabel = "DB-USER", description = "the database user")
        private final String dbUser = "root";

        @Option(names = { "-db-password" }, paramLabel = "DB-PASSWORD", description = "the database password")
        private final String dbPassword = "admin";

        @Option(names = { "-db-name" }, paramLabel = "DB-NAME", description = "the database name")
        private final String dbName = "terrain_32_32_32";

        @Override
        public void run() {
            var injector = Guice.createInjector(new AppModule());
            var app = injector.getInstance(App.class);
            app.start(injector, this);
        }

    }

    public static void main(String[] args) {
        new CommandLine(new AppCommandImpl()).execute(args);
    }

    @Inject
    private Engine engine;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameSettingsProvider gsp;

    private Injector parent;

    private AppCommandImpl command;

    private Injector injector;

    public App() {
        super(new StatsAppState(), new ConstantVerifierState(), new DebugKeysAppState());
    }

    private void start(Injector parent, AppCommandImpl command) {
        this.parent = parent;
        this.command = command;
        setupApp();
        super.start();
    }

    @SneakyThrows
    private void setupApp() {
        this.injector = parent.createChildInjector(new GamemapJmeModule(this));
        getStateManager().attach(injector.getInstance(DebugCoordinateAxesState.class));
        gsp.load();
        setShowSettings(false);
        var s = new AppSettings(true);
        loadAppIcon(s);
        s.setResizable(true);
        s.setWidth(gsp.get().windowWidth.get());
        s.setHeight(gsp.get().windowHeight.get());
        s.setVSync(false);
        s.setOpenCLSupport(false);
        setLostFocusBehavior(LostFocusBehavior.PauseOnLostFocus);
        setSettings(s);
    }

    private void loadAppIcon(AppSettings s) throws IOException {
        var logo = ImageIO.read(getClass().getResource("/app/logo.png"));
        s.setIcons(new BufferedImage[] { logo });
        s.setTitle(IOUtils.toString(getClass().getResource("/app/title.txt"), UTF_8));
    }

    @Override
    public void simpleInitApp() {
        log.debug("simpleInitApp");
        createApp();
        createPanel();
    }

    private void createApp() {
        AppActor.create(injector, ofSeconds(1), command).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("AppActor.create", ex);
                actor.tell(new AppErrorMessage(ex));
            } else {
                log.debug("AppActor created");
            }
        });
    }

    private void createPanel() {
        GameMainPanelActor.create(injector, ofSeconds(1), actor.getObjectGetterAsync(StoredObjectsJcsCacheActor.ID))
                .whenComplete((ret, ex) -> {
                    if (ex != null) {
                        log.error("GameMainPanelActor.create", ex);
                        actor.tell(new AppErrorMessage(ex));
                    } else {
                        log.debug("GameMainPanelActor created");
                        attachGui(ret);
                    }
                });
    }

    @SuppressWarnings("unused")
    private void attachGui(ActorRef<Message> receiver) {
        CompletionStage<AttachGuiFinishedMessage> result = ask(receiver, AttachGuiMessage::new, ofMinutes(1),
                actor.getActorSystem().scheduler());
        result.whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("AttachGuiMessage", ex);
                actor.tell(new AppErrorMessage(ex));
            } else if (ret instanceof AttachGuiFinishedMessage rm) {
                log.debug("AttachGuiMessage {}", ret);
                inputManager.deleteMapping(INPUT_MAPPING_EXIT);
                createGameMap();
            }
        });
    }

    private void createGameMap() {
        TerrainActor.create(injector, ofSeconds(1), //
                actor.getObjectGetterAsync(StoredObjectsJcsCacheActor.ID), //
                actor.getObjectGetterAsync(MaterialAssetsCacheActor.ID), //
                actor.getObjectGetterAsync(ModelsAssetsCacheActor.ID)). //
                whenComplete((ret, ex) -> {
                    if (ex != null) {
                        log.error("TerrainActor.create", ex);
                        actor.tell(new AppErrorMessage(ex));
                    } else {
                        log.debug("TerrainActor created");
                    }
                });
    }

    @Override
    @SneakyThrows
    public void stop(boolean waitFor) {
        updateCammera(gsp);
        gsp.get().windowFullscreen.set(context.getSettings().isFullscreen());
        gsp.save();
        actor.get().tell(new ShutdownMessage());
        super.stop(waitFor);
    }

    private void updateCammera(GameSettingsProvider gsp) {
        var camera = getCamera();
        if (camera == null) {
            return;
        }
        gsp.get().windowWidth.set(camera.getWidth());
        gsp.get().windowHeight.set(camera.getHeight());
    }

    @Override
    public void simpleUpdate(float tpf) {
        engine.update(tpf);
    }
}
