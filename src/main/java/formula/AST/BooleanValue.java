package formula.AST;

import formula.evaluator.TypeErrorException;

public class BooleanValue implements Expression {
    public boolean value;

    @Override
    public void accept(Visitor v) throws TypeErrorException {
        v.visitBooleanValue(this);
    }
}
