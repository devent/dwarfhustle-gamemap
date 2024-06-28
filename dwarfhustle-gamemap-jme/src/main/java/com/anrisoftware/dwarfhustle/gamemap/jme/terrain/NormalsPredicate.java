package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;

@FunctionalInterface
public interface NormalsPredicate {

    boolean test(MapBlock mb, float n0x, float n0y, float n0z, float n1x, float n1y, float n1z, float n2x, float n2y,
            float n2z);

}
