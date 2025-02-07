package org.dwarfhustle.gamemap.tester.gui.javafx.actor

import static java.time.Duration.ofSeconds

import java.time.Duration
import java.util.concurrent.CountDownLatch

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import com.anrisoftware.dwarfhustle.gamemap.model.resources.DwarfhustleGamemapModelResourcesModule
import com.anrisoftware.dwarfhustle.gui.javafx.actor.DwarfhustleGamemapGuiJavafxUtilsModule
import com.anrisoftware.dwarfhustle.gui.javafx.messages.AttachGuiMessage
import com.anrisoftware.dwarfhustle.model.actor.ActorSystemProvider
import com.anrisoftware.dwarfhustle.model.actor.DwarfhustleModelActorsModule
import com.anrisoftware.dwarfhustle.model.actor.MessageActor.Message
import com.anrisoftware.dwarfhustle.model.api.objects.ObjectsGetter
import com.anrisoftware.resources.binary.internal.maps.BinariesDefaultMapsModule
import com.anrisoftware.resources.binary.internal.resources.BinaryResourceModule
import com.anrisoftware.resources.images.internal.images.ImagesResourcesModule
import com.anrisoftware.resources.images.internal.mapcached.ResourcesImagesCachedMapModule
import com.anrisoftware.resources.images.internal.scaling.ResourcesSmoothScalingModule
import com.anrisoftware.resources.texts.internal.texts.TextsResourcesDefaultModule
import com.anrisoftware.resources.texts.internal.texts.TextsResourcesModule
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provides
import com.jme3.app.Application
import com.jme3.app.SimpleApplication

import akka.actor.typed.ActorRef
import akka.actor.typed.javadsl.AskPattern
import groovy.util.logging.Slf4j

/**
 * @see TesterMainPanelActor
 * @author Erwin Müller <erwin@muellerpublic.de>
 */
@Slf4j
//@ExtendWith(ApplicationExtension.class)
class TesterMainPanelActorTest {

    static SimpleApplication app

    static Injector injector

    static ActorRef<Message> knowledgeActor

    static ActorSystemProvider actor

    @BeforeAll
    static void setupActor() {
        app = new TestApp()
        injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                    }
                    @Provides
                    public Application getApp() {
                        return app
                    }
                },
                new BinaryResourceModule(),
                new BinariesDefaultMapsModule(),
                new ImagesResourcesModule(),
                new ResourcesImagesCachedMapModule(),
                new ResourcesSmoothScalingModule(),
                new TextsResourcesDefaultModule(),
                new TextsResourcesModule(),
                new DwarfhustleGamemapModelResourcesModule(),
                new DwarfhustleModelActorsModule(),
                new DwarfhustleGamemapGuiJavafxUtilsModule(),
                new DwarfhustleGamemapTesterGuiJavafxActorModule())
        actor = injector.getInstance(ActorSystemProvider.class)
    }

    @AfterAll
    static void closeActor() {
        actor.shutdownWait()
    }

    @Test
    void show_tester_window() {
        def og = { type, key -> } as ObjectsGetter
        def panelActor
        app.start()
        TesterMainPanelActor.create(injector, ofSeconds(1)).whenComplete({ it, ex ->
            panelActor = it
        } ).get()
        def result = AskPattern.ask(panelActor, {replyTo ->
            new AttachGuiMessage(replyTo)
        }, Duration.ofSeconds(300), actor.scheduler)
        def lock = new CountDownLatch(1)
        result.whenComplete( { reply, failure ->
            log.info "AttachGuiMessage reply ${reply} failure ${failure}"
            if (failure == null) {
            }
            lock.countDown()
        })
        lock.await()
        Thread.sleep(1000000)
    }
}
