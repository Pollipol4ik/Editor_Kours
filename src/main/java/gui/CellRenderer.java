package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CellRenderer extends DefaultTableCellRenderer {

    private static CellRenderer renderer;

    private CellRenderer() { }

    public static CellRenderer getRenderer() {
        if (renderer == null) {
            renderer = new CellRenderer();
        }
        return renderer;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return super.getTableCellRendererComponent(table, value, table.isRowSelected(row) && table.isColumnSelected(column), hasFocus, row, column);
    }
}
