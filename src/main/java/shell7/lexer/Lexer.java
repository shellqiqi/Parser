package shell7.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

public class Lexer {

    private char peek;

    private DFA dfa;
    private Hashtable<String, Token> words;

    private BufferedReader bufferedReader;

    public Lexer(BufferedReader reader) throws Exception {
        init();
        bufferedReader = reader;
    }

    private void init() throws Exception {
        peek = ' ';
        dfa = new DFA();
        words = new Hashtable<String, Token>();
        reserve(new Token("MAIN", "main"));
        reserve(new Token("IF", "if"));
        reserve(new Token("ELSE", "else"));
        reserve(new Token("FOR", "for"));
        reserve(new Token("TYPE", "void"));
        reserve(new Token("TYPE", "char"));
        reserve(new Token("TYPE", "int"));
        reserve(new Token("MODIFIER", "signed"));
        reserve(new Token("MODIFIER", "unsigned"));
        reserve(new Token("RETURN", "return"));
    }

    public void reserve(Token w) {
        words.put(w.getValue(), w);
    }

    public void readNextChar() throws IOException {
        peek = (char) bufferedReader.read();
    }

    public Token getNextToken() throws Exception {
        int state = 0;
        int next;
        StringBuffer buffer = new StringBuffer();

        while (true) {
            next = dfa.nextState(state, peek);
            String stateName = dfa.stateType(next);

            if (dfa.isStop(next)) {
                return null;
            }

            if (dfa.isAccept(next)) { // Accept state
                if (dfa.isBack(next)) { // State with *
                    if (words.containsKey(buffer.toString())) // Check keywords
                        return words.get(buffer.toString());
                    if (stateName.equals("WS")) { // Ignore whitespace
                        state = 0;
                        buffer = new StringBuffer();
                        continue;
                    }
                    return new Token(stateName, buffer.toString());
                } else {
                    buffer.append(peek);
                    readNextChar();
                    return new Token(stateName, buffer.toString());
                }
            }
            state = next;

            buffer.append(peek);
            readNextChar();
        }
    }
}
