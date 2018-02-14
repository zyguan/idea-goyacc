package idea.goyacc.format

import com.intellij.application.options._
import com.intellij.lang.Language
import com.intellij.openapi.options.Configurable
import com.intellij.psi.codeStyle._
import idea.goyacc.GoYaccLanguage

class GoYaccCodeStyleSettings(settings: CodeStyleSettings)
  extends CustomCodeStyleSettings("GoYaccCodeSettings", settings)

class GoYaccCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
  import GoYaccCodeStyleSettingsProvider._

  override def getConfigurableDisplayName: String = "Go Yacc"

  override def createCustomSettings(settings: CodeStyleSettings): CustomCodeStyleSettings =
    new GoYaccCodeStyleSettings(settings)

  override def createSettingsPage(settings: CodeStyleSettings, originalSettings: CodeStyleSettings): Configurable = {
    new CodeStyleAbstractConfigurable(settings, originalSettings, getConfigurableDisplayName) {

      override def createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel =
        new GoYaccCodeStyleMainPanel(getCurrentSettings, settings)

    }
  }
}

object GoYaccCodeStyleSettingsProvider {
  class GoYaccCodeStyleMainPanel(currentSettings: CodeStyleSettings, settings: CodeStyleSettings)
    extends TabbedLanguageCodeStylePanel(GoYaccLanguage, currentSettings, settings) {
    override def initTabs(settings: CodeStyleSettings): Unit = {
      addIndentOptionsTab(settings)
    }
  }
}

class GoYaccLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {

  override def getLanguage: Language = GoYaccLanguage

  override def getCodeSample(settingsType: LanguageCodeStyleSettingsProvider.SettingsType): String =
    """/* Infix notation calculator.  */
      |
      |%{
      |package calc
      |import (
      |	"fmt"
      |)
      |%}
      |
      |/* Bison declarations.  */
      |%token NUM
      |%left '-' '+'
      |%left '*' '/'
      |%precedence NEG   /* negation--unary minus */
      |%right '^'        /* exponentiation */
      |
      |%% /* The grammar follows.  */
      |
      |input:
      |	%empty
      ||	input line
      |;
      |
      |line:
      |	'\n'
      ||	exp '\n'		{ printf ("\t%.10g\n", $1); }
      |;
      |
      |exp:
      |	NUM			{ $$ = $1;           }
      ||	exp '+' exp		{ $$ = $1 + $3;      }
      ||	exp '-' exp	 	{ $$ = $1 - $3;      }
      ||	exp '*' exp	 	{ $$ = $1 * $3;      }
      ||	exp '/' exp	 	{ $$ = $1 / $3;      }
      ||	'-' exp  %prec NEG	{ $$ = -$2;          }
      ||	exp '^' exp	 	{ $$ = pow ($1, $3); }
      ||	'(' exp ')'	 	{ $$ = $2;           }
      |;
      |
      |%%
    """.stripMargin

  override def getDefaultCommonSettings: CommonCodeStyleSettings = {
    val settings = new CommonCodeStyleSettings(GoYaccLanguage)
    settings.initIndentOptions()
    settings.getIndentOptions.INDENT_SIZE = 8
    settings.getIndentOptions.TAB_SIZE = 8
    settings.getIndentOptions.USE_TAB_CHARACTER = true
    settings
  }

  override def getIndentOptionsEditor: IndentOptionsEditor = new SmartIndentOptionsEditor()

//  override def customizeSettings(consumer: CodeStyleSettingsCustomizable,
//                                 settingsType: LanguageCodeStyleSettingsProvider.SettingsType): Unit =
//    settingsType match {
//      case SettingsType.INDENT_SETTINGS =>
//      case _ =>
//    }

}
