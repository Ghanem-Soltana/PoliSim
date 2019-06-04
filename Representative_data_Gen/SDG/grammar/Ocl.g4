grammar Logic;

rule_set : expression EOF ;

conclusion : IDENTIFIER ;

expression
 : not expression
 | expression and expression
 | expression or expression
 | expression xor expression
 | expression implies expression
 | LPAREN expression RPAREN
 | atom;

atom : IDENTIFIER
| LPAREN IDENTIFIER RPAREN;

not : 'not';
and : 'and' ;
or : 'or' ;
xor : 'xor';
implies : 'implies';

LPAREN : '(' ;
RPAREN : ')' ;

IDENTIFIER : [a-zA-Z]+[0-9]*
				|[0-9]+;

WS : [ \r\t\n]+ -> skip ;