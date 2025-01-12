package gui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SpreadSheetJFileChooser extends JFileChooser {

    private static final FileFilter[] fileFilters = {
            new FileNameExtensionFilter("OpenDocument spreadsheet (*.ods)", "ods"),
            new FileNameExtensionFilter("Custom spreadsheet (*.spreadsheet)", "spreadsheet")
    };

    private static SpreadSheetJFileChooser spreadSheetJFileChooser;

    private SpreadSheetJFileChooser() {
        for (FileFilter ff : fileFilters) {
            addChoosableFileFilter(ff);
        }
        setAcceptAllFileFilterUsed(false); // Отключаем возможность выбора "All Files"
        setFileFilter(fileFilters[0]); // Устанавливаем первый формат как дефолтный
    }

    public static SpreadSheetJFileChooser getSpreadSheetJFileChooser() {
        if (spreadSheetJFileChooser == null) {
            spreadSheetJFileChooser = new SpreadSheetJFileChooser();
        }
        return spreadSheetJFileChooser;
    }
}
