/* Infix notation calculator.  */

%{
package calc
%}

%union {
  val int
}

/* Bison declarations.  */
%token <val> NUM "number"
%type  <val> exp

%left '-' '+'
%left '*' '/'
%precedence NEG   /* negation--unary minus */
%right '^'        /* exponentiation */


%% /* The grammar follows.  */

exp<fold text='...'>:
  NUM                {<fold text='...'> $$ = atoi($1)    </fold>}
| exp '+' exp        {<fold text='...'> $$ = $1 + $3     </fold>}
| exp '-' exp        {<fold text='...'> $$ = $1 - $3     </fold>}
| exp '*' exp        {<fold text='...'> $$ = $1 * $3     </fold>}
| exp '/' exp        {<fold text='...'> $$ = $1 / $3     </fold>}
| '-' exp  %prec NEG {<fold text='...'> $$ = -$2         </fold>}
| exp '^' exp        {<fold text='...'> $$ = pow($1, $3) </fold>}
| '(' exp ')'        {<fold text='...'> $$ = $2          </fold>}
;</fold>

%%
