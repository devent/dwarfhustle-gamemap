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

import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeTreeSapling;
import com.anrisoftware.dwarfhustle.model.api.vegetations.TreeSapling;
import com.google.auto.service.AutoService;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.val;

/**
 * @see TreeSapling
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@NoArgsConstructor
@AutoService(GameMapObjectInfoPaneItem.class)
public class TreeSaplingInfoPaneItem extends AbstractVegetationInfoPaneItem {

    public TreeSaplingInfoPaneItem(int type, long id, ObjectsGetter og, KnowledgeGetter kg, boolean selected) {
        super(type, id, og, kg, selected);
    }

    @Override
    public void update(MapBlockItemWidgetController controller) {
        super.update(controller);
        val klo = kg.get(KnowledgeTreeSapling.TYPE.hashCode());
        TreeSapling go = og.get(type, id);
        var ko = klo.objects.detect(it -> it.getKid() == go.getKid());
        controller.objectInfoTitle.setText(ko.getName());
    }

    @Override
    public AbstractGameMapObjectInfoPaneItem create(int type, long id, ObjectsGetter og, KnowledgeGetter kg,
            boolean selected) {
        return new TreeSaplingInfoPaneItem(type, id, og, kg, selected);
    }

    @Override
    public int getType() {
        return KnowledgeTreeSapling.TYPE.hashCode();
    }
}
