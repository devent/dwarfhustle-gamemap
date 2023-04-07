package com.anrisoftware.dwarfhustle.gamemap.model.resources;

import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Game asset object.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class AssetObject extends GameObject {

    private static final long serialVersionUID = 1L;

    public static final String OBJECT_TYPE = AssetObject.class.getSimpleName();

    public AssetObject(byte[] idbuf) {
        super(idbuf);
    }

    public AssetObject(long id) {
        super(id);
    }

    @Override
    public String getObjectType() {
        return AssetObject.OBJECT_TYPE;
    }

}
