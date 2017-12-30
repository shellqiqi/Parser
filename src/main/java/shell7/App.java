package shell7;

import shell7.lexer.Lexer;
import shell7.lexer.Token;
import shell7.parser.Parser;

import java.io.BufferedReader;
import java.io.FileReader;

public class App {
    public static void main(String[] args) {
        try {
            Parser parser = new Parser(new Lexer(new BufferedReader(new FileReader("./resource/test.txt"))));
            parser.printTree();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
