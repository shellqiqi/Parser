# 语法分析实验

本次实验选择 C 的子集 Mini-C 构造一个语法分析器

## C11 规范

C11 规范来自于 [cppreference.com](http://en.cppreference.com) 中所列举的 C 语言结构描述。Mini-C 从中选取了常用的关键字来构成 C 语言的子集，包括数字、常用的数据类型、条件控制语句、For 循环、和常见的运算操作符与函数调用。为了表示的简洁性，本次实验只保留条件控制语句。本次报告的示例程序如下所示。

```c
// 本次实验的测试用例
if (i)
    if (j)
        a;
    else
        b;
else
    if (k)
        d;
```

## BNF 范式

* S → if B S
* S → if B S else S
* S → id ;
* B → ( id )

## LR(1) 项集

![]()

## LR(1) 分析表

|     | if  | id  | (   | )   | ;   | $   | else | S   | B   |
| --- | --- | --- | --- | --- | --- | --- | ---- | --- | --- |
| 0   | s2  | s3  |     |     |     |     |      | 1   |     |
| 1   |     |     |     |     |     | acc |      |     |     |
| 2   |     |     | s5  |     |     |     |      |     | 4   |
| 3   |     |     |     |     | s6  |     |      |     |     |
| 4   | s8  | s9  |     |     |     |     |      | 7   |     |
| 5   |     | s10 |     |     |     |     |      |     |     |
| 6   |     |     |     |     |     | r4  |      |     |     |
| 7   |     |     |     |     |     | r1  | s11  |     |     |
| 8   |     |     | s5  |     |     |     |      |     | 12  |
| 9   |     |     |     |     | s13 |     |      |     |     |
| 10  |     |     |     | s14 |     |     |      |     |     |
| 11  | s2  | s3  |     |     |     |     |      | 15  |     |
| 12  | s8  | s9  |     |     |     |     |      | 16  |     |
| 13  |     |     |     |     |     | r4  | r4   |     |     |
| 14  | r3  | r3  |     |     |     |     |      |     |     |
| 15  |     |     |     |     |     | r2  |      |     |     |
| 16  |     |     |     |     |     | r1  | s17  |     |     |
| 17  | s8  | s9  |     |     |     |     |      | 18  |     |
| 18  |     |     |     |     |     | r2  | r2   |     |     |

## 语法分析程序

项目复用了前一次语法分析实验的内容，在 [github.com/shellqiqi/LexicalAnalysis](https://github.com/shellqiqi/LexicalAnalysis) 中描述了词法分析的细节，本节不介绍相关的内容。

### 程序结构

本程序分两个包，lexer 包包含词法分析类，parser 包包含语法分析类, App 作为入口类。lexer 中有三个类，DFA 类从 resource 中读取一个状态转化表，Lexer 类做词法分析，Token 是一个词素。parser 中有四个类，Parser 类做语法分析并给出语法分析树，SyntaxTree 类由 SyntaxTreeNode 类与 TokenNode 类组合而来，表示一个语法分析树。资源文件包括一个由 Mini-C 编写的程序以及识别语言的状态转化表。为了节约空间，代码经过了适当的压缩。

### 语法分析树数据结构

![]()

### 程序源码

语法分析主要程序，包含了状态转移和移进、规约算法。类的实例在初始化时便生成语法分析树。

```java
package shell7.parser; // file: Parser.java
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
        while ((t = lexer.getNextToken()) != null)
            inputs.addLast(t);
        inputs.addLast(new Token("$", "$"));
        generateSyntaxTree();
    }
    public SyntaxTree getSyntaxTree() { return symbols; } // 获取语法分析树
    private void next() { // 获取下一个Token
        symbols.pushToken(inputs.getFirst());
        inputs.removeFirst();
    }
    private void shift(int i) { state.push(i); next(); } // 移进
    private void reduce(int i) throws Exception { // 规约
        TokenNode tokenNode;
        switch (i) {
            case 1:
                tokenNode = new TokenNode(new Token("S", "if B S"));
                tokenNode.setChild(reducedToSyntaxTreeNode(3, tokenNode)); break;
            case 2:
                tokenNode = new TokenNode(new Token("S", "if B S else S"));
                tokenNode.setChild(reducedToSyntaxTreeNode(5, tokenNode)); break;
            case 3:
                tokenNode = new TokenNode(new Token("B", "( id )"));
                tokenNode.setChild(reducedToSyntaxTreeNode(3, tokenNode)); break;
            case 4:
                tokenNode = new TokenNode(new Token("S", "id ;"));
                tokenNode.setChild(reducedToSyntaxTreeNode(2, tokenNode)); break;
            default: throw new Exception("Reduction" + i + " not defined");
        }
        symbols.push(tokenNode);
    }
    private void go(int i) { state.push(i); }
    private SyntaxTreeNode reducedToSyntaxTreeNode(int popCount, TokenNode parent) {
        SyntaxTreeNode syntaxTreeNode = new SyntaxTreeNode(parent);
        for (int i = 0; i < popCount; i++) {
            syntaxTreeNode.tokenNodes.addFirst(symbols.peek());
            state.pop();
            symbols.pop();
        }
        return syntaxTreeNode;
    }
    private boolean isAction(String s) { return inputs.getFirst().getTag().equals(s); }
    private boolean isGoto(String s) {
        return !symbols.empty() && symbols.peekToken().getTag().equals(s);
    }
    private void generateSyntaxTree() throws Exception { // 根据状态生成语法分析树
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
```

语法分析树类保存了树的根节点和根上 Token 列表的栈操作。

```java
package shell7.parser; // file: SyntaxTree.java
import shell7.lexer.Token;
import java.util.LinkedList;
public class SyntaxTree {
    private SyntaxTreeNode root; // 语法分析树根结点
    public SyntaxTree() { root = new SyntaxTreeNode(null); }
    public SyntaxTreeNode getRoot() { return root; }
    public void push(TokenNode tokenNode) { getRoot().tokenNodes.push(tokenNode); }
    public void pushToken(Token token) { getRoot().tokenNodes.push(new TokenNode(token)); }
    public void pop() { getRoot().tokenNodes.pop(); }
    public TokenNode peek() { return getRoot().tokenNodes.peek(); }
    public Token peekToken() { return getRoot().tokenNodes.peek().getToken(); }
    public boolean empty() { return getRoot().tokenNodes.isEmpty(); }
    public void print() { print(getRoot(), 0); }
    private void print(SyntaxTreeNode syntaxTreeNode, int deep) { // 前序遍历语法分析树
        LinkedList<SyntaxTreeNode> nodeQueue = new LinkedList<SyntaxTreeNode>();
        if (!syntaxTreeNode.tokenNodes.isEmpty()) {
            for (int i = 0; i < deep; i++) System.out.print("  ");
            if (syntaxTreeNode.hasParent())
                System.out.print(syntaxTreeNode.getParent().getToken().getTag() + " -> ");
            for (TokenNode node : syntaxTreeNode.tokenNodes) {
                System.out.print(node.getToken().getTag() + " ");
                if (node.hasChild())
                    nodeQueue.addLast(node.getChild());
            }
            System.out.print("\n");
            for (SyntaxTreeNode node : nodeQueue)
                print(node, deep + 1);
        }
    }
}
```

语法分析树结点保持了该结点的亲属和一个 Token 列表

```java
package shell7.parser; // file: SyntaxTreeNode.java
import java.util.LinkedList;
public class SyntaxTreeNode {
    private TokenNode parent;
    public LinkedList<TokenNode> tokenNodes;
    public SyntaxTreeNode(TokenNode parent) {
        this.parent = parent;
        this.tokenNodes = new LinkedList<TokenNode>();
    }
    public boolean hasParent() { return !(parent == null); }
    public TokenNode getParent() { return parent; }
    public void setParent(TokenNode parent) { this.parent = parent; }
}
```

Token 结点保持了一个终结符或一个非终结符的一个展开

```java
package shell7.parser; // file: TokenNode.java
import shell7.lexer.Token;
public class TokenNode {
    private Token token;
    private SyntaxTreeNode child;
    public TokenNode(Token token) {
        this.token = token;
        this.child = null;
    }
    public boolean hasChild() { return !(child == null); }
    public Token getToken() { return token; }
    public void setToken(Token token) { this.token = token; }
    public SyntaxTreeNode getChild() { return child; }
    public void setChild(SyntaxTreeNode child) { this.child = child; }
}
```

### 测试结果

```
S
    S -> IF B S ELSE S
        B -> ( ID )
        S -> IF B S ELSE S
            B -> ( ID )
            S -> ID ;
            S -> ID ;
        S -> IF B S
            B -> ( ID )
            S -> ID ;
```
