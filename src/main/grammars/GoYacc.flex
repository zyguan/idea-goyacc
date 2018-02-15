package idea.goyacc.ast;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import idea.goyacc.psi.GoYaccType;
import idea.goyacc.psi.GoYaccTypeExt;

%%

%class _GoYaccLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

%{
  protected int braceLevel = 0;
%}

//WS        = [ \t\b]
//NL        = [\u2028\u2029\u000A\u000B\u000C\u000D\u0085] | \u000D\u000A
WSNL      = [\u2028\u2029\u000A\u000B\u000C\u000D\u0085\t\b\ ]
NNL       = [^\u2028\u2029\u000A\u000B\u000C\u000D\u0085]

COMMENT_B  = "/*" ( [^*] | \*+ [^*/] )* \*+ "/"
COMMENT_L  = "//" {NNL}*
STR_LIT_SQ = \' (\\[^\n\r]|[^\n\r\'\\])* \'
STR_LIT_DQ = \" (\\[^\n\r]|[^\n\r\"\\])* \"
STR_LIT    = {STR_LIT_DQ}
NUM_LIT    = [0-9]+
NAME       = [_a-zA-Z][_a-zA-Z0-9.]*
IDENT      = {NAME}|{STR_LIT_SQ}
GO_REST    = [^\{\}\"\'/]|"/"[^*/]
GO_CODE    = ({GO_REST}|{STR_LIT_SQ}|{STR_LIT_DQ}|{COMMENT_L}|{COMMENT_B})+

%state HEAD_CODE UNION_CODE ACTION_CODE RULES TAIL UNCLOSED

%%

<UNCLOSED> [^] { yypushback(yylength()); return GoYaccTypeExt.UNCLOSED; }

<YYINITIAL> {
    "%union"         { return GoYaccType.UNION; }
    "%left"          { return GoYaccType.LEFT; }
    "%right"         { return GoYaccType.RIGHT; }
    "%nonassoc"      { return GoYaccType.NONASSOC; }
    "%precedence"    { return GoYaccType.PRECEDENCE; }
    "%start"         { return GoYaccType.START; }
    "%token"         { return GoYaccType.TOKEN; }
    "%type"          { return GoYaccType.TYPE; }
    "%error-verbose" { return GoYaccType.ERROR_VERBOSE; }
}

<YYINITIAL> "%{"     { yybegin(HEAD_CODE); return GoYaccType.LCURL; }
<HEAD_CODE> {
    "%}"             { yybegin(YYINITIAL); return GoYaccType.RCURL; }
    ([^\%]|\%[^\}])+ { return GoYaccType.GO_CODE; }
    <<EOF>>          { yybegin(UNCLOSED); return GoYaccType.GO_CODE; }
}

<YYINITIAL> "{"      { braceLevel = 0; yybegin(UNION_CODE); return GoYaccType.BEGIN; }
<YYINITIAL> "}"      { yybegin(YYINITIAL); return GoYaccType.END; }
<UNION_CODE> {
    "{"              { braceLevel++; }
    "}"              { if (braceLevel > 0) braceLevel--; else { yypushback(1); yybegin(YYINITIAL); return GoYaccType.GO_CODE; } }
    {GO_CODE}        { /* append code */ }
    <<EOF>>          { yybegin(UNCLOSED); return GoYaccType.GO_CODE; }
}

<YYINITIAL> ","      { return GoYaccTypeExt.COMMA; }
<YYINITIAL> "<"      { return GoYaccTypeExt.LT; }
<YYINITIAL> ">"      { return GoYaccTypeExt.GT; }

<YYINITIAL> ^ "%%" { yybegin(RULES); return GoYaccType.START_RULES; }


<RULES> {IDENT} / {WSNL}* ":" { return GoYaccType.C_IDENT; }

<RULES> "%prec"  { return GoYaccType.PREC; }
<RULES> "{"      { braceLevel = 0; yybegin(ACTION_CODE); return GoYaccType.BEGIN; }
<RULES> "}"      { yybegin(RULES); return GoYaccType.END; }
<ACTION_CODE> {
    "{"          { braceLevel++; }
    "}"          { if (braceLevel > 0) braceLevel--; else { yypushback(1); yybegin(RULES); return GoYaccType.GO_CODE; } }
    {GO_CODE}    { /* append code */ }
    <<EOF>>      { yybegin(UNCLOSED); return GoYaccType.GO_CODE; }
}

<RULES> "|"      { return GoYaccTypeExt.OR; }
<RULES> ":"      { return GoYaccTypeExt.COLON; }
<RULES> ";"      { return GoYaccTypeExt.SEMI; }

<RULES> ^ "%%" { yybegin(TAIL); return GoYaccType.START_TAIL; }


<TAIL> [^]+ { if (yylength() > 0) return GoYaccType.GO_CODE; }


<YYINITIAL,RULES> {
    {STR_LIT}   { return GoYaccType.STR_LIT; }
    {NUM_LIT}   { return GoYaccType.NUM_LIT; }
    {IDENT}     { return GoYaccType.IDENT; }
    {COMMENT_B} { return GoYaccTypeExt.LINE_COMMENT; }
    {COMMENT_L} { return GoYaccTypeExt.BLOCK_COMMENT; }
    {WSNL}+     { return TokenType.WHITE_SPACE; }
}

[^]     { return TokenType.BAD_CHARACTER; }
