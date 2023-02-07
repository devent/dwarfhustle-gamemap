package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import javax.inject.Inject;

import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.google.inject.assistedinject.Assisted;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import lombok.Getter;

/**
 * Renders a {@link MapBlock}.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Getter
public class MapBlockBox {

	public interface MapBlockBoxFactory {
		MapBlockBox create(MapBlockComponent c);
	}

	private final Box box;

	private final Spatial geo;

	@Inject
	public MapBlockBox(@Assisted MapBlockComponent c, AssetManager am) {
		this.box = new Box(c.mb.getWidth(), c.mb.getHeight(), c.mb.getDepth());
		this.geo = new Geometry(Long.toString(c.mb.getId()), box);
		var mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setWireframe(true);
		mat.getAdditionalRenderState().setLineWidth(4);
		mat.setColor("Color", ColorRGBA.Red);
		geo.setMaterial(mat);
	}

}
