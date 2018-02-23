package idea.goyacc

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import com.intellij.codeInsight.generation.actions.{CommentByBlockCommentAction, CommentByLineCommentAction}
import com.intellij.openapi.actionSystem.IdeActions

class GoYaccCommenterTest extends LightCodeInsightFixtureTestCase {

  override def getTestDataPath: String = "src/test/resources/testData/insight"

  def testLineCommenter(): Unit = {
    myFixture.configureByFiles("calc1.y")
    val commentAction = new CommentByLineCommentAction
    commentAction.actionPerformedImpl(getProject, myFixture.getEditor)
    myFixture.checkResultByFile("calc1-line-commented.y")
    myFixture.performEditorAction(IdeActions.ACTION_EDITOR_MOVE_CARET_UP)
    commentAction.actionPerformedImpl(getProject, myFixture.getEditor)
    myFixture.checkResultByFile("calc1-uncommented.y")
  }

  def testBlockCommenter(): Unit = {
    myFixture.configureByFiles("calc1.y")
    myFixture.performEditorAction(IdeActions.ACTION_EDITOR_MOVE_LINE_START)
    myFixture.performEditorAction(IdeActions.ACTION_EDITOR_MOVE_CARET_DOWN_WITH_SELECTION)
    val commentAction = new CommentByBlockCommentAction
    commentAction.actionPerformedImpl(getProject, myFixture.getEditor)
    myFixture.checkResultByFile("calc1-block-commented.y")
    commentAction.actionPerformedImpl(getProject, myFixture.getEditor)
    myFixture.checkResultByFile("calc1-uncommented.y")
  }

}
