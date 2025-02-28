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
package com.anrisoftware.dwarfhustle.gui.javafx.controllers;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeTreeSapling;
import com.anrisoftware.dwarfhustle.model.api.vegetations.TreeSapling;
import com.google.auto.service.AutoService;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.ToString;
import lombok.val;

/**
 * @see TreeSapling
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@AutoService(GameMapObjectItem.class)
public class SaplingVegetationItem extends AbstractVegetationItem {

    public SaplingVegetationItem() {
    }

    public SaplingVegetationItem(GameMapObject go, KnowledgeGetter kg) {
        super(go, kg);
    }

    @Override
    public void setTitle(Label label) {
        val klo = kg.get(KnowledgeTreeSapling.TYPE.hashCode());
        var ko = klo.objects.detect(it -> it.getKid() == go.getKid());
        label.setText(ko.getName());
    }

    @Override
    public void setInfo(VBox box) {
        super.setInfo(box);
    }

    @Override
    public AbstractGameMapObjectItem create(GameMapObject go, KnowledgeGetter kg) {
        return new SaplingVegetationItem(go, kg);
    }

    @Override
    public int getType() {
        return KnowledgeTreeSapling.TYPE.hashCode();
    }
}
