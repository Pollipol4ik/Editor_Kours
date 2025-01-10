package formula.evaluator;

import formula.AST.*;
import spreadsheet.ICell;
import spreadsheet.ISheet;

/**
 * DependencyVisitor is responsible for adding dependencies between cells
 * based on the parsed formula's abstract syntax tree (AST).
 */
public class DependencyVisitor implements Visitor {

    private static DependencyGraph dependencyGraph;

    private static DependencyVisitor dependencyVisitor;

    private ICell cell;

    private boolean dependenciesFound;

    /**
     * Singleton method to get an instance of DependencyVisitor.
     */
    public static DependencyVisitor getDependencyVisitor() {
        if (dependencyVisitor == null) {
            dependencyVisitor = new DependencyVisitor();
        }
        return dependencyVisitor;
    }

    /**
     * Adds dependencies to the dependency graph based on the formula and the current cell.
     *
     * @param dependencyGraph the dependency graph to update
     * @param formula         the formula to process
     * @param cell            the current cell
     * @return true if dependencies were found, false otherwise
     */
    public boolean addDependencies(DependencyGraph dependencyGraph, Formula formula, ICell cell) {
        DependencyVisitor.dependencyGraph = dependencyGraph;
        this.cell = cell;
        dependenciesFound = false;
        visitFormula(formula);
        return dependenciesFound;
    }

    @Override
    public void visitFormula(Formula f) {
        f.exp.accept(this);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression exp) {
        exp.left.accept(this);
        exp.right.accept(this);
    }

    @Override
    public void visitUnaryExpression(UnaryExpression exp) {
        exp.exp.accept(this);
    }

    @Override
    public void visitFunctionCall(FunctionCall call) {
        call.argumentList.forEach(arg -> arg.accept(this));
    }

    @Override
    public void visitCellReference(CellReference ref) {
        ISheet targetSheet = ref.sheet == null ? cell.getSheet() : cell.getSheet().getSpreadsheet().getSheet(ref.sheet);

        if (ref.isRange()) {
            // Process a range of cells
            for (int row = ref.startRow; row <= ref.endRow; row++) {
                for (int col = ref.startColumn; col <= ref.endColumn; col++) {
                    ICell targetCell = targetSheet.getCellAt(row, col);
                    dependencyGraph.addDependency(cell.getAddress(), targetCell.getAddress());
                    dependenciesFound = true;
                }
            }
        } else {
            // Process a single cell
            ICell targetCell = targetSheet.getCellAt(ref.startRow, ref.startColumn);
            dependencyGraph.addDependency(cell.getAddress(), targetCell.getAddress());
            dependenciesFound = true;
        }
    }

    @Override
    public void visitParenExpression(ParenExpression exp) {
        exp.exp.accept(this);
    }

    @Override
    public void visitIntegerNumber(IntegerNumber num) {
        // Integer numbers do not introduce dependencies
    }

    @Override
    public void visitDoubleNumber(DoubleNumber num) {
        // Double numbers do not introduce dependencies
    }

    @Override
    public void visitBooleanValue(BooleanValue b) {
        // Boolean values do not introduce dependencies
    }

    @Override
    public void visitStringLiteral(StringLiteral lit) {
        // String literals do not introduce dependencies
    }
}
