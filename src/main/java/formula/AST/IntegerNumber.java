package formula.AST;

public class IntegerNumber implements Expression {
    public int value;

    @Override
    public void accept(Visitor v) {
        v.visitIntegerNumber(this);
    }
}
