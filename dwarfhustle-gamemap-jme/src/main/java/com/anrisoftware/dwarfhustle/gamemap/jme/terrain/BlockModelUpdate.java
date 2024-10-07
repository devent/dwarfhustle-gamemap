package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.cid2Id;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.getChunk;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.findChunk;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.getBlocks;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.getNeighborEast;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.getNeighborNorth;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.getNeighborSouth;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.getNeighborWest;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.math3.util.FastMath;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.map.primitive.LongObjectMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.tuple.Pair;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.google.inject.assistedinject.Assisted;
import com.jme3.bounding.BoundingBox;
import com.jme3.renderer.Camera;
import com.jme3.renderer.Camera.FrustumIntersect;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import jakarta.inject.Inject;

/**
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class BlockModelUpdate {

    /**
     * Factory to create {@link BlockModelUpdate}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface BlockModelUpdateFactory {
        BlockModelUpdate create(@Assisted("materials") ObjectsGetter materials);
    }

    @Inject
    private Camera camera;

    public void collectChunks(MapChunk root, int z, int currentZ, int visible, int d,
            Function<MapBlock, Boolean> isVisible, ObjectsGetter chunks, BiConsumer<MapChunk, MapBlock> consumer) {
        var firstchunk = findChunk(root, 0, 0, z, chunks);
        putChunkSortBlocks(firstchunk, currentZ, visible, isVisible, chunks, consumer);
        long chunkid = 0;
        var nextchunk = firstchunk;
        // nextchunk = firstchunk;
        while (true) {
            chunkid = nextchunk.getNeighborEast();
            if (chunkid == 0) {
                chunkid = firstchunk.getNeighborSouth();
                if (chunkid == 0) {
                    if (nextchunk.pos.ep.z < d && currentZ + visible - nextchunk.pos.ep.z > 0) {
                        nextchunk = findChunk(root, 0, 0, nextchunk.pos.ep.z, chunks);
                        collectChunks(nextchunk, nextchunk.pos.z, currentZ, visible, d, isVisible, chunks, consumer);
                    }
                    break;
                }
                firstchunk = getChunk(chunks, cid2Id(chunkid));
                nextchunk = firstchunk;
                putChunkSortBlocks(nextchunk, currentZ, visible, isVisible, chunks, consumer);
            } else {
                nextchunk = getChunk(chunks, cid2Id(chunkid));
                putChunkSortBlocks(nextchunk, currentZ, visible, isVisible, chunks, consumer);
            }
        }
    }

    private void putChunkSortBlocks(MapChunk chunk, int currentZ, int visible, Function<MapBlock, Boolean> isVisible,
            ObjectsGetter chunks, BiConsumer<MapChunk, MapBlock> consumer) {
        var contains = getIntersectBb(chunk);
        if (contains == FrustumIntersect.Outside) {
            return;
        }
        for (var mb : getBlocks(chunk)) {
            if (mb.pos.z < currentZ + visible && !(mb.pos.z < currentZ) && isVisible.apply(mb)) {
                consumer.accept(chunk, mb);
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

    public int updateModelBlocks(LongObjectMap<Multimap<MaterialKey, MapBlock>> mbs, GameMap gm,
            Function<MapBlock, Mesh> meshSupplier, NormalsPredicate faceSkipTest, ObjectsGetter chunks,
            BiConsumer<MaterialKey, Geometry> consumer) {
        int w = gm.getWidth(), h = gm.getHeight(), d = gm.getDepth();
        var cursor = gm.cursor;
        int bnum = 0;
        for (var cidMatBlocks : mbs.keyValuesView()) {
            long cid = cidMatBlocks.getOne();
            var chunk = getChunk(chunks, cid);
            var matBlocks = cidMatBlocks.getTwo();
            for (Pair<MaterialKey, RichIterable<MapBlock>> pblocks : matBlocks.keyMultiValuePairsView()) {
                final RichIterable<MapBlock> bs = pblocks.getTwo();
                int spos = 0;
                int sindex = 0;
                for (MapBlock mb : bs) {
                    var mesh = meshSupplier.apply(mb);
                    spos += mesh.getBuffer(Type.Position).getNumElements();
                    sindex += mesh.getBuffer(Type.Index).getNumElements();
                    bnum++;
                }
                final var m = pblocks.getOne();
                final var cpos = BufferUtils.createFloatBuffer(3 * spos);
                final var cindex = BufferUtils.createShortBuffer(3 * sindex);
                final var cnormal = BufferUtils.createFloatBuffer(3 * spos);
                final var ctex = BufferUtils.createFloatBuffer(2 * spos);
                final var ctex3 = BufferUtils.createFloatBuffer(2 * spos);
                final var ccolor = BufferUtils.createFloatBuffer(3 * 4 * spos);
                fillBuffers(bs, meshSupplier, faceSkipTest, w, h, d, m, cursor, chunk, gm, chunks, cpos, cindex,
                        cnormal, ctex, ctex3, ccolor);
                final var mesh = new Mesh();
                mesh.setBuffer(Type.Position, 3, cpos);
                mesh.setBuffer(Type.Index, 1, cindex);
                mesh.setBuffer(Type.Normal, 3, cnormal);
                mesh.setBuffer(Type.TexCoord, 2, ctex);
                mesh.setBuffer(Type.TexCoord3, 2, ctex3);
                mesh.setBuffer(Type.Color, 4, ccolor);
                mesh.setMode(Mode.Triangles);
                mesh.updateBound();
                final var geo = new Geometry("block-mesh", mesh);
                geo.setMaterial(m.m);
                consumer.accept(m, geo);
            }
        }
        return bnum;
    }

    private void fillBuffers(RichIterable<MapBlock> blocks, Function<MapBlock, Mesh> meshSupplier,
            NormalsPredicate faceSkipTest, int w, int h, int d, MaterialKey m, GameBlockPos cursor, MapChunk chunk,
            GameMap gm, ObjectsGetter chunks, FloatBuffer cpos, ShortBuffer cindex, FloatBuffer cnormal,
            FloatBuffer ctex, FloatBuffer ctex3, FloatBuffer ccolor) {
        short in0, in1, in2, i0, i1, i2;
        float n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z;
        int delta;
        Mesh mesh;
        ShortBuffer bindex;
        FloatBuffer bnormal;
        for (MapBlock mb : blocks) {
            mesh = meshSupplier.apply(mb);
            bindex = mesh.getShortBuffer(Type.Index).rewind();
            bnormal = mesh.getFloatBuffer(Type.Normal).rewind();
            delta = cpos.position() / 3;
            for (int i = 0; i < bindex.limit() / 3; i++) {
                in0 = bindex.get();
                in1 = bindex.get();
                in2 = bindex.get();
                i0 = (short) (in0 * 3);
                i1 = (short) (in1 * 3);
                i2 = (short) (in2 * 3);
                n0x = bnormal.get(i0);
                n0y = bnormal.get(i0 + 1);
                n0z = bnormal.get(i0 + 2);
                n1x = bnormal.get(i1);
                n1y = bnormal.get(i1 + 1);
                n1z = bnormal.get(i1 + 2);
                n2x = bnormal.get(i2);
                n2y = bnormal.get(i2 + 1);
                n2z = bnormal.get(i2 + 2);
                n0x = FastMath.round((n0x + n1x + n2x) / 3f);
                n0y = FastMath.round((n0y + n1y + n2y) / 3f);
                n0z = FastMath.round((n0z + n1z + n2z) / 3f);
                if (faceSkipTest.test(mb, n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z)) {
                    continue;
                }
                if (n0x < 0.0f && isSkipCheckNeighborWest(mb, chunk, gm, chunks)) {
                    continue;
                }
                if (n0x > 0.0f && isSkipCheckNeighborEast(mb, chunk, gm, chunks)) {
                    continue;
                }
                if (n0y > 0.0f && isSkipCheckNeighborNorth(mb, chunk, gm, chunks)) {
                    continue;
                }
                if (n0y < 0.0f && isSkipCheckNeighborSouth(mb, chunk, gm, chunks)) {
                    continue;
                }
                cindex.put((short) (in0 + delta));
                cindex.put((short) (in1 + delta));
                cindex.put((short) (in2 + delta));
            }
            copyNormal(mb, mesh, cnormal);
            copyTex(mb, mesh, m.tex, ctex, Type.TexCoord);
            copyPosColor(mb, mesh, cpos, ccolor, w, h, d, cursor);
            if (m.emissive != null) {
                copyTex(mb, mesh, m.emissive, ctex3, Type.TexCoord3);
            }
        }
        cpos.flip();
        cindex.flip();
        cnormal.flip();
        ctex.flip();
        ccolor.flip();
    }

    private boolean isSkipCheckNeighborNorth(MapBlock mb, MapChunk chunk, GameMap gm, ObjectsGetter chunks) {
        var nmb = getNeighborNorth(mb, chunk, gm.width, gm.height, gm.depth, chunks);
        return isSkipCheckNeighborEdge(nmb);
    }

    private boolean isSkipCheckNeighborSouth(MapBlock mb, MapChunk chunk, GameMap gm, ObjectsGetter chunks) {
        var nmb = getNeighborSouth(mb, chunk, gm.width, gm.height, gm.depth, chunks);
        return isSkipCheckNeighborEdge(nmb);
    }

    private boolean isSkipCheckNeighborEast(MapBlock mb, MapChunk chunk, GameMap gm, ObjectsGetter chunks) {
        var nmb = getNeighborEast(mb, chunk, gm.width, gm.height, gm.depth, chunks);
        return isSkipCheckNeighborEdge(nmb);
    }

    private boolean isSkipCheckNeighborWest(MapBlock mb, MapChunk chunk, GameMap gm, ObjectsGetter chunks) {
        var nmb = getNeighborWest(mb, chunk, gm.width, gm.height, gm.depth, chunks);
        return isSkipCheckNeighborEdge(nmb);
    }

    private boolean isSkipCheckNeighborEdge(MapBlock nmb) {
        if (nmb != null) {
            if (nmb.isFilled()) {
                return true;
            } else if (nmb.isRamp()) {
                return true;
            } else if (nmb.isLiquid()) {
                return true;
            } else if (nmb.isEmpty()) {
                return false;
            }
        }
        return false;
    }

    private void copyNormal(MapBlock mb, Mesh mesh, FloatBuffer cnormal) {
        var normal = mesh.getFloatBuffer(Type.Normal).rewind();
        cnormal.put(normal);
    }

    private void copyTex(MapBlock mb, Mesh mesh, TextureCacheObject t, FloatBuffer ctex, Type type) {
        var btex = mesh.getFloatBuffer(type).rewind();
        for (int i = 0; i < btex.limit(); i += 2) {
            float tx = btex.get();
            float ty = btex.get();
            float x = t.x + tx * t.w;
            float y = t.y + ty * t.h;
            ctex.put(x);
            ctex.put(y);
        }
    }

    /**
     * Transforms the position values based on the block position. Sets the diffuse
     * color of the tile.
     */
    private void copyPosColor(MapBlock mb, Mesh mesh, FloatBuffer cpos, FloatBuffer ccolor, float w, float h, float d,
            GameBlockPos cursor) {
        var pos = mesh.getFloatBuffer(Type.Position).rewind();
        float x = mb.pos.x, y = mb.pos.y, z = mb.pos.z, vx, vy, vz;
        float tx = -w + 2f * x + 1f;
        float ty = h - 2f * y - 1;
        float tz = (cursor.z - z) * 2f;
        for (int i = 0; i < pos.limit(); i += 3) {
            vx = pos.get();
            vy = pos.get();
            vz = pos.get();
            vx += tx;
            vy += ty;
            vz += tz;
            cpos.put(vx);
            cpos.put(vy);
            cpos.put(vz);
            if (mb.pos.z - cursor.z > 1) {
                ccolor.put(1f / ((mb.pos.z - cursor.z) * 2f));
                ccolor.put(1f / ((mb.pos.z - cursor.z) * 2f));
                ccolor.put(1f / ((mb.pos.z - cursor.z) * 2f));
                ccolor.put(1f);
            }
        }
    }

}
