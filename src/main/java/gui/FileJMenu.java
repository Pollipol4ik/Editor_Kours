package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class FileJMenu extends JMenu {
    FileJMenu() {
        super("File");
        setMnemonic(KeyEvent.VK_F);

        JMenuItem newJMenuItem = new JMenuItem("New");
        newJMenuItem.setMnemonic(KeyEvent.VK_N);
        add(newJMenuItem);

        JMenuItem openJMenuItem = new JMenuItem("Open...");
        openJMenuItem.setMnemonic(KeyEvent.VK_O);
        add(openJMenuItem);
        openJMenuItem.addActionListener(new OpenActionListener(getParent()));

        JMenuItem saveJMenuItem = new JMenuItem("Save");
        saveJMenuItem.setMnemonic(KeyEvent.VK_S);
        add(saveJMenuItem);
        saveJMenuItem.addActionListener(new SaveActionListener(getParent()));

        JMenuItem saveAsJMenuItem = new JMenuItem("Save as...");
        saveAsJMenuItem.setMnemonic(KeyEvent.VK_A);
        add(saveAsJMenuItem);
        saveAsJMenuItem.addActionListener(new SaveAsActionListener(getParent()));

        JMenuItem exitJMenuItem = new JMenuItem("Exit");
        exitJMenuItem.setMnemonic(KeyEvent.VK_X);
        add(exitJMenuItem);
        exitJMenuItem.addActionListener(e -> System.exit(0));
    }
}
