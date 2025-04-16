/*
 * dwarfhustle-gamemap-gui-javafx-utils - Game map.
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
package com.anrisoftware.dwarfhustle.gui.javafx.actor;

import static com.anrisoftware.dwarfhustle.gui.javafx.actor.AdditionalCss.ADDITIONAL_CSS;
import static com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil.runFxThread;
import static com.anrisoftware.dwarfhustle.model.api.objects.GameMap.getGameMap;
import static com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeObject.id2Kid;
import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage.askKnowledgeId;
import static com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.KnowledgeGetMessage.askKnowledgeObjects;
import static java.time.Duration.ofMillis;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.set.primitive.IntSet;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.GameTickMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.GameSettingsProvider;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.PanelControllerBuild.PanelControllerResult;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GameMapObjectInfoPaneItem;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.JobCreatePaneController;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.JobCreatePaneController.GameMapObjectItem;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.MapBlockItemWidgetController;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.GameQuitMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.MainWindowResizedMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.messages.ObjectPaneAttachToTabMessage;
import com.anrisoftware.dwarfhustle.gui.javafx.utils.JavaFxUtil;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.actor.ShutdownMessage;
import com.anrisoftware.dwarfhustle.model.api.buildings.KnowledgeBuilding;
import com.anrisoftware.dwarfhustle.model.api.buildings.KnowledgeWorkJob;
import com.anrisoftware.dwarfhustle.model.api.materials.KnowledgeMaterial;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMap;
import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter;
import com.anrisoftware.dwarfhustle.model.db.cache.MapChunksJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.MapObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.db.cache.StoredObjectsJcsCacheActor;
import com.anrisoftware.dwarfhustle.model.knowledge.powerloom.pl.PowerLoomKnowledgeActor;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;
import com.google.inject.Injector;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Create jJobs pane.
 *
 * @author Erwin Müller
 */
