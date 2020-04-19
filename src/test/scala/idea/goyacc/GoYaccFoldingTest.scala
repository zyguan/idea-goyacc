package idea.goyacc

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.testFramework.fixtures.impl.{JavaCodeInsightTestFixtureImpl, LightTempDirTestFixtureImpl}
import com.intellij.testFramework.fixtures.{IdeaTestFixtureFactory, LightJavaCodeInsightFixtureTestCase}

class GoYaccFoldingTest extends LightJavaCodeInsightFixtureTestCase {

  override def getTestDataPath: String = "src/test/resources/testData/insight"

  override def setUp(): Unit = {
    super.setUp()
    myFixture.tearDown()

    val fixture = IdeaTestFixtureFactory
      .getFixtureFactory
      .createLightFixtureBuilder(getProjectDescriptor)
      .getFixture

    myFixture = new JavaCodeInsightTestFixtureImpl(fixture, new LightTempDirTestFixtureImpl(true)) {
      override def getFoldingDescription(withCollapseStatus: Boolean): String = {
        performEditorAction(IdeActions.ACTION_COLLAPSE_ALL_REGIONS)
        super.getFoldingDescription(withCollapseStatus)
      }
    }
    myFixture.setUp()
    myFixture.setTestDataPath(getTestDataPath)

    // myModule = myFixture.getModule

  }


  def testFolding(): Unit = {
    myFixture.testFolding(getTestDataPath + "/calc2-folded.y")
  }

}
