package com.anrisoftware.dwarfhustle.gui.javafx.controllers;

import static java.time.Duration.ofSeconds;

import com.anrisoftware.dwarfhustle.gui.javafx.actor.JobsPanelActor;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.JobsPanelActor.AttachJobsPanelMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AttachGuiMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.google.inject.Injector;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Building jobs pane tab.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Data
@Slf4j
public class BuildingJobsPaneTab implements ObjectPaneTab {

    protected final int type;

    protected final long id;

    protected final ObjectsGetter og;

    protected final KnowledgeGetter kg;

    @Override
    @SneakyThrows
    public void create(Injector injector, TabPane tabPane) {
        JobsPanelActor.create(injector, ofSeconds(1)).whenComplete((res, ex) -> {
            if (ex == null) {
                res.tell(new AttachGuiMessage(null));
                JavaFxUtil.runFxThread(() -> {
                    val tab = new Tab();
                    tab.setText("Jobs");
                    res.tell(new AttachJobsPanelMessage(tab));
                    tabPane.getTabs().add(0, tab);
                    tabPane.getSelectionModel().select(0);
                });
            } else {
                log.error("JobsPanelActor", ex);
            }
        }).toCompletableFuture().get();
    }

}
