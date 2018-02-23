package idea.goyacc

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import org.junit.Assert._

class GoYaccFindUsagesTest extends LightCodeInsightFixtureTestCase {

  override def getTestDataPath: String = "src/test/resources/testData/insight"

  def testFindNonterminalUsages(): Unit = {
    val usages = myFixture.testFindUsages("calc2.y")
    assertEquals(13, usages.size)
  }

  def testFindSymbolTokenUsages(): Unit = {
    val usages = myFixture.testFindUsages("calc3.y")
    assertEquals(1, usages.size)
  }

  def testFindLiteralTokenUsages(): Unit = {
    val usages = myFixture.testFindUsages("calc4.y")
    assertEquals(2, usages.size)
  }

  def testFindAliasUsages(): Unit = {
    val usages = myFixture.testFindUsages("calc6.y")
    assertEquals(1, usages.size)
  }

}
