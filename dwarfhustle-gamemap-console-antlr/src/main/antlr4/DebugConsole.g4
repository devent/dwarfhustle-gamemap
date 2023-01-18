/**
 * Define a grammar called DebugConsole.
 *
 * add Dwarf z=4, y=5, x=6
 * add Dwarf here
 * set position 4,10,10 to Dwarf with id=445566
 * set coordinates z=4, y=5, x=6 to Dwarf with id = -3466937467377025024
 * apply impulse vx=0.5, vy=0, vz=0 z=4, y=5, x=6 to Dwarf with id = -3466937467377025024
 *
 */
grammar DebugConsole;

sentence
    : add objectType coordinates
    | add objectType HERE
	| set property 'to' object selector?
	| save object selector?
	| apply physics vector position? 'to' object selector?
	;

physics
	: impulse
	;

add : 'add' ;

set : 'set' ;

save : 'save' ;

apply : 'apply' ;

impulse : 'impulse' ;

object
	: TILES
	| MARK
	| ID
	| CAMERA
	;

TILES: 'tiles' ;

MARK: 'mark' ;

CAMERA: 'camera' ;

HERE: 'here' ;

property
	: 'coordinates' coordinates
	| 'rotation' rotation
	| 'scale' scale
    | 'position' position
    | 'panningVelocity' panningVelocity
	;

coordinates
	: 'x' '=' x ',' 'y' '=' y ',' 'z' '=' z ('xx' '=' xx ',' 'yy' '=' yy ',' 'zz' '=' zz)?
	| 'z' '=' z ',' 'y' '=' y ',' 'x' '=' x ('zz' '=' zz ',' 'yy' '=' yy ',' 'xx' '=' xx)?
	| 'xx' '=' xx ',' 'yy' '=' yy ',' 'zz' '=' zz
	| 'zz' '=' zz ',' 'yy' '=' yy ',' 'xx' '=' xx
	;

rotation
	: 'x' '=' xx ',' 'y' '=' yy ',' 'z' '=' zz
	| 'z' '=' zz ',' 'y' '=' yy ',' 'x' '=' xx
	;

scale
	: 'x' '=' xx ',' 'y' '=' yy ',' 'z' '=' zz
	| 'z' '=' zz ',' 'y' '=' yy ',' 'x' '=' xx
	;

position
    : 'x' '=' xx ',' 'y' '=' yy ',' 'z' '=' zz
    | 'z' '=' zz ',' 'y' '=' yy ',' 'x' '=' xx
    ;

panningVelocity
    : 'x' '=' xx ',' 'y' '=' yy ',' 'z' '=' zz
    | 'z' '=' zz ',' 'y' '=' yy ',' 'x' '=' xx
    ;

vector
    : 'vx' '=' vx ',' 'vy' '=' vy ',' 'vz' '=' vz
    | 'vz' '=' vz ',' 'vy' '=' vy ',' 'vx' '=' vx
    ;

x : INT ;

y : INT ;

z : INT ;

xx : FLOAT ;

yy : FLOAT ;

zz : FLOAT ;

vx : FLOAT ;

vy : FLOAT ;

vz : FLOAT ;

selector : 'with' parameter ;

parameter
	: 'id' '=' id ;

id : INT ;

objectType: ID ;


INT : '-'? DIGIT+ ;

FLOAT: '-'? ('0'..'9')+ ('.' ('0'..'9')+)?;

fragment DIGIT: [0-9];

STRING
	: '"' (~["\r\n] | '""')* '"';

ID
    : ID_CHARS;

ID_CHARS
    : [a-zA-Z_]+[a-zA-Z0-9_]*;

WS
   : [ \r\n\t]+ -> skip;
