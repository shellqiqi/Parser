package shell7.parser;

import shell7.lexer.Lexer;
import shell7.lexer.Token;

import java.util.LinkedList;
import java.util.Stack;

public class Parser {

    private LinkedList<Token> inputs = new LinkedList<Token>();
    private SyntaxTree symbols = new SyntaxTree();
    private Stack<Integer> state = new Stack<Integer>();

    public Parser(Lexer lexer) throws Exception {
        Token t;
        while ((t = lexer.getNextToken()) != null) {
            inputs.addLast(t);
        }
        inputs.addLast(new Token("$", "$"));
        generateSyntaxTree();
    }

    public SyntaxTree getSyntaxTree() {
        return symbols;
    }

    private void next() {
        symbols.pushToken(inputs.getFirst());
        inputs.removeFirst();
    }

    private void shift(int i) {
        state.push(i);
        next();
    }

    private void reduce(int i) throws Exception {
        TokenNode tokenNode;
        switch (i) {
            case 1:
                tokenNode = new TokenNode(new Token("S", "if B S"));
                tokenNode.setChild(reducedToSyntaxTreeNode(3, tokenNode));
                break;
            case 2:
                tokenNode = new TokenNode(new Token("S", "if B S else S"));
                tokenNode.setChild(reducedToSyntaxTreeNode(5, tokenNode));
                break;
            case 3:
                tokenNode = new TokenNode(new Token("B", "( id )"));
                tokenNode.setChild(reducedToSyntaxTreeNode(3, tokenNode));
                break;
            case 4:
                tokenNode = new TokenNode(new Token("S", "id ;"));
                tokenNode.setChild(reducedToSyntaxTreeNode(2, tokenNode));
                break;
            default: throw new Exception("Reduction" + i + " not defined");
        }
        symbols.push(tokenNode);
    }

    private void go(int i) {
        state.push(i);
    }

    private SyntaxTreeNode reducedToSyntaxTreeNode(int popCount, TokenNode parent) {
        SyntaxTreeNode syntaxTreeNode = new SyntaxTreeNode(parent);
        for (int i = 0; i < popCount; i++) {
            syntaxTreeNode.tokenNodes.addFirst(symbols.peek());
            state.pop();
            symbols.pop();
        }
        return syntaxTreeNode;
    }

    private boolean isAction(String s) {
        return inputs.getFirst().getTag().equals(s);
    }

    private boolean isGoto(String s) {
        return !symbols.empty() && symbols.peekToken().getTag().equals(s);
    }

    private void generateSyntaxTree() throws Exception {
        state.push(0); // Init state
        while (true) {
            switch (state.peek()) {
                case 0:
                    if (isGoto("S")) go(1);
                    else if (isAction("IF")) shift(2);
                    else if (isAction("ID")) shift(3);
                    else throw new Exception("Syntax error"); break;
                case 1:
                    if (isAction("$")) return;
                case 2:
                    if (isGoto("B")) go(4);
                    else if (isAction("(")) shift(5);
                    else throw new Exception("Syntax error"); break;
                case 3:
                    if (isAction(";")) shift(6);
                    else throw new Exception("Syntax error"); break;
                case 4:
                    if (isGoto("S")) go(7);
                    else if (isAction("IF")) shift(8);
                    else if (isAction("ID")) shift(9);
                    else throw new Exception("Syntax error"); break;
                case 5:
                    if (isAction("ID")) shift(10);
                    else throw new Exception("Syntax error"); break;
                case 6:
                    if (isAction("$")) reduce(4);
                    else throw new Exception("Syntax error"); break;
                case 7:
                    if (isAction("$")) reduce(1);
                    else if (isAction("ELSE")) shift(11);
                    else throw new Exception("Syntax error"); break;
                case 8:
                    if (isGoto("B")) go(12);
                    else if (isAction("(")) shift(5);
                    else throw new Exception("Syntax error"); break;
                case 9:
                    if (isAction(";")) shift(13);
                    else throw new Exception("Syntax error"); break;
                case 10:
                    if (isAction(")")) shift(14);
                    else throw new Exception("Syntax error"); break;
                case 11:
                    if (isGoto("S")) go(15);
                    else if (isAction("IF")) shift(2);
                    else if (isAction("ID")) shift(3);
                    else throw new Exception("Syntax error"); break;
                case 12:
                    if (isGoto("S")) go(16);
                    else if (isAction("IF")) shift(8);
                    else if (isAction("ID")) shift(9);
                    else throw new Exception("Syntax error"); break;
                case 13:
                    if (isAction("$")) reduce(4);
                    else if (isAction("ELSE")) reduce(4);
                    else throw new Exception("Syntax error"); break;
                case 14:
                    if (isAction("IF")) reduce(3);
                    else if (isAction("ID")) reduce(3);
                    else throw new Exception("Syntax error"); break;
                case 15:
                    if (isAction("$")) reduce(2);
                    else throw new Exception("Syntax error"); break;
                case 16:
                    if (isAction("$")) reduce(1);
                    else if (isAction("ELSE")) shift(17);
                    else throw new Exception("Syntax error"); break;
                case 17:
                    if (isGoto("S")) go(18);
                    else if (isAction("IF")) shift(8);
                    else if (isAction("ID")) shift(9);
                    else throw new Exception("Syntax error"); break;
                case 18:
                    if (isAction("$")) reduce(2);
                    else if (isAction("ELSE")) reduce(2);
                    else throw new Exception("Syntax error"); break;
                default:
                    throw new Exception("Syntax error");
            }
        }
    }
}
