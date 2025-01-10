package gui;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ContextMenuManager {

    /**
     * Adds a context menu with Copy, Cut, and Paste options to a JTable.
     * This example includes custom methods for copying/cutting/pasting a single cell.
     */
    public static void addContextMenuToTable(JTable table) {
        JPopupMenu popupMenu = new JPopupMenu();

        // "Копировать"
        JMenuItem copyItem = new JMenuItem("Копировать");
        copyItem.addActionListener(e -> copyCell(table));
        popupMenu.add(copyItem);

        // "Вырезать"
        JMenuItem cutItem = new JMenuItem("Вырезать");
        cutItem.addActionListener(e -> cutCell(table));
        popupMenu.add(cutItem);

        // "Вставить"
        JMenuItem pasteItem = new JMenuItem("Вставить");
        pasteItem.addActionListener(e -> pasteCell(table));
        popupMenu.add(pasteItem);

        // Mouse listener to display context menu on right-click
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && table.getSelectedRow() != -1) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() && table.getSelectedRow() != -1) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Adds a context menu with Copy, Cut, and Paste options to any JTextComponent.
     * This uses built-in .copy(), .cut(), and .paste() methods directly.
     */
    public static void addContextMenuToTextComponent(JTextComponent textComponent) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem copyItem = new JMenuItem("Копировать");
        copyItem.addActionListener(e -> textComponent.copy());
        popupMenu.add(copyItem);

        JMenuItem cutItem = new JMenuItem("Вырезать");
        cutItem.addActionListener(e -> textComponent.cut());
        popupMenu.add(cutItem);

        JMenuItem pasteItem = new JMenuItem("Вставить");
        pasteItem.addActionListener(e -> textComponent.paste());
        popupMenu.add(pasteItem);

        textComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Copy a single cell from the current selection in the JTable.
     */
     static void copyCell(JTable table) {
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        if (row != -1 && col != -1) {
            Object value = table.getValueAt(row, col);
            if (value != null) {
                StringSelection stringSelection = new StringSelection(value.toString());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }
        }
    }

    /**
     * Cut a single cell from the current selection in the JTable.
     * Copies the cell value to the clipboard, then clears it.
     */
     static void cutCell(JTable table) {
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        if (row != -1 && col != -1) {
            Object value = table.getValueAt(row, col);
            if (value != null) {
                StringSelection stringSelection = new StringSelection(value.toString());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                table.setValueAt("", row, col);
            }
        }
    }

    /**
     * Paste text from the clipboard into the selected cell in the JTable.
     */
     static void pasteCell(JTable table) {
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        if (row != -1 && col != -1) {
            try {
                String clipboardText = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
                        .getData(DataFlavor.stringFlavor);
                table.setValueAt(clipboardText, row, col);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}