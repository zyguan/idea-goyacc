package idea.goyacc;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoYaccFileType extends LanguageFileType {

    public static final GoYaccFileType INSTANCE = new GoYaccFileType();

    private GoYaccFileType() { super(GoYaccLanguage.INSTANCE); }

    @NotNull
    @Override
    public String getName() { return "GoYacc File"; }

    @NotNull
    @Override
    public String getDescription() { return "The file for goyacc language"; }

    @NotNull
    @Override
    public String getDefaultExtension() { return "y"; }

    @Nullable
    @Override
    public Icon getIcon() { return GoYaccLanguage.ICON_FILE; }
}
