package gui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SpreadSheetJFileChooser extends JFileChooser {

    private static final FileFilter[] fileFilters = {
            new FileNameExtensionFilter("OpenDocument spreadsheet (*.spreadsheet)", "spreadsheet")
    };

    private static SpreadSheetJFileChooser spreadSheetJFileChooser;

    private SpreadSheetJFileChooser() {
        for (FileFilter ff : fileFilters) {
            addChoosableFileFilter(ff);
        }
    }

    public static SpreadSheetJFileChooser getSpreadSheetJFileChooser() {
        if (spreadSheetJFileChooser == null) {
            spreadSheetJFileChooser = new SpreadSheetJFileChooser();
        }
        return spreadSheetJFileChooser;
    }
}
