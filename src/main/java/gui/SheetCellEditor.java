package gui;

import spreadsheet.ICell;

import javax.swing.*;

public class SheetCellEditor extends DefaultCellEditor {
    public SheetCellEditor(JTextField textField) {
        super(textField);
        delegate = new EditorDelegate() {
            public void setValue(Object value) {
                textField.setText((value == null)
                        ? ""
                        : (((ICell)value).getFormula() == null
                            ? value.toString()
                            : (((ICell)value).getFormulaString())));
            }

            public Object getCellEditorValue() {
                return textField.getText();
            }
        };
        textField.addActionListener(delegate);
    }
}
