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
		NUMBER=18, STRING=19, ID=20, ID_CHARS=21, WS=22, NATURAL=23;
	public static final int
		RULE_sentence = 0, RULE_verb = 1, RULE_physics = 2, RULE_object = 3, RULE_property = 4, 
		RULE_coordinates = 5, RULE_rotation = 6, RULE_scale = 7, RULE_position = 8, 
		RULE_panningVelocity = 9, RULE_vector = 10, RULE_x = 11, RULE_y = 12, 
		RULE_z = 13, RULE_xx = 14, RULE_yy = 15, RULE_zz = 16, RULE_vx = 17, RULE_vy = 18, 
		RULE_vz = 19, RULE_selector = 20, RULE_parameter = 21, RULE_id = 22, RULE_objectType = 23, 
		RULE_layers = 24;
	private static String[] makeRuleNames() {
		return new String[] {
			"sentence", "verb", "physics", "object", "property", "coordinates", "rotation", 
			"scale", "position", "panningVelocity", "vector", "x", "y", "z", "xx", 
			"yy", "zz", "vx", "vy", "vz", "selector", "parameter", "id", "objectType", 
			"layers"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'here'", "'to'", "'add'", "'set'", "'save'", "'apply'", "'impulse'", 
			"'coordinates'", "'rotation'", "'scale'", "'position'", "'panningVelocity'", 
			"'layers'", "','", "'with'", "'id'", "'='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "NUMBER", "STRING", "ID", "ID_CHARS", 
			"WS", "NATURAL"
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
			setState(81);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(50);
				verb();
				setState(51);
				objectType();
				setState(52);
				coordinates();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(54);
				verb();
				setState(55);
				objectType();
				setState(56);
				match(T__0);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(58);
				verb();
				setState(59);
				property();
				setState(60);
				match(T__1);
				setState(61);
				object();
				setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__14) {
					{
					setState(62);
					selector();
					}
				}

				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(65);
				verb();
				setState(66);
				object();
				setState(68);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__14) {
					{
					setState(67);
					selector();
					}
				}

				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(70);
				verb();
				setState(71);
				physics();
				setState(72);
				vector();
				setState(74);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NUMBER) {
					{
					setState(73);
					position();
					}
				}

				setState(76);
				match(T__1);
				setState(77);
				object();
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__14) {
					{
					setState(78);
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
			setState(83);
			_la = _input.LA(1);
			if ( !(((_la) & ~0x3f) == 0 && ((1L << _la) & 120L) != 0) ) {
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
			setState(85);
			match(T__6);
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
			setState(87);
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
			setState(101);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				enterOuterAlt(_localctx, 1);
				{
				setState(89);
				match(T__7);
				setState(90);
				coordinates();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
				setState(91);
				match(T__8);
				setState(92);
				rotation();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 3);
				{
				setState(93);
				match(T__9);
				setState(94);
				scale();
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 4);
				{
				setState(95);
				match(T__10);
				setState(96);
				position();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 5);
				{
				setState(97);
				match(T__11);
				setState(98);
				panningVelocity();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 6);
				{
				setState(99);
				match(T__12);
				setState(100);
				layers();
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
			setState(103);
			x();
			setState(104);
			match(T__13);
			setState(105);
			y();
			setState(106);
			match(T__13);
			setState(107);
			z();
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NUMBER) {
				{
				setState(108);
				xx();
				setState(109);
				match(T__13);
				setState(110);
				yy();
				setState(111);
				match(T__13);
				setState(112);
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
			setState(116);
			x();
			setState(117);
			match(T__13);
			setState(118);
			y();
			setState(119);
			match(T__13);
			setState(120);
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
			setState(122);
			x();
			setState(123);
			match(T__13);
			setState(124);
			y();
			setState(125);
			match(T__13);
			setState(126);
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
			setState(128);
			x();
			setState(129);
			match(T__13);
			setState(130);
			y();
			setState(131);
			match(T__13);
			setState(132);
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
			setState(134);
			x();
			setState(135);
			match(T__13);
			setState(136);
			y();
			setState(137);
			match(T__13);
			setState(138);
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
			setState(140);
			vx();
			setState(141);
			match(T__13);
			setState(142);
			vy();
			setState(143);
			match(T__13);
			setState(144);
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
			setState(146);
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
			setState(148);
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
			setState(150);
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
			setState(152);
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
			setState(154);
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
		enterRule(_localctx, 40, RULE_selector);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(T__14);
			setState(165);
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
		enterRule(_localctx, 42, RULE_parameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			match(T__15);
			setState(168);
			match(T__16);
			setState(169);
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
		enterRule(_localctx, 44, RULE_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
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
		enterRule(_localctx, 46, RULE_objectType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
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
		public TerminalNode NATURAL() { return getToken(DebugConsoleParser.NATURAL, 0); }
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
		enterRule(_localctx, 48, RULE_layers);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(175);
			match(NATURAL);
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
		"\u0004\u0001\u0017\u00b2\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007"+
		"\u0018\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0003\u0000@\b\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0003\u0000E\b\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0003\u0000K\b\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003"+
		"\u0000P\b\u0000\u0003\u0000R\b\u0000\u0001\u0001\u0001\u0001\u0001\u0002"+
		"\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004f\b\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005s\b\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0011"+
		"\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0000\u0000\u0019\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.0\u0000\u0001\u0001"+
		"\u0000\u0003\u0006\u00a6\u0000Q\u0001\u0000\u0000\u0000\u0002S\u0001\u0000"+
		"\u0000\u0000\u0004U\u0001\u0000\u0000\u0000\u0006W\u0001\u0000\u0000\u0000"+
		"\be\u0001\u0000\u0000\u0000\ng\u0001\u0000\u0000\u0000\ft\u0001\u0000"+
		"\u0000\u0000\u000ez\u0001\u0000\u0000\u0000\u0010\u0080\u0001\u0000\u0000"+
		"\u0000\u0012\u0086\u0001\u0000\u0000\u0000\u0014\u008c\u0001\u0000\u0000"+
		"\u0000\u0016\u0092\u0001\u0000\u0000\u0000\u0018\u0094\u0001\u0000\u0000"+
		"\u0000\u001a\u0096\u0001\u0000\u0000\u0000\u001c\u0098\u0001\u0000\u0000"+
		"\u0000\u001e\u009a\u0001\u0000\u0000\u0000 \u009c\u0001\u0000\u0000\u0000"+
		"\"\u009e\u0001\u0000\u0000\u0000$\u00a0\u0001\u0000\u0000\u0000&\u00a2"+
		"\u0001\u0000\u0000\u0000(\u00a4\u0001\u0000\u0000\u0000*\u00a7\u0001\u0000"+
		"\u0000\u0000,\u00ab\u0001\u0000\u0000\u0000.\u00ad\u0001\u0000\u0000\u0000"+
		"0\u00af\u0001\u0000\u0000\u000023\u0003\u0002\u0001\u000034\u0003.\u0017"+
		"\u000045\u0003\n\u0005\u00005R\u0001\u0000\u0000\u000067\u0003\u0002\u0001"+
		"\u000078\u0003.\u0017\u000089\u0005\u0001\u0000\u00009R\u0001\u0000\u0000"+
		"\u0000:;\u0003\u0002\u0001\u0000;<\u0003\b\u0004\u0000<=\u0005\u0002\u0000"+
		"\u0000=?\u0003\u0006\u0003\u0000>@\u0003(\u0014\u0000?>\u0001\u0000\u0000"+
		"\u0000?@\u0001\u0000\u0000\u0000@R\u0001\u0000\u0000\u0000AB\u0003\u0002"+
		"\u0001\u0000BD\u0003\u0006\u0003\u0000CE\u0003(\u0014\u0000DC\u0001\u0000"+
		"\u0000\u0000DE\u0001\u0000\u0000\u0000ER\u0001\u0000\u0000\u0000FG\u0003"+
		"\u0002\u0001\u0000GH\u0003\u0004\u0002\u0000HJ\u0003\u0014\n\u0000IK\u0003"+
		"\u0010\b\u0000JI\u0001\u0000\u0000\u0000JK\u0001\u0000\u0000\u0000KL\u0001"+
		"\u0000\u0000\u0000LM\u0005\u0002\u0000\u0000MO\u0003\u0006\u0003\u0000"+
		"NP\u0003(\u0014\u0000ON\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000"+
		"PR\u0001\u0000\u0000\u0000Q2\u0001\u0000\u0000\u0000Q6\u0001\u0000\u0000"+
		"\u0000Q:\u0001\u0000\u0000\u0000QA\u0001\u0000\u0000\u0000QF\u0001\u0000"+
		"\u0000\u0000R\u0001\u0001\u0000\u0000\u0000ST\u0007\u0000\u0000\u0000"+
		"T\u0003\u0001\u0000\u0000\u0000UV\u0005\u0007\u0000\u0000V\u0005\u0001"+
		"\u0000\u0000\u0000WX\u0005\u0014\u0000\u0000X\u0007\u0001\u0000\u0000"+
		"\u0000YZ\u0005\b\u0000\u0000Zf\u0003\n\u0005\u0000[\\\u0005\t\u0000\u0000"+
		"\\f\u0003\f\u0006\u0000]^\u0005\n\u0000\u0000^f\u0003\u000e\u0007\u0000"+
		"_`\u0005\u000b\u0000\u0000`f\u0003\u0010\b\u0000ab\u0005\f\u0000\u0000"+
		"bf\u0003\u0012\t\u0000cd\u0005\r\u0000\u0000df\u00030\u0018\u0000eY\u0001"+
		"\u0000\u0000\u0000e[\u0001\u0000\u0000\u0000e]\u0001\u0000\u0000\u0000"+
		"e_\u0001\u0000\u0000\u0000ea\u0001\u0000\u0000\u0000ec\u0001\u0000\u0000"+
		"\u0000f\t\u0001\u0000\u0000\u0000gh\u0003\u0016\u000b\u0000hi\u0005\u000e"+
		"\u0000\u0000ij\u0003\u0018\f\u0000jk\u0005\u000e\u0000\u0000kr\u0003\u001a"+
		"\r\u0000lm\u0003\u001c\u000e\u0000mn\u0005\u000e\u0000\u0000no\u0003\u001e"+
		"\u000f\u0000op\u0005\u000e\u0000\u0000pq\u0003 \u0010\u0000qs\u0001\u0000"+
		"\u0000\u0000rl\u0001\u0000\u0000\u0000rs\u0001\u0000\u0000\u0000s\u000b"+
		"\u0001\u0000\u0000\u0000tu\u0003\u0016\u000b\u0000uv\u0005\u000e\u0000"+
		"\u0000vw\u0003\u0018\f\u0000wx\u0005\u000e\u0000\u0000xy\u0003\u001a\r"+
		"\u0000y\r\u0001\u0000\u0000\u0000z{\u0003\u0016\u000b\u0000{|\u0005\u000e"+
		"\u0000\u0000|}\u0003\u0018\f\u0000}~\u0005\u000e\u0000\u0000~\u007f\u0003"+
		"\u001a\r\u0000\u007f\u000f\u0001\u0000\u0000\u0000\u0080\u0081\u0003\u0016"+
		"\u000b\u0000\u0081\u0082\u0005\u000e\u0000\u0000\u0082\u0083\u0003\u0018"+
		"\f\u0000\u0083\u0084\u0005\u000e\u0000\u0000\u0084\u0085\u0003\u001a\r"+
		"\u0000\u0085\u0011\u0001\u0000\u0000\u0000\u0086\u0087\u0003\u0016\u000b"+
		"\u0000\u0087\u0088\u0005\u000e\u0000\u0000\u0088\u0089\u0003\u0018\f\u0000"+
		"\u0089\u008a\u0005\u000e\u0000\u0000\u008a\u008b\u0003\u001a\r\u0000\u008b"+
		"\u0013\u0001\u0000\u0000\u0000\u008c\u008d\u0003\"\u0011\u0000\u008d\u008e"+
		"\u0005\u000e\u0000\u0000\u008e\u008f\u0003$\u0012\u0000\u008f\u0090\u0005"+
		"\u000e\u0000\u0000\u0090\u0091\u0003&\u0013\u0000\u0091\u0015\u0001\u0000"+
		"\u0000\u0000\u0092\u0093\u0005\u0012\u0000\u0000\u0093\u0017\u0001\u0000"+
		"\u0000\u0000\u0094\u0095\u0005\u0012\u0000\u0000\u0095\u0019\u0001\u0000"+
		"\u0000\u0000\u0096\u0097\u0005\u0012\u0000\u0000\u0097\u001b\u0001\u0000"+
		"\u0000\u0000\u0098\u0099\u0005\u0012\u0000\u0000\u0099\u001d\u0001\u0000"+
		"\u0000\u0000\u009a\u009b\u0005\u0012\u0000\u0000\u009b\u001f\u0001\u0000"+
		"\u0000\u0000\u009c\u009d\u0005\u0012\u0000\u0000\u009d!\u0001\u0000\u0000"+
		"\u0000\u009e\u009f\u0005\u0012\u0000\u0000\u009f#\u0001\u0000\u0000\u0000"+
		"\u00a0\u00a1\u0005\u0012\u0000\u0000\u00a1%\u0001\u0000\u0000\u0000\u00a2"+
		"\u00a3\u0005\u0012\u0000\u0000\u00a3\'\u0001\u0000\u0000\u0000\u00a4\u00a5"+
		"\u0005\u000f\u0000\u0000\u00a5\u00a6\u0003*\u0015\u0000\u00a6)\u0001\u0000"+
		"\u0000\u0000\u00a7\u00a8\u0005\u0010\u0000\u0000\u00a8\u00a9\u0005\u0011"+
		"\u0000\u0000\u00a9\u00aa\u0003,\u0016\u0000\u00aa+\u0001\u0000\u0000\u0000"+
		"\u00ab\u00ac\u0005\u0012\u0000\u0000\u00ac-\u0001\u0000\u0000\u0000\u00ad"+
		"\u00ae\u0005\u0014\u0000\u0000\u00ae/\u0001\u0000\u0000\u0000\u00af\u00b0"+
		"\u0005\u0017\u0000\u0000\u00b01\u0001\u0000\u0000\u0000\u0007?DJOQer";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}