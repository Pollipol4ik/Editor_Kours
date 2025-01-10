package formula.AST;

public class ParenExpression implements Expression {
    public Expression exp;

    @Override
    public void accept(Visitor v) {
        v.visitParenExpression(this);
    }
}
