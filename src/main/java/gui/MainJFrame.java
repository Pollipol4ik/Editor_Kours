package gui;

import spreadsheet.ISpreadsheet;
import spreadsheet.Spreadsheet;
import spreadsheet.SpreadsheetOpener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class MainJFrame extends JFrame {

    private static MainJFrame frame;
    private static ISpreadsheet spreadsheet;
    private static SheetJTabbedPane sheetTabbedPane;

    public MainJFrame(boolean isNew) {
        super("Spreadsheet Editor");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Отключаем закрытие по умолчанию
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new GridLayout(0, 1));

        // Установка главного меню
        JMenuBar mainMenuBar = new MainJMenuBar(this);  // Передаем текущее окно
        setJMenuBar(mainMenuBar);

        // Если это новое окно, создаем пустое состояние
        if (isNew) {
            spreadsheet = new Spreadsheet();  // Инициализация пустого spreadsheet
            spreadsheet.empty();  // Пустой документ
        } else {
            if (spreadsheet == null) {
                spreadsheet = new Spreadsheet();
            }
        }

        // Создание вкладок с таблицами
        sheetTabbedPane = new SheetJTabbedPane(spreadsheet);
        add(sheetTabbedPane);

        // Применение горячих клавиш для таблиц
        sheetTabbedPane.addChangeListener(e -> {
            Component selectedComponent = sheetTabbedPane.getSelectedComponent();
            if (selectedComponent instanceof SheetJPanel) {
                JTable table = ((SheetJPanel) selectedComponent).getTable();
                HotKeyManager.setupHotKeysForTable(table);
            }
        });

        // Устанавливаем видимость окна
        setVisible(true);
    }

    public void closeWindow() {
        int result = closeSpreadsheet();
        if (result == JOptionPane.YES_OPTION) {
            dispose();  // Закрытие текущего окна, если выбрали "Save"
        } else if (result == JOptionPane.NO_OPTION) {
            dispose();  // Закрытие текущего окна, если выбрали "Don't save"
        }
        // Если выбран "Cancel", окно не закрывается, поэтому ничего не делаем
    }


    // Обычный конструктор для старого окна
    public MainJFrame() {
        this(false);  // Создание окна с обычным состоянием
    }

    public static MainJFrame getFrame() {
        if (frame == null) {
            frame = new MainJFrame();
        }
        return frame;
    }

    public static MainJFrame getFrame(String fileName) {
        try {
            spreadsheet = SpreadsheetOpener.open(fileName);
        } catch (Exception e) {
            getFrame();
            JOptionPane.showMessageDialog(frame,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return frame;
    }

    // Другие методы, такие как open, save, и т.д.

    public static int closeSpreadsheet() {
        if (spreadsheet.isModified()) {
            Object[] options = {"Save",
                    "Don't save",
                    "Cancel"};
            int n = JOptionPane.showOptionDialog(frame,
                    "Save changes to document \\“"
                            + (spreadsheet.getFileName() == null ? "Untitled" : spreadsheet.getFileName())
                            + "\\” before closing?",
                    "Save Document?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == JOptionPane.YES_OPTION) {
                saveSpreadsheet();
            }
            return n;
        }
        return JOptionPane.YES_OPTION;
    }

    // Закрытие текущего окна
    public static void closeCurrentWindow() {
        if (frame != null) {
            frame.dispose();
        }
    }

    public static ISpreadsheet getSpreadsheet() {
        return spreadsheet;
    }


    public static void openSpreadsheet(String fileName) {
        if (closeSpreadsheet() == JOptionPane.CANCEL_OPTION) {
            return;
        }
        try {
            spreadsheet = SpreadsheetOpener.open(fileName);
            sheetTabbedPane.setSpreadsheet(spreadsheet);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(MainJFrame.getFrame(),
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static int saveSpreadsheet(String fileName) {
        try {
            spreadsheet.save(fileName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(MainJFrame.getFrame(),
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return JOptionPane.CANCEL_OPTION;
        }
        return JOptionPane.YES_OPTION;
    }

    public static int saveSpreadsheet() {
        if (spreadsheet.getFileName() == null) {
            SpreadSheetJFileChooser fc = SpreadSheetJFileChooser.getSpreadSheetJFileChooser();
            int returnVal = fc.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                saveSpreadsheet(file.getPath());
                return JOptionPane.YES_OPTION;
            }
            return JOptionPane.CANCEL_OPTION;
        }
        return saveSpreadsheet(spreadsheet.getFileName());
    }

    public static void redrawTable() {
        sheetTabbedPane.getSelectedComponent().repaint();
    }

}
