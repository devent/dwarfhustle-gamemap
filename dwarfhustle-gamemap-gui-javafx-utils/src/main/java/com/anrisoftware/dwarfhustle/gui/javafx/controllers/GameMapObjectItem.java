package com.anrisoftware.dwarfhustle.gui.javafx.controllers;

import com.anrisoftware.dwarfhustle.model.api.objects.GameMapObject;
import com.anrisoftware.dwarfhustle.model.api.objects.KnowledgeGetter;

/**
 * Service to create {@link AbstractGameMapObjectItem}.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public interface GameMapObjectItem {

    AbstractGameMapObjectItem create(GameMapObject go, KnowledgeGetter kg);

    int getType();
}
