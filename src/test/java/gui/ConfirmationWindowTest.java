package gui;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

public class ConfirmationWindowTest {

    private JInternalFrame mockInternalFrame;
    private JFrame mockFrame;

    @Before
    public void setUp() {
        // Создаем мок-объекты JInternalFrame и JFrame
        mockInternalFrame = Mockito.mock(JInternalFrame.class);
        mockFrame = Mockito.mock(JFrame.class);

        // Настраиваем моки, чтобы они возвращали пустые массивы слушателей по умолчанию
        when(mockInternalFrame.getInternalFrameListeners()).thenReturn(new InternalFrameListener[0]);
        when(mockFrame.getWindowListeners()).thenReturn(new WindowListener[0]);
    }

    // Проверка закрытия окна при выборе "Да"
    @Test
    public void testInternalFrameClosing_YesOption() {
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            // Мокируем статический метод showConfirmDialog
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(mockInternalFrame),
                    eq("Вы уверены, что хотите закрыть это окно?"),
                    eq("Подтверждение выхода"),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE)
            )).thenReturn(JOptionPane.YES_OPTION);

            // Создаем ArgumentCaptor для захвата слушателя
            ArgumentCaptor<InternalFrameListener> listenerCaptor = ArgumentCaptor.forClass(InternalFrameListener.class);

            // Добавляем слушатель закрытия
            ConfirmationWindow.addCloseConfirmation(mockInternalFrame);

            // Захватываем добавленный слушатель
            verify(mockInternalFrame).addInternalFrameListener(listenerCaptor.capture());
            InternalFrameListener listener = listenerCaptor.getValue();

            // Симулируем событие закрытия окна
            listener.internalFrameClosing(new InternalFrameEvent(mockInternalFrame, InternalFrameEvent.INTERNAL_FRAME_CLOSING));

            // Проверяем, что метод dispose был вызван
            verify(mockInternalFrame, times(1)).dispose();
        }
    }

    // Проверка, что окно не закрывается при выборе "Нет"
    @Test
    public void testInternalFrameClosing_NoOption() {
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            // Мокируем статический метод showConfirmDialog для возврата "Нет"
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(mockInternalFrame),
                    eq("Вы уверены, что хотите закрыть это окно?"),
                    eq("Подтверждение выхода"),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE)
            )).thenReturn(JOptionPane.NO_OPTION);

            ArgumentCaptor<InternalFrameListener> listenerCaptor = ArgumentCaptor.forClass(InternalFrameListener.class);

            ConfirmationWindow.addCloseConfirmation(mockInternalFrame);

            verify(mockInternalFrame).addInternalFrameListener(listenerCaptor.capture());
            InternalFrameListener listener = listenerCaptor.getValue();

            listener.internalFrameClosing(new InternalFrameEvent(mockInternalFrame, InternalFrameEvent.INTERNAL_FRAME_CLOSING));

            verify(mockInternalFrame, times(1)).setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            verify(mockInternalFrame, never()).dispose();
        }
    }

    // Проверка, что основное окно не закрывается при выборе "Нет"
    @Test
    public void testWindowClosing_NoOption() {
        try (MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            // Мокируем статический метод showConfirmDialog для возврата "Нет"
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(
                    eq(mockFrame),
                    eq("Вы уверены, что хотите выйти из приложения?"),
                    eq("Подтверждение выхода"),
                    eq(JOptionPane.YES_NO_OPTION),
                    eq(JOptionPane.QUESTION_MESSAGE)
            )).thenReturn(JOptionPane.NO_OPTION);

            ArgumentCaptor<WindowAdapter> listenerCaptor = ArgumentCaptor.forClass(WindowAdapter.class);

            ConfirmationWindow.addCloseConfirmation(mockFrame);

            verify(mockFrame).addWindowListener(listenerCaptor.capture());
            WindowAdapter listener = listenerCaptor.getValue();

            listener.windowClosing(new WindowEvent(mockFrame, WindowEvent.WINDOW_CLOSING));

            // Проверяем, что метод setDefaultCloseOperation был вызван с DO_NOTHING_ON_CLOSE
            verify(mockFrame, times(1)).setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        }
    }
}