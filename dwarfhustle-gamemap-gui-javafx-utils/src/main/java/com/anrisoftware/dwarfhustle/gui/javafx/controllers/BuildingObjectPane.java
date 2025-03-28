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

import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.api.buildings.Building;
import com.anrisoftware.dwarfhustle.model.api.buildings.KnowledgeBuilding;
import com.google.auto.service.AutoService;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.val;

/**
 * @see Building
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@NoArgsConstructor
@AutoService(ObjectPane.class)
public class BuildingObjectPane extends AbstractObjectPane {

    private BuildingJobsPaneTab jobsTab;

    public BuildingObjectPane(int type, ActorSystemProvider actor) {
        super(type, actor);
    }

    @Override
    public ObjectPane create(int type, ActorSystemProvider actor) {
        return new BuildingObjectPane(type, actor);
    }

    @Override
    public int getType() {
        return KnowledgeBuilding.TYPE.hashCode();
    }

    @Override
    public void update(long id, ObjectPaneController c) {
        super.update(id, c);
        val klo = kg.get(KnowledgeBuilding.TYPE.hashCode());
        Building go = og.get(type, id);
        var ko = klo.objects.detect(it -> it.getKid() == go.getKid());
        if (jobsTab == null) {
            this.jobsTab = new BuildingJobsPaneTab(type, id, og, kg);
            c.objectTabs.add(jobsTab);
        }
    }
}
