package idea.goyacc.insight

import java.util
import java.util.regex.Pattern

import com.intellij.lang.cacheBuilder.{DefaultWordsScanner, WordsScanner}
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lang.refactoring.NamesValidator
import com.intellij.openapi.project.Project
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.{PsiElement, PsiNamedElement, PsiRecursiveElementWalkingVisitor, PsiReference}
import com.intellij.refactoring.rename.RenamePsiElementProcessor
import idea.goyacc.ast.GoYaccLexer
import idea.goyacc.psi._

class GoYaccFindUsagesProvider extends FindUsagesProvider {

  override def canFindUsagesFor(psiElement: PsiElement): Boolean =
    psiElement.isInstanceOf[PsiNamedElement]

  override def getHelpId(psiElement: PsiElement): String = null

  override def getWordsScanner: WordsScanner =
    new DefaultWordsScanner(new GoYaccLexer,
      TokenSet.create(GoYaccType.C_IDENT, GoYaccType.IDENT),
      TokenSet.create(GoYaccTypeExt.BLOCK_COMMENT, GoYaccTypeExt.LINE_COMMENT),
      TokenSet.create(GoYaccType.STR_LIT))

  override def getNodeText(element: PsiElement, useFullName: Boolean): String = element match {
    case named: PsiNamedElement => named.getName
    case _ => ""
  }

  override def getDescriptiveName(element: PsiElement): String = element match {
    case named: PsiNamedElement => named.getName
    case _ => ""
  }

  override def getType(element: PsiElement): String =  element match {
    case _: GoYaccTokenName => "token"
    case _: GoYaccTokenAlias => "token alias"
    case _: GoYaccRule => "symbol"
    case _ => ""
  }
}


object GoYaccNamesValitor {
  val NAME_PAT: Pattern = """[_a-zA-Z][_a-zA-Z0-9.]*""".r.pattern
  val STR1_PAT: Pattern = """'[^']+'""".r.pattern
  val STR2_PAT: Pattern = """"\w+"""".r.pattern
}

class GoYaccNamesValitor extends NamesValidator {
  import GoYaccNamesValitor._

  override def isKeyword(name: String, project: Project): Boolean = false

  override def isIdentifier(name: String, project: Project): Boolean =
    if (name.length == 0) false
    else if (NAME_PAT.matcher(name).matches()) true
    else if (STR1_PAT.matcher(name).matches()) true
    else if (STR2_PAT.matcher(name).matches()) true
    else false
}

class GoYaccRenameSpecialElementProcessor extends RenamePsiElementProcessor {
  override def canProcessElement(element: PsiElement): Boolean = element match {
    case named: GoYaccNamedElement =>
      val c = named.getName.charAt(0)
      c == '\'' || c == '"'
    case _ => false
  }

  override def findReferences(element: PsiElement): util.Collection[PsiReference] = {
    val result = new util.ArrayList[PsiReference]()
    val key = element.getFirstChild.getText
    element.getContainingFile.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
      override def visitElement(elem: PsiElement): Unit = elem match {
        case _: GoYaccRuleElem => super.visitElement(elem)
        case _: GoYaccAlias if elem.getText == key => result.add(elem.getReference)
        case _: GoYaccSymbol if elem.getText == key => result.add(elem.getReference)
        case _: GoYaccRuleAlt => super.visitElement(elem)
        case _: GoYaccRule => super.visitElement(elem)
        case _: GoYaccRuleList => super.visitElement(elem)
        case _ =>
      }
    })
    result
  }

}
