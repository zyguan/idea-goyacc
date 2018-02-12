package idea.goyacc.psi

object GoYaccPsiUtil {

  def getElemName(elem: GoYaccElem): String =
    elem.getNode.findChildByType(GoYaccType.IDENTIFIER).getText

  def getElemAlias(elem: GoYaccElem): Option[String] =
    elem.getNode.findChildByType(GoYaccType.STR_LITERAL) match {
      case null => None
      case node => Some(node.getText)
    }

}
