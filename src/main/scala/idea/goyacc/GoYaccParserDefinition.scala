package idea.goyacc

import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.lang.{ASTNode, ParserDefinition, PsiParser}
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.tree.{IFileElementType, TokenSet}
import com.intellij.psi.{FileViewProvider, PsiElement, PsiFile}
import idea.goyacc.ast.{GoYaccLexer, GoYaccParser}
import idea.goyacc.psi.{GoYaccFile, GoYaccType}

class GoYaccParserDefinition extends ParserDefinition {
  import GoYaccParserDefinition._

  override def createElement(node: ASTNode): PsiElement = GoYaccType.Factory.createElement(node)
  override def createFile(provider: FileViewProvider): PsiFile = new GoYaccFile(provider)
  override def createLexer(project: Project): Lexer = new GoYaccLexer()
  override def createParser(project: Project): PsiParser = new GoYaccParser()

  override def getFileNodeType: IFileElementType = FILE_TYPE
  override def getCommentTokens: TokenSet = Comments
  override def getStringLiteralElements: TokenSet = StringLiterals

  override def spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements = {
    SpaceRequirements.MAY
  }
}

object GoYaccParserDefinition {
  import GoYaccType._
  import idea.goyacc.psi.GoYaccTypeExt._

  val FILE_TYPE = new IFileElementType(GoYaccLanguage.INSTANCE)

  val Comments: TokenSet = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
  val StringLiterals: TokenSet = TokenSet.create(STR_LIT)

}
