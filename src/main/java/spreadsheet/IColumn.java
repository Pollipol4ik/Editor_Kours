package spreadsheet;

/**
 * This interface represents a cell.
 */
public interface IColumn {

    /**
     * Get cell at given row.
     *
     * @return cell at given row
     * @param row row number
     */
    ICell getCellAt(int row);

    /**
     * Set cell value at given row.
     *
     * @param value new value
     * @param row row number
     */
    void setValueAt(Object value, int row);

    /**
     * Set column number.
     *
     * @param colNumber new column number
     */
    void setColNumber(int colNumber);


    int getColNumber();

    ISheet getSheet();
}
