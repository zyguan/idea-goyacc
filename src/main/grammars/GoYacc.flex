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
%eof{
%eof}

%{
  protected int braceLevel = 0;
  protected int codeSize = 0;
%}

LIT_NUM = 0 | [1-9][0-9]*
LIT_STR_SQ = \' (\\[^\n\r]|[^\n\r\'\\])* \'
LIT_STR_DQ = \" (\\[^\n\r]|[^\n\r\"\\])* \"
COMMENT_B = "/*" ( [^*] | \*+ [^/*] )* \*+ "/"
COMMENT_L = "//" [^\r\n]*
NAME = [_a-zA-Z][_a-zA-Z0-9.]*
TAG = "<" [_a-zA-Z][_a-zA-Z0-9]* ">"

DLR = "$" {TAG}? ("$"|"-"?{LIT_NUM})
NL = \R
WS = [ \t\f]

%state HEAD_CODE UNION_CODE ACTION_CODE RULES TAIL

%%

<YYINITIAL> "%union"         { return GoYaccType.UNION; }
<YYINITIAL> "%left"          { return GoYaccType.LEFT; }
<YYINITIAL> "%right"         { return GoYaccType.RIGHT; }
<YYINITIAL> "%nonassoc"      { return GoYaccType.NONASSOC; }
<YYINITIAL> "%precedence"    { return GoYaccType.PRECEDENCE; }
<YYINITIAL> "%start"         { return GoYaccType.START; }
<YYINITIAL> "%token"         { return GoYaccType.TOKEN; }
<YYINITIAL> "%type"          { return GoYaccType.TYPE; }
<YYINITIAL> "%error-verbose" { return GoYaccType.ERROR_VERBOSE; }

<YYINITIAL> "%{" { yybegin(HEAD_CODE); return GoYaccType.LCURL; }
<YYINITIAL> "%}" { yybegin(YYINITIAL); return GoYaccType.RCURL; }
<HEAD_CODE> {
    "%}"         { yypushback(2); yybegin(YYINITIAL); return GoYaccType.GO_CODE; }
    ({NL}|{WS})+ { /* append code */ }
    .            { /* append code */ }
}

<YYINITIAL> "{"  { yybegin(UNION_CODE); return GoYaccType.BEGIN; }
<YYINITIAL> "}"  { yybegin(YYINITIAL); return GoYaccType.END; }
<UNION_CODE> {
    "{"          { braceLevel += 1; }
    "}"          { if (braceLevel > 0) { braceLevel -= 1; } else { yypushback(1); yybegin(YYINITIAL); return GoYaccType.GO_CODE; } }
    ({NL}|{WS})+ { /* append code */ }
    .            { /* append code */ }
}

<YYINITIAL> ","  { return GoYaccTypeExt.COMMA; }
<YYINITIAL> "<"  { return GoYaccTypeExt.LT; }
<YYINITIAL> ">"  { return GoYaccTypeExt.GT; }

<YYINITIAL> "%%" { yybegin(RULES); return GoYaccType.START_RULES; }



<RULES> "%prec" { return GoYaccType.PREC; }

<RULES> ({NAME}|{LIT_STR_SQ}) ({NL}|{WS})* ":" { yypushback(1); return GoYaccType.RULE_NAME; }

<RULES> "{"  { yybegin(ACTION_CODE); return GoYaccType.BEGIN; }
<RULES> "}"  { yybegin(RULES); return GoYaccType.END; }
<ACTION_CODE> {
    "{"          { braceLevel += 1; codeSize += 1; }
    {DLR}        { if (codeSize > 0) { yypushback(yytext().length()); codeSize = 0; return GoYaccType.GO_CODE; } else { return GoYaccType.DLR; } }
    "}"          { if (braceLevel > 0) { braceLevel -= 1; codeSize += 1; } else { yypushback(1); yybegin(RULES); codeSize = 0; return GoYaccType.GO_CODE; } }
    ({NL}|{WS})+ { codeSize += yytext().length(); }
    .            { codeSize += 1; }
}

<RULES> "|"  { return GoYaccTypeExt.OR; }
<RULES> ":"  { return GoYaccTypeExt.COLON; }
<RULES> ";"  { return GoYaccTypeExt.SEMI; }

<RULES> "%%" { yybegin(TAIL); return GoYaccType.START_TAIL; }

<TAIL> {
    ({NL}|{WS})+ { codeSize += yytext().length(); }
    .            { codeSize += 1; }
}



<YYINITIAL,RULES> ({LIT_NUM})               { return GoYaccType.NUMBER; }
<YYINITIAL,RULES> ({LIT_STR_DQ})            { return GoYaccType.STR_LITERAL; }
<YYINITIAL,RULES> ({NAME}|{LIT_STR_SQ})     { return GoYaccType.IDENTIFIER; }
<YYINITIAL,RULES> ({COMMENT_B}|{COMMENT_L}) { return GoYaccTypeExt.COMMENT; }

({NL}|{WS})+ { return TokenType.WHITE_SPACE; }
.            { return TokenType.BAD_CHARACTER; }
<<EOF>>      { if (codeSize > 0 && yystate() == TAIL) { codeSize = 0; return GoYaccType.GO_CODE; } else { return null; } }
