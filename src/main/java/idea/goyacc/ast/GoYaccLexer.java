package idea.goyacc.ast;

import com.intellij.lexer.FlexAdapter;

public class GoYaccLexer extends FlexAdapter {
    public GoYaccLexer() {
        super(new _GoYaccLexer(null));
    }
}
