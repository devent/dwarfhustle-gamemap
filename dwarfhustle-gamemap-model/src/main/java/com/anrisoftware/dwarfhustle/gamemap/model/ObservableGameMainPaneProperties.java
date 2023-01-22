/*
 * dwarfhustle-gamemap-model - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.model;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
public class ObservableGameMainPaneProperties {

    /**
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @Data
    public static class GameMainPaneProperties {

        public double splitMainPosition = 0.5;

        public double z = 0.0;

        public float cameraPosX = 0.002901543f;

        public float cameraPosY = -0.013370683f;

        public float cameraPosZ = 28.217747f;

        public float cameraRotX = -4.8154507E-6f;

        public float cameraRotY = 0.9999911f;

        public float cameraRotZ = 0.0012241602f;

        public float cameraRotW = 0.004027171f;

    }

    public final DoubleProperty splitMainPosition;

    public final DoubleProperty z;

    public final FloatProperty cameraPosX;

    public final FloatProperty cameraPosY;

    public final FloatProperty cameraPosZ;

    public final FloatProperty cameraRotX;

    public final FloatProperty cameraRotY;

    public final FloatProperty cameraRotZ;

    public final FloatProperty cameraRotW;

    @SneakyThrows
    public ObservableGameMainPaneProperties(GameMainPaneProperties p) {
        this.splitMainPosition = JavaBeanDoublePropertyBuilder.create().bean(p).name("splitMainPosition").build();
        this.z = JavaBeanDoublePropertyBuilder.create().bean(p).name("z").build();
        this.cameraPosX = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraPosX").build();
        this.cameraPosY = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraPosY").build();
        this.cameraPosZ = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraPosZ").build();
        this.cameraRotX = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraRotX").build();
        this.cameraRotY = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraRotY").build();
        this.cameraRotZ = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraRotZ").build();
        this.cameraRotW = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraRotW").build();
    }

    public void copy(GameMainPaneProperties other) {
        splitMainPosition.set(other.splitMainPosition);
        z.set(other.z);
        cameraPosX.set(other.cameraPosX);
        cameraPosY.set(other.cameraPosY);
        cameraPosZ.set(other.cameraPosZ);
        cameraRotX.set(other.cameraRotX);
        cameraRotY.set(other.cameraRotY);
        cameraRotZ.set(other.cameraRotZ);
        cameraRotW.set(other.cameraRotW);
    }

    public Vector3f getCameraPos() {
        return new Vector3f(cameraPosX.get(), cameraPosY.get(), cameraPosZ.get());
    }

    public Quaternion getCameraRot() {
        return new Quaternion(cameraRotX.get(), cameraRotY.get(), cameraRotZ.get(), cameraRotW.get());
    }

    public void setCameraPos(Vector3f l) {
        cameraPosX.set(l.x);
        cameraPosY.set(l.y);
        cameraPosZ.set(l.z);
    }

    public void setCameraRot(Quaternion r) {
        cameraRotX.set(r.getX());
        cameraRotY.set(r.getY());
        cameraRotZ.set(r.getZ());
        cameraRotW.set(r.getW());
    }
}
