package formula.AST;

public class Formula implements Visitable {
    public Expression exp;

    @Override
    public void accept(Visitor v) {
        v.visitFormula(this);
    }
}
