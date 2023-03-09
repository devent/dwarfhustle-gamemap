/*
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - View
 * ****************************************************************************
 *
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - View is a derivative work based on Josua Tippetts' C++ library:
 * http://accidentalnoise.sourceforge.net/index.html
 * ****************************************************************************
 *
 * Copyright (C) 2011 Joshua Tippetts
 *
 *   This software is provided 'as-is', without any express or implied
 *   warranty.  In no event will the authors be held liable for any damages
 *   arising from the use of this software.
 *
 *   Permission is granted to anyone to use this software for any purpose,
 *   including commercial applications, and to alter it and redistribute it
 *   freely, subject to the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not
 *      claim that you wrote the original software. If you use this software
 *      in a product, an acknowledgment in the product documentation would be
 *      appreciated but is not required.
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *   3. This notice may not be removed or altered from any source distribution.
 *
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - View bundles and uses the RandomCL library:
 * https://github.com/bstatcomp/RandomCL
 * ****************************************************************************
 *
 * BSD 3-Clause License
 *
 * Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import static com.jme3.input.MouseInput.BUTTON_MIDDLE;

import javax.inject.Inject;
import javax.inject.Named;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapCursorSetMessage;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.google.common.base.Objects;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.util.TempVars;

import akka.actor.typed.ActorRef;
import lombok.extern.slf4j.Slf4j;

/**
 * Picks the map tile under the mouse cursor.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class MapCursorState extends BaseAppState implements ActionListener, RawInputListener {

    private static final String LEFT_BUTTON_MAPPING = "MapCursorState_left";

    private static final String[] MAPPINGS = new String[] { LEFT_BUTTON_MAPPING };

    @Inject
    private InputManager inputManager;

    @Inject
    private Camera camera;

    @Inject
    @Named("rootNode")
    private Node rootNode;

    @Inject
    private ActorRef<Message> actor;

    private final Vector2f mouse = new Vector2f();

    private boolean leftMouseDown = false;

    private CollisionResults results = new CollisionResults();

    private Ray ray = new Ray();

    private long motionTime;

    private int cursorX = -1;

    private int cursorY = -1;

    @Inject
    public MapCursorState() {
        super(MapCursorState.class.getSimpleName());
    }

    @Override
    protected void initialize(Application app) {
        log.debug("initialize");
    }

    @Override
    protected void cleanup(Application app) {
        log.debug("cleanup");
    }

    @Override
    protected void onEnable() {
        log.debug("onEnable");
        initKeys();
    }

    @Override
    protected void onDisable() {
        log.debug("onDisable");
        deleteKeys();
    }

    private void initKeys() {
        inputManager.addListener(this, MAPPINGS);
        inputManager.addRawInputListener(this);
        inputManager.addMapping(LEFT_BUTTON_MAPPING, new MouseButtonTrigger(BUTTON_MIDDLE));
    }

    private void deleteKeys() {
        inputManager.removeRawInputListener(this);
        for (var i = 0; i < MAPPINGS.length; i++) {
            inputManager.deleteMapping(MAPPINGS[i]);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
        case LEFT_BUTTON_MAPPING:
            leftMouseDown = isPressed;
            return;
        }
    }

    @Override
    public void beginInput() {
    }

    @Override
    public void endInput() {
    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {
    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
        mouse.x = evt.getX();
        mouse.y = evt.getY();
        if (evt.getTime() - motionTime < 600000) {
            return;
        }
        pickObject();
        motionTime = evt.getTime();
        if (leftMouseDown) {
            return;
        }
    }

    private void pickObject() {
        var temp = TempVars.get();
        try {
            results.clear();
            var click3d = camera.getWorldCoordinates(mouse, 0f, temp.vect1);
            var dir = camera.getWorldCoordinates(mouse, 1f, temp.vect2).subtractLocal(click3d).normalizeLocal();
            ray.setOrigin(click3d);
            ray.setDirection(dir);
            rootNode.collideWith(ray, results);
            if (results.size() > 0) {
                var target = results.getClosestCollision().getGeometry();
                var parent = target.getParent();
                if (Objects.equal(parent.getUserData("name"), MapTerrainTile.NAME)) {
                    int x = parent.getUserData("x");
                    int y = parent.getUserData("y");
                    int level = parent.getUserData("level");
                    if (cursorX != x || cursorY != y) {
                        this.cursorX = x;
                        this.cursorY = y;
                        actor.tell(new MapCursorSetMessage(level, y, x));
                    }
                }
            }
        } finally {
            temp.release();
        }
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
    }

    @Override
    public void onTouchEvent(TouchEvent evt) {
    }

}
