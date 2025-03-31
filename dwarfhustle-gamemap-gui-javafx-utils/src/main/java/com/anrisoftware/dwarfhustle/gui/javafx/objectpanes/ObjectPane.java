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
package com.anrisoftware.dwarfhustle.gui.javafx.objectpanes;

import com.anrisoftware.dwarfhustle.gui.javafx.controllers.ObjectPaneController;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;

/**
 * Object pane, updates the object properties tab and can add additional tabs.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public interface ObjectPane {

    ObjectPane create(int type, ActorSystemProvider actor);

    void updateOnFxThread(long id, ObjectPaneController c);

    int getType();
}
