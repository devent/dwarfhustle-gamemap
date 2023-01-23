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
package com.anrisoftware.dwarfhustle.gamemap.jme;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CompletionStage;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import com.anrisoftware.dwarfhustle.gamemap.model.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gui.actor.GameMainPanelActor;
import com.anrisoftware.dwarfhustle.gui.messages.AttachGuiMessage;
import com.anrisoftware.dwarfhustle.gui.messages.AttachGuiMessage.AttachGuiFinishedMessage;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.badlogic.ashley.core.Engine;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jme3.app.LostFocusBehavior;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ConstantVerifierState;
import com.jme3.system.AppSettings;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.AskPattern;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class App extends SimpleApplication {

	public static void main(String[] args) {
		var injector = Guice.createInjector(new AppModule());
		var app = injector.getInstance(App.class);
		app.start(injector);
	}

	@Inject
	private Engine engine;

	@Inject
	private ActorSystemProvider actor;

	@Inject
	private GameSettingsProvider gsp;

	private Injector parent;

	private Injector injector;

	private ActorRef<Message> mainWindowActor;

	public App() {
		super(new ConstantVerifierState());
	}

	private void start(Injector parent) {
		this.parent = parent;
		setupApp();
		super.start();
	}

	@SneakyThrows
	private void setupApp() {
		this.injector = parent.createChildInjector(new GamemapJmeModule(this));
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
		BufferedImage logo = ImageIO.read(getClass().getResource("/app/logo.png"));
		s.setIcons(new BufferedImage[] { logo });
		s.setTitle(IOUtils.toString(getClass().getResource("/app/title.txt"), UTF_8));
	}

	@Override
	public void simpleInitApp() {
		log.debug("simpleInitApp");
		GameMainPanelActor.create(injector, ofSeconds(1)).whenComplete((ret, ex) -> {
			mainWindowActor = ret;
			CompletionStage<AttachGuiFinishedMessage> result = AskPattern.ask(mainWindowActor,
					replyTo -> new AttachGuiMessage(replyTo), ofMinutes(1), actor.getActorSystem().scheduler());
			result.whenComplete((ret1, ex1) -> {
				inputManager.deleteMapping(INPUT_MAPPING_EXIT);
			});
		});
	}

	@Override
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
		gsp.get().setCameraPos(camera.getLocation());
		gsp.get().setCameraRot(camera.getRotation());
		gsp.get().windowWidth.set(camera.getWidth());
		gsp.get().windowHeight.set(camera.getHeight());
	}

	@Override
	public void simpleUpdate(float tpf) {
		engine.update(tpf);
	}
}
