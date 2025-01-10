package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class EditJMenu extends JMenu {

    EditJMenu() {
        super("Edit");
        setMnemonic(KeyEvent.VK_E); // Устанавливаем горячую клавишу для меню

        // Создание пункта меню для Undo
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.setMnemonic(KeyEvent.VK_U); // Горячая клавиша для Undo
        undoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Логика для отмены действия (Undo)
                System.out.println("Undo action performed");
                // Здесь добавь логику для отмены действия
            }
        });
        add(undoMenuItem); // Добавляем в меню

        // Создание пункта меню для Redo
        JMenuItem redoMenuItem = new JMenuItem("Redo");
        redoMenuItem.setMnemonic(KeyEvent.VK_R); // Горячая клавиша для Redo
        redoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Логика для повторения действия (Redo)
                System.out.println("Redo action performed");
                // Здесь добавь логику для повторения действия
            }
        });
        add(redoMenuItem); // Добавляем в меню
    }
}
