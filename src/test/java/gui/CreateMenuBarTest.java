package gui;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.swing.*;

import static org.mockito.Mockito.*;

public class CreateMenuBarTest {

    @Test
    void systemMenu_ExitButtonClick_ShouldCallCloseApplication() {
        JFrame mockFrame = mock(JFrame.class);
        CreateMenuBar menuBarCreator = new CreateMenuBar(mockFrame);

        // Мокируем статические методы ConfirmationWindow
        try (MockedStatic<ConfirmationWindow> mockedWindow = mockStatic(ConfirmationWindow.class)) {
            // Настраиваем поведение confirmAction, чтобы возвращать true
            mockedWindow.when(() -> ConfirmationWindow.confirmAction(mockFrame))
                    .thenReturn(true);

            // Получаем меню и кнопку выхода
            JMenu systemMenu = menuBarCreator.systemMenu();
            JMenuItem exitButton = (JMenuItem) systemMenu.getItem(0);

            // Имитируем нажатие
            exitButton.doClick();

            // Проверяем, что был вызван closeApplication
            mockedWindow.verify(() -> ConfirmationWindow.closeApplication(mockFrame));
        }
    }
}