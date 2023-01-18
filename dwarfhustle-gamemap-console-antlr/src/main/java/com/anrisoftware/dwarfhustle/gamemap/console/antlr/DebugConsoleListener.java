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

// Generated from java-escape by ANTLR 4.11.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DebugConsoleParser}.
 */
public interface DebugConsoleListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#sentence}.
	 * @param ctx the parse tree
	 */
	void enterSentence(DebugConsoleParser.SentenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#sentence}.
	 * @param ctx the parse tree
	 */
	void exitSentence(DebugConsoleParser.SentenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#physics}.
	 * @param ctx the parse tree
	 */
	void enterPhysics(DebugConsoleParser.PhysicsContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#physics}.
	 * @param ctx the parse tree
	 */
	void exitPhysics(DebugConsoleParser.PhysicsContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#add}.
	 * @param ctx the parse tree
	 */
	void enterAdd(DebugConsoleParser.AddContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#add}.
	 * @param ctx the parse tree
	 */
	void exitAdd(DebugConsoleParser.AddContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#set}.
	 * @param ctx the parse tree
	 */
	void enterSet(DebugConsoleParser.SetContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#set}.
	 * @param ctx the parse tree
	 */
	void exitSet(DebugConsoleParser.SetContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#save}.
	 * @param ctx the parse tree
	 */
	void enterSave(DebugConsoleParser.SaveContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#save}.
	 * @param ctx the parse tree
	 */
	void exitSave(DebugConsoleParser.SaveContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#apply}.
	 * @param ctx the parse tree
	 */
	void enterApply(DebugConsoleParser.ApplyContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#apply}.
	 * @param ctx the parse tree
	 */
	void exitApply(DebugConsoleParser.ApplyContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#impulse}.
	 * @param ctx the parse tree
	 */
	void enterImpulse(DebugConsoleParser.ImpulseContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#impulse}.
	 * @param ctx the parse tree
	 */
	void exitImpulse(DebugConsoleParser.ImpulseContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#objectType}.
	 * @param ctx the parse tree
	 */
	void enterObject(DebugConsoleParser.ObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#objectType}.
	 * @param ctx the parse tree
	 */
	void exitObject(DebugConsoleParser.ObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#property}.
	 * @param ctx the parse tree
	 */
	void enterProperty(DebugConsoleParser.PropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#property}.
	 * @param ctx the parse tree
	 */
	void exitProperty(DebugConsoleParser.PropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#coordinates}.
	 * @param ctx the parse tree
	 */
	void enterCoordinates(DebugConsoleParser.CoordinatesContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#coordinates}.
	 * @param ctx the parse tree
	 */
	void exitCoordinates(DebugConsoleParser.CoordinatesContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#rotation}.
	 * @param ctx the parse tree
	 */
	void enterRotation(DebugConsoleParser.RotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#rotation}.
	 * @param ctx the parse tree
	 */
	void exitRotation(DebugConsoleParser.RotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#scale}.
	 * @param ctx the parse tree
	 */
	void enterScale(DebugConsoleParser.ScaleContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#scale}.
	 * @param ctx the parse tree
	 */
	void exitScale(DebugConsoleParser.ScaleContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#position}.
	 * @param ctx the parse tree
	 */
	void enterPosition(DebugConsoleParser.PositionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#position}.
	 * @param ctx the parse tree
	 */
	void exitPosition(DebugConsoleParser.PositionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#panningVelocity}.
	 * @param ctx the parse tree
	 */
	void enterPanningVelocity(DebugConsoleParser.PanningVelocityContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#panningVelocity}.
	 * @param ctx the parse tree
	 */
	void exitPanningVelocity(DebugConsoleParser.PanningVelocityContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#vector}.
	 * @param ctx the parse tree
	 */
	void enterVector(DebugConsoleParser.VectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#vector}.
	 * @param ctx the parse tree
	 */
	void exitVector(DebugConsoleParser.VectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#x}.
	 * @param ctx the parse tree
	 */
	void enterX(DebugConsoleParser.XContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#x}.
	 * @param ctx the parse tree
	 */
	void exitX(DebugConsoleParser.XContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#y}.
	 * @param ctx the parse tree
	 */
	void enterY(DebugConsoleParser.YContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#y}.
	 * @param ctx the parse tree
	 */
	void exitY(DebugConsoleParser.YContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#z}.
	 * @param ctx the parse tree
	 */
	void enterZ(DebugConsoleParser.ZContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#z}.
	 * @param ctx the parse tree
	 */
	void exitZ(DebugConsoleParser.ZContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#xx}.
	 * @param ctx the parse tree
	 */
	void enterXx(DebugConsoleParser.XxContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#xx}.
	 * @param ctx the parse tree
	 */
	void exitXx(DebugConsoleParser.XxContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#yy}.
	 * @param ctx the parse tree
	 */
	void enterYy(DebugConsoleParser.YyContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#yy}.
	 * @param ctx the parse tree
	 */
	void exitYy(DebugConsoleParser.YyContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#zz}.
	 * @param ctx the parse tree
	 */
	void enterZz(DebugConsoleParser.ZzContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#zz}.
	 * @param ctx the parse tree
	 */
	void exitZz(DebugConsoleParser.ZzContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#vx}.
	 * @param ctx the parse tree
	 */
	void enterVx(DebugConsoleParser.VxContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#vx}.
	 * @param ctx the parse tree
	 */
	void exitVx(DebugConsoleParser.VxContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#vy}.
	 * @param ctx the parse tree
	 */
	void enterVy(DebugConsoleParser.VyContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#vy}.
	 * @param ctx the parse tree
	 */
	void exitVy(DebugConsoleParser.VyContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#vz}.
	 * @param ctx the parse tree
	 */
	void enterVz(DebugConsoleParser.VzContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#vz}.
	 * @param ctx the parse tree
	 */
	void exitVz(DebugConsoleParser.VzContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#selector}.
	 * @param ctx the parse tree
	 */
	void enterSelector(DebugConsoleParser.SelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#selector}.
	 * @param ctx the parse tree
	 */
	void exitSelector(DebugConsoleParser.SelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(DebugConsoleParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(DebugConsoleParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#id}.
	 * @param ctx the parse tree
	 */
	void enterId(DebugConsoleParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#id}.
	 * @param ctx the parse tree
	 */
	void exitId(DebugConsoleParser.IdContext ctx);
	/**
	 * Enter a parse tree produced by {@link DebugConsoleParser#objectType}.
	 * @param ctx the parse tree
	 */
	void enterObjectType(DebugConsoleParser.ObjectTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DebugConsoleParser#objectType}.
	 * @param ctx the parse tree
	 */
	void exitObjectType(DebugConsoleParser.ObjectTypeContext ctx);
}