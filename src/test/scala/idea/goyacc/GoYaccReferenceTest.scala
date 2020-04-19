package idea.goyacc

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import idea.goyacc.psi.{GoYaccRule, GoYaccTokenAlias, GoYaccTokenName}
import org.junit.Assert._

class GoYaccReferenceTest extends LightJavaCodeInsightFixtureTestCase {

  override def getTestDataPath: String = "src/test/resources/testData/insight"

  def testRuleRef(): Unit = {
    Seq("calc1.y", "calc2.y")
      .map(myFixture.configureByFile(_).findElementAt(myFixture.getCaretOffset).getParent)
      .foreach(elem => {
        val rule = elem.getReference.resolve()
        assertTrue(rule.isInstanceOf[GoYaccRule])
        assertEquals(myFixture.getFile.getName, rule.getFirstChild.getText, elem.getText)
      })
  }

  def testTokenRef(): Unit = {
    Seq("calc3.y", "calc4.y", "calc5.y")
      .map(myFixture.configureByFile(_).findElementAt(myFixture.getCaretOffset).getParent)
      .foreach(elem => {
        val token = elem.getReference.resolve()
        assertTrue(token.isInstanceOf[GoYaccTokenName])
        assertEquals(myFixture.getFile.getName, token.getFirstChild.getText, elem.getText)
      })
  }

  def testAliasRef(): Unit = {
    Seq("calc6.y")
      .map(myFixture.configureByFile(_).findElementAt(myFixture.getCaretOffset).getParent)
      .foreach(elem => {
        val alias = elem.getReference.resolve()
        assertTrue(alias.isInstanceOf[GoYaccTokenAlias])
        assertEquals(myFixture.getFile.getName, alias.getFirstChild.getText, elem.getText)
      })
  }

}
