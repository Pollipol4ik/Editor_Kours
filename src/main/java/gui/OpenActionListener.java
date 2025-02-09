package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class OpenActionListener implements ActionListener {

    private final Component parent;
    private final JFileChooser fc = SpreadSheetJFileChooser.getSpreadSheetJFileChooser();

    OpenActionListener(Component parent){
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        int returnVal = fc.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            MainJFrame.openSpreadsheet(file.getPath());
        }
    }
}
