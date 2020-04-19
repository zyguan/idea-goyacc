package idea.goyacc.psi

import java.util
import java.util.Collections

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.{Key, TextRange}
import com.intellij.psi._
import com.intellij.psi.util.{CachedValue, CachedValueProvider, CachedValuesManager, PsiTreeUtil}
import idea.goyacc.GoYaccLanguage

import scala.collection.mutable


object GoYaccPsiUtil {

  import GoYaccElementFactory._

  def setName(o: GoYaccRule, name: String): PsiElement =
    o.getNameIdentifier.replace(createCIdent(o.getProject, name))

  def getNameIdentifier(rule: GoYaccRule): PsiElement = rule.getFirstChild


  def setName(o: GoYaccTokenName, name: String): PsiElement =
    o.getNameIdentifier.replace(createIdent(o.getProject, name))

  def getNameIdentifier(name: GoYaccTokenName): PsiElement = name.getFirstChild


  def setName(o: GoYaccTokenAlias, name: String): PsiElement =
    o.getNameIdentifier.replace(createStrLit(o.getProject, name))

  def getNameIdentifier(alias: GoYaccTokenAlias): PsiElement = alias.getFirstChild


  def getName(o: PsiNameIdentifierOwner): String = o.getNameIdentifier.getText


  def getReference(elem: GoYaccNonterminalName): PsiReference = new GoYaccSymbolRef(elem)

  def getReference(elem: GoYaccSymbol): PsiReference = new GoYaccSymbolRef(elem)

  class GoYaccSymbolRef(elem: PsiElement)
    extends PsiReferenceBase[PsiElement](elem, TextRange.from(0, elem.getTextRange.getLength)) {

    val key: String = elem.getText.substring(
      getRangeInElement.getStartOffset, getRangeInElement.getEndOffset)

    implicit val file: PsiFile = elem.getContainingFile

    override def resolve(): PsiElement =
      if (key.startsWith("'"))
        cached(resolveTokens)
          .find(_.getTokenName.getName == key)
          .map(_.getTokenName)
          .orNull
      else
        cached(resolveRules)
          .find(_.getName == key)
          .orElse(cached(resolveTokens)
            .find(_.getTokenName.getName == key)
            .map(_.getTokenName.asInstanceOf[PsiElement]))
          .orNull

    override def getVariants: Array[AnyRef] =
      cached(resolveRules).map(_.getName).toArray

    override def handleElementRename(newElementName: String): PsiElement =
      elem.getFirstChild.replace(createIdent(elem.getProject, newElementName))
  }


  def getReference(elem: GoYaccAlias): PsiReference = new GoYaccAliasRef(elem)

  class GoYaccAliasRef(elem: GoYaccAlias)
    extends PsiReferenceBase[PsiElement](elem, TextRange.from(0, elem.getTextRange.getLength)) {

    val key: String = elem.getText.substring(
      getRangeInElement.getStartOffset, getRangeInElement.getEndOffset)

    implicit val file: PsiFile = elem.getContainingFile

    override def resolve(): PsiElement =
      cached(resolveTokens)
        .find(tok => tok.getTokenAlias != null && tok.getTokenAlias.getName == key)
        .map(_.getTokenAlias)
        .orNull

    override def getVariants: Array[AnyRef] =
      cached(resolveTokens)
        .filter(_.getTokenAlias != null)
        .map(_.getTokenAlias.getName)
        .toArray

    override def handleElementRename(newElementName: String): PsiElement =
      elem.getFirstChild.replace(createStrLit(elem.getProject, newElementName))
  }

  def cached[T](resolve: (PsiFile) => util.List[T])(implicit file: PsiFile): mutable.Buffer[T] = {
    import scala.collection.JavaConverters._
    CachedValuesManager.getCachedValue(file,
      Key.create[CachedValue[mutable.Buffer[T]]](resolve.getClass.toString),
      () => CachedValueProvider.Result.create(resolve(file).asScala, file))
  }

  def resolveRules(file: PsiFile): util.List[GoYaccRule] = {
    val lst = PsiTreeUtil.getChildOfType(file, classOf[GoYaccRuleList])
    if (lst != null) lst.getRuleList else Collections.emptyList()
  }

  def resolveTokens(file: PsiFile): util.List[GoYaccToken] = {
    val lst = PsiTreeUtil.getChildOfType(file, classOf[GoYaccDeclList])
    if (lst != null) {
      val toks = new util.ArrayList[GoYaccToken]()
      lst.getTokenDeclList.forEach(_.getTokenList.forEach(tok => toks.add(tok)))
      lst.getPrecDeclList.forEach(_.getTokenList.forEach(tok => toks.add(tok)))
      toks
    } else Collections.emptyList()
  }

}

object GoYaccElementFactory {

  def createCIdent(proj: Project, name: String): PsiElement =
    PsiTreeUtil.findChildOfType(
      createFile(proj, s"%%\n$name:"),
      classOf[GoYaccRule]
    ).getFirstChild

  def createIdent(proj: Project, name: String): PsiElement =
    PsiTreeUtil.findChildOfType(
      createFile(proj, s"%start $name\n%%\na:"),
      classOf[GoYaccStartDecl]
    ).getLastChild

  def createStrLit(proj: Project, name: String): PsiElement = {
    val unquoted = name.stripPrefix("\"").stripSuffix("\"")
    PsiTreeUtil.findChildOfType(
      createFile(proj, "%token t \""+unquoted+"\"\n%%\na:"),
      classOf[GoYaccTokenAlias]
    ).getFirstChild
  }

  def createFile(proj: Project, text: String): PsiFile =
    PsiFileFactory.getInstance(proj).createFileFromText("a.y", GoYaccLanguage.INSTANCE, text, false, false)

}
