package gui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SaveAsActionListener implements ActionListener {

    private final Component parent;
    private final JFileChooser fc = SpreadSheetJFileChooser.getSpreadSheetJFileChooser();

    SaveAsActionListener(Component parent) {
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        int returnVal = fc.showSaveDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            // Получаем выбранный фильтр для автоматического добавления расширения
            String extension = "";
            FileFilter selectedFilter = fc.getFileFilter();
            if (selectedFilter.getDescription().contains("*.ods")) {
                extension = ".ods";
            } else if (selectedFilter.getDescription().contains("*.spreadsheet")) {
                extension = ".spreadsheet";
            }

            // Добавляем расширение, если его нет
            if (!file.getName().toLowerCase().endsWith(extension)) {
                file = new File(file.getAbsolutePath() + extension);
            }

            MainJFrame.saveSpreadsheet(file.getPath());
        }
    }
}
