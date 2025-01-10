package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SaveActionListener implements ActionListener {

    private Component parent;
    private JFileChooser fc = SpreadSheetJFileChooser.getSpreadSheetJFileChooser();

    SaveActionListener(Component parent){
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        MainJFrame.saveSpreadsheet();
    }
}
