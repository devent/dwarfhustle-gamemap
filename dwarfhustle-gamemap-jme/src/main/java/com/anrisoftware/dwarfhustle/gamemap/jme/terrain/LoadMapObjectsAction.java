package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcIndex;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.findChunk;

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

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

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

    final private Duration timeout;

    private final GameMap gm;

    private final int sx;

    private final int sy;

    private final int sz;

    private final int ex;

    private final int ey;

    private final int ez;

    @Override
    protected void compute() {
        final int maxSize = gm.chunkSize;
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
        return new LoadMapObjectsAction(root, chunks, storage, timeout, gm, sx, sy, sz, ex, ey, ez);
    }

    @SneakyThrows
    protected void processing() {
        storage.getObjectsRange(sx, sy, sz, ex, ey, ez, this::addObject);
    }

    private void addObject(long cid, int type, long id, int x, int y, int z) {
        final int index = calcIndex(gm.getWidth(), gm.getHeight(), gm.getDepth(), 0, 0, 0, x, y, z);
        final var chunk = findChunk(root, x, y, z, chunks);
        gm.addFilledBlock(chunk.getCid(), index);
    }

}
