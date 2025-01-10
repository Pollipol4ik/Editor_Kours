package formula.AST;

import formula.evaluator.TypeErrorException;

public interface Visitor {
    void visitFormula(Formula f) throws TypeErrorException;
    void visitBinaryExpression(BinaryExpression exp) throws TypeErrorException;
    void visitUnaryExpression(UnaryExpression exp) throws TypeErrorException;
    void visitFunctionCall(FunctionCall call);
    void visitCellReference(CellReference ref);
    void visitParenExpression(ParenExpression exp) throws TypeErrorException;
    void visitIntegerNumber(IntegerNumber num);
    void visitDoubleNumber(DoubleNumber num);
    void visitBooleanValue(BooleanValue b);
    void visitStringLiteral(StringLiteral lit);
}
