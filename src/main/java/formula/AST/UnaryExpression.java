package formula.AST;

public class UnaryExpression implements Expression {
    public Expression exp;
    public UnaryOperator operator;

    @Override
    public void accept(Visitor v) {
        v.visitUnaryExpression(this);
    }
}
