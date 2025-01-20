/*
 * dwarfhustle-gamemap-gui-javafx - GUI in Javafx.
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
package com.anrisoftware.dwarfhustle.gui.javafx.utils;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.util.Arrays.stream;
import static javafx.embed.swing.SwingFXUtils.toFXImage;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.ImageResource;
import com.anrisoftware.resources.images.external.Images;
import com.jayfella.jme.jfx.JavaFxUI;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Control;
import javafx.scene.image.ImageView;
import lombok.SneakyThrows;

/**
 * Utils methods to run on the different threats.
 *
 * @author Erwin Müller
 */
public class JavaFxUtil {

    /**
     * Returns an {@link ImageView} from the image resource.
     */
    public static ImageView getImageView(Images images, String name, Locale locale, IconSize size) {
        final var res = images.getResource(name, locale, size).getBufferedImage(TYPE_INT_ARGB);
        final var view = new ImageView(toFXImage(res, null));
        return view;
    }

    /**
     * Runs the task on the JME thread.
     */
    public static void runInJmeThread(Runnable task) {
        JavaFxUI.getInstance().runInJmeThread(task);
    }

    /**
     * Runs the task on the JavaFX thread.
     */
    public static void runFxThread(Runnable task) {
        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }

    /**
     * Run the task on the JavaFx thread and wait for its completion. Taken from
     * {@link http://www.java2s.com/example/java/javafx/run-and-wait-on-javafx-thread.html}
     *
     * @param runnable the {@link Runnable} task to run.
     * @param result   the result of the task.
     * @return the result of the task.
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public static <V> V runFxAndWait(long timeout, TimeUnit unit, Runnable runnable, V result)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return result;
        } else {
            var futureTask = new FutureTask<>(runnable, result);
            Platform.runLater(futureTask);
            return futureTask.get(timeout, unit);
        }
    }

    /**
     * Run the task on the JavaFx thread and wait for its completion. Taken from
     * {@link http://www.java2s.com/example/java/javafx/run-and-wait-on-javafx-thread.html}
     *
     * @param callable the {@link Callable} task to run.
     * @return the result of the task.
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public static <V> V runFxAndWait(long timeout, TimeUnit unit, Callable<V> callable)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (Platform.isFxApplicationThread()) {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        } else {
            var futureTask = new FutureTask<>(callable);
            Platform.runLater(futureTask);
            return futureTask.get(timeout, unit);
        }
    }

    /**
     * Returns a {@link ImageView} from the image resource.
     */
    public static ImageView toGraphicFromResource(ImageResource res) {
        return new ImageView(SwingFXUtils.toFXImage(res.getBufferedImage(TYPE_INT_ARGB), null));
    }

    /**
     * Iterates over the fields of an object for all of sub-type of {@link Control}.
     */
    public static <T extends Control> void forEachController(Object controller, Class<T> type, Consumer<T> consumer) {
        stream(controller.getClass().getDeclaredFields()).filter(f -> type.isAssignableFrom(f.getType()))
                .forEach((f) -> {
                    final var c = getControl(type, f, controller);
                    consumer.accept(c);
                });
    }

    @SneakyThrows
    private static <T extends Control> T getControl(Class<T> type, Field field, Object controller) {
        return type.cast(field.get(controller));
    }

    private JavaFxUtil() {
        // Utility class
    }
}
