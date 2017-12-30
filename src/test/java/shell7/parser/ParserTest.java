package shell7.parser;

import org.junit.Before;
import org.junit.Test;
import shell7.lexer.Lexer;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.Assert.*;

public class ParserTest {

    Parser parser;

    @Before
    public void setUp() throws Exception {
        parser = new Parser(new Lexer(new BufferedReader(new FileReader("./resource/test.txt"))));
    }

    @Test
    public void printTree() throws Exception {
        parser.printTree();
    }
}