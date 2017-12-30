package shell7.parser;

import shell7.lexer.Token;

public class SyntaxTree {

    private SyntaxTreeNode root = new SyntaxTreeNode(null);

    public void push(TokenNode tokenNode) {
        root.tokenNodes.push(tokenNode);
    }

    public void pushToken(Token token) {
        root.tokenNodes.push(new TokenNode(token));
    }

    public void pop() {
        root.tokenNodes.pop();
    }

    public TokenNode peek() {
        return root.tokenNodes.peek();
    }

    public Token peekToken() {
        return root.tokenNodes.peek().getToken();
    }

    public boolean empty() {
        return root.tokenNodes.isEmpty();
    }
}
