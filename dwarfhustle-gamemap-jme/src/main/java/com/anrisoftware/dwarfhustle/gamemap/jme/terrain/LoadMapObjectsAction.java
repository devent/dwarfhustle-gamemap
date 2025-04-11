/*
 * dwarfhustle-gamemap-jme - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcIndex;
import static com.anrisoftware.dwarfhustle.model.db.cache.MapObject.getMapObject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.MapObjectsStorage;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsSetter;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObject;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

/**
 * Loads {@link GameMapObject}(s) from the database and stores them in the
 * cache.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
public class LoadMapObjectsAction extends RecursiveAction {

    private static final long serialVersionUID = 1L;

    private final MapChunk root;

    private final ObjectsGetter chunks;

    private final MapObjectsStorage storage;

    private final ObjectsGetter mg;

    private final ObjectsSetter ms;

    private final Duration timeout;

    private final GameMap gm;

    private final int sx;

    private final int sy;

    private final int sz;

    private final int ex;

    private final int ey;

    private final int ez;

    @Override
    protected void compute() {
        final int maxSize = gm.getChunkSize();
        if (ex - sx > maxSize || ey - sy > maxSize || ez - sz > maxSize) {
            var tasks = ForkJoinTask.invokeAll(createSubtasks());
            for (var action : tasks) {
                action.join();
            }
        } else {
            processing();
        }
    }

    private Collection<RecursiveAction> createSubtasks() {
        List<RecursiveAction> dividedTasks = new ArrayList<>();
        final int exh = (ex - sx) / 2;
        final int eyh = (ey - sy) / 2;
        final int ezh = (ez - sz) / 2;
        dividedTasks.add(create(sx, sy, sz, sx + exh, sy + eyh, sz + ezh));
        dividedTasks.add(create(sx + exh, sy + eyh, sz, sx + exh * 2, sy + eyh * 2, sz + ezh));
        dividedTasks.add(create(sx + exh, sy + eyh, sz + ezh, sx + exh * 2, sy + eyh * 2, sz + ezh * 2));
        dividedTasks.add(create(sx + exh, sy, sz, sx + exh * 2, sy + eyh, sz + ezh));
        dividedTasks.add(create(sx + exh, sy, sz + ezh, sx + exh * 2, sy + eyh, sz + ezh * 2));
        dividedTasks.add(create(sx, sy + eyh, sz, sx + exh, sy + eyh * 2, sz + ezh));
        dividedTasks.add(create(sx, sy, sz + ezh, sx + exh, sy + eyh, sz + ezh * 2));
        dividedTasks.add(create(sx, sy + eyh, sz + ezh, sx + exh, sy + eyh * 2, sz + ezh * 2));
        return dividedTasks;
    }

    private RecursiveAction create(int sx, int sy, int sz, int ex, int ey, int ez) {
        return new LoadMapObjectsAction(root, chunks, storage, mg, ms, timeout, gm, sx, sy, sz, ex, ey, ez);
    }

    @SneakyThrows
    protected void processing() {
        storage.getObjectsRange(sx, sy, sz, ex, ey, ez, this::addObject);
    }

    private void addObject(long cid, int type, long id, int x, int y, int z) {
        final int index = calcIndex(gm.getWidth(), gm.getHeight(), gm.getDepth(), 0, 0, 0, x, y, z);
        val mo = getMapObject(mg, gm, index);
        mo.addObject(type, id);
        gm.addFilledBlock((int) cid, index);
        gm.addTypeObject(type, id);
        ms.set(MapObject.OBJECT_TYPE, mo);
    }

}
