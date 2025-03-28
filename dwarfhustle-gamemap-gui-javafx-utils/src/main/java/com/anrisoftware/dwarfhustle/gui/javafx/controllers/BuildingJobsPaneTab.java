package com.anrisoftware.dwarfhustle.gui.javafx.controllers;

import com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;

import lombok.Data;

/**
 * Building jobs pane tab.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Data
public class BuildingJobsPaneTab implements ObjectPaneTab<JobsPaneController> {

    protected final int type;

    protected final long id;

    protected final ObjectsGetter og;

    protected final KnowledgeGetter kg;

    @Override
    public ObjectPaneTabController create() {
        return JavaFxUtil.loadFXML(getClass(), "/work_jobs_pane_ui.fxml");
    }

    @Override
    public void update(JobsPaneController c) {

    }

}
