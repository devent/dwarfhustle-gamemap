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

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import com.anrisoftware.dwarfhustle.gui.controllers.GlobalKeys;
import com.anrisoftware.dwarfhustle.gui.controllers.JavaFxUtil;
import com.google.inject.Injector;
import com.jayfella.jme.jfx.JavaFxUI;
import com.jme3.app.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Loads the FXML and creates the panel controller.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class PanelControllerBuild {

    /**
     * Initializes the JavaFx UI and loads the FXML and creates the panel
     * controller.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class PanelControllerInitializeFxBuild extends PanelControllerBuild {

        @Inject
        PanelControllerInitializeFxBuild(Application app, GlobalKeys globalKeys) {
            super(app, globalKeys);
        }

        @Override
        @SneakyThrows
        protected void initializeFx(List<String> css) {
            var task = app.enqueue(() -> {
                JavaFxUI.initialize(app, css.toArray(new String[0]));
                return true;
            });
            task.get();
            globalKeys.setup(JavaFxUI.getInstance(), app.getInputManager());
        }
    }

    /**
     * Contains the loaded panel and controller.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @RequiredArgsConstructor
    @ToString
    public static class PanelControllerResult<T> {

        public final Region root;

        public final T controller;

    }

    protected final Application app;

    protected final GlobalKeys globalKeys;

    @Inject
    public PanelControllerBuild(Application app, GlobalKeys globalKeys) {
        this.app = app;
        this.globalKeys = globalKeys;
    }

    public <T> CompletableFuture<PanelControllerResult<T>> loadFxml(Injector injector, Executor executor,
            String fxmlfile, String... additionalCss) {
        return CompletableFuture.supplyAsync(() -> loadFxml0(injector, fxmlfile, additionalCss), executor);
    }

    @SneakyThrows
    private <T> PanelControllerResult<T> loadFxml0(Injector injector, String fxmlfile, String... additionalCss) {
        log.debug("loadFxml0 {} {}", fxmlfile, additionalCss);
        loadFont();
        var css = new ArrayList<String>();
        css.add(getCss());
        css.addAll(Arrays.asList(additionalCss));
        initializeFx(css);
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlfile));
        Pane root = loadFxml(loader, fxmlfile);
        T controller = loader.getController();
        injector.injectMembers(controller);
        return new PanelControllerResult<>(root, controller);
    }

    private void loadFont() {
        // Font.loadFont(MainPanelControllerBuild.class.getResource("/Fonts/Behrensschrift.ttf").toExternalForm(),
        // 14);
    }

    protected void initializeFx(List<String> css) {
        // call JavaFxUI.initialize if needed
    }

    @SneakyThrows
    private String getCss() {
        return IOUtils.resourceToURL("/dwarf_theme.css").toExternalForm();
    }

    @SneakyThrows
    private Pane loadFxml(FXMLLoader loader, String res) {
        return JavaFxUtil.runFxAndWait(10, SECONDS, () -> {
            log.debug("Load FXML file {}", res);
            return (Pane) loader.load(getClass().getResourceAsStream(res));
        });
    }

}
