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
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @see GameMapObject
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@NoArgsConstructor
public abstract class AbstractGameMapObjectInfoPaneItem implements MapBlockInfoPaneItem, GameMapObjectInfoPaneItem {

    protected int type;

    protected long id;

    protected KnowledgeGetter kg;

    protected ObjectsGetter og;

    protected boolean selected;

    public AbstractGameMapObjectInfoPaneItem(int type, long id, ObjectsGetter og, KnowledgeGetter kg,
            boolean selected) {
        this.type = type;
        this.id = id;
        this.og = og;
        this.kg = kg;
        this.selected = selected;
    }

    @Override
    public void update(MapBlockItemWidgetController controller) {
        if (selected) {
            controller.objectInfoPane.getStyleClass().clear();
            controller.objectInfoPane.getStyleClass().add("objectInfoPaneSelected");
        } else {
            controller.objectInfoPane.getStyleClass().clear();
            controller.objectInfoPane.getStyleClass().add("objectInfoPane");
        }
    }

    @Override
    public long getId() {
        return id;
    }
}
