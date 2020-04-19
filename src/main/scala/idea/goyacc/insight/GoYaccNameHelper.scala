package idea.goyacc.insight

import java.util.regex.Pattern

import com.intellij.lang.cacheBuilder.{DefaultWordsScanner, WordsScanner}
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lang.refactoring.NamesValidator
import com.intellij.openapi.application.{QueryExecutorBase, ReadAction}
import com.intellij.openapi.project.Project
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.{PsiElement, PsiNamedElement, PsiRecursiveElementWalkingVisitor, PsiReference}
import com.intellij.util.Processor
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


object GoYaccNamesValidator {
  val NAME_PAT: Pattern = """[_a-zA-Z][_a-zA-Z0-9.]*""".r.pattern
  val STR1_PAT: Pattern = """'[^']+'""".r.pattern
  val STR2_PAT: Pattern = """"\w+"""".r.pattern
}

class GoYaccNamesValidator extends NamesValidator {
  import GoYaccNamesValidator._

  override def isKeyword(name: String, project: Project): Boolean = false

  override def isIdentifier(name: String, project: Project): Boolean =
    if (name.length == 0) false
    else if (NAME_PAT.matcher(name).matches()) true
    else if (STR1_PAT.matcher(name).matches()) true
    else if (STR2_PAT.matcher(name).matches()) true
    else false
}


class GoYaccSpecialReferenceSearcher extends QueryExecutorBase[PsiReference, ReferencesSearch.SearchParameters] {
  override def processQuery(params: ReferencesSearch.SearchParameters, consumer: Processor[_ >: PsiReference]): Unit =
    params.getElementToSearch match {
      case elem: GoYaccNamedElement if elem.getName.startsWith("'") || elem.getName.startsWith("\"") =>
        ReadAction.run(() => {
          elem.getContainingFile.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            override def visitElement(cur: PsiElement): Unit = cur match {
              case _: GoYaccRuleElem => super.visitElement(cur)
              case _: GoYaccAlias if cur.getText == elem.getName => consumer.process(cur.getReference)
              case _: GoYaccSymbol if cur.getText == elem.getName => consumer.process(cur.getReference)
              case _: GoYaccRuleAlt => super.visitElement(cur)
              case _: GoYaccRule => super.visitElement(cur)
              case _: GoYaccRuleList => super.visitElement(cur)
              case _ =>
            }
          })
        })
      case _ =>
    }
}
