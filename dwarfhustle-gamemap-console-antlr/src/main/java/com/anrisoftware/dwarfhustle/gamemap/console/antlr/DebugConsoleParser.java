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

import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
// Generated from java-escape by ANTLR 4.11.1
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class DebugConsoleParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
	T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, T__8 = 9, T__9 = 10, T__10 = 11,
			T__11 = 12, T__12 = 13, T__13 = 14, T__14 = 15, T__15 = 16, T__16 = 17, T__17 = 18, T__18 = 19, T__19 = 20,
			T__20 = 21, T__21 = 22, T__22 = 23, T__23 = 24, TILES = 25, MARK = 26, CAMERA = 27, HERE = 28, INT = 29,
			FLOAT = 30, STRING = 31, ID = 32,
		ID_CHARS=33, WS=34;
	public static final int
	RULE_sentence = 0, RULE_physics = 1, RULE_add = 2, RULE_set = 3, RULE_save = 4, RULE_apply = 5, RULE_impulse = 6,
			RULE_object = 7, RULE_property = 8, RULE_coordinates = 9, RULE_rotation = 10, RULE_scale = 11,
			RULE_position = 12, RULE_panningVelocity = 13, RULE_vector = 14, RULE_x = 15, RULE_y = 16, RULE_z = 17,
			RULE_xx = 18, RULE_yy = 19, RULE_zz = 20, RULE_vx = 21, RULE_vy = 22,
		RULE_vz = 23, RULE_selector = 24, RULE_parameter = 25, RULE_id = 26, RULE_objectType = 27;
	private static String[] makeRuleNames() {
		return new String[] {
				"sentence", "physics", "add", "set", "save", "apply", "impulse", "object", "property", "coordinates",
				"rotation", "scale", "position", "panningVelocity", "vector", "x", "y", "z", "xx", "yy", "zz", "vx",
				"vy", "vz", "selector",
			"parameter", "id", "objectType"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
				null, "'to'", "'add'", "'set'", "'save'", "'apply'", "'impulse'", "'coordinates'", "'rotation'",
				"'scale'", "'position'", "'panningVelocity'", "'x'", "'='", "','", "'y'", "'z'", "'xx'", "'yy'", "'zz'",
				"'vx'", "'vy'", "'vz'",
			"'with'", "'id'", "'tiles'", "'mark'", "'camera'", "'here'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, "TILES", "MARK", "CAMERA", "HERE", "INT", "FLOAT",
				"STRING", "ID",
			"ID_CHARS", "WS"
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
		public AddContext add() {
			return getRuleContext(AddContext.class,0);
		}
		public ObjectTypeContext objectType() {
			return getRuleContext(ObjectTypeContext.class,0);
		}
		public CoordinatesContext coordinates() {
			return getRuleContext(CoordinatesContext.class,0);
		}
		public TerminalNode HERE() { return getToken(DebugConsoleParser.HERE, 0); }
		public SetContext set() {
			return getRuleContext(SetContext.class,0);
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
		public SaveContext save() {
			return getRuleContext(SaveContext.class,0);
		}
		public ApplyContext apply() {
			return getRuleContext(ApplyContext.class,0);
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
			setState(87);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(56);
				add();
				setState(57);
				objectType();
				setState(58);
				coordinates();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(60);
				add();
				setState(61);
				objectType();
				setState(62);
				match(HERE);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(64);
				set();
				setState(65);
				property();
				setState(66);
				match(T__0);
				setState(67);
				object();
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__22) {
					{
					setState(68);
					selector();
					}
				}

				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(71);
				save();
				setState(72);
				object();
				setState(74);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__22) {
					{
					setState(73);
					selector();
					}
				}

				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(76);
				apply();
				setState(77);
				physics();
				setState(78);
				vector();
				setState(80);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11 || _la==T__15) {
					{
					setState(79);
					position();
					}
				}

				setState(82);
				match(T__0);
				setState(83);
				object();
				setState(85);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__22) {
					{
					setState(84);
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
	public static class PhysicsContext extends ParserRuleContext {
		public ImpulseContext impulse() {
			return getRuleContext(ImpulseContext.class,0);
		}
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
		enterRule(_localctx, 2, RULE_physics);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			impulse();
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
	public static class AddContext extends ParserRuleContext {
		public AddContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_add; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterAdd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitAdd(this);
		}
	}

	public final AddContext add() throws RecognitionException {
		AddContext _localctx = new AddContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_add);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			match(T__1);
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
	public static class SetContext extends ParserRuleContext {
		public SetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_set; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterSet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitSet(this);
		}
	}

	public final SetContext set() throws RecognitionException {
		SetContext _localctx = new SetContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_set);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			match(T__2);
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
	public static class SaveContext extends ParserRuleContext {
		public SaveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_save; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterSave(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitSave(this);
		}
	}

	public final SaveContext save() throws RecognitionException {
		SaveContext _localctx = new SaveContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_save);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(T__3);
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
	public static class ApplyContext extends ParserRuleContext {
		public ApplyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_apply; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterApply(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitApply(this);
		}
	}

	public final ApplyContext apply() throws RecognitionException {
		ApplyContext _localctx = new ApplyContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_apply);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			match(T__4);
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
	public static class ImpulseContext extends ParserRuleContext {
		public ImpulseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_impulse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).enterImpulse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DebugConsoleListener ) ((DebugConsoleListener)listener).exitImpulse(this);
		}
	}

	public final ImpulseContext impulse() throws RecognitionException {
		ImpulseContext _localctx = new ImpulseContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_impulse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(T__5);
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
		public TerminalNode TILES() { return getToken(DebugConsoleParser.TILES, 0); }
		public TerminalNode MARK() { return getToken(DebugConsoleParser.MARK, 0); }
		public TerminalNode ID() { return getToken(DebugConsoleParser.ID, 0); }
		public TerminalNode CAMERA() { return getToken(DebugConsoleParser.CAMERA, 0); }
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
		enterRule(_localctx, 14, RULE_object);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			_la = _input.LA(1);
			if ( !(((_la) & ~0x3f) == 0 && ((1L << _la) & 4529848320L) != 0) ) {
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
		enterRule(_localctx, 16, RULE_property);
		try {
			setState(113);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__6:
				enterOuterAlt(_localctx, 1);
				{
				setState(103);
				match(T__6);
				setState(104);
				coordinates();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				setState(105);
				match(T__7);
				setState(106);
				rotation();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 3);
				{
				setState(107);
				match(T__8);
				setState(108);
				scale();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 4);
				{
				setState(109);
				match(T__9);
				setState(110);
				position();
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 5);
				{
				setState(111);
				match(T__10);
				setState(112);
				panningVelocity();
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
		enterRule(_localctx, 18, RULE_coordinates);
		int _la;
		try {
			setState(189);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
				enterOuterAlt(_localctx, 1);
				{
				setState(115);
				match(T__11);
				setState(116);
				match(T__12);
				setState(117);
				x();
				setState(118);
				match(T__13);
				setState(119);
				match(T__14);
				setState(120);
				match(T__12);
				setState(121);
				y();
				setState(122);
				match(T__13);
				setState(123);
				match(T__15);
				setState(124);
				match(T__12);
				setState(125);
				z();
				setState(138);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__16) {
					{
					setState(126);
					match(T__16);
					setState(127);
					match(T__12);
					setState(128);
					xx();
					setState(129);
					match(T__13);
					setState(130);
					match(T__17);
					setState(131);
					match(T__12);
					setState(132);
					yy();
					setState(133);
					match(T__13);
					setState(134);
					match(T__18);
					setState(135);
					match(T__12);
					setState(136);
					zz();
					}
				}

				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 2);
				{
				setState(140);
				match(T__15);
				setState(141);
				match(T__12);
				setState(142);
				z();
				setState(143);
				match(T__13);
				setState(144);
				match(T__14);
				setState(145);
				match(T__12);
				setState(146);
				y();
				setState(147);
				match(T__13);
				setState(148);
				match(T__11);
				setState(149);
				match(T__12);
				setState(150);
				x();
				setState(163);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__18) {
					{
					setState(151);
					match(T__18);
					setState(152);
					match(T__12);
					setState(153);
					zz();
					setState(154);
					match(T__13);
					setState(155);
					match(T__17);
					setState(156);
					match(T__12);
					setState(157);
					yy();
					setState(158);
					match(T__13);
					setState(159);
					match(T__16);
					setState(160);
					match(T__12);
					setState(161);
					xx();
					}
				}

				}
				break;
			case T__16:
				enterOuterAlt(_localctx, 3);
				{
				setState(165);
				match(T__16);
				setState(166);
				match(T__12);
				setState(167);
				xx();
				setState(168);
				match(T__13);
				setState(169);
				match(T__17);
				setState(170);
				match(T__12);
				setState(171);
				yy();
				setState(172);
				match(T__13);
				setState(173);
				match(T__18);
				setState(174);
				match(T__12);
				setState(175);
				zz();
				}
				break;
			case T__18:
				enterOuterAlt(_localctx, 4);
				{
				setState(177);
				match(T__18);
				setState(178);
				match(T__12);
				setState(179);
				zz();
				setState(180);
				match(T__13);
				setState(181);
				match(T__17);
				setState(182);
				match(T__12);
				setState(183);
				yy();
				setState(184);
				match(T__13);
				setState(185);
				match(T__16);
				setState(186);
				match(T__12);
				setState(187);
				xx();
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
	public static class RotationContext extends ParserRuleContext {
		public XxContext xx() {
			return getRuleContext(XxContext.class,0);
		}
		public YyContext yy() {
			return getRuleContext(YyContext.class,0);
		}
		public ZzContext zz() {
			return getRuleContext(ZzContext.class,0);
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
		enterRule(_localctx, 20, RULE_rotation);
		try {
			setState(215);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
				enterOuterAlt(_localctx, 1);
				{
				setState(191);
				match(T__11);
				setState(192);
				match(T__12);
				setState(193);
				xx();
				setState(194);
				match(T__13);
				setState(195);
				match(T__14);
				setState(196);
				match(T__12);
				setState(197);
				yy();
				setState(198);
				match(T__13);
				setState(199);
				match(T__15);
				setState(200);
				match(T__12);
				setState(201);
				zz();
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 2);
				{
				setState(203);
				match(T__15);
				setState(204);
				match(T__12);
				setState(205);
				zz();
				setState(206);
				match(T__13);
				setState(207);
				match(T__14);
				setState(208);
				match(T__12);
				setState(209);
				yy();
				setState(210);
				match(T__13);
				setState(211);
				match(T__11);
				setState(212);
				match(T__12);
				setState(213);
				xx();
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
	public static class ScaleContext extends ParserRuleContext {
		public XxContext xx() {
			return getRuleContext(XxContext.class,0);
		}
		public YyContext yy() {
			return getRuleContext(YyContext.class,0);
		}
		public ZzContext zz() {
			return getRuleContext(ZzContext.class,0);
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
		enterRule(_localctx, 22, RULE_scale);
		try {
			setState(241);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
				enterOuterAlt(_localctx, 1);
				{
				setState(217);
				match(T__11);
				setState(218);
				match(T__12);
				setState(219);
				xx();
				setState(220);
				match(T__13);
				setState(221);
				match(T__14);
				setState(222);
				match(T__12);
				setState(223);
				yy();
				setState(224);
				match(T__13);
				setState(225);
				match(T__15);
				setState(226);
				match(T__12);
				setState(227);
				zz();
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 2);
				{
				setState(229);
				match(T__15);
				setState(230);
				match(T__12);
				setState(231);
				zz();
				setState(232);
				match(T__13);
				setState(233);
				match(T__14);
				setState(234);
				match(T__12);
				setState(235);
				yy();
				setState(236);
				match(T__13);
				setState(237);
				match(T__11);
				setState(238);
				match(T__12);
				setState(239);
				xx();
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
	public static class PositionContext extends ParserRuleContext {
		public XxContext xx() {
			return getRuleContext(XxContext.class,0);
		}
		public YyContext yy() {
			return getRuleContext(YyContext.class,0);
		}
		public ZzContext zz() {
			return getRuleContext(ZzContext.class,0);
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
		enterRule(_localctx, 24, RULE_position);
		try {
			setState(267);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
				enterOuterAlt(_localctx, 1);
				{
				setState(243);
				match(T__11);
				setState(244);
				match(T__12);
				setState(245);
				xx();
				setState(246);
				match(T__13);
				setState(247);
				match(T__14);
				setState(248);
				match(T__12);
				setState(249);
				yy();
				setState(250);
				match(T__13);
				setState(251);
				match(T__15);
				setState(252);
				match(T__12);
				setState(253);
				zz();
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 2);
				{
				setState(255);
				match(T__15);
				setState(256);
				match(T__12);
				setState(257);
				zz();
				setState(258);
				match(T__13);
				setState(259);
				match(T__14);
				setState(260);
				match(T__12);
				setState(261);
				yy();
				setState(262);
				match(T__13);
				setState(263);
				match(T__11);
				setState(264);
				match(T__12);
				setState(265);
				xx();
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
	public static class PanningVelocityContext extends ParserRuleContext {
		public XxContext xx() {
			return getRuleContext(XxContext.class,0);
		}
		public YyContext yy() {
			return getRuleContext(YyContext.class,0);
		}
		public ZzContext zz() {
			return getRuleContext(ZzContext.class,0);
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
		enterRule(_localctx, 26, RULE_panningVelocity);
		try {
			setState(293);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
				enterOuterAlt(_localctx, 1);
				{
				setState(269);
				match(T__11);
				setState(270);
				match(T__12);
				setState(271);
				xx();
				setState(272);
				match(T__13);
				setState(273);
				match(T__14);
				setState(274);
				match(T__12);
				setState(275);
				yy();
				setState(276);
				match(T__13);
				setState(277);
				match(T__15);
				setState(278);
				match(T__12);
				setState(279);
				zz();
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 2);
				{
				setState(281);
				match(T__15);
				setState(282);
				match(T__12);
				setState(283);
				zz();
				setState(284);
				match(T__13);
				setState(285);
				match(T__14);
				setState(286);
				match(T__12);
				setState(287);
				yy();
				setState(288);
				match(T__13);
				setState(289);
				match(T__11);
				setState(290);
				match(T__12);
				setState(291);
				xx();
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
		enterRule(_localctx, 28, RULE_vector);
		try {
			setState(319);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__19:
				enterOuterAlt(_localctx, 1);
				{
				setState(295);
				match(T__19);
				setState(296);
				match(T__12);
				setState(297);
				vx();
				setState(298);
				match(T__13);
				setState(299);
				match(T__20);
				setState(300);
				match(T__12);
				setState(301);
				vy();
				setState(302);
				match(T__13);
				setState(303);
				match(T__21);
				setState(304);
				match(T__12);
				setState(305);
				vz();
				}
				break;
			case T__21:
				enterOuterAlt(_localctx, 2);
				{
				setState(307);
				match(T__21);
				setState(308);
				match(T__12);
				setState(309);
				vz();
				setState(310);
				match(T__13);
				setState(311);
				match(T__20);
				setState(312);
				match(T__12);
				setState(313);
				vy();
				setState(314);
				match(T__13);
				setState(315);
				match(T__19);
				setState(316);
				match(T__12);
				setState(317);
				vx();
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
	public static class XContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(DebugConsoleParser.INT, 0); }
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
		enterRule(_localctx, 30, RULE_x);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(321);
			match(INT);
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
		public TerminalNode INT() { return getToken(DebugConsoleParser.INT, 0); }
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
		enterRule(_localctx, 32, RULE_y);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(323);
			match(INT);
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
		public TerminalNode INT() { return getToken(DebugConsoleParser.INT, 0); }
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
		enterRule(_localctx, 34, RULE_z);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(325);
			match(INT);
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
		public TerminalNode FLOAT() { return getToken(DebugConsoleParser.FLOAT, 0); }
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
		enterRule(_localctx, 36, RULE_xx);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(327);
			match(FLOAT);
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
		public TerminalNode FLOAT() { return getToken(DebugConsoleParser.FLOAT, 0); }
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
		enterRule(_localctx, 38, RULE_yy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(329);
			match(FLOAT);
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
		public TerminalNode FLOAT() { return getToken(DebugConsoleParser.FLOAT, 0); }
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
		enterRule(_localctx, 40, RULE_zz);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331);
			match(FLOAT);
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
		public TerminalNode FLOAT() { return getToken(DebugConsoleParser.FLOAT, 0); }
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
		enterRule(_localctx, 42, RULE_vx);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(333);
			match(FLOAT);
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
		public TerminalNode FLOAT() { return getToken(DebugConsoleParser.FLOAT, 0); }
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
		enterRule(_localctx, 44, RULE_vy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			match(FLOAT);
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
		public TerminalNode FLOAT() { return getToken(DebugConsoleParser.FLOAT, 0); }
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
		enterRule(_localctx, 46, RULE_vz);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
			match(FLOAT);
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
			setState(339);
			match(T__22);
			setState(340);
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
			setState(342);
			match(T__23);
			setState(343);
			match(T__12);
			setState(344);
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
		public TerminalNode INT() { return getToken(DebugConsoleParser.INT, 0); }
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
			setState(346);
			match(INT);
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
			setState(348);
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

	public static final String _serializedATN =
		"\u0004\u0001\"\u015f\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0003\u0000F\b\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0003\u0000K\b\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0003\u0000Q\b\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000"+
		"V\b\u0000\u0003\u0000X\b\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003"+
		"\br\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t\u008b\b\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t\u00a4\b\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0003\t\u00be\b\t\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0003\n\u00d8\b\n\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u00f2\b\u000b\u0001\f"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u010c\b\f\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u0126\b\r\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u0140"+
		"\b\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001"+
		"\u0011\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001"+
		"\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001"+
		"\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0000\u0000\u001c\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
		"\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.0246\u0000\u0001\u0002\u0000"+
		"\u0019\u001b  \u0158\u0000W\u0001\u0000\u0000\u0000\u0002Y\u0001\u0000"+
		"\u0000\u0000\u0004[\u0001\u0000\u0000\u0000\u0006]\u0001\u0000\u0000\u0000"+
		"\b_\u0001\u0000\u0000\u0000\na\u0001\u0000\u0000\u0000\fc\u0001\u0000"+
		"\u0000\u0000\u000ee\u0001\u0000\u0000\u0000\u0010q\u0001\u0000\u0000\u0000"+
		"\u0012\u00bd\u0001\u0000\u0000\u0000\u0014\u00d7\u0001\u0000\u0000\u0000"+
		"\u0016\u00f1\u0001\u0000\u0000\u0000\u0018\u010b\u0001\u0000\u0000\u0000"+
		"\u001a\u0125\u0001\u0000\u0000\u0000\u001c\u013f\u0001\u0000\u0000\u0000"+
		"\u001e\u0141\u0001\u0000\u0000\u0000 \u0143\u0001\u0000\u0000\u0000\""+
		"\u0145\u0001\u0000\u0000\u0000$\u0147\u0001\u0000\u0000\u0000&\u0149\u0001"+
		"\u0000\u0000\u0000(\u014b\u0001\u0000\u0000\u0000*\u014d\u0001\u0000\u0000"+
		"\u0000,\u014f\u0001\u0000\u0000\u0000.\u0151\u0001\u0000\u0000\u00000"+
		"\u0153\u0001\u0000\u0000\u00002\u0156\u0001\u0000\u0000\u00004\u015a\u0001"+
		"\u0000\u0000\u00006\u015c\u0001\u0000\u0000\u000089\u0003\u0004\u0002"+
		"\u00009:\u00036\u001b\u0000:;\u0003\u0012\t\u0000;X\u0001\u0000\u0000"+
		"\u0000<=\u0003\u0004\u0002\u0000=>\u00036\u001b\u0000>?\u0005\u001c\u0000"+
		"\u0000?X\u0001\u0000\u0000\u0000@A\u0003\u0006\u0003\u0000AB\u0003\u0010"+
		"\b\u0000BC\u0005\u0001\u0000\u0000CE\u0003\u000e\u0007\u0000DF\u00030"+
		"\u0018\u0000ED\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000\u0000FX\u0001"+
		"\u0000\u0000\u0000GH\u0003\b\u0004\u0000HJ\u0003\u000e\u0007\u0000IK\u0003"+
		"0\u0018\u0000JI\u0001\u0000\u0000\u0000JK\u0001\u0000\u0000\u0000KX\u0001"+
		"\u0000\u0000\u0000LM\u0003\n\u0005\u0000MN\u0003\u0002\u0001\u0000NP\u0003"+
		"\u001c\u000e\u0000OQ\u0003\u0018\f\u0000PO\u0001\u0000\u0000\u0000PQ\u0001"+
		"\u0000\u0000\u0000QR\u0001\u0000\u0000\u0000RS\u0005\u0001\u0000\u0000"+
		"SU\u0003\u000e\u0007\u0000TV\u00030\u0018\u0000UT\u0001\u0000\u0000\u0000"+
		"UV\u0001\u0000\u0000\u0000VX\u0001\u0000\u0000\u0000W8\u0001\u0000\u0000"+
		"\u0000W<\u0001\u0000\u0000\u0000W@\u0001\u0000\u0000\u0000WG\u0001\u0000"+
		"\u0000\u0000WL\u0001\u0000\u0000\u0000X\u0001\u0001\u0000\u0000\u0000"+
		"YZ\u0003\f\u0006\u0000Z\u0003\u0001\u0000\u0000\u0000[\\\u0005\u0002\u0000"+
		"\u0000\\\u0005\u0001\u0000\u0000\u0000]^\u0005\u0003\u0000\u0000^\u0007"+
		"\u0001\u0000\u0000\u0000_`\u0005\u0004\u0000\u0000`\t\u0001\u0000\u0000"+
		"\u0000ab\u0005\u0005\u0000\u0000b\u000b\u0001\u0000\u0000\u0000cd\u0005"+
		"\u0006\u0000\u0000d\r\u0001\u0000\u0000\u0000ef\u0007\u0000\u0000\u0000"+
		"f\u000f\u0001\u0000\u0000\u0000gh\u0005\u0007\u0000\u0000hr\u0003\u0012"+
		"\t\u0000ij\u0005\b\u0000\u0000jr\u0003\u0014\n\u0000kl\u0005\t\u0000\u0000"+
		"lr\u0003\u0016\u000b\u0000mn\u0005\n\u0000\u0000nr\u0003\u0018\f\u0000"+
		"op\u0005\u000b\u0000\u0000pr\u0003\u001a\r\u0000qg\u0001\u0000\u0000\u0000"+
		"qi\u0001\u0000\u0000\u0000qk\u0001\u0000\u0000\u0000qm\u0001\u0000\u0000"+
		"\u0000qo\u0001\u0000\u0000\u0000r\u0011\u0001\u0000\u0000\u0000st\u0005"+
		"\f\u0000\u0000tu\u0005\r\u0000\u0000uv\u0003\u001e\u000f\u0000vw\u0005"+
		"\u000e\u0000\u0000wx\u0005\u000f\u0000\u0000xy\u0005\r\u0000\u0000yz\u0003"+
		" \u0010\u0000z{\u0005\u000e\u0000\u0000{|\u0005\u0010\u0000\u0000|}\u0005"+
		"\r\u0000\u0000}\u008a\u0003\"\u0011\u0000~\u007f\u0005\u0011\u0000\u0000"+
		"\u007f\u0080\u0005\r\u0000\u0000\u0080\u0081\u0003$\u0012\u0000\u0081"+
		"\u0082\u0005\u000e\u0000\u0000\u0082\u0083\u0005\u0012\u0000\u0000\u0083"+
		"\u0084\u0005\r\u0000\u0000\u0084\u0085\u0003&\u0013\u0000\u0085\u0086"+
		"\u0005\u000e\u0000\u0000\u0086\u0087\u0005\u0013\u0000\u0000\u0087\u0088"+
		"\u0005\r\u0000\u0000\u0088\u0089\u0003(\u0014\u0000\u0089\u008b\u0001"+
		"\u0000\u0000\u0000\u008a~\u0001\u0000\u0000\u0000\u008a\u008b\u0001\u0000"+
		"\u0000\u0000\u008b\u00be\u0001\u0000\u0000\u0000\u008c\u008d\u0005\u0010"+
		"\u0000\u0000\u008d\u008e\u0005\r\u0000\u0000\u008e\u008f\u0003\"\u0011"+
		"\u0000\u008f\u0090\u0005\u000e\u0000\u0000\u0090\u0091\u0005\u000f\u0000"+
		"\u0000\u0091\u0092\u0005\r\u0000\u0000\u0092\u0093\u0003 \u0010\u0000"+
		"\u0093\u0094\u0005\u000e\u0000\u0000\u0094\u0095\u0005\f\u0000\u0000\u0095"+
		"\u0096\u0005\r\u0000\u0000\u0096\u00a3\u0003\u001e\u000f\u0000\u0097\u0098"+
		"\u0005\u0013\u0000\u0000\u0098\u0099\u0005\r\u0000\u0000\u0099\u009a\u0003"+
		"(\u0014\u0000\u009a\u009b\u0005\u000e\u0000\u0000\u009b\u009c\u0005\u0012"+
		"\u0000\u0000\u009c\u009d\u0005\r\u0000\u0000\u009d\u009e\u0003&\u0013"+
		"\u0000\u009e\u009f\u0005\u000e\u0000\u0000\u009f\u00a0\u0005\u0011\u0000"+
		"\u0000\u00a0\u00a1\u0005\r\u0000\u0000\u00a1\u00a2\u0003$\u0012\u0000"+
		"\u00a2\u00a4\u0001\u0000\u0000\u0000\u00a3\u0097\u0001\u0000\u0000\u0000"+
		"\u00a3\u00a4\u0001\u0000\u0000\u0000\u00a4\u00be\u0001\u0000\u0000\u0000"+
		"\u00a5\u00a6\u0005\u0011\u0000\u0000\u00a6\u00a7\u0005\r\u0000\u0000\u00a7"+
		"\u00a8\u0003$\u0012\u0000\u00a8\u00a9\u0005\u000e\u0000\u0000\u00a9\u00aa"+
		"\u0005\u0012\u0000\u0000\u00aa\u00ab\u0005\r\u0000\u0000\u00ab\u00ac\u0003"+
		"&\u0013\u0000\u00ac\u00ad\u0005\u000e\u0000\u0000\u00ad\u00ae\u0005\u0013"+
		"\u0000\u0000\u00ae\u00af\u0005\r\u0000\u0000\u00af\u00b0\u0003(\u0014"+
		"\u0000\u00b0\u00be\u0001\u0000\u0000\u0000\u00b1\u00b2\u0005\u0013\u0000"+
		"\u0000\u00b2\u00b3\u0005\r\u0000\u0000\u00b3\u00b4\u0003(\u0014\u0000"+
		"\u00b4\u00b5\u0005\u000e\u0000\u0000\u00b5\u00b6\u0005\u0012\u0000\u0000"+
		"\u00b6\u00b7\u0005\r\u0000\u0000\u00b7\u00b8\u0003&\u0013\u0000\u00b8"+
		"\u00b9\u0005\u000e\u0000\u0000\u00b9\u00ba\u0005\u0011\u0000\u0000\u00ba"+
		"\u00bb\u0005\r\u0000\u0000\u00bb\u00bc\u0003$\u0012\u0000\u00bc\u00be"+
		"\u0001\u0000\u0000\u0000\u00bds\u0001\u0000\u0000\u0000\u00bd\u008c\u0001"+
		"\u0000\u0000\u0000\u00bd\u00a5\u0001\u0000\u0000\u0000\u00bd\u00b1\u0001"+
		"\u0000\u0000\u0000\u00be\u0013\u0001\u0000\u0000\u0000\u00bf\u00c0\u0005"+
		"\f\u0000\u0000\u00c0\u00c1\u0005\r\u0000\u0000\u00c1\u00c2\u0003$\u0012"+
		"\u0000\u00c2\u00c3\u0005\u000e\u0000\u0000\u00c3\u00c4\u0005\u000f\u0000"+
		"\u0000\u00c4\u00c5\u0005\r\u0000\u0000\u00c5\u00c6\u0003&\u0013\u0000"+
		"\u00c6\u00c7\u0005\u000e\u0000\u0000\u00c7\u00c8\u0005\u0010\u0000\u0000"+
		"\u00c8\u00c9\u0005\r\u0000\u0000\u00c9\u00ca\u0003(\u0014\u0000\u00ca"+
		"\u00d8\u0001\u0000\u0000\u0000\u00cb\u00cc\u0005\u0010\u0000\u0000\u00cc"+
		"\u00cd\u0005\r\u0000\u0000\u00cd\u00ce\u0003(\u0014\u0000\u00ce\u00cf"+
		"\u0005\u000e\u0000\u0000\u00cf\u00d0\u0005\u000f\u0000\u0000\u00d0\u00d1"+
		"\u0005\r\u0000\u0000\u00d1\u00d2\u0003&\u0013\u0000\u00d2\u00d3\u0005"+
		"\u000e\u0000\u0000\u00d3\u00d4\u0005\f\u0000\u0000\u00d4\u00d5\u0005\r"+
		"\u0000\u0000\u00d5\u00d6\u0003$\u0012\u0000\u00d6\u00d8\u0001\u0000\u0000"+
		"\u0000\u00d7\u00bf\u0001\u0000\u0000\u0000\u00d7\u00cb\u0001\u0000\u0000"+
		"\u0000\u00d8\u0015\u0001\u0000\u0000\u0000\u00d9\u00da\u0005\f\u0000\u0000"+
		"\u00da\u00db\u0005\r\u0000\u0000\u00db\u00dc\u0003$\u0012\u0000\u00dc"+
		"\u00dd\u0005\u000e\u0000\u0000\u00dd\u00de\u0005\u000f\u0000\u0000\u00de"+
		"\u00df\u0005\r\u0000\u0000\u00df\u00e0\u0003&\u0013\u0000\u00e0\u00e1"+
		"\u0005\u000e\u0000\u0000\u00e1\u00e2\u0005\u0010\u0000\u0000\u00e2\u00e3"+
		"\u0005\r\u0000\u0000\u00e3\u00e4\u0003(\u0014\u0000\u00e4\u00f2\u0001"+
		"\u0000\u0000\u0000\u00e5\u00e6\u0005\u0010\u0000\u0000\u00e6\u00e7\u0005"+
		"\r\u0000\u0000\u00e7\u00e8\u0003(\u0014\u0000\u00e8\u00e9\u0005\u000e"+
		"\u0000\u0000\u00e9\u00ea\u0005\u000f\u0000\u0000\u00ea\u00eb\u0005\r\u0000"+
		"\u0000\u00eb\u00ec\u0003&\u0013\u0000\u00ec\u00ed\u0005\u000e\u0000\u0000"+
		"\u00ed\u00ee\u0005\f\u0000\u0000\u00ee\u00ef\u0005\r\u0000\u0000\u00ef"+
		"\u00f0\u0003$\u0012\u0000\u00f0\u00f2\u0001\u0000\u0000\u0000\u00f1\u00d9"+
		"\u0001\u0000\u0000\u0000\u00f1\u00e5\u0001\u0000\u0000\u0000\u00f2\u0017"+
		"\u0001\u0000\u0000\u0000\u00f3\u00f4\u0005\f\u0000\u0000\u00f4\u00f5\u0005"+
		"\r\u0000\u0000\u00f5\u00f6\u0003$\u0012\u0000\u00f6\u00f7\u0005\u000e"+
		"\u0000\u0000\u00f7\u00f8\u0005\u000f\u0000\u0000\u00f8\u00f9\u0005\r\u0000"+
		"\u0000\u00f9\u00fa\u0003&\u0013\u0000\u00fa\u00fb\u0005\u000e\u0000\u0000"+
		"\u00fb\u00fc\u0005\u0010\u0000\u0000\u00fc\u00fd\u0005\r\u0000\u0000\u00fd"+
		"\u00fe\u0003(\u0014\u0000\u00fe\u010c\u0001\u0000\u0000\u0000\u00ff\u0100"+
		"\u0005\u0010\u0000\u0000\u0100\u0101\u0005\r\u0000\u0000\u0101\u0102\u0003"+
		"(\u0014\u0000\u0102\u0103\u0005\u000e\u0000\u0000\u0103\u0104\u0005\u000f"+
		"\u0000\u0000\u0104\u0105\u0005\r\u0000\u0000\u0105\u0106\u0003&\u0013"+
		"\u0000\u0106\u0107\u0005\u000e\u0000\u0000\u0107\u0108\u0005\f\u0000\u0000"+
		"\u0108\u0109\u0005\r\u0000\u0000\u0109\u010a\u0003$\u0012\u0000\u010a"+
		"\u010c\u0001\u0000\u0000\u0000\u010b\u00f3\u0001\u0000\u0000\u0000\u010b"+
		"\u00ff\u0001\u0000\u0000\u0000\u010c\u0019\u0001\u0000\u0000\u0000\u010d"+
		"\u010e\u0005\f\u0000\u0000\u010e\u010f\u0005\r\u0000\u0000\u010f\u0110"+
		"\u0003$\u0012\u0000\u0110\u0111\u0005\u000e\u0000\u0000\u0111\u0112\u0005"+
		"\u000f\u0000\u0000\u0112\u0113\u0005\r\u0000\u0000\u0113\u0114\u0003&"+
		"\u0013\u0000\u0114\u0115\u0005\u000e\u0000\u0000\u0115\u0116\u0005\u0010"+
		"\u0000\u0000\u0116\u0117\u0005\r\u0000\u0000\u0117\u0118\u0003(\u0014"+
		"\u0000\u0118\u0126\u0001\u0000\u0000\u0000\u0119\u011a\u0005\u0010\u0000"+
		"\u0000\u011a\u011b\u0005\r\u0000\u0000\u011b\u011c\u0003(\u0014\u0000"+
		"\u011c\u011d\u0005\u000e\u0000\u0000\u011d\u011e\u0005\u000f\u0000\u0000"+
		"\u011e\u011f\u0005\r\u0000\u0000\u011f\u0120\u0003&\u0013\u0000\u0120"+
		"\u0121\u0005\u000e\u0000\u0000\u0121\u0122\u0005\f\u0000\u0000\u0122\u0123"+
		"\u0005\r\u0000\u0000\u0123\u0124\u0003$\u0012\u0000\u0124\u0126\u0001"+
		"\u0000\u0000\u0000\u0125\u010d\u0001\u0000\u0000\u0000\u0125\u0119\u0001"+
		"\u0000\u0000\u0000\u0126\u001b\u0001\u0000\u0000\u0000\u0127\u0128\u0005"+
		"\u0014\u0000\u0000\u0128\u0129\u0005\r\u0000\u0000\u0129\u012a\u0003*"+
		"\u0015\u0000\u012a\u012b\u0005\u000e\u0000\u0000\u012b\u012c\u0005\u0015"+
		"\u0000\u0000\u012c\u012d\u0005\r\u0000\u0000\u012d\u012e\u0003,\u0016"+
		"\u0000\u012e\u012f\u0005\u000e\u0000\u0000\u012f\u0130\u0005\u0016\u0000"+
		"\u0000\u0130\u0131\u0005\r\u0000\u0000\u0131\u0132\u0003.\u0017\u0000"+
		"\u0132\u0140\u0001\u0000\u0000\u0000\u0133\u0134\u0005\u0016\u0000\u0000"+
		"\u0134\u0135\u0005\r\u0000\u0000\u0135\u0136\u0003.\u0017\u0000\u0136"+
		"\u0137\u0005\u000e\u0000\u0000\u0137\u0138\u0005\u0015\u0000\u0000\u0138"+
		"\u0139\u0005\r\u0000\u0000\u0139\u013a\u0003,\u0016\u0000\u013a\u013b"+
		"\u0005\u000e\u0000\u0000\u013b\u013c\u0005\u0014\u0000\u0000\u013c\u013d"+
		"\u0005\r\u0000\u0000\u013d\u013e\u0003*\u0015\u0000\u013e\u0140\u0001"+
		"\u0000\u0000\u0000\u013f\u0127\u0001\u0000\u0000\u0000\u013f\u0133\u0001"+
		"\u0000\u0000\u0000\u0140\u001d\u0001\u0000\u0000\u0000\u0141\u0142\u0005"+
		"\u001d\u0000\u0000\u0142\u001f\u0001\u0000\u0000\u0000\u0143\u0144\u0005"+
		"\u001d\u0000\u0000\u0144!\u0001\u0000\u0000\u0000\u0145\u0146\u0005\u001d"+
		"\u0000\u0000\u0146#\u0001\u0000\u0000\u0000\u0147\u0148\u0005\u001e\u0000"+
		"\u0000\u0148%\u0001\u0000\u0000\u0000\u0149\u014a\u0005\u001e\u0000\u0000"+
		"\u014a\'\u0001\u0000\u0000\u0000\u014b\u014c\u0005\u001e\u0000\u0000\u014c"+
		")\u0001\u0000\u0000\u0000\u014d\u014e\u0005\u001e\u0000\u0000\u014e+\u0001"+
		"\u0000\u0000\u0000\u014f\u0150\u0005\u001e\u0000\u0000\u0150-\u0001\u0000"+
		"\u0000\u0000\u0151\u0152\u0005\u001e\u0000\u0000\u0152/\u0001\u0000\u0000"+
		"\u0000\u0153\u0154\u0005\u0017\u0000\u0000\u0154\u0155\u00032\u0019\u0000"+
		"\u01551\u0001\u0000\u0000\u0000\u0156\u0157\u0005\u0018\u0000\u0000\u0157"+
		"\u0158\u0005\r\u0000\u0000\u0158\u0159\u00034\u001a\u0000\u01593\u0001"+
		"\u0000\u0000\u0000\u015a\u015b\u0005\u001d\u0000\u0000\u015b5\u0001\u0000"+
		"\u0000\u0000\u015c\u015d\u0005 \u0000\u0000\u015d7\u0001\u0000\u0000\u0000"+
		"\u000eEJPUWq\u008a\u00a3\u00bd\u00d7\u00f1\u010b\u0125\u013f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}