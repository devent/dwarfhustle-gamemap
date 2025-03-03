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
package com.anrisoftware.dwarfhustle.gui.javafx.actor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import java.util.ServiceLoader;

import org.eclipse.collections.api.factory.primitive.IntObjectMaps;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;

import com.anrisoftware.dwarfhustle.gui.javafx.actor.GameTimeSpeedActor.GameTimeSpeedActorFactory;
import com.anrisoftware.dwarfhustle.gui.javafx.actor.InfoPanelActor.InfoPanelActorFactory;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.GameMapObjectItem;
import com.anrisoftware.dwarfhustle.gui.javafx.controllers.InfoPaneController;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * @see GameTimeSpeedActorFactory
 * @see InfoPanelActorFactory
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class DwarfhustleGamemapGuiJavafxUtilsModule extends AbstractModule {

    private final IntObjectMap<GameMapObjectItem> gameMapObjectItems;

    public DwarfhustleGamemapGuiJavafxUtilsModule() {
        this.gameMapObjectItems = loadGameMapObjectItem();
    }

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(new TypeLiteral<AbstractPaneActor<? extends InfoPaneController>>() {
                }, InfoPanelActor.class).build(InfoPanelActorFactory.class));
        install(new FactoryModuleBuilder().implement(GameTimeSpeedActor.class, GameTimeSpeedActor.class)
                .build(GameTimeSpeedActorFactory.class));
    }

    private IntObjectMap<GameMapObjectItem> loadGameMapObjectItem() {
        MutableIntObjectMap<GameMapObjectItem> map = IntObjectMaps.mutable.empty();
        for (final var s : ServiceLoader.load(GameMapObjectItem.class)) {
            map.put(s.getType(), s);
        }
        assertThat("GameMapObjectItem(s)", map.size(), is(greaterThan(0)));
        return map;
    }

    @Singleton
    @Provides
    @Named("gameMapObjectItems")
    public IntObjectMap<GameMapObjectItem> getGameMapObjectItems() {
        return gameMapObjectItems;
    }

}
