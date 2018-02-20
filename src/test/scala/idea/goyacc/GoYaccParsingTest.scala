package idea.goyacc

import com.intellij.testFramework.ParsingTestCase

class GoYaccParsingTest extends ParsingTestCase("parsing", "y", new GoYaccParserDefinition) {

  override def getTestDataPath: String = "src/test/resources/testData"

  override def includeRanges(): Boolean = true

  def test1(): Unit = doTest(true)

  def test2(): Unit = doTest(true)

  def test3(): Unit = doTest(true)

  def test4(): Unit = doTest(true)

  def testding(): Unit = doTest(true)

  def testgo(): Unit = doTest(true)

  def testso(): Unit = doTest(true)

}
