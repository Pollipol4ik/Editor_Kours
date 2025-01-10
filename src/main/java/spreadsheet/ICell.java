package spreadsheet;

import formula.AST.Formula;
import formula.parser.ParseErrorException;

/**
 * This interface represents a cell.
 */
public interface ICell {

    /**
     * Get cell value.
     *
     * @return value object
     */
    Object getValue();

    String getFormulaString();

    Formula getFormula();

    ISheet getSheet();

    int getRow();

    int getColumn();

    void setValue(Object value) throws ParseErrorException;

    CellAddress getAddress();

    void updateValue(Object value);

    void evaluate();
}
