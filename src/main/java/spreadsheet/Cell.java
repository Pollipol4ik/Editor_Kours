package spreadsheet;

import formula.AST.Formula;
import formula.evaluator.EvaluatorVisitor;
import formula.parser.FormulaParser;
import formula.evaluator.DependencyGraph;
import formula.evaluator.DependencyVisitor;
import formula.parser.ParseErrorException;
import gui.MainJFrame;
import org.w3c.dom.Element;

public class Cell implements ICell {

    private Formula formula;
    private String formulaRaw;
    private Object value;
    private int row;
    private final Column column;

    Cell(Column column, int row) {
        this.row = row;
        this.column = column;
    }

    Cell(Column column, int row, Object value) {
        this(column, row);
        setValue(value);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getFormulaString() {
        return formulaRaw;
    }

    @Override
    public Formula getFormula() {
        return formula;
    }

    @Override
    public ISheet getSheet() {
        return column.getSheet();
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column.getColNumber();
    }

    @Override
    public void setValue(Object value) throws ParseErrorException {
        if (formula != null) {
            getSheet().getSpreadsheet().getDependencyGraph().removeDependenciesFrom(getAddress());
        }
        if (value instanceof String strValue) {
            if (strValue.length() == 0) {
                this.value = this.formula = null;
            }
            try {
                this.value = Integer.parseInt(strValue);
                formula = null;
                formulaRaw = null;
                return;
            } catch (NumberFormatException ignored) { }
            try {
                this.value = (double) Integer.parseInt(strValue);
                formula = null;
                formulaRaw = null;
                return;
            } catch (NumberFormatException ignored) { }
            if (strValue.matches("(true|TRUE|false|FALSE)")) {
                this.value = Boolean.parseBoolean(strValue);
                formula = null;
                formulaRaw = null;
                return;
            }
            if (strValue.matches("\"[^\"]\"")) {
                this.value = strValue;
                formula = null;
                formulaRaw = null;
                return;
            }
            if (strValue.matches("^=.*")) {
                FormulaParser parser = FormulaParser.getParser();
                Formula formula = parser.parse(strValue.substring(1), getSheet().getName());
                if (formula != null) {
                    this.formula = formula;
                    this.formulaRaw = strValue;
                    DependencyVisitor.getDependencyVisitor().addDependencies(
                            getSheet().getSpreadsheet().getDependencyGraph(),
                            formula,
                            this);
                    evaluate();
                    return;
                }
            } else {
                formulaRaw = null;
            }
        }
        this.value = value;
    }

    @Override
    public CellAddress getAddress() {
        return new CellAddress(getSheet().getName(), row, column.getColNumber());
    }

    @Override
    public void updateValue(Object value) {
        this.value = value;
        MainJFrame.redrawTable();
    }

    @Override
    public void evaluate() {
        if (formula == null) {
            return;
        }
        EvaluatorVisitor.getEvaluatorVisitor().evaluate(this);
        getSheet().getSpreadsheet().getDependencyGraph().usedBy(getAddress()).forEach((address)
                -> getSheet().getSpreadsheet().getSheet(address.sheetName())
                .getCellAt(address.row(), address.column()).evaluate());
    }

    public void save(Element cellElement) {
        cellElement.setAttribute("value", value == null ? "" : value.toString());
        cellElement.setAttribute("formula", formulaRaw);
    }

    public static Cell cellFromXMLElement(Element cellElement, Column column) {
        Cell result = new Cell(column, Integer.parseInt(cellElement.getAttribute("index")));
        String formula = cellElement.getAttribute("formula");
        if (formula == null || formula.length() == 0){
            result.setValue(cellElement.getAttribute("value"));
        } else {
            result.setValue(formula);
        }
        return result;
    }

    public void addDependencies(DependencyGraph dependencyGraph){
        if (formula == null) {
            return;
        }
        DependencyVisitor.getDependencyVisitor().addDependencies(dependencyGraph, formula, this);
    }

    @Override
    public String toString() {
        return value == null ? ""
                : value instanceof String && formula != null ? "\"" + value + "\""
                : value.toString();
    }
}
