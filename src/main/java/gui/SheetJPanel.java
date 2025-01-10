package gui;

import spreadsheet.ISheet;

import javax.swing.*;
import java.awt.*;

public class SheetJPanel extends JPanel {
    SheetJPanel(ISheet sheet) {
        setLayout(new BorderLayout());
        SheetJTable sheetJTable = new SheetJTable(sheet);
        JTable  sheetJTableRowHeader = sheetJTable.getRowTableHeader();
        JScrollPane scrollPane = new JScrollPane(sheetJTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setRowHeaderView(sheetJTableRowHeader);
        scrollPane.setColumnHeaderView(sheetJTable.getTableHeader());
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, sheetJTableRowHeader.getTableHeader());
        scrollPane.getHorizontalScrollBar().addAdjustmentListener((SheetJTableModel)sheetJTable.getModel());
        scrollPane.getVerticalScrollBar().addAdjustmentListener((SheetJTableModel)sheetJTable.getModel());
        add(scrollPane, BorderLayout.CENTER);
    }
}
