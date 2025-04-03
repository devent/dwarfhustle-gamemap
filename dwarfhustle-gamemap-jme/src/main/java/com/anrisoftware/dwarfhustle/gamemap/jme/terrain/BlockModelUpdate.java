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

import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcX;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcY;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos.calcZ;
import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.getChunk;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapBlockBuffer.getProp;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.getNeighborEast;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.getNeighborNorth;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.getNeighborSouth;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.getNeighborUp;
import static com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.getNeighborWest;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.function.BiConsumer;

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
import com.anrisoftware.dwarfhustle.model.api.objects.PropertiesSet;
import com.anrisoftware.dwarfhustle.model.db.buffers.MapChunkBuffer.MapBlockResult;
import com.google.inject.assistedinject.Assisted;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

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

    public int updateModelBlocks(LongObjectMap<Multimap<MaterialKey, Integer>> materialBlocks, GameMap gm,
            MeshSupplier meshSupplier, NormalsPredicate faceSkipTest, ObjectsGetter chunks,
            BiConsumer<MaterialKey, Geometry> consumer) {
        int w = gm.getWidth(), h = gm.getHeight(), d = gm.getDepth();
        var cursor = gm.getCursor();
        int bnum = 0;
        for (var cidMatBlocks : materialBlocks.keyValuesView()) {
            long cid = cidMatBlocks.getOne();
            var chunk = getChunk(chunks, cid);
            var matBlocks = cidMatBlocks.getTwo();
            for (Pair<MaterialKey, RichIterable<Integer>> pblocks : matBlocks.keyMultiValuePairsView()) {
                final RichIterable<Integer> bs = pblocks.getTwo();
                int spos = 0;
                int sindex = 0;
                for (int mb : bs) {
                    var mesh = meshSupplier.getMesh(chunk, mb);
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
                final var cobjtex = new FloatBuffer[m.objects.length];
                for (int i = 0; i < m.objects.length; i++) {
                    if (m.objects[i] != null) {
                        cobjtex[i] = BufferUtils.createFloatBuffer(2 * spos);
                    }
                }
                final var ccolor = BufferUtils.createFloatBuffer(3 * 4 * spos);
                fillBuffers(bs, meshSupplier, faceSkipTest, w, h, d, m, cursor, chunk, gm, chunks, cpos, cindex,
                        cnormal, ctex, ctex3, cobjtex, ccolor);
                final var mesh = new Mesh();
                mesh.setBuffer(Type.Position, 3, cpos);
                mesh.setBuffer(Type.Index, 1, cindex);
                mesh.setBuffer(Type.Normal, 3, cnormal);
                mesh.setBuffer(Type.TexCoord, 2, ctex);
                mesh.setBuffer(Type.TexCoord3, 2, ctex3);
                for (int i = 0; i < m.objects.length; i++) {
                    if (m.objects[i] != null) {
                        mesh.setBuffer(TexCoordinate.getType(i + 3), 2, cobjtex[i]);
                    }
                }
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

    private void fillBuffers(RichIterable<Integer> bs, MeshSupplier meshSupplier, NormalsPredicate faceSkipTest, int w,
            int h, int d, MaterialKey m, GameBlockPos cursor, MapChunk chunk, GameMap gm, ObjectsGetter chunks,
            FloatBuffer cpos, ShortBuffer cindex, FloatBuffer cnormal, FloatBuffer ctex, FloatBuffer ctex3,
            FloatBuffer[] cobjtex, FloatBuffer ccolor) {
        short in0, in1, in2, i0, i1, i2;
        float n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z;
        int delta;
        Mesh mesh;
        ShortBuffer bindex;
        FloatBuffer bnormal;
        for (int index : bs) {
            mesh = meshSupplier.getMesh(chunk, index);
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
                if (faceSkipTest.test(chunk, index, n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z)) {
                    continue;
                }
                if (n0x < 0.0f && isSkipCheckNeighborWest(index, chunk, gm, chunks)) {
                    continue;
                }
                if (n0x > 0.0f && isSkipCheckNeighborEast(index, chunk, gm, chunks)) {
                    continue;
                }
                if (n0y > 0.0f && isSkipCheckNeighborNorth(index, chunk, gm, chunks)) {
                    continue;
                }
                if (n0y < 0.0f && isSkipCheckNeighborSouth(index, chunk, gm, chunks)) {
                    continue;
                }
                if (n0z > 0.0f && isSkipCheckNeighborUp(index, chunk, gm, chunks)) {
                    // continue;
                }
                cindex.put((short) (in0 + delta));
                cindex.put((short) (in1 + delta));
                cindex.put((short) (in2 + delta));
            }
            copyNormal(index, mesh, cnormal);
            copyTex(index, mesh, m.tex, ctex, Type.TexCoord);
            copyPosColor(index, chunk, mesh, cpos, ccolor, w, h, d, cursor);
            if (m.emissive != null) {
                copyTex(index, mesh, m.emissive, ctex3, Type.TexCoord3);
            }
            for (int i = 0; i < m.objects.length; i++) {
                if (m.objects[i] != null) {
                    copyTex(index, mesh, m.objects[i], cobjtex[i], TexCoordinate.getType(i + 3));
                }
            }
        }
        cpos.flip();
        cindex.flip();
        cnormal.flip();
        ctex.flip();
        ctex3.flip();
        for (var b : cobjtex) {
            if (b != null) {
                b.flip();
            }
        }
        ccolor.flip();
    }

    private boolean isSkipCheckNeighborNorth(int index, MapChunk chunk, GameMap gm, ObjectsGetter chunks) {
        var nmb = getNeighborNorth(index, chunk, gm.getWidth(), gm.getHeight(), gm.getDepth(), chunks);
        return isSkipCheckNeighborEdge(nmb);
    }

    private boolean isSkipCheckNeighborSouth(int index, MapChunk chunk, GameMap gm, ObjectsGetter chunks) {
        var nmb = getNeighborSouth(index, chunk, gm.getWidth(), gm.getHeight(), gm.getDepth(), chunks);
        return isSkipCheckNeighborEdge(nmb);
    }

    private boolean isSkipCheckNeighborEast(int index, MapChunk chunk, GameMap gm, ObjectsGetter chunks) {
        var nmb = getNeighborEast(index, chunk, gm.getWidth(), gm.getHeight(), gm.getDepth(), chunks);
        return isSkipCheckNeighborEdge(nmb);
    }

    private boolean isSkipCheckNeighborWest(int index, MapChunk chunk, GameMap gm, ObjectsGetter chunks) {
        var nmb = getNeighborWest(index, chunk, gm.getWidth(), gm.getHeight(), gm.getDepth(), chunks);
        return isSkipCheckNeighborEdge(nmb);
    }

    private boolean isSkipCheckNeighborUp(int index, MapChunk chunk, GameMap gm, ObjectsGetter chunks) {
        var res = getNeighborUp(index, chunk, gm.getWidth(), gm.getHeight(), gm.getDepth(), chunks);
        if (res.isValid()) {
            if (PropertiesSet.get(getProp(res.c.getBlocks(), res.getOff()), MapBlock.FILLED_POS)) {
                return true;
            }
            if (PropertiesSet.get(getProp(res.c.getBlocks(), res.getOff()), MapBlock.RAMP_POS)) {
                return true;
            }
            if (PropertiesSet.get(getProp(res.c.getBlocks(), res.getOff()), MapBlock.LIQUID_POS)) {
                return false;
            }
            if (PropertiesSet.get(getProp(res.c.getBlocks(), res.getOff()), MapBlock.EMPTY_POS)) {
                return false;
            }
        }
        return false;
    }

    private boolean isSkipCheckNeighborEdge(MapBlockResult res) {
        if (res.isValid()) {
            if (PropertiesSet.get(getProp(res.c.getBlocks(), res.getOff()), MapBlock.FILLED_POS)) {
                return true;
            }
            if (PropertiesSet.get(getProp(res.c.getBlocks(), res.getOff()), MapBlock.RAMP_POS)) {
                return true;
            }
            if (PropertiesSet.get(getProp(res.c.getBlocks(), res.getOff()), MapBlock.LIQUID_POS)) {
                return true;
            }
            if (PropertiesSet.get(getProp(res.c.getBlocks(), res.getOff()), MapBlock.EMPTY_POS)) {
                return false;
            }
        }
        return false;
    }

    private void copyNormal(int index, Mesh mesh, FloatBuffer cnormal) {
        var normal = mesh.getFloatBuffer(Type.Normal).rewind();
        cnormal.put(normal);
    }

    private void copyTex(int index, Mesh mesh, TextureCacheObject t, FloatBuffer ctex, Type type) {
        var buffer = mesh.getFloatBuffer(type);
        var btex = buffer.rewind();
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
    private void copyPosColor(int index, MapChunk chunk, Mesh mesh, FloatBuffer cpos, FloatBuffer ccolor, float w,
            float h, float d, GameBlockPos cursor) {
        var pos = mesh.getFloatBuffer(Type.Position).rewind();
        float x = calcX(index, chunk), y = calcY(index, chunk), z = calcZ(index, chunk), vx, vy, vz;
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
            if (z - cursor.z > 1) {
                ccolor.put(1f / ((z - cursor.z) * 2f));
                ccolor.put(1f / ((z - cursor.z) * 2f));
                ccolor.put(1f / ((z - cursor.z) * 2f));
                ccolor.put(1f);
            }
        }
    }

}
