package idea.goyacc.insight

import java.util
import java.util.Collections
import javax.swing.Icon

import com.intellij.ide.structureView._
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.ide.util.treeView.smartTree.{SortableTreeElement, Sorter}
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.openapi.editor.Editor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.{PsiElement, PsiFile}
import com.intellij.util.PlatformIcons
import idea.goyacc.psi._

class GoYaccStructureViewFactory extends PsiStructureViewFactory {
  override def getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder =
    new TreeBasedStructureViewBuilder {
      override def createStructureViewModel(editor: Editor): StructureViewModel = new GoYaccStructureViewModel(psiFile)

      override def isRootNodeShown: Boolean = false
    }
}

class GoYaccStructureViewModel(file: PsiFile)
  extends StructureViewModelBase(file, new GoYaccStructureViewElement(file))
    with StructureViewModel.ElementInfoProvider {

  override def isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = false

  override def isAlwaysLeaf(element: StructureViewTreeElement): Boolean = element.getValue match {
    case _: GoYaccRule => true
    case _: GoYaccToken => true
    case _: GoYaccNonterminal => true
    case _ => false
  }

  override def getSorters: Array[Sorter] = Array(Sorter.ALPHA_SORTER)
}

class GoYaccStructureViewElement(element: PsiElement) extends PsiTreeElementBase[PsiElement](element) with SortableTreeElement {

  import collection.JavaConverters._

  private def declSuffix(e: PsiElement, v: String = ""): String = Option[PsiElement](e).map(e ⇒ s": ${e.getText}").getOrElse(v)

  override def getAlphaSortKey: String = getPresentableText

  override def getPresentableText: String = element match {
    case rule: GoYaccRule => rule.getFirstChild.getText
    case decl: GoYaccTokenDecl => "Tokens" + declSuffix(decl.getField)
    case decl: GoYaccPrecDecl => "Tokens" + Option[PsiElement](decl.getFirstChild).map(e ⇒ s"(${e.getText.substring(1)})").getOrElse("") + declSuffix(decl.getField)
    case decl: GoYaccTypeDecl => "Types" + declSuffix(decl.getField)
    case tok: GoYaccToken => tok.getTokenName.getText
    case sym: GoYaccNonterminal => sym.getNonterminalName.getText
    case _: GoYaccRuleList => "Rules"
    case _: GoYaccUnionDecl => "Union"
    case _ => element.toString
  }

  override def getChildrenBase: util.Collection[StructureViewTreeElement] = getElement match {
    case _: GoYaccFile =>
      val arr = new util.ArrayList[StructureViewTreeElement]()
      val decls = PsiTreeUtil.getChildOfType(element, classOf[GoYaccDeclList])
      if (decls != null) {
        decls.getUnionDeclList.forEach(uni => arr.add(new GoYaccStructureViewElement(uni)))
      }
      val rules = PsiTreeUtil.getChildOfType(element, classOf[GoYaccRuleList])
      if (rules != null && rules.getRuleList.size() > 0) arr.add(new GoYaccStructureViewElement(rules))
      if (decls != null) {
        decls.getTypeDeclList.forEach(sym => arr.add(new GoYaccStructureViewElement(sym)))
        decls.getTokenDeclList.forEach(tok => arr.add(new GoYaccStructureViewElement(tok)))
        decls.getPrecDeclList.forEach(tok => arr.add(new GoYaccStructureViewElement(tok)))
      }
      arr
    case rules: GoYaccRuleList =>
      rules.getRuleList.asScala.map(e ⇒ new GoYaccStructureViewElement(e): StructureViewTreeElement).asJava
    case decl: GoYaccTokenDecl =>
      decl.getTokenList.asScala.map(e ⇒ new GoYaccStructureViewElement(e): StructureViewTreeElement).asJava
    case decl: GoYaccPrecDecl =>
      decl.getTokenList.asScala.map(e ⇒ new GoYaccStructureViewElement(e): StructureViewTreeElement).asJava
    case decl: GoYaccTypeDecl =>
      decl.getNonterminalList.asScala.map(e ⇒ new GoYaccStructureViewElement(e): StructureViewTreeElement).asJava
    case _ =>
      Collections.emptyList()
  }

  override def getIcon(open: Boolean): Icon = element match {
    case _: GoYaccRuleList => PlatformIcons.PACKAGE_ICON
    case _: GoYaccRule => PlatformIcons.FUNCTION_ICON
    case _: GoYaccTokenDecl => PlatformIcons.SOURCE_FOLDERS_ICON
    case _: GoYaccPrecDecl => PlatformIcons.SOURCE_FOLDERS_ICON
    case _: GoYaccTypeDecl => PlatformIcons.TEST_SOURCE_FOLDER
    case _: GoYaccUnionDecl => PlatformIcons.INTERFACE_ICON
    case _: GoYaccToken => PlatformIcons.CLASS_ICON
    case _: GoYaccNonterminal => PlatformIcons.FIELD_ICON
    case _ => null
  }
}
