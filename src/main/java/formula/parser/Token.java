package formula.parser;

public class Token {
    public TokenType type;
    String value;

    Token(TokenType type) {
        this.type = type;
    }

    Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }
}
