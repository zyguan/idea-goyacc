package idea.goyacc.insight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.{SyntaxHighlighter, SyntaxHighlighterBase, SyntaxHighlighterFactory}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import idea.goyacc.ast.GoYaccLexer


object GoYaccSyntaxHighlighter extends SyntaxHighlighterBase {
  import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
  import com.intellij.openapi.fileTypes.SyntaxHighlighterBase.pack
  import idea.goyacc.psi.GoYaccType._
  import idea.goyacc.psi.GoYaccTypeExt._

  val Identifier: TextAttributesKey = createTextAttributesKey("Y_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
  val Keyword: TextAttributesKey = createTextAttributesKey("Y_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
  val Comment: TextAttributesKey = createTextAttributesKey("Y_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
  val String: TextAttributesKey = createTextAttributesKey("Y_STRING", DefaultLanguageHighlighterColors.STRING)
  val Number: TextAttributesKey = createTextAttributesKey("Y_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
  val Braces: TextAttributesKey = createTextAttributesKey("Y_BRACES", DefaultLanguageHighlighterColors.BRACES)
  val Angles: TextAttributesKey = createTextAttributesKey("Y_ANGLES", DefaultLanguageHighlighterColors.PARENTHESES)
  val Sign: TextAttributesKey = createTextAttributesKey("Y_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN)
  val Rule: TextAttributesKey = createTextAttributesKey("Y_RULE", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
  val Code: TextAttributesKey = createTextAttributesKey("Y_CODE", DefaultLanguageHighlighterColors.BLOCK_COMMENT)

  override def getHighlightingLexer: Lexer = new GoYaccLexer()

  override def getTokenHighlights(tokenType: IElementType): Array[TextAttributesKey] =
    tokenType match {
      case IDENT =>
        pack(Identifier)
      case COMMENT =>
        pack(Comment)
      case UNION | LEFT | RIGHT | LCURL | RCURL | START | NONASSOC | PRECEDENCE | PREC | TOKEN | TYPE | ERROR_VERBOSE =>
        pack(Keyword)
      case STR_LIT =>
        pack(String)
      case START | END =>
        pack(Braces)
      case GT | LT =>
        pack(Angles)
      case COLON =>
        pack(Sign)
      case C_IDENT =>
        pack(Rule)
      case GO_CODE =>
        pack(Code)
      case _ =>
        Array.empty
    }
}

class GoYaccSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
  override def getSyntaxHighlighter(proj: Project, vfile: VirtualFile): SyntaxHighlighter = GoYaccSyntaxHighlighter
}

