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

import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.vegetations.Grass;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeGrass;
import com.google.auto.service.AutoService;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.ToString;
import lombok.val;

/**
 * @see Grass
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@AutoService(GameMapObjectItem.class)
public class GrassVegetationItem extends AbstractVegetationItem {

    public GrassVegetationItem() {
    }

    public GrassVegetationItem(GameMapObject go, KnowledgeGetter kg, boolean selected) {
        super(go, kg, selected);
    }

    @Override
    public void setTitle(Label label) {
        val klo = kg.get(KnowledgeGrass.TYPE.hashCode());
        var ko = klo.objects.detect(it -> it.getKid() == go.getKid());
        label.setText(ko.getName());
    }

    @Override
    public void setInfo(VBox box) {
        super.setInfo(box);
    }

    @Override
    public AbstractGameMapObjectItem create(GameMapObject go, KnowledgeGetter kg, boolean selected) {
        return new GrassVegetationItem(go, kg, selected);
    }

    @Override
    public int getType() {
        return KnowledgeGrass.TYPE.hashCode();
    }
}
