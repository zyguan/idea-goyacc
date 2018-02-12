package idea.goyacc.psi

import com.intellij.psi.tree.IElementType
import idea.goyacc.GoYaccLanguage

object GoYaccTypeFactory {

  class GoYaccElementType(name: String) extends IElementType(name, GoYaccLanguage)
  def newElementType(name: String) = new GoYaccElementType(name)

  class GoYaccTokenType(name: String) extends IElementType(name, GoYaccLanguage)
  def newTokenType(name: String) = new GoYaccTokenType(name)
}
