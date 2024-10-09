package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.jme3.scene.Mesh;

@FunctionalInterface
public interface MeshSupplier {

    Mesh getMesh(MapChunk chunk, int index);
}
