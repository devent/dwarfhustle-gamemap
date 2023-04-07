package com.anrisoftware.dwarfhustle.gamemap.jme.actors;

import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetKey;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetObject;

/**
 * Retrieves game assets.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public interface GameAssets {

    public AssetObject get(AssetKey<?> key);
}
