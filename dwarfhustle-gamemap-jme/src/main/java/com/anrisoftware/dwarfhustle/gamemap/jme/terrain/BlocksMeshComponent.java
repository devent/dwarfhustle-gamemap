package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import com.badlogic.ashley.core.Component;
import com.jme3.scene.Geometry;

import lombok.Data;

/**
 *
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Data
public class BlocksMeshComponent implements Component {

    public final Geometry geo;
}
