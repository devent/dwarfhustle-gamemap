package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import javax.inject.Inject;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.google.inject.assistedinject.Assisted;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.Camera.FrustumIntersect;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * Renders a {@link MapBlock}.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class MapBlockBox {

    /**
     * Factory to create {@link MapBlockBox}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface MapBlockBoxFactory {
        MapBlockBox create(MapBlockComponent c, Node node);
    }

    /**
     * Factory to create {@link MapBlockBox}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface MapBlockBoxRootFactory {
        MapBlockBoxRoot create(MapBlockComponent c, Node node);
    }

    /**
     * Root {@link MapBlock}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class MapBlockBoxRoot extends MapBlockBox {

        @Inject
        public MapBlockBoxRoot(@Assisted MapBlockComponent c, @Assisted Node node, AssetManager am) {
            super(c, node, am);
        }

        @Override
        public void updateVisible(Camera cam) {
            // root block is always visible
        }

    }

    public final Box box;

    public final Spatial geo;

    public final MapBlockComponent c;

    public boolean visible = true;

    public final MutableList<MapBlockBox> children = Lists.mutable.empty();

    public final Node node;

    @Inject
    public MapBlockBox(@Assisted MapBlockComponent c, @Assisted Node node, AssetManager am) {
        this.c = c;
        this.node = node;
        this.box = new Box(c.mb.getWidth(), c.mb.getHeight(), c.mb.getDepth());
        this.geo = new Geometry(Long.toString(c.mb.getId()), box);
        var mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setLineWidth(4);
        mat.setColor("Color", new ColorRGBA(64f / c.mb.getWidth() / 255f, 64f / c.mb.getHeight() / 255f,
                64f / c.mb.getDepth() / 255f, 0));
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Front);
        geo.setMaterial(mat);
        geo.setLocalTranslation(c.mb.getStartPos().getX(), c.mb.getStartPos().getY(), c.mb.getStartPos().getZ());
        geo.updateModelBound();
        node.attachChild(geo);
    }

    public BoundingBox getWorldBound() {
        return (BoundingBox) geo.getWorldBound();
    }

    public void updateVisible(Camera cam) {
        var planeState = cam.getPlaneState();
        cam.setPlaneState(0);
        this.visible = cam.contains(geo.getWorldBound()) != FrustumIntersect.Outside;
        cam.setPlaneState(planeState);
    }

    public void attachChild(MapBlockBox mbb) {
        var boxid = mbb.c.mb.getId();
        if (children.notEmpty()) {
            var child = children.select(c -> c.c.mb.getBlocks().contains(boxid));
            if (child.isEmpty()) {
                addChild(mbb);
            } else {
                for (var childbox : child) {
                    childbox.attachChild(mbb);
                }
            }
        } else {
            addChild(mbb);
        }
    }

    private void addChild(MapBlockBox mbb) {
        children.add(mbb);
    }

}
