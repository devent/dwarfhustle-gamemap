/*
 * Dwarf Hustle Game Map - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.jme.map;

import static com.jme3.input.MouseInput.BUTTON_MIDDLE;

import javax.inject.Inject;
import javax.inject.Named;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapTileEmptyUnderCursorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.MapTileUnderCursorMessage;
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
                String name = parent.getUserData("name");
                if (Objects.equal(name, MapTerrainTile.NAME)) {
                    int x = parent.getUserData("x");
                    int y = parent.getUserData("y");
                    int level = parent.getUserData("level");
                    if (cursorX != x || cursorY != y) {
                        this.cursorX = x;
                        this.cursorY = y;
                        actor.tell(new MapTileUnderCursorMessage(level, y, x));
                    }
                }
            } else {
                this.cursorX = -1;
                this.cursorY = -1;
                actor.tell(new MapTileEmptyUnderCursorMessage());
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
