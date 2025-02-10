/*
 * dwarfhustle-gamemap-jme - Game map.
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
package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import static com.anrisoftware.dwarfhustle.model.api.objects.MapChunk.cid2Id;
import static com.jme3.input.MouseInput.BUTTON_LEFT;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.collections.api.block.function.primitive.LongToObjectFunction;

import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.MapChunk;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.util.TempVars;

import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Selects the map block under the mouse cursor.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainSelectBlockState extends BaseAppState implements ActionListener, AnalogListener, RawInputListener {

    private static final String LEFT_MOUSE_BUTTON_MAPPING = "TerrainSelectBlockState_LEFT_MOUSE_BUTTON_MAPPING";

    private static final String[] MAPPINGS = new String[] { LEFT_MOUSE_BUTTON_MAPPING };

    private final Vector2f mouse;

    @Inject
    private InputManager inputManager;

    @Inject
    private Camera camera;

    private boolean keyInit = false;

    private boolean searchingFocusedBlock;

    private Supplier<GameMap> onRetrieveGameMap;

    private Consumer<GameBlockPos> onSaveCursor;

    private BiConsumer<GameBlockPos, GameBlockPos> onSelectSet;

    private final GameBlockPos oldCursor = new GameBlockPos();

    private final GameBlockPos selectStartCursor = new GameBlockPos();

    private final GameBlockPos selectEndCursor = new GameBlockPos();

    private boolean selecting = false;

    private ForkJoinPool pool;

    private LongToObjectFunction<MapChunk> onRetrieveChunk;

    private final Vector2f selectStartMouse = new Vector2f();

    private final Vector2f selectEndMouse = new Vector2f();

    private boolean multiSelectEnabled = false;

    private boolean singleSelectEnabled;

    @Inject
    public TerrainSelectBlockState() {
        super(TerrainSelectBlockState.class.getSimpleName());
        this.mouse = new Vector2f();
    }

    public void setOnRetrieveChunk(LongToObjectFunction<MapChunk> onRetrieveChunk) {
        this.onRetrieveChunk = onRetrieveChunk;
    }

    public void setOnRetrieveMap(Supplier<GameMap> onRetrieveGameMap) {
        this.onRetrieveGameMap = onRetrieveGameMap;
    }

    public void setOnSaveCursor(Consumer<GameBlockPos> onSaveCursor) {
        this.onSaveCursor = onSaveCursor;
    }

    public void setOnSelectSet(BiConsumer<GameBlockPos, GameBlockPos> onSelectSet) {
        this.onSelectSet = onSelectSet;
    }

    public void setMultiSelectEnabled(boolean enabled) {
        this.multiSelectEnabled = enabled;
        if (!enabled) {
            this.selecting = false;
        }
    }

    public void setSingleSelectEnabled(boolean enabled) {
        this.singleSelectEnabled = enabled;
        if (!enabled) {
            this.selecting = false;
        }
    }

    @Override
    protected void initialize(Application app) {
        this.pool = new ForkJoinPool(4);
        log.debug("initialize");
    }

    @Override
    protected void cleanup(Application app) {
        pool.close();
        log.debug("cleanup");
    }

    @Override
    protected void onEnable() {
        log.debug("onEnable");
    }

    @Override
    protected void onDisable() {
        log.debug("onDisable");
        if (keyInit) {
            deleteKeys();
        }
    }

    public void initKeys() {
        if (keyInit) {
            return;
        }
        inputManager.addRawInputListener(this);
        inputManager.addListener(this, MAPPINGS);
        inputManager.addMapping(LEFT_MOUSE_BUTTON_MAPPING, new MouseButtonTrigger(BUTTON_LEFT));
        this.keyInit = true;
    }

    private void deleteKeys() {
        inputManager.removeListener(this);
        inputManager.removeRawInputListener(this);
        for (final String element : MAPPINGS) {
            inputManager.deleteMapping(element);
        }
        this.keyInit = false;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
        case LEFT_MOUSE_BUTTON_MAPPING:
            if (isPressed && !selecting) {
                this.selecting = true;
                this.selectStartMouse.x = mouse.x;
                this.selectStartMouse.y = mouse.y;
                if (singleSelectEnabled) {
                    this.selecting = false;
                    collectSingleSelectedBlocks();
                }
            }
            if (!isPressed && selecting) {
                this.selecting = false;
                this.selectEndMouse.x = mouse.x;
                this.selectEndMouse.y = mouse.y;
                if (multiSelectEnabled) {
                    collectMultiSelectedBlocks();
                }
            }
            break;
        default:
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
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
        updateFocusedBlock();
    }

    @SneakyThrows
    private void updateFocusedBlock() {
        if (searchingFocusedBlock) {
            return;
        }
        this.searchingFocusedBlock = true;
        final var gm = onRetrieveGameMap.get();
        var task = pool.submit(new FindBlockUnderMouseAction(gm, mouse, onSaveCursor::accept));
        task.get();
        this.searchingFocusedBlock = false;
    }

    @SneakyThrows
    private void collectMultiSelectedBlocks() {
        final var gm = onRetrieveGameMap.get();
        final var startMouse = new Vector2f(Math.min(selectStartMouse.x, selectEndMouse.x),
                Math.min(selectStartMouse.y, selectEndMouse.y));
        final var endMouse = new Vector2f(Math.max(selectStartMouse.x, selectEndMouse.x),
                Math.max(selectStartMouse.y, selectEndMouse.y));
        var taskStart = pool.submit(new FindBlockUnderMouseAction(gm, startMouse, (c) -> {
            selectStartCursor.x = c.x;
            selectStartCursor.y = c.y;
            selectStartCursor.z = c.z;
        }));
        var taskEnd = pool.submit(new FindBlockUnderMouseAction(gm, endMouse, (c) -> {
            selectEndCursor.x = c.x;
            selectEndCursor.y = c.y;
            selectEndCursor.z = c.z;
        }));
        taskStart.get();
        taskEnd.get();
        onSelectSet.accept(selectStartCursor, selectEndCursor);
    }

    @SneakyThrows
    private void collectSingleSelectedBlocks() {
        val gm = onRetrieveGameMap.get();
        val startMouse = new Vector2f(selectStartMouse.x, selectStartMouse.y);
        var taskStart = pool.submit(new FindBlockUnderMouseAction(gm, startMouse, (c) -> {
            selectStartCursor.x = c.x;
            selectStartCursor.y = c.y;
            selectStartCursor.z = c.z;
            selectEndCursor.x = c.x;
            selectEndCursor.y = c.y;
            selectEndCursor.z = c.z;
        }));
        taskStart.get();
        onSelectSet.accept(selectStartCursor, selectEndCursor);
    }

    @RequiredArgsConstructor
    private class FindBlockUnderMouseAction implements Callable<Boolean> {

        private final int width;

        private final int height;

        private final int cursorZ;

        private final int chunksCount;

        private final Vector2f mouse = new Vector2f();

        private final Consumer<GameBlockPos> foundCursor;

        public FindBlockUnderMouseAction(GameMap gm, Vector2f mouse, Consumer<GameBlockPos> foundCursor) {
            this.width = gm.getWidth();
            this.height = gm.getHeight();
            this.cursorZ = gm.getCursorZ();
            this.chunksCount = gm.getChunksCount();
            this.mouse.x = mouse.x;
            this.mouse.y = mouse.y;
            this.foundCursor = foundCursor;
        }

        @Override
        public Boolean call() {
            final var temp = TempVars.get();
            for (int i = 0; i < chunksCount; i++) {
                final MapChunk chunk = onRetrieveChunk.apply(cid2Id(i));
                if (chunk.isLeaf() && chunk.pos.z <= cursorZ && chunk.pos.ep.z > cursorZ) {
                    final var c = chunk.getCenterExtent();
                    if (checkCenterExtent(temp, mouse, c.centerx, c.centery, c.centerz, c.extentx, c.extenty,
                            c.extentz)) {
                        if (findBlockUnderCursor(temp, mouse, chunk, cursorZ, width, height, foundCursor)) {
                            temp.release();
                            return true;
                        }
                    }
                }
            }
            temp.release();
            return false;
        }

    }

    private boolean findBlockUnderCursor(TempVars temp, Vector2f mouse, MapChunk chunk, int cursorZ, float w, float h,
            Consumer<GameBlockPos> foundCursor) {
        for (int x = chunk.getPos().getX(); x < chunk.getPos().getEp().getX(); x++) {
            for (int y = chunk.getPos().getY(); y < chunk.getPos().getEp().getY(); y++) {
                for (int z = chunk.getPos().getZ(); z < chunk.getPos().getEp().getZ(); z++) {
                    final float tx = -w + 2f * x + 1f;
                    final float ty = h - 2f * y - 1f;
                    final float centerx = tx;
                    final float centery = ty;
                    final float centerz = 0;
                    final float extentx = 1f;
                    final float extenty = 1f;
                    final float extentz = 1f;
                    if (z == cursorZ
                            && checkCenterExtent(temp, mouse, centerx, centery, centerz, extentx, extenty, extentz)) {
                        foundCursor.accept(new GameBlockPos(x, y, z));
                        this.oldCursor.x = x;
                        this.oldCursor.y = y;
                        this.oldCursor.z = z;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkCenterExtent(TempVars temp, Vector2f mouse, float centerx, float centery, float centerz,
            float extentx, float extenty, float extentz) {
        final var bottomc = temp.vect1;
        final var topc = temp.vect2;
        bottomc.x = centerx - extentx;
        bottomc.y = centery - extenty;
        bottomc.z = centerz - extentz;
        camera.getScreenCoordinates(bottomc, bottomc);
        topc.x = centerx + extentx;
        topc.y = centery + extenty;
        topc.z = centerz + extentz;
        camera.getScreenCoordinates(topc, topc);
        if (mouse.x >= bottomc.x && mouse.y >= bottomc.y && mouse.x <= topc.x && mouse.y <= topc.y) {
            return true;
        } else {
            return false;
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
