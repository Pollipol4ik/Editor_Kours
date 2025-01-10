package gui;

import javax.swing.*;

public class MainJMenuBar extends JMenuBar  {
    MainJMenuBar() {
        add(new FileJMenu());
        //add(new EditJMenu());
    }
}
