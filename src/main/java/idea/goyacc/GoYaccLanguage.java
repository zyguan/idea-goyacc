package idea.goyacc;

import com.intellij.lang.Language;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class GoYaccLanguage extends Language {

    public static final GoYaccLanguage INSTANCE = new GoYaccLanguage();
    public static final Icon ICON_FILE = IconLoader.getIcon("/icons/y.png");

    private GoYaccLanguage() { super("GoYacc"); }

}
