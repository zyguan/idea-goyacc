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
    case _: GoYaccSymbol => true
    case _ => false
  }

  override def getSorters: Array[Sorter] = Array(Sorter.ALPHA_SORTER)
}

class GoYaccStructureViewElement(element: PsiElement) extends PsiTreeElementBase[PsiElement](element) with SortableTreeElement {

  override def getAlphaSortKey: String = getPresentableText

  override def getPresentableText: String = element match {
    case _: GoYaccRuleList => "Rules"
    case rule: GoYaccRule => rule.getFirstChild.getText
    case decl: GoYaccTokenDecl => s"Tokens :: ${decl.getField.getText}"
    case decl: GoYaccPrecDecl => s"Tokens :: ${decl.getField.getText} ${decl.getFirstChild.getText}"
    case decl: GoYaccTypeDecl => s"Types :: ${decl.getField.getText}"
    case tok: GoYaccToken => tok.getFirstChild.getText
    case sym: GoYaccSymbol => sym.getFirstChild.getText
    case _ => element.toString
  }

  override def getChildrenBase: util.Collection[StructureViewTreeElement] = getElement match {
    case file: GoYaccFile =>
      val arr = new util.ArrayList[StructureViewTreeElement]()
      val decls = PsiTreeUtil.getChildOfType(element, classOf[GoYaccDeclList])
      if (decls != null) {
        decls.getTokenDeclList.forEach(tok => if (tok.getField != null) arr.add(new GoYaccStructureViewElement(tok)))
        decls.getPrecDeclList.forEach(tok => if (tok.getField != null) arr.add(new GoYaccStructureViewElement(tok)))
        decls.getTypeDeclList.forEach(sym => arr.add(new GoYaccStructureViewElement(sym)))
      }
      val rules = PsiTreeUtil.getChildOfType(element, classOf[GoYaccRuleList])
      if (rules != null && rules.getRuleList.size() > 0) arr.add(new GoYaccStructureViewElement(rules))
      arr
    case rules: GoYaccRuleList =>
      val elems = new util.ArrayList[StructureViewTreeElement](rules.getRuleList.size())
      rules.getRuleList.forEach(r => elems.add(new GoYaccStructureViewElement(r)))
      elems
    case decl: GoYaccTokenDecl =>
      val ts = new util.ArrayList[StructureViewTreeElement](decl.getTokenList.size())
      decl.getTokenList.forEach(t => ts.add(new GoYaccStructureViewElement(t)))
      ts
    case decl: GoYaccPrecDecl =>
      val ts = new util.ArrayList[StructureViewTreeElement](decl.getTokenList.size())
      decl.getTokenList.forEach(t => ts.add(new GoYaccStructureViewElement(t)))
      ts
    case decl: GoYaccTypeDecl =>
      val ts = new util.ArrayList[StructureViewTreeElement](decl.getNonterminalList.size())
      decl.getNonterminalList.forEach(t => ts.add(new GoYaccStructureViewElement(t)))
      ts
    case _ =>
      Collections.emptyList()
  }

  override def getIcon(open: Boolean): Icon = element match {
    case _: GoYaccRuleList => PlatformIcons.PACKAGE_ICON
    case _: GoYaccRule => PlatformIcons.FUNCTION_ICON
    case _: GoYaccTokenDecl => PlatformIcons.SOURCE_FOLDERS_ICON
    case _: GoYaccPrecDecl => PlatformIcons.SOURCE_FOLDERS_ICON
    case _: GoYaccTypeDecl => PlatformIcons.TEST_SOURCE_FOLDER
    case _: GoYaccToken => PlatformIcons.CLASS_ICON
    case _: GoYaccSymbol => PlatformIcons.FIELD_ICON
    case _ => null
  }
}
