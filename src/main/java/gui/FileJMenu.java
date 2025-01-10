package gui;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class FileJMenu extends JMenu {

    private final MainJFrame mainFrame;

    public FileJMenu(MainJFrame mainFrame) {
        super("File");
        this.mainFrame = mainFrame;
        setMnemonic(KeyEvent.VK_F);

        JMenuItem newJMenuItem = new JMenuItem("New");
        newJMenuItem.setMnemonic(KeyEvent.VK_N);
        add(newJMenuItem);
        newJMenuItem.addActionListener(e -> {
            MainJFrame newFrame = new MainJFrame(true);  // Открытие нового окна
            newFrame.setVisible(true);
        });

        JMenuItem closeTabJMenuItem = new JMenuItem("Close Tab");
        closeTabJMenuItem.setMnemonic(KeyEvent.VK_W);
        add(closeTabJMenuItem);
        closeTabJMenuItem.addActionListener(e -> mainFrame.closeWindow());  // Закрытие текущего окна


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
        exitJMenuItem.addActionListener(e -> System.exit(0));  // Закрытие всего приложения
    }
}
