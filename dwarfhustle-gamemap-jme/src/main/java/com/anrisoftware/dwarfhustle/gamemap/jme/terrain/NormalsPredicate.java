package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;

@FunctionalInterface
public interface NormalsPredicate {

    public static final NormalsPredicate FALSE = (mb, n0x, n0y, n0z, n1x, n1y, n1z, n2x, n2y, n2z) -> {
        return false;
    };

    boolean test(MapBlock mb, float n0x, float n0y, float n0z, float n1x, float n1y, float n1z, float n2x, float n2y,
            float n2z);

}
