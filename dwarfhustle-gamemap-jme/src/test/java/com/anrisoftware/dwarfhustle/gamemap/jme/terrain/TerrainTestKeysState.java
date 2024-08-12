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

import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage.askKnowledgeObjects;

import java.time.Duration;
import java.util.function.Function;

import org.lable.oss.uniqueid.IDGenerator;

import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameBlockPos;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObjectsStorage;
import com.anrisoftware.dwarfhustle.model.api.objects.IdsObjectsProvider.IdsObjects;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject;
import com.anrisoftware.dwarfhustle.model.api.objects.MapBlock;
import com.anrisoftware.dwarfhustle.model.api.objects.MapObjectsStorage;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeShrub;
import com.anrisoftware.dwarfhustle.model.api.vegetations.KnowledgeTreeSampling;
import com.anrisoftware.dwarfhustle.model.db.store.MapChunksStore;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import akka.actor.typed.ActorRef;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Inputs for testing.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class TerrainTestKeysState extends BaseAppState implements ActionListener {

    private static final String SHOW_SELECTED_BLOCK_MAPPING = "SHOW_SELECTED_BLOCK_MAPPING";

    private static final String SHOW_OBJECTS_BLOCK_MAPPING = "SHOW_OBJECTS_BLOCK_MAPPING";

    private static final String ADD_SHRUB_MAPPING = "ADD_SHRUB_MAPPING";

    private static final String ADD_SAMPLING_MAPPING = "ADD_SAMPLING_MAPPING";

    private static final String[] MAPPINGS = new String[] { SHOW_SELECTED_BLOCK_MAPPING, SHOW_OBJECTS_BLOCK_MAPPING,
            ADD_SHRUB_MAPPING, ADD_SAMPLING_MAPPING };

    private static final Runnable EMPTY_ACTION = () -> {
    };

    @Inject
    private InputManager inputManager;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    @IdsObjects
    private IDGenerator ids;

    @Inject
    private GameObjectsStorage goStorage;

    @Inject
    private MapObjectsStorage moStorage;

    private GameMap gm;

    private MapChunksStore mapStore;

    private boolean keyInit = false;

    private boolean showSelectedBlock = false;

    private float time = 0;

    private GameBlockPos oldCursor = new GameBlockPos();

    private Runnable nextAction = EMPTY_ACTION;

    private ActorRef<Message> knowledgeActor;

    @Inject
    public TerrainTestKeysState() {
        super(TerrainTestKeysState.class.getSimpleName());
    }

    public void setKnowledge(ActorRef<Message> knowledgeActor) {
        this.knowledgeActor = knowledgeActor;
    }

    public void setMapStore(MapChunksStore store) {
        this.mapStore = store;
    }

    public void setObjectsStore(MapChunksStore store) {
        this.mapStore = store;
    }

    public void setGameMap(GameMap gm) {
        this.gm = gm;
        this.oldCursor = gm.getCursor();
        if (!keyInit) {
            initKeys();
        }
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
    }

    @Override
    protected void onDisable() {
        log.debug("onDisable");
        if (keyInit) {
            deleteKeys();
        }
    }

    private void initKeys() {
        inputManager.addMapping(SHOW_SELECTED_BLOCK_MAPPING, new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping(SHOW_OBJECTS_BLOCK_MAPPING, new KeyTrigger(KeyInput.KEY_O));
        inputManager.addMapping(ADD_SHRUB_MAPPING, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(ADD_SAMPLING_MAPPING, new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(this, MAPPINGS);
        System.out.println("I - " + SHOW_SELECTED_BLOCK_MAPPING);
        System.out.println("O - " + SHOW_OBJECTS_BLOCK_MAPPING);
        System.out.println("S - " + ADD_SHRUB_MAPPING);
        System.out.println("T - " + ADD_SAMPLING_MAPPING);
        this.keyInit = true;
    }

    private void deleteKeys() {
        inputManager.removeListener(this);
        for (String element : MAPPINGS) {
            inputManager.deleteMapping(element);
        }
        this.keyInit = false;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed) {
            switch (name) {
            case SHOW_SELECTED_BLOCK_MAPPING: {
                this.showSelectedBlock = !showSelectedBlock;
                break;
            }
            case SHOW_OBJECTS_BLOCK_MAPPING: {
                this.nextAction = this::showObjects;
                break;
            }
            case ADD_SHRUB_MAPPING: {
                this.nextAction = this::addShrub;
                break;
            }
            case ADD_SAMPLING_MAPPING: {
                this.nextAction = this::addSampling;
                break;
            }
            default:
                throw new IllegalArgumentException("Unexpected value: " + name);
            }
        }
    }

    @Override
    public void update(float tpf) {
        this.time += tpf;
        if (this.time > 0.333f) {
            this.time = 0;
            nextAction.run();
            this.nextAction = EMPTY_ACTION;
            if (showSelectedBlock) {
                if (!oldCursor.equals(gm.getCursor())) {
                    this.oldCursor = gm.getCursor();
                    var mb = mapStore.findBlock(gm.getCursor());
                    System.out.println(mb);
                }
            }
        }
    }

    private void showObjects() {
        var omb = mapStore.findBlock(gm.getCursor());
        if (!omb.isEmpty()) {
            var mb = omb.get().getTwo();
            moStorage.getObjects(mb.getPos().getX(), mb.getPos().getY(), mb.getPos().getZ(), (type, id) -> {
                var go = goStorage.get(type, id);
                System.out.println(go); // TODO
            });
        }
    }

    private void addShrub() {
        addObject(KnowledgeShrub.TYPE, (mb) -> mb.isFilled() && mb.isDiscovered());
    }

    private void addSampling() {
        addObject(KnowledgeTreeSampling.TYPE, (mb) -> mb.isFilled() && mb.isDiscovered());
    }

    private void addObject(String type, Function<MapBlock, Boolean> validBlock) {
        this.oldCursor = gm.getCursor();
        var omb = mapStore.findBlock(gm.getCursor());
        if (!omb.isEmpty()) {
            var mb = omb.get().getTwo();
            if (validBlock.apply(mb)) {
                insertObject(type, mb);
            } else {
                System.out.printf("%f is not a valid block\n", mb); // TODO
            }
        }
    }

    private void insertObject(String type, MapBlock mb) {
        askKnowledgeObjects(knowledgeActor, Duration.ofSeconds(1), actor.getScheduler(), type)
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        var first = res.getFirst();
                        insertObject(mb, first);
                    }
                });
    }

    @SneakyThrows
    private void insertObject(MapBlock mb, KnowledgeObject ko) {
        System.out.println(ko); // TODO
        var o = (GameMapObject) ko.createObject(ids.generate());
        o.setMap(gm.id);
        o.setPos(mb.getPos());
        goStorage.set(o.getObjectType(), o);
        moStorage.putObject(o.getPos().getX(), o.getPos().getY(), o.getPos().getZ(), o.getObjectType(), o.getId());
    }
}
