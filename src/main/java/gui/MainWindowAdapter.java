package gui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindowAdapter extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent we)
    {
        if (MainJFrame.closeSpreadsheet() != JOptionPane.CANCEL_OPTION) {
            System.exit(0);
        }
    }
}
