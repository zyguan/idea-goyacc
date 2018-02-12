package idea.goyacc

import javax.swing.Icon

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.{FileTypeConsumer, FileTypeFactory, LanguageFileType}
import com.intellij.openapi.util.IconLoader

object GoYaccLanguage extends Language("GoYacc") {
  val ICON: Icon = IconLoader.getIcon("/icons/jar-gray.png") // TODO
}

object GoYaccFileType extends LanguageFileType(GoYaccLanguage) {
  override def getName: String = "Go Yacc File"
  override def getDescription: String = "The file for goyacc language"
  override def getDefaultExtension: String = "y"
  override def getIcon: Icon = GoYaccLanguage.ICON
}

class GoYaccFileTypeFactory extends FileTypeFactory {
  override def createFileTypes(consumer: FileTypeConsumer): Unit =
    consumer.consume(GoYaccFileType, GoYaccFileType.getDefaultExtension)
}
