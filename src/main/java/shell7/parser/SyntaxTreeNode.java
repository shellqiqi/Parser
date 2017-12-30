package shell7.parser;

import java.util.LinkedList;

public class SyntaxTreeNode {

    private SyntaxTreeNode parent;
    public LinkedList<TokenNode> tokenNodes;

    public SyntaxTreeNode(SyntaxTreeNode parent) {
        this.parent = parent;
        this.tokenNodes = new LinkedList<TokenNode>();
    }

    public SyntaxTreeNode getParent() {
        return parent;
    }
}
