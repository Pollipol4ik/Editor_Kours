package formula.AST;

public class StringLiteral implements Expression {
    public String value;

    @Override
    public void accept(Visitor v) {
        v.visitStringLiteral(this);
    }
}
