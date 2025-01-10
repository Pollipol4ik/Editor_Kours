package formula.AST;

public class DoubleNumber implements Expression {
    public double value;

    @Override
    public void accept(Visitor v) {
        v.visitDoubleNumber(this);
    }
}
