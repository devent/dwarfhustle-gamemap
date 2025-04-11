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

import com.anrisoftware.dwarfhustle.model.api.materials.KnowledgeMaterial;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.ToString;
import lombok.val;

/**
 * The terrain.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
public class TerrainInfoPaneItem implements MapBlockInfoPaneItem {

    protected final ObjectsGetter cg;

    protected final MapBlock mb;

    protected final KnowledgeGetter kg;

    public TerrainInfoPaneItem(MapBlock mb, KnowledgeGetter kg, ObjectsGetter cg) {
        this.mb = mb;
        this.kg = kg;
        this.cg = cg;
    }

    @Override
    public void update(MapBlockItemWidgetController controller) {
        val klo = kg.get(KnowledgeMaterial.TYPE.hashCode());
        var ko = klo.objects.detect(it -> it.getKid() == mb.getMaterial());
        controller.objectInfoTitle.setText(ko.getName());
        controller.objectInfoBox.getChildren().clear();
        putItems(controller.objectInfoBox);
        controller.objectInfoBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
    }

    protected void putItems(VBox box) {
        box.getChildren().add(
                new Label("\u2022\u2002" + mb.getPos().getX() + "/" + mb.getPos().getY() + "/" + mb.getPos().getZ()));
    }

    @Override
    public int getType() {
        return KnowledgeMaterial.OBJECT_TYPE;
    }

    @Override
    public long getId() {
        return 0;
    }
}
