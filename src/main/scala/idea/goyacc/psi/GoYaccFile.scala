package idea.goyacc.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import idea.goyacc.{GoYaccFileType, GoYaccLanguage}

class GoYaccFile(provider: FileViewProvider) extends PsiFileBase(provider, GoYaccLanguage.INSTANCE) {
  override def getFileType: FileType = GoYaccFileType.INSTANCE
}
