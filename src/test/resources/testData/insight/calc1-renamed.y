/* Infix notation calculator.  */

%{
package calc
%}

%union {
  val int
}

/* Bison declarations.  */
%token <val> NUM "number"
%type  <val> Exp

%left '-' '+'
%left '*' '/'
%precedence NEG   /* negation--unary minus */
%right '^'        /* exponentiation */


%% /* The grammar follows.  */

Exp:
  NUM                { $$ = atoi($1)    }
| Exp '+' Exp        { $$ = $1 + $3     }
| Exp '-' Exp        { $$ = $1 - $3     }
| Exp '*' Exp        { $$ = $1 * $3     }
| Exp '/' Exp        { $$ = $1 / $3     }
| '-' Exp  %prec NEG { $$ = -$2         }
| Exp '^' Exp        { $$ = pow($1, $3) }
| '(' Exp ')'        { $$ = $2          }
;

%%
