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

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.AddContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.ApplyContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.CoordinatesContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.IdContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.ObjectContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.ObjectTypeContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.PanningVelocityContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.PhysicsContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.PositionContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.RotationContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.SaveContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.ScaleContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.SetContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.VxContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.VyContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.VzContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.XContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.XxContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.YContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.YyContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.ZContext;
import com.anrisoftware.dwarfhustle.gamemap.console.antlr.DebugConsoleParser.ZzContext;

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
	public interface DebugParserServiceFactory {

		DebugConsoleParserService create();
	}

    public String verb;

    public String property;

    public String object;

	public String objectType;

    public String physics;

    public Long id;

    public Integer x;

    public Integer y;

    public Integer z;

    public Float xx;

    public Float yy;

    public Float zz;

    public Float vx;

    public Float vy;

    public Float vz;

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

    @Override
    public void enterAdd(AddContext ctx) {
        this.verb = "add";
    }

    @Override
    public void enterSet(SetContext ctx) {
        this.verb = "set";
    }

    @Override
    public void enterSave(SaveContext ctx) {
        this.verb = "save";
    }

    @Override
    public void enterApply(ApplyContext ctx) {
        this.verb = "apply";
    }

    @Override
    public void enterCoordinates(CoordinatesContext ctx) {
        this.property = "coordinates";
    }

    @Override
    public void enterPosition(PositionContext ctx) {
        this.property = "position";
    }

    @Override
    public void enterRotation(RotationContext ctx) {
        this.property = "rotation";
    }

    @Override
    public void enterScale(ScaleContext ctx) {
        this.property = "scale";
    }

    @Override
    public void enterPanningVelocity(PanningVelocityContext ctx) {
        this.property = "panningVelocity";
    }

    @Override
	public void enterObjectType(ObjectTypeContext ctx) {
		this.objectType = ctx.getText();
    }

    @Override
    public void enterPhysics(PhysicsContext ctx) {
        this.physics = ctx.getText();
    }

    @Override
    public void enterX(XContext ctx) {
        this.x = Integer.parseInt(ctx.getText());
    }

    @Override
    public void enterY(YContext ctx) {
        this.y = Integer.parseInt(ctx.getText());
    }

    @Override
    public void enterZ(ZContext ctx) {
        this.z = Integer.parseInt(ctx.getText());
    }

    @Override
	public void enterId(IdContext ctx) {
        this.id = Long.parseLong(ctx.getText());
    }

    @Override
    public void enterObject(ObjectContext ctx) {
        this.object = ctx.getText();
    }

    @Override
    public void enterXx(XxContext ctx) {
        this.xx = Float.parseFloat(ctx.getText());
    }

    @Override
    public void enterYy(YyContext ctx) {
        this.yy = Float.parseFloat(ctx.getText());
    }

    @Override
    public void enterZz(ZzContext ctx) {
        this.zz = Float.parseFloat(ctx.getText());
    }

    @Override
    public void enterVx(VxContext ctx) {
        this.vx = Float.parseFloat(ctx.getText());
    }

    @Override
    public void enterVy(VyContext ctx) {
        this.vy = Float.parseFloat(ctx.getText());
    }

    @Override
    public void enterVz(VzContext ctx) {
        this.vz = Float.parseFloat(ctx.getText());
    }
}
