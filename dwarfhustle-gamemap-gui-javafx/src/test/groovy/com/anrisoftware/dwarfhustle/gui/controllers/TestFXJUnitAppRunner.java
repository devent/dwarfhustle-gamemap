package com.anrisoftware.dwarfhustle.gui.controllers;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.scenicview.ScenicView;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.robot.Motion;
import org.testfx.util.WaitForAsyncUtils;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMapPos;
import com.anrisoftware.dwarfhustle.model.api.objects.MapTile;
import com.anrisoftware.dwarfhustle.model.api.objects.Person;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class TestFXJUnitAppRunner extends ApplicationTest {

    private static Injector injector;

    private InfoPaneController controller;

    private Scene scene;

    @BeforeAll
    public static void setup() {
        injector = Guice.createInjector(new AbstractModule() {
        });
    }

    @BeforeEach
    public void runAppToTests() throws Exception {
        FxToolkit.registerPrimaryStage();
        FxToolkit.showStage();
        WaitForAsyncUtils.waitForFxEvents(100);
    }

    @AfterEach
    public void stopApp() throws TimeoutException {
        FxToolkit.cleanupStages();
    }

    @Override
    public void start(Stage primaryStage) {
        loadInfoPane(primaryStage);
        primaryStage.show();
    }

    @SneakyThrows
    private void loadInfoPane(Stage primaryStage) {
        var loader = new FXMLLoader(getClass().getResource("/info_pane_ui.fxml"));
        primaryStage.setScene(scene = new Scene(loader.load(), 300, 300));
        scene.getStylesheets().add("dwarf_theme.css");
        controller = loader.getController();
        injector.injectMembers(controller);
        controller.setup(injector);
    }

    @Test
    public void testBlueHasOnlyOneEntry() {
        interact(() -> {
            var mt = new MapTile(1);
            mt.setMaterial("Soil");
            mt.setPos(new GameMapPos(1, 5, 5, 5));
            var p = new Person(1);
            p.setFirstName("Gorbir");
            p.setLastName("Shatterfeet");
            controller.items.clear();
            controller.items.add(new MapTileItem(mt));
            controller.items.add(new MapTileItem(p));
        });
        clickOn(controller.infoBox, Motion.DIRECT, MouseButton.PRIMARY);
        interact(() -> ScenicView.show(scene));
        sleep(600, TimeUnit.SECONDS);
    }
}
