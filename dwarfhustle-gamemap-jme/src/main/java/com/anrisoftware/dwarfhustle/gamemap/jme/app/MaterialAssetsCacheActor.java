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
package com.anrisoftware.dwarfhustle.gamemap.jme.app;

import static com.anrisoftware.dwarfhustle.model.actor.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import com.anrisoftware.dwarfhustle.gamemap.model.messages.GetTextureMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.GetTextureMessage.GetTextureSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesErrorMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.messages.LoadTexturesMessage.LoadTexturesSuccessMessage;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.AssetCacheObject;
import com.anrisoftware.dwarfhustle.gamemap.model.resources.TextureCacheObject;
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider;
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message;
import com.anrisoftware.dwarfhustle.model.api.objects.GameObject;
import com.anrisoftware.dwarfhustle.model.db.cache.CacheGetMessage;
import com.google.inject.Injector;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Cache for material textures.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class MaterialAssetsCacheActor extends AbstractAssetsCacheActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            MaterialAssetsCacheActor.class.getSimpleName());

    public static final String NAME = MaterialAssetsCacheActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    /**
     * Factory to create {@link MaterialAssetsCacheActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface MaterialAssetsJcsCacheActorFactory extends AbstractAssetsCacheActorFactory {

        @Override
        MaterialAssetsCacheActor create(ActorContext<Message> context);
    }

    /**
     * Creates the {@link MaterialAssetsCacheActor}.
     *
     * @param injector the {@link Injector} injector.
     * @param timeout  the {@link Duration} timeout.
     */
    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        var actorFactory = injector.getInstance(MaterialAssetsJcsCacheActorFactory.class);
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector, actorFactory));
    }

    @Inject
    private AssetsLoadMaterialTextures textures;

    @Override
    protected int getId() {
        return ID;
    }

    @Override
    protected void retrieveValueFromBackend(CacheGetMessage<?> m, Consumer<GameObject> consumer) {
        var to = textures.loadTextureObject((long) m.key);
        consumer.accept(to);
    }

    @Override
    @SneakyThrows
    protected <T extends GameObject> AssetCacheObject getValueFromBackend(int type, Object key) {
        return textures.loadTextureObject((long) key);
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    private Behavior<Message> onLoadTextures(@SuppressWarnings("rawtypes") LoadTexturesMessage m) {
        log.debug("onLoadTextures {}", m);
        try {
            textures.loadMaterialTextures(cache);
            m.replyTo.tell(new LoadTexturesSuccessMessage<>(m));
        } catch (Throwable e) {
            log.error("onLoadTextures", e);
            m.replyTo.tell(new LoadTexturesErrorMessage<>(m, e));
        }
        return Behaviors.same();
    }

    /**
     * <ul>
     * <li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    private Behavior<Message> onGetTexture(@SuppressWarnings("rawtypes") GetTextureMessage m) {
        log.debug("onGetTexture {}", m);
        var to = (TextureCacheObject) cache.getIfAbsent(m.key, () -> retrieveTexture(m));
        m.consumer.accept(to);
        m.replyTo.tell(new GetTextureSuccessMessage<>(m, to));
        return Behaviors.stopped();
    }

    private TextureCacheObject retrieveTexture(GetTextureMessage<?> m) {
        var to = textures.loadTextureObject(m.key);
        return to;
    }

    @Override
    protected BehaviorBuilder<Message> getInitialBehavior() {
        return super.getInitialBehavior()//
                .onMessage(LoadTexturesMessage.class, this::onLoadTextures)//
                .onMessage(GetTextureMessage.class, this::onGetTexture)//
        ;
    }

}
