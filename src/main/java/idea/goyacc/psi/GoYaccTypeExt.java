package idea.goyacc.psi;

import com.intellij.psi.tree.IElementType;

public interface GoYaccTypeExt {

    IElementType COMMENT = GoYaccTypeFactory.newTokenType("COMMENT");
    IElementType COMMA = GoYaccTypeFactory.newTokenType("COMMA");
    IElementType COLON = GoYaccTypeFactory.newTokenType("COLON");
    IElementType SEMI = GoYaccTypeFactory.newTokenType("SEMI");
    IElementType LT = GoYaccTypeFactory.newTokenType("LT");
    IElementType GT = GoYaccTypeFactory.newTokenType("GT");
    IElementType OR = GoYaccTypeFactory.newTokenType("OR");

}
