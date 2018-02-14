package idea.goyacc.insight

import com.intellij.lang.{BracePair, PairedBraceMatcher}
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType


class GoYaccBraceMatcher extends PairedBraceMatcher {

  override def isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType): Boolean = true

  override def getPairs: Array[BracePair] = GoYaccBraceMatcher.Pairs

  override def getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset
}

object GoYaccBraceMatcher {
  import idea.goyacc.psi.GoYaccType.{BEGIN, END, LCURL, RCURL}
  import idea.goyacc.psi.GoYaccTypeExt.{LT, GT}

  val Pairs: Array[BracePair] = Array(
    new BracePair(LCURL, RCURL, false),
    new BracePair(BEGIN, END, false),
    new BracePair(LT, GT, false))
}
