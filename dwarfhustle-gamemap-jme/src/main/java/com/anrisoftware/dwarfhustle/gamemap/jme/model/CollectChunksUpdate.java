package com.anrisoftware.dwarfhustle.gamemap.jme.model;

import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.cid2Id;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.getChunk;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.findChunk;

import com.anrisoftware.dwarfhustle.model.api.objects.GameChunkPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.jme3.bounding.BoundingBox;
import com.jme3.renderer.Camera;
import com.jme3.renderer.Camera.FrustumIntersect;

import jakarta.inject.Inject;

/**
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class CollectChunksUpdate {

    /**
     * Factory to create {@link CollectChunksUpdate}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface CollectChunksUpdateFactory {
        CollectChunksUpdate create();
    }

    @Inject
    private Camera camera;

    public void collectChunks(MapChunk root, int z, int currentZ, int visible, GameMap gm, MapBlockVisible isVisible,
            ObjectsGetter chunks, PutMapBlock consumer) {
        final int depth = gm.getDepth();
        var firstchunk = findChunk(root, 0, 0, z, chunks);
        putChunkSortBlocks(gm, firstchunk, currentZ, visible, isVisible, chunks, consumer);
        long chunkid = 0;
        var nextchunk = firstchunk;
        // nextchunk = firstchunk;
        while (true) {
            chunkid = nextchunk.getNeighborEast();
            if (chunkid == 0) {
                chunkid = firstchunk.getNeighborSouth();
                if (chunkid == 0) {
                    int ez = nextchunk.getPos().getEp().getZ();
                    if (ez < depth && currentZ + visible - ez > 0) {
                        nextchunk = findChunk(root, 0, 0, ez, chunks);
                        int z2 = nextchunk.getPos().getZ();
                        collectChunks(nextchunk, z2, currentZ, visible, gm, isVisible, chunks, consumer);
                    }
                    break;
                }
                firstchunk = getChunk(chunks, cid2Id(chunkid));
                nextchunk = firstchunk;
                putChunkSortBlocks(gm, nextchunk, currentZ, visible, isVisible, chunks, consumer);
            } else {
                nextchunk = getChunk(chunks, cid2Id(chunkid));
                putChunkSortBlocks(gm, nextchunk, currentZ, visible, isVisible, chunks, consumer);
            }
        }
    }

    private void putChunkSortBlocks(GameMap gm, MapChunk chunk, int currentZ, int visible, MapBlockVisible isVisible,
            ObjectsGetter chunks, PutMapBlock consumer) {
        var contains = getIntersectBb(chunk);
        if (contains == FrustumIntersect.Outside) {
            return;
        }
        final var pos = chunk.getPos();
        final int cw = pos.getSizeX();
        final int ch = chunk.getPos().getSizeY();
        final int cd = chunk.getPos().getSizeZ();
        final int sx = chunk.getPos().getX();
        final int sy = chunk.getPos().getY();
        final int sz = chunk.getPos().getZ();
        for (int x = chunk.getPos().getX(); x < chunk.getPos().getEp().getX(); x++) {
            for (int y = chunk.getPos().getY(); y < chunk.getPos().getEp().getY(); y++) {
                for (int z = chunk.getPos().getZ(); z < chunk.getPos().getEp().getZ(); z++) {
                    if (z < currentZ + visible && !(z < currentZ) && isVisible.isVisible(gm, chunk, 0, x, y, z)) {
                        final int i = GameChunkPos.calcIndex(cw, ch, cd, sx, sy, sz, x, y, z);
                        consumer.putBlock(gm, chunk, i);
                    }
                }
            }
        }
    }

    private FrustumIntersect getIntersectBb(MapChunk chunk) {
        var bb = createBb(chunk);
        return getIntersect(bb);
    }

    private FrustumIntersect getIntersect(BoundingBox bb) {
        int planeState = camera.getPlaneState();
        camera.setPlaneState(0);
        var contains = camera.contains(bb);
        camera.setPlaneState(planeState);
        return contains;
    }

    private BoundingBox createBb(MapChunk chunk) {
        var bb = new BoundingBox();
        bb.setXExtent(chunk.getCenterExtent().extentx);
        bb.setYExtent(chunk.getCenterExtent().extenty);
        bb.setZExtent(chunk.getCenterExtent().extentz);
        bb.setCenter(chunk.getCenterExtent().centerx, chunk.getCenterExtent().centery, chunk.getCenterExtent().centerz);
        return bb;
    }

}
