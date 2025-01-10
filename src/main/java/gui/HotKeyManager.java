package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.util.StringJoiner;

public class HotKeyManager {


    /**
     * Копирует выделенные ячейки в буфер обмена.
     * @param table Таблица, из которой копируются данные.
     */
    private static void copyCells(JTable table) {
        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();

        if (selectedRows.length == 0 || selectedColumns.length == 0) {
            return;
        }

        StringJoiner rowJoiner = new StringJoiner("\n");
        for (int row : selectedRows) {
            StringJoiner cellJoiner = new StringJoiner("\t");
            for (int column : selectedColumns) {
                Object value = table.getValueAt(row, column);
                cellJoiner.add(value == null ? "" : value.toString());
            }
            rowJoiner.add(cellJoiner.toString());
        }

        StringSelection selection = new StringSelection(rowJoiner.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

    /**
     * Вырезает выделенные ячейки и копирует их в буфер обмена.
     * @param table Таблица, из которой вырезаются данные.
     */
    private static void cutCells(JTable table) {
        copyCells(table);

        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();

        for (int row : selectedRows) {
            for (int column : selectedColumns) {
                table.setValueAt(null, row, column);
            }
        }
    }

    /**
     * Вставляет данные из буфера обмена в таблицу.
     * @param table Таблица, в которую вставляются данные.
     */
    private static void pasteCells(JTable table) {
        try {
            String data = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);

            int startRow = table.getSelectedRow();
            int startColumn = table.getSelectedColumn();

            if (startRow == -1 || startColumn == -1) {
                return;
            }

            String[] rows = data.split("\n");
            for (int i = 0; i < rows.length; i++) {
                String[] cells = rows[i].split("\t");
                for (int j = 0; j < cells.length; j++) {
                    int targetRow = startRow + i;
                    int targetColumn = startColumn + j;

                    if (targetRow < table.getRowCount() && targetColumn < table.getColumnCount()) {
                        table.setValueAt(cells[j], targetRow, targetColumn);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setupHotKeysForTable(JTable table) {
        InputMap inputMap = table.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = table.getActionMap();

        // Ctrl + C -> Copy
        inputMap.put(KeyStroke.getKeyStroke("control C"), "copy");
        actionMap.put("copy", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyRange(table);
            }
        });

        // Ctrl + X -> Cut
        inputMap.put(KeyStroke.getKeyStroke("control X"), "cut");
        actionMap.put("cut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cutRange(table);
            }
        });

        // Ctrl + V -> Paste
        inputMap.put(KeyStroke.getKeyStroke("control V"), "paste");
        actionMap.put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteRange(table);
            }
        });
    }

    private static void copyRange(JTable table) {
        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();

        if (selectedRows.length == 0 || selectedColumns.length == 0) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int row : selectedRows) {
            for (int col : selectedColumns) {
                Object value = table.getValueAt(row, col);
                sb.append(value != null ? value.toString() : "").append("\t");
            }
            sb.setLength(sb.length() - 1); // Убираем последний таб
            sb.append("\n");
        }
        sb.setLength(sb.length() - 1); // Убираем последний перевод строки

        StringSelection selection = new StringSelection(sb.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

    private static void cutRange(JTable table) {
        copyRange(table); // Сначала копируем
        int[] selectedRows = table.getSelectedRows();
        int[] selectedColumns = table.getSelectedColumns();

        for (int row : selectedRows) {
            for (int col : selectedColumns) {
                table.setValueAt(null, row, col);
            }
        }
    }

    private static void pasteRange(JTable table) {
        try {
            // Получаем данные из буфера обмена
            String clipboardData = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);

            if (clipboardData == null || clipboardData.isEmpty()) {
                return; // Если буфер обмена пуст, ничего не делаем
            }

            // Разбиваем данные на строки
            String[] rows = clipboardData.split("\\r?\\n");

            // Определяем начальную позицию вставки
            int startRow = table.getSelectedRow();
            int startColumn = table.getSelectedColumn();

            if (startRow == -1 || startColumn == -1) {
                return; // Если не выбрана начальная ячейка, выходим
            }

            // Проходим по строкам данных
            for (int i = 0; i < rows.length; i++) {
                String[] cells = rows[i].split("\t"); // Разбиваем строку на столбцы
                for (int j = 0; j < cells.length; j++) {
                    int targetRow = startRow + i;
                    int targetColumn = startColumn + j;

                    // Проверяем, чтобы не выйти за пределы таблицы
                    if (targetRow < table.getRowCount() && targetColumn < table.getColumnCount()) {
                        table.setValueAt(cells[j], targetRow, targetColumn);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
