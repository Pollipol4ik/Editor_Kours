package gui;

import spreadsheet.ISheet;

import javax.swing.*;
import java.awt.*;

public class SheetJPanel extends JPanel {
    private final SheetJTable sheetJTable;

    /**
     * Конструктор панели, содержащей таблицу с данными.
     * @param sheet объект ISheet, содержащий данные для таблицы.
     */
    public SheetJPanel(ISheet sheet) {
        setLayout(new BorderLayout());
        sheetJTable = new SheetJTable(sheet); // Инициализация основной таблицы
        JTable sheetJTableRowHeader = sheetJTable.getRowTableHeader(); // Таблица для отображения заголовков строк

        JScrollPane scrollPane = new JScrollPane(
                sheetJTable,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
        );

        // Настройка области прокрутки
        scrollPane.setRowHeaderView(sheetJTableRowHeader);
        scrollPane.setColumnHeaderView(sheetJTable.getTableHeader());
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, sheetJTableRowHeader.getTableHeader());

        // Слушатели для синхронизации прокрутки
        scrollPane.getHorizontalScrollBar().addAdjustmentListener((SheetJTableModel) sheetJTable.getModel());
        scrollPane.getVerticalScrollBar().addAdjustmentListener((SheetJTableModel) sheetJTable.getModel());

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Возвращает объект JTable, содержащий данные листа.
     * @return основная таблица листа.
     */
    public JTable getTable() {
        return sheetJTable;
    }
}
