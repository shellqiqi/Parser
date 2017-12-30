package shell7.parser;

import shell7.lexer.Token;

public class TokenNode {
    private Token token;
    private SyntaxTreeNode child;

    public TokenNode(Token token) {
        this.token = token;
        this.child = null;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public SyntaxTreeNode getChild() {
        return child;
    }

    public void setChild(SyntaxTreeNode child) {
        this.child = child;
    }
}
