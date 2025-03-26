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

import java.text.DecimalFormat;
import java.util.Locale;

import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.vegetations.Vegetation;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @see Vegetation
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@NoArgsConstructor
public abstract class AbstractVegetationInfoPaneItem extends AbstractGameMapObjectInfoPaneItem {

    public AbstractVegetationInfoPaneItem(int type, long id, ObjectsGetter og, KnowledgeGetter kg, boolean selected) {
        super(type, id, og, kg, selected);
    }

    @Override
    public void update(MapBlockItemWidgetController controller) {
        super.update(controller);
        Vegetation v = og.get(type, id);
        var format = DecimalFormat.getInstance(Locale.ENGLISH);
        controller.objectInfoBox.getChildren().clear();
        controller.objectInfoBox.getChildren().add(new Label("\u2022\u2002" + format.format(v.getGrowth() * 100f)));
        controller.objectInfoBox.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
    }

}
