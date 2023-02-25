/*
 * dwarfhustle-gamemap-console-antlr - Console debug commands defined in ANTLR 4.
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
package com.anrisoftware.dwarfhustle.gamemap.console.antlr;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.Optional.of;

import java.util.Optional;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.CoordinatesContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.IdContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.LayersContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.ObjectContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.ObjectTypeContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.PanningVelocityContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.PhysicsContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.PositionContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.RotationContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.ScaleContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.VerbContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.VxContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.VyContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.VzContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.XContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.XxContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.YContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.YyContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.ZContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.ZzContext;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Parses the input string.
 *
 * @author Erwin Müller
 */
@Slf4j
@ToString
public class DebugConsoleParserService extends DebugConsoleBaseListener {

    /**
     * @author Erwin Müller
     */
    public interface DebugConsoleParserServiceFactory {

        DebugConsoleParserService create();
    }

    @RequiredArgsConstructor
    @ToString
    public static class TupelXyz {
        public final float x;
        public final float y;
        public final float z;
    }

    @RequiredArgsConstructor
    @ToString
    public static class TupelObjectIdXyz {
        public final String object;
        public final long id;
        public final float x;
        public final float y;
        public final float z;
    }

    @RequiredArgsConstructor
    @ToString
    public static class TupelXyzXxYyZz {
        public final float x;
        public final float y;
        public final float z;
        public final float xx;
        public final float yy;
        public final float zz;
    }

    @RequiredArgsConstructor
    @ToString
    public static class TupelXyzVxVyVz {
        public final float x;
        public final float y;
        public final float z;
        public final float vx;
        public final float vy;
        public final float vz;
    }

    public Optional<String> verb = Optional.empty();

    public Optional<String> property = Optional.empty();

    public Optional<String> object = Optional.empty();

    public Optional<String> objectType = Optional.empty();

    public Optional<String> physics = Optional.empty();

    public Optional<Long> id = Optional.empty();

    public Optional<Float> x = Optional.empty();

    public Optional<Float> y = Optional.empty();

    public Optional<Float> z = Optional.empty();

    public Optional<Float> xx = Optional.empty();

    public Optional<Float> yy = Optional.empty();

    public Optional<Float> zz = Optional.empty();

    public Optional<Float> vx = Optional.empty();

    public Optional<Float> vy = Optional.empty();

    public Optional<Float> vz = Optional.empty();

    public Optional<Integer> layers = Optional.empty();

    public DebugConsoleParserService parse(String string) {
        var lexer = new DebugConsoleLexer(CharStreams.fromString(string));
        var tokens = new CommonTokenStream(lexer);
        var parser = new DebugConsoleParser(tokens);
        var sentenceContext = parser.sentence();
        var walker = new ParseTreeWalker();
        walker.walk(this, sentenceContext);
        log.debug("Parsed {}", this);
        return this;
    }

    public Optional<TupelXyz> getTupelXyz() {
        if (x.isEmpty() || y.isEmpty() || z.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new TupelXyz(x.get(), y.get(), z.get()));
        }
    }

    public Optional<TupelObjectIdXyz> getTupelObjectIdXyz() {
        if (x.isEmpty() || y.isEmpty() || z.isEmpty() || object.isEmpty() || id.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new TupelObjectIdXyz(object.get(), id.get(), x.get(), y.get(), z.get()));
        }
    }

    public Optional<TupelXyzXxYyZz> getTupelXyzXxYyZz() {
        if (x.isEmpty() || y.isEmpty() || z.isEmpty()) {
            return Optional.empty();
        } else if (xx.isEmpty() || yy.isEmpty() || zz.isEmpty()) {
            return Optional.of(new TupelXyzXxYyZz(x.get(), y.get(), z.get(), 0f, 0f, 0f));
        } else {
            return Optional.of(new TupelXyzXxYyZz(x.get(), y.get(), z.get(), xx.get(), yy.get(), zz.get()));
        }
    }

    public Optional<TupelXyzVxVyVz> getTupelXyzVxVyVz() {
        if (x.isEmpty() || y.isEmpty() || z.isEmpty() || vx.isEmpty() || vy.isEmpty() || vz.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new TupelXyzVxVyVz(x.get(), y.get(), z.get(), vx.get(), vy.get(), vz.get()));
        }
    }

    @Override
    public void enterVerb(VerbContext ctx) {
        this.verb = of(ctx.getText());
    }

    @Override
    public void enterCoordinates(CoordinatesContext ctx) {
        this.property = of("coordinates");
    }

    @Override
    public void enterPosition(PositionContext ctx) {
        this.property = of("position");
    }

    @Override
    public void enterRotation(RotationContext ctx) {
        this.property = of("rotation");
    }

    @Override
    public void enterScale(ScaleContext ctx) {
        this.property = of("scale");
    }

    @Override
    public void enterPanningVelocity(PanningVelocityContext ctx) {
        this.property = of("panningVelocity");
    }

    @Override
    public void enterObjectType(ObjectTypeContext ctx) {
        this.objectType = of(ctx.getText());
    }

    @Override
    public void enterPhysics(PhysicsContext ctx) {
        this.physics = of(ctx.getText());
    }

    @Override
    public void enterX(XContext ctx) {
        this.x = of(parseFloat(ctx.getText()));
    }

    @Override
    public void enterY(YContext ctx) {
        this.y = of(parseFloat(ctx.getText()));
    }

    @Override
    public void enterZ(ZContext ctx) {
        this.z = of(parseFloat(ctx.getText()));
    }

    @Override
    public void enterId(IdContext ctx) {
        this.id = of(parseLong(ctx.getText()));
    }

    @Override
    public void enterObject(ObjectContext ctx) {
        this.object = of(ctx.getText());
    }

    @Override
    public void enterXx(XxContext ctx) {
        this.xx = of(parseFloat(ctx.getText()));
    }

    @Override
    public void enterYy(YyContext ctx) {
        this.yy = of(parseFloat(ctx.getText()));
    }

    @Override
    public void enterZz(ZzContext ctx) {
        this.zz = of(parseFloat(ctx.getText()));
    }

    @Override
    public void enterVx(VxContext ctx) {
        this.vx = of(parseFloat(ctx.getText()));
    }

    @Override
    public void enterVy(VyContext ctx) {
        this.vy = of(parseFloat(ctx.getText()));
    }

    @Override
    public void enterVz(VzContext ctx) {
        this.vz = of(parseFloat(ctx.getText()));
    }

    @Override
    public void enterLayers(LayersContext ctx) {
        this.property = of("layers");
        this.layers = of(parseInt(ctx.getText()));
    }
}
