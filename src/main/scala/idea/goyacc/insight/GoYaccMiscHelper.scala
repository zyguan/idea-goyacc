package idea.goyacc.insight

import com.intellij.lang.folding.{FoldingBuilderEx, FoldingDescriptor}
import com.intellij.lang.{ASTNode, BracePair, CodeDocumentationAwareCommenter, PairedBraceMatcher}
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.{PsiComment, PsiElement, PsiFile}
import idea.goyacc.psi._


class GoYaccBraceMatcher extends PairedBraceMatcher {

  override def isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType): Boolean = true

  override def getPairs: Array[BracePair] = GoYaccBraceMatcher.Pairs

  override def getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset
}

object GoYaccBraceMatcher {
  import GoYaccType.{BEGIN, END, LCURL, RCURL}
  import GoYaccTypeExt.{LT, GT}

  val Pairs: Array[BracePair] = Array(
    new BracePair(LCURL, RCURL, false),
    new BracePair(BEGIN, END, false),
    new BracePair(LT, GT, false))
}


class GoYaccCommenter extends CodeDocumentationAwareCommenter {

  override def isDocumentationComment(element: PsiComment): Boolean = false

  override def getLineCommentTokenType: IElementType = GoYaccTypeExt.LINE_COMMENT

  override def getBlockCommentTokenType: IElementType = GoYaccTypeExt.BLOCK_COMMENT

  override def getDocumentationCommentTokenType: IElementType = null

  override def getLineCommentPrefix: String = "//"

  override def getBlockCommentPrefix: String = "/*"

  override def getBlockCommentSuffix: String = "*/"

  override def getCommentedBlockCommentPrefix: String = null

  override def getCommentedBlockCommentSuffix: String = null

  override def getDocumentationCommentPrefix: String = null

  override def getDocumentationCommentSuffix: String = null

  override def getDocumentationCommentLinePrefix: String = null
}


class GoYaccFoldingBuilder extends FoldingBuilderEx {

  override def getPlaceholderText(node: ASTNode): String = "..."

  override def isCollapsedByDefault(node: ASTNode): Boolean = false

  override def buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array[FoldingDescriptor] = {
    PsiTreeUtil.findChildrenOfAnyType(root,
      classOf[GoYaccRule], classOf[GoYaccAction], classOf[GoYaccTokenDecl], classOf[GoYaccTypeDecl])
      .toArray()
      .filter {
        case _: GoYaccRule => true
        case action: GoYaccAction => action.getGoCode != null
        case decl: GoYaccTokenDecl => decl.getTokenList.size() > 1
        case decl: GoYaccTypeDecl => decl.getSymbolList.size() > 1
        case _ => false
      }
      .map {
        case rule: GoYaccRule =>
          new FoldingDescriptor(rule, new TextRange(
            rule.getFirstChild.getTextRange.getEndOffset, rule.getTextRange.getEndOffset))
        case action: GoYaccAction =>
          new FoldingDescriptor(action, action.getGoCode.getTextRange)
        case decl: GoYaccTokenDecl =>
          val field = decl.getField
          val start =
            if (field != null) field.getNextSibling.getTextRange.getStartOffset
            else decl.getFirstChild.getNextSibling.getTextRange.getStartOffset
          new FoldingDescriptor(decl, new TextRange(start, decl.getTextRange.getEndOffset))
        case decl: GoYaccTypeDecl =>
          val field = decl.getField
          val start =
            if (field != null) field.getNextSibling.getTextRange.getStartOffset
            else decl.getFirstChild.getNextSibling.getTextRange.getStartOffset
          new FoldingDescriptor(decl, new TextRange(start, decl.getTextRange.getEndOffset))
      }
  }

}
