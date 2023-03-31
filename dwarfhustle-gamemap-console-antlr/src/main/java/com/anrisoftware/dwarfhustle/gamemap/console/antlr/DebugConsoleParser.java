/*
 * Dwarf Hustle Game Map - Game map.
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
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class DebugConsoleParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, NUMBER=21, STRING=22, ID=23, ID_CHARS=24, 
		WS=25;
	public static final int
		RULE_sentence = 0, RULE_verb = 1, RULE_physics = 2, RULE_object = 3, RULE_property = 4, 
		RULE_coordinates = 5, RULE_rotation = 6, RULE_scale = 7, RULE_position = 8, 
		RULE_panningVelocity = 9, RULE_vector = 10, RULE_x = 11, RULE_y = 12, 
		RULE_z = 13, RULE_xx = 14, RULE_yy = 15, RULE_zz = 16, RULE_vx = 17, RULE_vy = 18, 
		RULE_vz = 19, RULE_time = 20, RULE_hours = 21, RULE_minutes = 22, RULE_seconds = 23, 
		RULE_selector = 24, RULE_parameter = 25, RULE_id = 26, RULE_objectType = 27, 
		RULE_layers = 28;
	private static String[] makeRuleNames() {
		return new String[] {
			"sentence", "verb", "physics", "object", "property", "coordinates", "rotation", 
			"scale", "position", "panningVelocity", "vector", "x", "y", "z", "xx", 
			"yy", "zz", "vx", "vy", "vz", "time", "hours", "minutes", "seconds", 
			"selector", "parameter", "id", "objectType", "layers"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'here'", "'to'", "'add'", "'set'", "'save'", "'apply'", "'open'", 
			"'impulse'", "'coordinates'", "'rotation'", "'scale'", "'position'", 
			"'panningVelocity'", "'layers'", "'time'", "','", "':'", "'with'", "'id'", 
			"'='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "NUMBER", "STRING", 
			"ID", "ID_CHARS", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "java-escape"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DebugConsoleParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SentenceContext extends ParserRuleContext {
		public VerbContext verb() {
			return getRuleContext(VerbContext.class,0);
		}
		public ObjectTypeContext objectType() {
			return getRuleContext(ObjectTypeContext.class,0);
		}
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public PropertyContext property() {
			return getRuleContext(PropertyContext.class,0);
		}
		public ObjectContext object() {
			return getRuleContext(ObjectContext.class,0);
		}
		public SelectorContext selector() {
			return getRuleContext(SelectorContext.class,0);
		}
		public PhysicsContext physics() {
			return getRuleContext(PhysicsContext.class,0);
		}
		public VectorContext vector() {
			return getRuleContext(VectorContext.class,0);
		}
		public PositionContext position() {
			return getRuleContext(PositionContext.class,0);
		}
		public SentenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sentence; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterSentence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitSentence(this);
		}
	}

	public final SentenceContext sentence() throws RecognitionException {
		SentenceContext _localctx = new SentenceContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_sentence);
		int _la;
		try {
			setState(89);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(58);
				verb();
				setState(59);
				objectType();
				setState(60);
				coordinates();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(62);
				verb();
				setState(63);
				objectType();
				setState(64);
				match(T__0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(66);
				verb();
				setState(67);
				property();
				setState(68);
				match(T__1);
				setState(69);
				object();
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__17) {
					{
					setState(70);
					selector();
					}
				}

				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(73);
				verb();
				setState(74);
				object();
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__17) {
					{
					setState(75);
					selector();
					}
				}

				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(78);
				verb();
				setState(79);
				physics();
				setState(80);
				vector();
				setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(81);
					position();
					}
				}

				setState(84);
				match(T__1);
				setState(85);
				object();
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__17) {
					{
					setState(86);
					selector();
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VerbContext extends ParserRuleContext {
		public VerbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_verb; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterVerb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitVerb(this);
		}
	}

	public final VerbContext verb() throws RecognitionException {
		VerbContext _localctx = new VerbContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_verb);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			_la = _input.LA(1);
			if ( !(((_la) & ~0x3f) == 0 && ((1L << _la) & 248L) != 0) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PhysicsContext extends ParserRuleContext {
		public PhysicsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_physics; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterPhysics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitPhysics(this);
		}
	}

	public final PhysicsContext physics() throws RecognitionException {
		PhysicsContext _localctx = new PhysicsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_physics);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			match(T__7);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ObjectContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(DebugConsoleParser.ID, 0); }
		public ObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_object; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitObject(this);
		}
	}

	public final ObjectContext object() throws RecognitionException {
		ObjectContext _localctx = new ObjectContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_object);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PropertyContext extends ParserRuleContext {
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public RotationContext rotation() {
			return getRuleContext(RotationContext.class,0);
		}
		public ScaleContext scale() {
			return getRuleContext(ScaleContext.class,0);
		}
		public PositionContext position() {
			return getRuleContext(PositionContext.class,0);
		}
		public PanningVelocityContext panningVelocity() {
			return getRuleContext(PanningVelocityContext.class,0);
		}
		public LayersContext layers() {
			return getRuleContext(LayersContext.class,0);
		}
		public TimeContext time() {
			return getRuleContext(TimeContext.class,0);
		}
		public PropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_property; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitProperty(this);
		}
	}

	public final PropertyContext property() throws RecognitionException {
		PropertyContext _localctx = new PropertyContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_property);
		try {
			setState(111);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__8:
				enterOuterAlt(_localctx, 1);
				{
				setState(97);
				match(T__8);
				setState(98);
				coordinates();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(99);
				match(T__9);
				setState(100);
				rotation();
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 3);
				{
				setState(101);
				match(T__10);
				setState(102);
				scale();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 4);
				{
				setState(103);
				match(T__11);
				setState(104);
				position();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 5);
				{
				setState(105);
				match(T__12);
				setState(106);
				panningVelocity();
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 6);
				{
				setState(107);
				match(T__13);
				setState(108);
				layers();
				}
				break;
			case T__14:
				enterOuterAlt(_localctx, 7);
				{
				setState(109);
				match(T__14);
				setState(110);
				time();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CoordinatesContext extends ParserRuleContext {
		public XContext x() {
			return getRuleContext(XContext.class,0);
		}
		public YContext y() {
			return getRuleContext(YContext.class,0);
		}
		public ZContext z() {
			return getRuleContext(ZContext.class,0);
		}
		public XxContext xx() {
			return getRuleContext(XxContext.class,0);
		}
		public YyContext yy() {
			return getRuleContext(YyContext.class,0);
		}
		public ZzContext zz() {
			return getRuleContext(ZzContext.class,0);
		}
		public CoordinatesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coordinates; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterCoordinates(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitCoordinates(this);
		}
	}

	public final CoordinatesContext coordinates() throws RecognitionException {
		CoordinatesContext _localctx = new CoordinatesContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_coordinates);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			x();
			setState(114);
			match(T__15);
			setState(115);
			y();
			setState(116);
			match(T__15);
			setState(117);
			z();
			setState(124);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NUMBER) {
				{
				setState(118);
				xx();
				setState(119);
				match(T__15);
				setState(120);
				yy();
				setState(121);
				match(T__15);
				setState(122);
				zz();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RotationContext extends ParserRuleContext {
		public XContext x() {
			return getRuleContext(XContext.class,0);
		}
		public YContext y() {
			return getRuleContext(YContext.class,0);
		}
		public ZContext z() {
			return getRuleContext(ZContext.class,0);
		}
		public RotationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rotation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterRotation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitRotation(this);
		}
	}

	public final RotationContext rotation() throws RecognitionException {
		RotationContext _localctx = new RotationContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_rotation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			x();
			setState(127);
			match(T__15);
			setState(128);
			y();
			setState(129);
			match(T__15);
			setState(130);
			z();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ScaleContext extends ParserRuleContext {
		public XContext x() {
			return getRuleContext(XContext.class,0);
		}
		public YContext y() {
			return getRuleContext(YContext.class,0);
		}
		public ZContext z() {
			return getRuleContext(ZContext.class,0);
		}
		public ScaleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scale; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterScale(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitScale(this);
		}
	}

	public final ScaleContext scale() throws RecognitionException {
		ScaleContext _localctx = new ScaleContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_scale);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			x();
			setState(133);
			match(T__15);
			setState(134);
			y();
			setState(135);
			match(T__15);
			setState(136);
			z();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PositionContext extends ParserRuleContext {
		public XContext x() {
			return getRuleContext(XContext.class,0);
		}
		public YContext y() {
			return getRuleContext(YContext.class,0);
		}
		public ZContext z() {
			return getRuleContext(ZContext.class,0);
		}
		public PositionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_position; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterPosition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitPosition(this);
		}
	}

	public final PositionContext position() throws RecognitionException {
		PositionContext _localctx = new PositionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_position);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			x();
			setState(139);
			match(T__15);
			setState(140);
			y();
			setState(141);
			match(T__15);
			setState(142);
			z();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PanningVelocityContext extends ParserRuleContext {
		public XContext x() {
			return getRuleContext(XContext.class,0);
		}
		public YContext y() {
			return getRuleContext(YContext.class,0);
		}
		public ZContext z() {
			return getRuleContext(ZContext.class,0);
		}
		public PanningVelocityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_panningVelocity; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterPanningVelocity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitPanningVelocity(this);
		}
	}

	public final PanningVelocityContext panningVelocity() throws RecognitionException {
		PanningVelocityContext _localctx = new PanningVelocityContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_panningVelocity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			x();
			setState(145);
			match(T__15);
			setState(146);
			y();
			setState(147);
			match(T__15);
			setState(148);
			z();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VectorContext extends ParserRuleContext {
		public VxContext vx() {
			return getRuleContext(VxContext.class,0);
		}
		public VyContext vy() {
			return getRuleContext(VyContext.class,0);
		}
		public VzContext vz() {
			return getRuleContext(VzContext.class,0);
		}
		public VectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterVector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitVector(this);
		}
	}

	public final VectorContext vector() throws RecognitionException {
		VectorContext _localctx = new VectorContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_vector);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			vx();
			setState(151);
			match(T__15);
			setState(152);
			vy();
			setState(153);
			match(T__15);
			setState(154);
			vz();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class XContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public XContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_x; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterX(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitX(this);
		}
	}

	public final XContext x() throws RecognitionException {
		XContext _localctx = new XContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_x);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class YContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public YContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_y; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterY(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitY(this);
		}
	}

	public final YContext y() throws RecognitionException {
		YContext _localctx = new YContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_y);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(158);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ZContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public ZContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_z; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterZ(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitZ(this);
		}
	}

	public final ZContext z() throws RecognitionException {
		ZContext _localctx = new ZContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_z);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class XxContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public XxContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xx; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterXx(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitXx(this);
		}
	}

	public final XxContext xx() throws RecognitionException {
		XxContext _localctx = new XxContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_xx);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class YyContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public YyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_yy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterYy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitYy(this);
		}
	}

	public final YyContext yy() throws RecognitionException {
		YyContext _localctx = new YyContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_yy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ZzContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public ZzContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_zz; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterZz(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitZz(this);
		}
	}

	public final ZzContext zz() throws RecognitionException {
		ZzContext _localctx = new ZzContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_zz);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VxContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public VxContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vx; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterVx(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitVx(this);
		}
	}

	public final VxContext vx() throws RecognitionException {
		VxContext _localctx = new VxContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_vx);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VyContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public VyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterVy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitVy(this);
		}
	}

	public final VyContext vy() throws RecognitionException {
		VyContext _localctx = new VyContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_vy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VzContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public VzContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vz; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterVz(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitVz(this);
		}
	}

	public final VzContext vz() throws RecognitionException {
		VzContext _localctx = new VzContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_vz);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TimeContext extends ParserRuleContext {
		public HoursContext hours() {
			return getRuleContext(HoursContext.class,0);
		}
		public MinutesContext minutes() {
			return getRuleContext(MinutesContext.class,0);
		}
		public SecondsContext seconds() {
			return getRuleContext(SecondsContext.class,0);
		}
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitTime(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(174);
			hours();
			setState(175);
			match(T__16);
			setState(176);
			minutes();
			setState(177);
			match(T__16);
			setState(178);
			seconds();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HoursContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public HoursContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hours; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterHours(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitHours(this);
		}
	}

	public final HoursContext hours() throws RecognitionException {
		HoursContext _localctx = new HoursContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_hours);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MinutesContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public MinutesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_minutes; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterMinutes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitMinutes(this);
		}
	}

	public final MinutesContext minutes() throws RecognitionException {
		MinutesContext _localctx = new MinutesContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_minutes);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SecondsContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public SecondsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_seconds; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterSeconds(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitSeconds(this);
		}
	}

	public final SecondsContext seconds() throws RecognitionException {
		SecondsContext _localctx = new SecondsContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_seconds);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(184);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectorContext extends ParserRuleContext {
		public ParameterContext parameter() {
			return getRuleContext(ParameterContext.class,0);
		}
		public SelectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterSelector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitSelector(this);
		}
	}

	public final SelectorContext selector() throws RecognitionException {
		SelectorContext _localctx = new SelectorContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_selector);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(186);
			match(T__17);
			setState(187);
			parameter();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParameterContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitParameter(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_parameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			match(T__18);
			setState(190);
			match(T__19);
			setState(191);
			id();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public IdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitId(this);
		}
	}

	public final IdContext id() throws RecognitionException {
		IdContext _localctx = new IdContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ObjectTypeContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(DebugConsoleParser.ID, 0); }
		public ObjectTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterObjectType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitObjectType(this);
		}
	}

	public final ObjectTypeContext objectType() throws RecognitionException {
		ObjectTypeContext _localctx = new ObjectTypeContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_objectType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LayersContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(DebugConsoleParser.NUMBER, 0); }
		public LayersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_layers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterLayers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitLayers(this);
		}
	}

	public final LayersContext layers() throws RecognitionException {
		LayersContext _localctx = new LayersContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_layers);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0019\u00c8\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007"+
		"\u001b\u0002\u001c\u0007\u001c\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000H\b\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0003\u0000M\b\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0003\u0000S\b\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0003\u0000X\b\u0000\u0003\u0000Z\b\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0003\u0004p\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0003\u0005}\b\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b"+
		"\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001"+
		"\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0012\u0001"+
		"\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001"+
		"\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001"+
		"\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0000\u0000\u001d"+
		"\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a"+
		"\u001c\u001e \"$&(*,.02468\u0000\u0001\u0001\u0000\u0003\u0007\u00b9\u0000"+
		"Y\u0001\u0000\u0000\u0000\u0002[\u0001\u0000\u0000\u0000\u0004]\u0001"+
		"\u0000\u0000\u0000\u0006_\u0001\u0000\u0000\u0000\bo\u0001\u0000\u0000"+
		"\u0000\nq\u0001\u0000\u0000\u0000\f~\u0001\u0000\u0000\u0000\u000e\u0084"+
		"\u0001\u0000\u0000\u0000\u0010\u008a\u0001\u0000\u0000\u0000\u0012\u0090"+
		"\u0001\u0000\u0000\u0000\u0014\u0096\u0001\u0000\u0000\u0000\u0016\u009c"+
		"\u0001\u0000\u0000\u0000\u0018\u009e\u0001\u0000\u0000\u0000\u001a\u00a0"+
		"\u0001\u0000\u0000\u0000\u001c\u00a2\u0001\u0000\u0000\u0000\u001e\u00a4"+
		"\u0001\u0000\u0000\u0000 \u00a6\u0001\u0000\u0000\u0000\"\u00a8\u0001"+
		"\u0000\u0000\u0000$\u00aa\u0001\u0000\u0000\u0000&\u00ac\u0001\u0000\u0000"+
		"\u0000(\u00ae\u0001\u0000\u0000\u0000*\u00b4\u0001\u0000\u0000\u0000,"+
		"\u00b6\u0001\u0000\u0000\u0000.\u00b8\u0001\u0000\u0000\u00000\u00ba\u0001"+
		"\u0000\u0000\u00002\u00bd\u0001\u0000\u0000\u00004\u00c1\u0001\u0000\u0000"+
		"\u00006\u00c3\u0001\u0000\u0000\u00008\u00c5\u0001\u0000\u0000\u0000:"+
		";\u0003\u0002\u0001\u0000;<\u00036\u001b\u0000<=\u0003\n\u0005\u0000="+
		"Z\u0001\u0000\u0000\u0000>?\u0003\u0002\u0001\u0000?@\u00036\u001b\u0000"+
		"@A\u0005\u0001\u0000\u0000AZ\u0001\u0000\u0000\u0000BC\u0003\u0002\u0001"+
		"\u0000CD\u0003\b\u0004\u0000DE\u0005\u0002\u0000\u0000EG\u0003\u0006\u0003"+
		"\u0000FH\u00030\u0018\u0000GF\u0001\u0000\u0000\u0000GH\u0001\u0000\u0000"+
		"\u0000HZ\u0001\u0000\u0000\u0000IJ\u0003\u0002\u0001\u0000JL\u0003\u0006"+
		"\u0003\u0000KM\u00030\u0018\u0000LK\u0001\u0000\u0000\u0000LM\u0001\u0000"+
		"\u0000\u0000MZ\u0001\u0000\u0000\u0000NO\u0003\u0002\u0001\u0000OP\u0003"+
		"\u0004\u0002\u0000PR\u0003\u0014\n\u0000QS\u0003\u0010\b\u0000RQ\u0001"+
		"\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000"+
		"TU\u0005\u0002\u0000\u0000UW\u0003\u0006\u0003\u0000VX\u00030\u0018\u0000"+
		"WV\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000XZ\u0001\u0000\u0000"+
		"\u0000Y:\u0001\u0000\u0000\u0000Y>\u0001\u0000\u0000\u0000YB\u0001\u0000"+
		"\u0000\u0000YI\u0001\u0000\u0000\u0000YN\u0001\u0000\u0000\u0000Z\u0001"+
		"\u0001\u0000\u0000\u0000[\\\u0007\u0000\u0000\u0000\\\u0003\u0001\u0000"+
		"\u0000\u0000]^\u0005\b\u0000\u0000^\u0005\u0001\u0000\u0000\u0000_`\u0005"+
		"\u0017\u0000\u0000`\u0007\u0001\u0000\u0000\u0000ab\u0005\t\u0000\u0000"+
		"bp\u0003\n\u0005\u0000cd\u0005\n\u0000\u0000dp\u0003\f\u0006\u0000ef\u0005"+
		"\u000b\u0000\u0000fp\u0003\u000e\u0007\u0000gh\u0005\f\u0000\u0000hp\u0003"+
		"\u0010\b\u0000ij\u0005\r\u0000\u0000jp\u0003\u0012\t\u0000kl\u0005\u000e"+
		"\u0000\u0000lp\u00038\u001c\u0000mn\u0005\u000f\u0000\u0000np\u0003(\u0014"+
		"\u0000oa\u0001\u0000\u0000\u0000oc\u0001\u0000\u0000\u0000oe\u0001\u0000"+
		"\u0000\u0000og\u0001\u0000\u0000\u0000oi\u0001\u0000\u0000\u0000ok\u0001"+
		"\u0000\u0000\u0000om\u0001\u0000\u0000\u0000p\t\u0001\u0000\u0000\u0000"+
		"qr\u0003\u0016\u000b\u0000rs\u0005\u0010\u0000\u0000st\u0003\u0018\f\u0000"+
		"tu\u0005\u0010\u0000\u0000u|\u0003\u001a\r\u0000vw\u0003\u001c\u000e\u0000"+
		"wx\u0005\u0010\u0000\u0000xy\u0003\u001e\u000f\u0000yz\u0005\u0010\u0000"+
		"\u0000z{\u0003 \u0010\u0000{}\u0001\u0000\u0000\u0000|v\u0001\u0000\u0000"+
		"\u0000|}\u0001\u0000\u0000\u0000}\u000b\u0001\u0000\u0000\u0000~\u007f"+
		"\u0003\u0016\u000b\u0000\u007f\u0080\u0005\u0010\u0000\u0000\u0080\u0081"+
		"\u0003\u0018\f\u0000\u0081\u0082\u0005\u0010\u0000\u0000\u0082\u0083\u0003"+
		"\u001a\r\u0000\u0083\r\u0001\u0000\u0000\u0000\u0084\u0085\u0003\u0016"+
		"\u000b\u0000\u0085\u0086\u0005\u0010\u0000\u0000\u0086\u0087\u0003\u0018"+
		"\f\u0000\u0087\u0088\u0005\u0010\u0000\u0000\u0088\u0089\u0003\u001a\r"+
		"\u0000\u0089\u000f\u0001\u0000\u0000\u0000\u008a\u008b\u0003\u0016\u000b"+
		"\u0000\u008b\u008c\u0005\u0010\u0000\u0000\u008c\u008d\u0003\u0018\f\u0000"+
		"\u008d\u008e\u0005\u0010\u0000\u0000\u008e\u008f\u0003\u001a\r\u0000\u008f"+
		"\u0011\u0001\u0000\u0000\u0000\u0090\u0091\u0003\u0016\u000b\u0000\u0091"+
		"\u0092\u0005\u0010\u0000\u0000\u0092\u0093\u0003\u0018\f\u0000\u0093\u0094"+
		"\u0005\u0010\u0000\u0000\u0094\u0095\u0003\u001a\r\u0000\u0095\u0013\u0001"+
		"\u0000\u0000\u0000\u0096\u0097\u0003\"\u0011\u0000\u0097\u0098\u0005\u0010"+
		"\u0000\u0000\u0098\u0099\u0003$\u0012\u0000\u0099\u009a\u0005\u0010\u0000"+
		"\u0000\u009a\u009b\u0003&\u0013\u0000\u009b\u0015\u0001\u0000\u0000\u0000"+
		"\u009c\u009d\u0005\u0015\u0000\u0000\u009d\u0017\u0001\u0000\u0000\u0000"+
		"\u009e\u009f\u0005\u0015\u0000\u0000\u009f\u0019\u0001\u0000\u0000\u0000"+
		"\u00a0\u00a1\u0005\u0015\u0000\u0000\u00a1\u001b\u0001\u0000\u0000\u0000"+
		"\u00a2\u00a3\u0005\u0015\u0000\u0000\u00a3\u001d\u0001\u0000\u0000\u0000"+
		"\u00a4\u00a5\u0005\u0015\u0000\u0000\u00a5\u001f\u0001\u0000\u0000\u0000"+
		"\u00a6\u00a7\u0005\u0015\u0000\u0000\u00a7!\u0001\u0000\u0000\u0000\u00a8"+
		"\u00a9\u0005\u0015\u0000\u0000\u00a9#\u0001\u0000\u0000\u0000\u00aa\u00ab"+
		"\u0005\u0015\u0000\u0000\u00ab%\u0001\u0000\u0000\u0000\u00ac\u00ad\u0005"+
		"\u0015\u0000\u0000\u00ad\'\u0001\u0000\u0000\u0000\u00ae\u00af\u0003*"+
		"\u0015\u0000\u00af\u00b0\u0005\u0011\u0000\u0000\u00b0\u00b1\u0003,\u0016"+
		"\u0000\u00b1\u00b2\u0005\u0011\u0000\u0000\u00b2\u00b3\u0003.\u0017\u0000"+
		"\u00b3)\u0001\u0000\u0000\u0000\u00b4\u00b5\u0005\u0015\u0000\u0000\u00b5"+
		"+\u0001\u0000\u0000\u0000\u00b6\u00b7\u0005\u0015\u0000\u0000\u00b7-\u0001"+
		"\u0000\u0000\u0000\u00b8\u00b9\u0005\u0015\u0000\u0000\u00b9/\u0001\u0000"+
		"\u0000\u0000\u00ba\u00bb\u0005\u0012\u0000\u0000\u00bb\u00bc\u00032\u0019"+
		"\u0000\u00bc1\u0001\u0000\u0000\u0000\u00bd\u00be\u0005\u0013\u0000\u0000"+
		"\u00be\u00bf\u0005\u0014\u0000\u0000\u00bf\u00c0\u00034\u001a\u0000\u00c0"+
		"3\u0001\u0000\u0000\u0000\u00c1\u00c2\u0005\u0015\u0000\u0000\u00c25\u0001"+
		"\u0000\u0000\u0000\u00c3\u00c4\u0005\u0017\u0000\u0000\u00c47\u0001\u0000"+
		"\u0000\u0000\u00c5\u00c6\u0005\u0015\u0000\u0000\u00c69\u0001\u0000\u0000"+
		"\u0000\u0007GLRWYo|";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}