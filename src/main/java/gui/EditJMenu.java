package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Stack;

public class EditJMenu extends JMenu {

    // Стек для хранения истории действий
    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private JTable table;

    // Конструктор, который инициализирует меню и команды
    EditJMenu(JTable table) {
        super("Edit");
        this.table = table; // Сохраняем ссылку на таблицу

        setMnemonic(KeyEvent.VK_E); // Устанавливаем горячую клавишу для меню

        // Создание пункта меню для Undo
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.setMnemonic(KeyEvent.VK_U); // Горячая клавиша для Undo
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK)); // Ctrl+Z
        undoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });
        add(undoMenuItem); // Добавляем в меню

        // Создание пункта меню для Redo
        JMenuItem redoMenuItem = new JMenuItem("Redo");
        redoMenuItem.setMnemonic(KeyEvent.VK_R); // Горячая клавиша для Redo
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK)); // Ctrl+Y
        redoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });
        add(redoMenuItem); // Добавляем в меню
    }

    // Логика для отмены действия (Undo)
    private void undo() {
        if (!undoStack.isEmpty()) {
            String lastAction = undoStack.pop();  // Извлекаем последнее действие из undoStack
            redoStack.push(lastAction); // Перемещаем это действие в redoStack
            System.out.println("Undo: " + lastAction);  // Выводим информацию о том, что действие отменено
            // Здесь нужно добавить логику для реального выполнения отмены действия
        } else {
            System.out.println("No actions to undo");
        }
    }

    // Логика для повторения действия (Redo)
    private void redo() {
        if (!redoStack.isEmpty()) {
            String lastUndoneAction = redoStack.pop();  // Извлекаем последнее отмененное действие из redoStack
            undoStack.push(lastUndoneAction); // Перемещаем его обратно в undoStack
            System.out.println("Redo: " + lastUndoneAction);  // Выводим информацию о том, что действие повторено
            // Здесь нужно добавить логику для реального выполнения повторения действия
        } else {
            System.out.println("No actions to redo");
        }
    }

    // Метод для добавления действия в стек (для тестирования)
    public void addAction(String action) {
        undoStack.push(action);  // Добавляем новое действие в undoStack
        redoStack.clear();  // Очищаем redoStack, так как после нового действия Redo становится невозможным
        System.out.println("Action added: " + action);  // Выводим информацию о том, что действие добавлено
    }
}
