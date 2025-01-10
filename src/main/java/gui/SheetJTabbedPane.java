package gui;

import spreadsheet.ISheet;
import spreadsheet.ISpreadsheet;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class SheetJTabbedPane extends JTabbedPane {
    private final JComponent newSheetComponent;
    private final NewSheetComponentListener newSheetComponentListener;
    private final ISpreadsheet spreadsheet;

    public SheetJTabbedPane(ISpreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;

        // Добавляем существующие листы
        for (ISheet sheet : spreadsheet) {
            addSheetTab(sheet);
        }

        // Добавляем компонент для создания нового листа
        newSheetComponent = createNewSheetComponent();
        addTab("+", newSheetComponent);

        // Добавляем слушатель для компонента нового листа
        newSheetComponentListener = new NewSheetComponentListener(this);
        newSheetComponent.addComponentListener(newSheetComponentListener);

        setTabPlacement(JTabbedPane.BOTTOM);
        setSelectedIndex(0);
    }

    /**
     * Добавляет новый лист в виде вкладки.
     */
    private void addSheetTab(ISheet sheet) {
        JComponent sheetEditor = new SheetJPanel(sheet);
        addTab(sheet.getName(), sheetEditor);
    }

    /**
     * Создает компонент для кнопки добавления нового листа.
     */
    private JComponent createNewSheetComponent() {
        return new JComponent() {
            @Override
            public void setInheritsPopupMenu(boolean value) {
                super.setInheritsPopupMenu(value);
            }
        };
    }

    /**
     * Обновляет вкладки на основе нового объекта Spreadsheet.
     */
    public void setSpreadsheet(ISpreadsheet newSpreadsheet) {
        newSheetComponent.removeComponentListener(newSheetComponentListener);

        // Удаляем все текущие вкладки, кроме последней (для добавления нового листа)
        for (int i = getTabCount() - 2; i >= 0; i--) {
            removeTabAt(i);
        }

        // Добавляем листы из нового Spreadsheet
        for (ISheet sheet : newSpreadsheet) {
            addSheetTab(sheet);
        }

        setSelectedIndex(0);
        newSheetComponent.addComponentListener(newSheetComponentListener);
    }

    /**
     * Слушатель для компонента добавления нового листа.
     */
    private static class NewSheetComponentListener implements ComponentListener {
        private final SheetJTabbedPane sheetJTabbedPane;

        NewSheetComponentListener(SheetJTabbedPane sheetJTabbedPane) {
            this.sheetJTabbedPane = sheetJTabbedPane;
        }

        @Override
        public void componentResized(ComponentEvent e) {
            // Не используется
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            // Не используется
        }

        @Override
        public void componentShown(ComponentEvent e) {
            ISheet sheet = MainJFrame.getSpreadsheet().addSheet();
            sheetJTabbedPane.insertTab(sheet.getName(), null, new SheetJPanel(sheet), null, sheetJTabbedPane.getTabCount() - 1);
            sheetJTabbedPane.setSelectedIndex(sheetJTabbedPane.getTabCount() - 2);
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            // Не используется
        }
    }
}
