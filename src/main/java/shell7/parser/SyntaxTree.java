package shell7.parser;

import shell7.lexer.Token;

import java.util.LinkedList;

public class SyntaxTree {

    private SyntaxTreeNode root;

    public SyntaxTree() {
        root = new SyntaxTreeNode(null);
    }

    public SyntaxTreeNode getRoot() {
        return root;
    }

    public void push(TokenNode tokenNode) {
        getRoot().tokenNodes.push(tokenNode);
    }

    public void pushToken(Token token) {
        getRoot().tokenNodes.push(new TokenNode(token));
    }

    public void pop() {
        getRoot().tokenNodes.pop();
    }

    public TokenNode peek() {
        return getRoot().tokenNodes.peek();
    }

    public Token peekToken() {
        return getRoot().tokenNodes.peek().getToken();
    }

    public boolean empty() {
        return getRoot().tokenNodes.isEmpty();
    }

    public void print() {
        print(getRoot(), 0);
    }

    private void print(SyntaxTreeNode syntaxTreeNode, int deep) {
        LinkedList<SyntaxTreeNode> nodeQueue = new LinkedList<SyntaxTreeNode>();
        if (!syntaxTreeNode.tokenNodes.isEmpty()) {
            for (int i = 0; i < deep; i++) {
                System.out.print("  ");
            }
            if (syntaxTreeNode.hasParent()) {
                System.out.print(syntaxTreeNode.getParent().getToken().getTag() + " -> ");
            }
            for (TokenNode node :
                    syntaxTreeNode.tokenNodes) {
                System.out.print(node.getToken().getTag() + " ");
                if (node.hasChild()) {
                    nodeQueue.addLast(node.getChild());
                }
            }
            System.out.print("\n");
            for (SyntaxTreeNode node :
                    nodeQueue) {
                print(node, deep + 1);
            }
        }
    }
}
