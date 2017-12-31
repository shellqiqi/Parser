package shell7.parser;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import shell7.lexer.Lexer;

import java.io.BufferedReader;
import java.io.FileReader;

public class ParserTest {

    private Parser parser;

    @Before
    public void setUp() throws Exception {
        parser = new Parser(new Lexer(new BufferedReader(new FileReader("./resource/test.txt"))));
    }

    @Test
    @Ignore
    public void getSyntaxTree() {
        parser.getSyntaxTree();
    }
}