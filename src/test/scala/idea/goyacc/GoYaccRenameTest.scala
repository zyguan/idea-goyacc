package idea.goyacc

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import idea.goyacc.insight.GoYaccNamesValitor
import org.junit.Assert._

class GoYaccRenameTest extends LightJavaCodeInsightFixtureTestCase {

  override def getTestDataPath: String = "src/test/resources/testData/insight"

  def testRenameNonterminal(): Unit = {
    myFixture.configureByFiles("calc1.y")
    myFixture.renameElementAtCaret("Exp")
    myFixture.checkResultByFile("calc1.y", "calc1-renamed.y", false)
  }

  def testRenameSymbolToken(): Unit = {
    myFixture.configureByFiles("calc3.y")
    myFixture.renameElementAtCaret("num")
    myFixture.checkResultByFile("calc3.y", "calc3-renamed.y", false)
  }

  def testRenameLiteralToken(): Unit = {
    myFixture.configureByFiles("calc4.y")
    myFixture.renameElementAtCaret("':-:'")
    myFixture.checkResultByFile("calc4.y", "calc4-renamed.y", false)
  }

  def testRenameAlias(): Unit = {
    myFixture.configureByFiles("calc6.y")
    myFixture.renameElementAtCaret("\"Number\"")
    myFixture.checkResultByFile("calc6.y", "calc6-renamed.y", false)
  }

  def testValidateIdentifier(): Unit = {
    val validator = new GoYaccNamesValitor
    assertTrue(validator.isIdentifier("foo", getProject))
    assertTrue(validator.isIdentifier("'<>'", getProject))
    assertTrue(validator.isIdentifier("\"bar\"", getProject))
    assertFalse(validator.isIdentifier("42", getProject))
    assertFalse(validator.isIdentifier("<>", getProject))
    assertFalse(validator.isIdentifier("\"\"", getProject))
  }

}
