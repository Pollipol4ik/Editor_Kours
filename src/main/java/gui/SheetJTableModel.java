package gui;

import formula.evaluator.TypeErrorException;
import formula.parser.ParseErrorException;
import spreadsheet.ISheet;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class SheetJTableModel extends AbstractTableModel implements AdjustmentListener {

    private int rowCount;
    private int columnCount;

    ISheet sheet;

    SheetJTableModel(ISheet sheet) {
        this.sheet = sheet;
        rowCount = 70;
        columnCount = 30;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return sheet.getCellAt(rowIndex + 1, columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            sheet.setValueAt(aValue, rowIndex + 1, columnIndex);
        } catch (ParseErrorException e) {
            JOptionPane.showMessageDialog(MainJFrame.getFrame(),
                    e.getMessage(),
                    "Formula parse error",
                    JOptionPane.ERROR_MESSAGE);
            throw e;
        } catch (TypeErrorException e) {
            JOptionPane.showMessageDialog(MainJFrame.getFrame(),
                    e.getMessage(),
                    "Formula evaluation error",
                    JOptionPane.ERROR_MESSAGE);
            throw e;
        }
        /*
        try {
            MainJFrame.getSpreadsheet().calculate();
        } catch (CyclicDependencyException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        Adjustable scroll = e.getAdjustable();
        if (scroll.getValue() + scroll.getVisibleAmount() >= scroll.getMaximum()) {
            if (scroll.getOrientation() == Adjustable.HORIZONTAL) {
                columnCount *= 1.1;
            } else if (scroll.getOrientation() == Adjustable.VERTICAL) {
                rowCount *= 1.1;
            }
            fireTableStructureChanged();
        }
        fireTableDataChanged();
    }
}
