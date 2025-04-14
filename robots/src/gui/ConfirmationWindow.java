package gui;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConfirmationWindow {

    // Общий метод для подтверждения действия
    public static boolean confirmAction(Component parent) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                "Вы уверены, что хотите закрыть это окно?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }

    // Метод для закрытия окон внутри основного
    public static void addCloseConfirmation(JInternalFrame frame) {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (confirmAction(frame)) {
                    frame.dispose();
                }
            }
        });
    }

    // Метод для закрытия основного окна
    public static void addCloseConfirmation(JFrame frame) {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirmAction(frame)) {
                    frame.dispose();
                    exitApplication();
                }
            }
        });
    }

    // Метод для непосредственного закрытия
    public static void closeApplication(JFrame frame) {
        if (confirmAction(frame)) {
            frame.dispose();
            exitApplication();
        }
    }

    protected static void exitApplication() {
        System.exit(0);
    }
}