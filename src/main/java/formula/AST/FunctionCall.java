package formula.AST;

import java.util.List;

public class FunctionCall implements Expression {
    public String functionName;
    public List<Expression> argumentList;

    @Override
    public void accept(Visitor v) {
        v.visitFunctionCall(this);
    }
}
