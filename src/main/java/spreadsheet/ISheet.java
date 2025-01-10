package spreadsheet;

import gui.SheetJTableModel;

/**
 * This interface represents a sheet.
 */
public interface ISheet {

    /**
     * Remove the sheet from its parent spreadsheet.
     */
    void remove();

    ICell getCellAt(int row, int col);

    Column getColumnAt(int col);

    /**
     * Get value at a specific cell
     *
     * @param row row number
     * @param col column number
     * @return value object
     */
    Object getValueAt(int row, int col);

    /**
     * Remove a column by index
     *
     * @param index of the column
     * @param keepWidth whether to keep an empty column
     */
    void removeColumn(int index, boolean keepWidth);

    /**
     * Remove a range of columns
     *
     * @param first the first column to remove
     * @param last the last column to remove
     * @param keepWidth whether to keep empty columns
     */
    void removeColumns(int first, int last, boolean keepWidth);

    /**
     * Remove a row by index
     *
     * @param index of the row
     * @param keepLength whether to keep an empty row
     */
    void removeRow(int index, boolean keepLength);

    /**
     * Remove a range of rows
     *
     * @param first the first row to remove
     * @param last the last row to remove
     * @param keepLength whether to keep empty rows
     */
    void removeRows(int first, int last, boolean keepLength);

    /**
     * Get sheet name
     *
     * @return sheet name
     */
    String getName();

    /**
     * Set sheet name
     *
     * @param name new name
     */
    void setName(String name);

    /**
     * Set insert a value into the sheet
     *
     * @param value to insert
     * @param row row index
     * @param col column index
     */
    void setValueAt(Object value, int row, int col);

    /**
     * Get parent spreadsheet
     *
     * @return parent spreadsheet
     */
    ISpreadsheet getSpreadsheet();
}
