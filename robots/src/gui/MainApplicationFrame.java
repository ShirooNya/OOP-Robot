package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private boolean restoringFromProfile = false;

    public MainApplicationFrame() {
        setTitle("Robots Program");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Проверяем наличие сохраненного профиля
        if (WindowProfile.hasSavedProfile()) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Обнаружен сохраненный профиль окон. Восстановить?",
                    "Восстановление профиля",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                restoringFromProfile = true;
                restoreFromProfile();
                return;
            }
        }

        // Создаем окна по умолчанию, если профиль не восстанавливали
        initializeDefaultWindows();
    }

    private void initializeDefaultWindows() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);
        ConfirmationWindow.addCloseConfirmation(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);
        ConfirmationWindow.addCloseConfirmation(gameWindow);

        CreateMenuBar createMenu = new CreateMenuBar(this);
        setJMenuBar(createMenu.generateMenuBar());
        ConfirmationWindow.addCloseConfirmation(this);
    }

    private void restoreFromProfile() {
        List<WindowProfile.WindowState> states = WindowProfile.loadProfile();
        if (states == null) {
            initializeDefaultWindows();
            return;
        }

        setContentPane(desktopPane);
        CreateMenuBar createMenu = new CreateMenuBar(this);
        setJMenuBar(createMenu.generateMenuBar());
        ConfirmationWindow.addCloseConfirmation(this);

        for (WindowProfile.WindowState state : states) {
            try {
                if ("main".equals(state.type)) {
                    setBounds(state.x, state.y, state.width, state.height);
                    setExtendedState(state.extendedState);
                } else {
                    JInternalFrame frame;
                    if ("Протокол работы".equals(state.title)) {
                        frame = createLogWindow();
                    } else if ("Игровое поле".equals(state.title)) {
                        frame = new GameWindow();
                    } else {
                        continue;
                    }

                    frame.setLocation(state.x, state.y);
                    frame.setSize(state.width, state.height);
                    if (state.isIcon) frame.setIcon(true);
                    if (state.isMaximum) frame.setMaximum(true);

                    addWindow(frame);
                    ConfirmationWindow.addCloseConfirmation(frame);
                }
            } catch (Exception e) {
                Logger.error("Failed to restore window: " + e.getMessage());
            }
        }
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public void saveWindowStates() {
        List<WindowProfile.WindowState> states = new ArrayList<>();

        // Сохраняем состояние главного окна
        states.add(new WindowProfile.WindowState(
                "main",
                getTitle(),
                getX(),
                getY(),
                getWidth(),
                getHeight(),
                false,
                false,
                getExtendedState()));

        // Сохраняем внутренние окна
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            WindowProfile.WindowState state = new WindowProfile.WindowState(
                    "internal",
                    frame.getTitle(),
                    frame.getX(),
                    frame.getY(),
                    frame.getWidth(),
                    frame.getHeight(),
                    frame.isIcon(),
                    frame.isMaximum(),
                    0);

            states.add(state);
        }

        WindowProfile.saveProfile(states);
    }
}