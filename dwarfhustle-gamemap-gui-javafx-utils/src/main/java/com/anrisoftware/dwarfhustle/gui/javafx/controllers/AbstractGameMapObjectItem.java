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

import javafx.scene.layout.VBox;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.val;

/**
 * @see GameMapObject
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@NoArgsConstructor
public abstract class AbstractGameMapObjectItem implements MapTileItem, GameMapObjectItem {

    protected GameMapObject go;

    protected KnowledgeGetter kg;

    protected boolean selected;

    public AbstractGameMapObjectItem(GameMapObject go, KnowledgeGetter kg, boolean selected) {
        this.go = go;
        this.kg = kg;
        this.selected = selected;
    }

    @Override
    public void setInfo(VBox box) {
        val parent = box.getParent();
        if (selected) {
            parent.getStyleClass().clear();
            parent.getStyleClass().add("objectInfoPaneSelected");
        } else {
            parent.getStyleClass().clear();
            parent.getStyleClass().add("objectInfoPane");
        }
    }
}
