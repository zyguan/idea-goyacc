package idea.goyacc.insight

import com.intellij.lang.{BracePair, CodeDocumentationAwareCommenter, PairedBraceMatcher}
import com.intellij.psi.{PsiComment, PsiFile}
import com.intellij.psi.tree.IElementType
import idea.goyacc.psi.{GoYaccType, GoYaccTypeExt}


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
