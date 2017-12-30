package shell7.parser;

import java.util.LinkedList;

public class SyntaxTreeNode {

    private TokenNode parent;
    public LinkedList<TokenNode> tokenNodes;

    public SyntaxTreeNode(TokenNode parent) {
        this.parent = parent;
        this.tokenNodes = new LinkedList<TokenNode>();
    }

    public TokenNode getParent() {
        return parent;
    }

    public void setParent(TokenNode parent) {
        this.parent = parent;
    }
}
