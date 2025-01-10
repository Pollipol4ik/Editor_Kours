package gui;

import spreadsheet.ISheet;
import spreadsheet.ISpreadsheet;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class SheetJTabbedPane extends JTabbedPane {
    private final JComponent newSheetComponent;
    private final NewSheetComponentListener newSheetComponentListener;

    SheetJTabbedPane(ISpreadsheet spreadsheet) {
        for (ISheet sheet : spreadsheet) {
            JComponent sheetEditor = new SheetJPanel(sheet);
            addTab(sheet.getName(), sheetEditor);
        }
        newSheetComponent = new JComponent() {
            @Override
            public void setInheritsPopupMenu(boolean value) {
                super.setInheritsPopupMenu(value);
            }
        };
        addTab("+", newSheetComponent);
        newSheetComponentListener = new NewSheetComponentListener(this);
        newSheetComponent.addComponentListener(newSheetComponentListener);
        setTabPlacement(JTabbedPane.BOTTOM);
        setSelectedIndex(0);
    }

    public void setSpreadsheet(ISpreadsheet spreadsheet) {
        newSheetComponent.removeComponentListener(newSheetComponentListener);
        for (int i = getTabCount() - 2; i >= 0;  i--) {
            removeTabAt(i);
        }
        for (ISheet sheet : spreadsheet) {
            insertTab(sheet.getName(), null, new SheetJPanel(sheet), null, getTabCount() - 1);
        }
        setSelectedIndex(0);
        newSheetComponent.addComponentListener(newSheetComponentListener);
    }

    private static class NewSheetComponentListener implements ComponentListener {

        SheetJTabbedPane sheetJTabbedPane;

        NewSheetComponentListener(SheetJTabbedPane sheetJTabbedPane) {
            this.sheetJTabbedPane = sheetJTabbedPane;
        }

        @Override
        public void componentResized(ComponentEvent e) {

        }

        @Override
        public void componentMoved(ComponentEvent e) {

        }

        @Override
        public void componentShown(ComponentEvent e) {
            ISheet sheet = MainJFrame.getSpreadsheet().addSheet();
            sheetJTabbedPane.insertTab(sheet.getName(), null, new SheetJPanel(sheet), null, sheetJTabbedPane.getTabCount() - 1);
            sheetJTabbedPane.setSelectedIndex(sheetJTabbedPane.getTabCount() - 2);
        }

        @Override
        public void componentHidden(ComponentEvent e) {

        }
    }
}
