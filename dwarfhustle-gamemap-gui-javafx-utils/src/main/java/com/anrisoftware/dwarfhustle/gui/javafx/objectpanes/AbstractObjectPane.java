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

import static com.anrisoftware.dwarfhustle.model.api.objects.StringObject.getStringObject;

import com.anrisoftware.dwarfhustle.gui.javafx.controllers.ObjectPaneController;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.api.buildings.KnowledgeBuilding;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.NamedObject;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StringObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.val;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
@NoArgsConstructor
public abstract class AbstractObjectPane implements ObjectPane {

    protected int type;

    protected ObjectsGetter og;

    protected KnowledgeGetter kg;

    protected ActorSystemProvider actor;

    protected ObjectsGetter sg;

    public AbstractObjectPane(int type, ActorSystemProvider actor) {
        this.type = type;
        this.actor = actor;
        this.kg = actor.getKnowledgeGetterAsyncNow(PowerLoomKnowledgeActor.ID);
        this.og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
        this.sg = actor.getObjectGetterAsyncNow(StringObjectsJcsCacheActor.ID);
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void updateOnFxThread(long id, ObjectPaneController c) {
        val klo = kg.get(KnowledgeBuilding.TYPE.hashCode());
        GameMapObject go = og.get(type, id);
        var ko = klo.objects.detect(it -> it.getKid() == go.getKid());
        if (go instanceof NamedObject no) {
            c.objectTitleLabel.setText("" + getStringObject(sg, no.getName()).getS());
        } else {
            c.objectTitleLabel.setText("" + ko.getName());
        }
    }
}
