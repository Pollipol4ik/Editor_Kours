package gui;

import spreadsheet.ISpreadsheet;
import spreadsheet.Spreadsheet;
import spreadsheet.SpreadsheetOpener;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainJFrame extends JFrame {

    private static MainJFrame frame;
    private static ISpreadsheet spreadsheet;
    private static SheetJTabbedPane sheetTabbedPane;

    private MainJFrame() {
        super("Spreadsheet Editor");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new MainWindowAdapter());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new GridLayout(0,1));

        JMenuBar mainMenuBar = new MainJMenuBar();
        setJMenuBar(mainMenuBar);

        if (spreadsheet == null) {
            spreadsheet = new Spreadsheet();
            spreadsheet.empty();
        }
        sheetTabbedPane = new SheetJTabbedPane(spreadsheet);
        add(sheetTabbedPane);

        setVisible(true);
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

    public static ISpreadsheet getSpreadsheet() {
        return spreadsheet;
    }

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

    public static void redrawTable(){
        sheetTabbedPane.getSelectedComponent().repaint();
    }
}
