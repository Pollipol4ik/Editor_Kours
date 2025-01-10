package formula.AST;

import formula.evaluator.TypeErrorException;

public interface Visitable {
    void accept(Visitor v) throws TypeErrorException;
}
