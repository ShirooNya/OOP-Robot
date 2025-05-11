package gui;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConfirmationWindow {
    public static boolean confirmAction(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    public static void addCloseConfirmation(JInternalFrame frame) {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (confirmAction(frame,
                        "Вы уверены, что хотите закрыть это окно?",
                        "Подтверждение выхода")) {
                    frame.dispose();
                }
            }
        });
    }

    public static void addCloseConfirmation(JFrame frame) {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirmAction(frame,
                        "Вы уверены, что хотите выйти из приложения?",
                        "Подтверждение выхода")) {
                    if (frame instanceof MainApplicationFrame) {
                        ((MainApplicationFrame)frame).saveWindowStates();
                    }
                    frame.dispose();
                    exitApplication();
                }
            }
        });
    }

    public static void closeApplication(JFrame frame) {
        if (confirmAction(frame,
                "Вы уверены, что хотите выйти из приложения?",
                "Подтверждение выхода")) {
            if (frame instanceof MainApplicationFrame) {
                ((MainApplicationFrame)frame).saveWindowStates();
            }
            frame.dispose();
            exitApplication();
        }
    }

    protected static void exitApplication() {
        System.exit(0);
    }
}