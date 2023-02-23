/**
 * Define a grammar called DebugConsole.
 *
 * add Dwarf 4,5,6
 * add Dwarf here
 * set position 4,10,10 to Dwarf with id=445566
 * set position 0,0,10 to camera
 * set layers 4 to terrain
 * set coordinates z=4, y=5, x=6 to Dwarf with id = -3466937467377025024
 * apply impulse 0.5,0,0 to Dwarf with id = -3466937467377025024
 *
 */
grammar DebugConsole;

sentence
    : verb objectType coordinates
    | verb objectType 'here'
    | verb property 'to' object selector?
    | verb object selector?
    | verb physics vector position? 'to' object selector?
    ;

verb
    : 'add'
    | 'set'
    | 'save'
    | 'apply'
    ;

physics
    : 'impulse'
    ;

object: ID ;

property
    : 'coordinates' coordinates
    | 'rotation' rotation
    | 'scale' scale
    | 'position' position
    | 'panningVelocity' panningVelocity
    | 'layers' layers
    ;

coordinates: x ',' y ',' z (xx ',' yy ',' zz)? ;

rotation: x ',' y ',' z ;

scale: x ',' y ',' z ;

position: x ',' y ',' z ;

panningVelocity: x ',' y ',' z ;

vector: vx ',' vy ',' vz ;

x : NUMBER ;

y : NUMBER ;

z : NUMBER ;

xx : NUMBER ;

yy : NUMBER ;

zz : NUMBER ;

vx : NUMBER ;

vy : NUMBER ;

vz : NUMBER ;

selector : 'with' parameter ;

parameter
    : 'id' '=' id ;

id : NUMBER ;

objectType: ID ;

layers : NATURAL ;

NUMBER: '-'? DIGIT+ ('.' DIGIT+ )?;

fragment DIGIT: [0-9];

STRING
    : '"' (~["\r\n] | '""')* '"';

ID
    : ID_CHARS;

ID_CHARS
    : [a-zA-Z_]+[a-zA-Z0-9_]*;

WS
   : [ \r\n\t]+ -> skip;
