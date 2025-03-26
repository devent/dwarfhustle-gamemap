/*
 * dwarfhustle-gamemap-gui-javafx-utils - Game map.
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
package com.anrisoftware.dwarfhustle.gui.javafx.controllers;

import com.anrisoftware.dwarfhustle.model.api.buildings.KnowledgeWorkJob;
import com.anrisoftware.dwarfhustle.model.api.buildings.WorkJob;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.google.auto.service.AutoService;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @see WorkJob
 * @see KnowledgeWorkJob
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@NoArgsConstructor
@AutoService(WorkJobPaneItem.class)
public class BuildingWorkJobPaneItem extends AbstractWorkJobPaneItem {

    public BuildingWorkJobPaneItem(WorkJob job, KnowledgeGetter kg, boolean selected) {
        super(job, kg, selected);
    }

    @Override
    public void update(JobItemPaneController controller) {
        super.update(controller);
    }

    @Override
    public AbstractWorkJobPaneItem create(WorkJob go, KnowledgeGetter kg, boolean selected) {
        return new BuildingWorkJobPaneItem(go, kg, selected);
    }

    @Override
    public int getType() {
        return WorkJob.OBJECT_TYPE;
    }

}
