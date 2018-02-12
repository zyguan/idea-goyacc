package idea.goyacc

import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.lang.{ASTNode, ParserDefinition, PsiParser}
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.tree.{IFileElementType, TokenSet}
import com.intellij.psi.{FileViewProvider, PsiElement, PsiFile}
import idea.goyacc.ast.{GoYaccLexer, GoYaccParser}
import idea.goyacc.psi.{GoYaccFile, GoYaccType, GoYaccTypeExt}

class GoYaccParserDefinition extends ParserDefinition {
  import GoYaccParserDefinition._

  override def createElement(node: ASTNode): PsiElement = GoYaccType.Factory.createElement(node)
  override def createFile(provider: FileViewProvider): PsiFile = new GoYaccFile(provider)
  override def createLexer(project: Project): Lexer = new GoYaccLexer()
  override def createParser(project: Project): PsiParser = new GoYaccParser()

  override def getFileNodeType: IFileElementType = FILE_TYPE
  override def getCommentTokens: TokenSet = COMMENT_TOKENS
  override def getStringLiteralElements: TokenSet = STRING_TOKENS

  override def spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements = {
    SpaceRequirements.MAY
  }
}

object GoYaccParserDefinition {

  val FILE_TYPE = new IFileElementType(GoYaccLanguage)

  val COMMENT_TOKENS: TokenSet = TokenSet.create(GoYaccTypeExt.COMMENT)
  val STRING_TOKENS: TokenSet = TokenSet.create(GoYaccType.STR_LITERAL)

}