@Slf4j
public class CreateJobsPanelActor extends AbstractPaneActor<JobCreatePaneController> {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            CreateJobsPanelActor.class.getSimpleName());

    public static final String NAME = CreateJobsPanelActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    private static final Map<String, PanelActorCreator> panelActors = Maps.mutable.empty();

    static {
    }

    public interface CreateJobsPanelActorFactory extends AbstractPaneActorFactory<JobCreatePaneController> {
    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        return AbstractPaneActor.create(injector, timeout, ID, KEY, NAME, CreateJobsPanelActorFactory.class,
                "/work_job_create_pane_ui.fxml", panelActors, PanelControllerBuild.class, ADDITIONAL_CSS);
    }

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameSettingsProvider gs;

    @Inject
    @Named("type-gameMapObjectInfoPaneItems")
    private IntObjectMap<GameMapObjectInfoPaneItem> gameMapObjectInfoPaneItems;

    @Inject
    @Named("AppTexts")
    private Texts appTexts;

    @Inject
    @Named("AppIcons")
    private Images appIcons;

    private PanelControllerResult<MapBlockItemWidgetController> mapTileItemWidget;

    private ObjectsGetter og;

    private ObjectsGetter cg;

    private ObjectsGetter mg;

    private long currentMap;

    private KnowledgeGetter kg;

    private long oldSelectedObject = 0;

    @Override
    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        this.og = actor.getObjectGetterAsyncNow(StoredObjectsJcsCacheActor.ID);
        this.cg = actor.getObjectGetterAsyncNow(MapChunksJcsCacheActor.ID);
        this.mg = actor.getObjectGetterAsyncNow(MapObjectsJcsCacheActor.ID);
        this.kg = actor.getKnowledgeGetterAsyncNow(PowerLoomKnowledgeActor.ID);
        this.currentMap = gs.get().currentMap.get();
        gs.get().currentMap.addListener((o, ov, nv) -> {
            currentMap = nv.longValue();
        });
        runFxThread(() -> {
            final var controller = is.controller;
        });
        return getDefaultBehavior()//
        ;
    }

    @Override
    protected Behavior<Message> onGameTick(GameTickMessage m) {
        // log.trace("onGameTick {}", m);
        updatePane();
        return Behaviors.same();
    }

    protected Behavior<Message> onAttachPane(ObjectPaneAttachToTabMessage m) {
        log.trace("onAttachPane {}", m);
        JavaFxUtil.runFxThread(() -> {
            is.controller.onUpdateSelectedJob = this::onUpdateSelectedJob;
            is.controller.onUpdateSelectedInputItem = this::onUpdateSelectedInputItem;
            is.controller.jobNamesList.getSelectionModel().select(0);
            m.tab.setContent(is.controller.jobCreatePane);
        });
        return Behaviors.same();
    }

    private BehaviorBuilder<Message> getDefaultBehavior() {
        return super.getBehaviorAfterAttachGui()//
                .onMessage(GameTickMessage.class, this::onGameTick)//
                .onMessage(ObjectPaneAttachToTabMessage.class, this::onAttachPane)//
        ;
    }

    @Override
    protected Behavior<Message> onShutdown(ShutdownMessage m) {
        return Behaviors.stopped();
    }

    @Override
    protected void attachPaneState() {
        // nothing
    }

    @Override
    protected void setupUiOnFxThread() {
        is.controller.setLocale(appTexts, appIcons, Locale.ENGLISH);
    }

    @Override
    protected Behavior<Message> onGameQuit(GameQuitMessage m) {
        return Behaviors.same();
    }

    @Override
    protected Behavior<Message> onMainWindowResized(MainWindowResizedMessage m) {
        return Behaviors.same();
    }

    @SneakyThrows
    private void updatePane() {
        final var gm = getGameMap(og, currentMap);
        if (oldSelectedObject == gm.getSelectedObjectId()) {
            return;
        }
        this.oldSelectedObject = gm.getSelectedObjectId();
        val buildingId = id2Kid(askKnowledgeId(actor.getActorSystem(), ofMillis(100), KnowledgeBuilding.class,
                KnowledgeBuilding.TYPE, (o) -> o.getName().equalsIgnoreCase("building-carpenter")));
        val jobs = askKnowledgeObjects(actor.getActorSystem(), ofMillis(100), KnowledgeWorkJob.class,
                KnowledgeWorkJob.TYPE, (o) -> o.getBuilding() == buildingId);
        runFxThread(() -> {
            is.controller.jobNamesItems.setAll(jobs.toList());
        });
    }

    private void clearInfoPane() {
        runFxThread(() -> {
        });
    }

    private void onUpdateSelectedJob(KnowledgeWorkJob job) {
        updateMaterialsListForJob(job);
    }

    private void onUpdateSelectedInputItem(GameMapObjectItem item) {

    }

    @SneakyThrows
    private void updateMaterialsListForJob(KnowledgeWorkJob k) {
        val gm = GameMap.getGameMap(og, currentMap);
        IntSet inputMaterials = k.getInputObjects().keySet();
        IntSet inputTypes = k.getInputObjects().keySet();
        List<GameMapObjectItem> items = Lists.mutable.empty();
        val materials = askKnowledgeObjects(actor.getActorSystem(), ofMillis(100), KnowledgeMaterial.class,
                KnowledgeMaterial.TYPE, o -> inputMaterials.contains(o.getObjectType()));
        for (final var it = inputTypes.intIterator(); it.hasNext();) {
            final int type = it.next();
            System.out.println("updateMaterialsListForJob " + type); // TODO
            val gos = gm.getObjectForType(type);
            for (Long id : gos) {
                System.out.println(id); // TODO
                GameMapObject go = og.get(type, id);
                val material = materials.detect((m) -> m.getKid() == go.getKid());
                items.add(new GameMapObjectItem(go, material));
            }
        }
        runFxThread(() -> {
            is.controller.jobMaterialsItems.setAll(items);
        });
    }

}
