package gui;

import log.Logger;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class CreateMenuBar {
    private final JFrame parentFrame;

    public CreateMenuBar(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(systemMenu());
        menuBar.add(lookAndFeelMenu());
        menuBar.add(testMenu());
        return menuBar;
    }

    public JMenu lookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);

        systemLookAndFeel.addActionListener((_) -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));

        crossplatformLookAndFeel.addActionListener((_) -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));

        lookAndFeelMenu.add(systemLookAndFeel);
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        return lookAndFeelMenu;
    }

    public JMenu testMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_V);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((_) -> Logger.debug("Новая строка"));
        testMenu.add(addLogMessageItem);

        return testMenu;
    }

    public JMenu systemMenu() {
        JMenu systemMenu = new JMenu("Система");
        systemMenu.setMnemonic(KeyEvent.VK_V);
        systemMenu.getAccessibleContext().setAccessibleDescription(
                "Тут можной выйти");

        JMenuItem exitButton = new JMenuItem("Выход", KeyEvent.VK_S);
        exitButton.addActionListener((_) -> ConfirmationWindow.closeApplication(parentFrame));
        systemMenu.add(exitButton);

        return systemMenu;
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(parentFrame);
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
