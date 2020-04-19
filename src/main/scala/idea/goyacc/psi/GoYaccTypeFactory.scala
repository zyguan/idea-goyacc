package idea.goyacc.psi

import com.intellij.psi.tree.IElementType
import idea.goyacc.GoYaccLanguage

object GoYaccTypeFactory {

  class GoYaccElementType(name: String) extends IElementType(name, GoYaccLanguage.INSTANCE)
  def newElementType(name: String) = new GoYaccElementType(name)

  class GoYaccTokenType(name: String) extends IElementType(name, GoYaccLanguage.INSTANCE)
  def newTokenType(name: String) = new GoYaccTokenType(name)
}
