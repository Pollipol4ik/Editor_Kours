package formula.parser;

public enum TokenType {
    DOUBLE("(-?[0-9]+)(\\.[0-9]+|[eE][+-]?[0-9]+)"),
    INT("-?[0-9]+"),
    BOOLEAN("(TRUE|FALSE|true|false)"),
    STRING("\"[^\"]*\""),
    PLUS("\\+"),
    MINUS("\\-"),
    MUL("\\*"),
    DIV("/"),
    AND("&"),
    OR("\\|"),
    NOT("!"),
    EQ("="),
    NEQ("<>"),
    LE("<="),
    GE(">="),
    LT("<"),
    GT(">"),
    POW("\\^"),
    REF("[A-Z]+[1-9]\\d*(?=[^\\w]|$)"),
    ID("[A-Za-z][A-Za-z0-9_]*"),
    DOT("\\."),
    COLON(":"),
    COMMA(","),
    PARENLEFT("\\("),
    PARENRIGHT("\\)"),
    FINISH("$"),
    FAIL(".*");

    public final String pattern;

    TokenType(String label) {
        this.pattern = label;
    }
}