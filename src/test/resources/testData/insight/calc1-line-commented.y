/* Infix notation calculator.  */

%{
package calc
%}

%union {
  val int
}

/* Bison declarations.  */
%token <val> NUM "number"
//%type  <val> exp

%left '-' '+'
%left '*' '/'
%precedence NEG   /* negation--unary minus */
%right '^'        /* exponentiation */


%% /* The grammar follows.  */

exp:
  NUM                { $$ = atoi($1)    }
| exp '+' exp        { $$ = $1 + $3     }
| exp '-' exp        { $$ = $1 - $3     }
| exp '*' exp        { $$ = $1 * $3     }
| exp '/' exp        { $$ = $1 / $3     }
| '-' exp  %prec NEG { $$ = -$2         }
| exp '^' exp        { $$ = pow($1, $3) }
| '(' exp ')'        { $$ = $2          }
;

%%
