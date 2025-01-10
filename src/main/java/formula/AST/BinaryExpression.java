package formula.AST;

public class BinaryExpression implements Expression {

    public Expression left, right;
    public BinaryOperator operator;

    @Override
    public void accept(Visitor v) {
        v.visitBinaryExpression(this);
    }
}
