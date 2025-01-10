package gui;

import javax.swing.*;

public class MainJMenuBar extends JMenuBar  {
    MainJMenuBar(MainJFrame mainJFrame) {
        add(new FileJMenu(mainJFrame));
        //add(new EditJMenu());
    }
}
