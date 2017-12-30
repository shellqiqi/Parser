package shell7.parser;

import shell7.lexer.Token;

public class SyntaxTree {

    private SyntaxTreeNode root = new SyntaxTreeNode(null);

    public void push(Token token) {
        root.tokenNodes.push(new TokenNode(token, null));
    }

    public void pop() {
        root.tokenNodes.pop();
    }

    public Token peek() {
        return root.tokenNodes.peek().getToken();
    }

    public boolean empty() {
        return root.tokenNodes.isEmpty();
    }
}
