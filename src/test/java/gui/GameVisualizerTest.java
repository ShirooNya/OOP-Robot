package gui;

import mechanics.Movements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameVisualizerTest {
    private GameVisualizer gameVisualizer;
    private Movements mockMovements;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() throws Exception {
        // Создаем тестовое изображение
        testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);

        // Мокаем статический метод ImageIO.read
        try (MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {
            mockedImageIO.when(() -> ImageIO.read((File) any())).thenReturn(testImage);

            // Создаем мок для Movements
            mockMovements = Mockito.mock(Movements.class);
            when(mockMovements.getPlayerX()).thenReturn(2);
            when(mockMovements.getPlayerY()).thenReturn(3);

            // Создаем экземпляр GameVisualizer
            gameVisualizer = new GameVisualizer();

            // Подменяем gameModel через reflection
            Field field = GameVisualizer.class.getDeclaredField("gameModel");
            field.setAccessible(true);
            field.set(gameVisualizer, mockMovements);

            // Устанавливаем размеры
            gameVisualizer.setSize(800, 600);
        }
    }

    @Test
    void testInitialization() {
        // Проверяем корректность инициализации объекта GameVisualizer
        assertNotNull(gameVisualizer);
        assertTrue(gameVisualizer.isFocusable());
        assertTrue(gameVisualizer.isDoubleBuffered());
        assertEquals(15, gameVisualizer.getFixedCols());
        assertEquals(15, gameVisualizer.getFixedRows());
    }

    @Test
    void testKeyPressHandling() {
        // Проверка обработки нажатий клавиш
        KeyEvent rightKeyEvent = new KeyEvent(gameVisualizer, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, 'D');

        gameVisualizer.getKeyListeners()[0].keyPressed(rightKeyEvent);

        verify(mockMovements).handleKeyPress(rightKeyEvent);
    }

    @Test
    void testPaintComponent() {
        // Проверка корректности отрисовки компонента
        BufferedImage bi = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();

        try {
            gameVisualizer.paintComponent(g2d);

            // Проверяем, что сетка была нарисована
            // Берем точку в центре клетки, а не на границе
            int checkX = gameVisualizer.getXOffset() + gameVisualizer.getCellSize() / 2;
            int checkY = gameVisualizer.getYOffset() + gameVisualizer.getCellSize() / 2;
            assertEquals(Color.WHITE.getRGB(), bi.getRGB(checkX, checkY));

            // Проверяем позицию игрока
            int expectedX = gameVisualizer.getXOffset() + 2 * gameVisualizer.getCellSize() + 2;
            int expectedY = gameVisualizer.getYOffset() + 3 * gameVisualizer.getCellSize() + 2;

            // Проверяем что это не белый цвет (значит что-то нарисовано)
            assertNotEquals(Color.WHITE.getRGB(), bi.getRGB(expectedX, expectedY));
        } finally {
            g2d.dispose();
        }
    }

    @Test
    void testGridDimensionsCalculation() throws Exception {
        // Проверка расчета размеров сетки

        // Устанавливаем размеры панели
        gameVisualizer.setSize(450, 300);

        // Вызываем расчет размеров
        Method calculateMethod = GameVisualizer.class.getDeclaredMethod("calculateGridDimensions");
        calculateMethod.setAccessible(true);
        calculateMethod.invoke(gameVisualizer);

        // Проверяем результаты
        assertEquals(20, gameVisualizer.getCellSize()); // 450/15 = 30, 300/15 = 20 -> min(30,20) = 20
        assertEquals(300, gameVisualizer.getGridWidth()); // 15*20 = 300
        assertEquals(300, gameVisualizer.getGridHeight()); // 15*20 = 300
        assertEquals(75, gameVisualizer.getXOffset()); // (450-300)/2 = 75
        assertEquals(0, gameVisualizer.getYOffset()); // (300-300)/2 = 0
    }

    @Test
    void testPreferredSize() {
        // Проверка изначального размера компонента
        Dimension preferredSize = gameVisualizer.getPreferredSize();
        assertEquals(600, preferredSize.width);  // 15*40 = 600
        assertEquals(600, preferredSize.height); // 15*40 = 600
    }

    @Test
    void testDrawGrid() throws Exception {
        // Подготовка
        GameVisualizer testVisualizer = GameVisualizer.createForTest(30, 450, 450, 50, 50);
        Graphics2D g2d = mock(Graphics2D.class);

        // Вызов
        testVisualizer.drawGrid(g2d);

        // Проверка фона
        verify(g2d).setColor(Color.WHITE);
        verify(g2d).fillRect(50, 50, 450, 450);

        // Проверка линий сетки
        verify(g2d).setColor(Color.LIGHT_GRAY);

        // Проверка количества линий (16 вертикальных и 16 горизонтальных)
        verify(g2d, times(16)).drawLine(
                anyInt(), eq(50), anyInt(), eq(50 + 450) // vertical
        );
        verify(g2d, times(16)).drawLine(
                eq(50), anyInt(), eq(50 + 450), anyInt() // horizontal
        );

        // Проверка конкретной линии
        verify(g2d).drawLine(eq(50 + 3*30), eq(50), eq(50 + 3*30), eq(50 + 450));

        // Проверка что ничего лишнего не вызвано
        verifyNoMoreInteractions(g2d);
    }

    @Test
    void testDrawPlayer() throws Exception {
        // Настройка
        GameVisualizer visualizer = GameVisualizer.createForTest(30, 0, 0, 50, 50);
        Movements mockModel = mock(Movements.class);
        when(mockModel.getPlayerX()).thenReturn(1);
        when(mockModel.getPlayerY()).thenReturn(1);

        // Подмена gameModel через reflection
        Field modelField = GameVisualizer.class.getDeclaredField("gameModel");
        modelField.setAccessible(true);
        modelField.set(visualizer, mockModel);

        // Создаем тестовое изображение
        BufferedImage bi = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();

        try {
            // Отрисовка
            visualizer.drawPlayer(g2d);

            // Проверяем всю область спрайта
            int startX = 50 + 1 * 30 + 2; // 82
            int startY = 50 + 1 * 30 + 2; // 82
            int spriteSize = 30 - 4;

            boolean hasVisiblePixels = false;
            for (int x = startX; x < startX + spriteSize; x++) {
                for (int y = startY; y < startY + spriteSize; y++) {
                    if ((bi.getRGB(x, y) & 0xFF000000) != 0) { // Проверка альфа-канала
                        hasVisiblePixels = true;
                        break;
                    }
                }
                if (hasVisiblePixels) break;
            }

            assertTrue(hasVisiblePixels, "В области спрайта должны быть непрозрачные пиксели");
        } finally {
            g2d.dispose();
        }
    }

    @Test
    void testComponentResize() throws Exception {
        // Проверка реакции на изменение размера

        // Создаем реальный экземпляр (не spy/mock)
        GameVisualizer visualizer = new GameVisualizer();
        visualizer.setSize(800, 600);

        // Подменяем gameModel через reflection
        Field gameModelField = GameVisualizer.class.getDeclaredField("gameModel");
        gameModelField.setAccessible(true);
        gameModelField.set(visualizer, mockMovements);

        // Получаем текущие значения до изменения размера
        int initialCellSize = visualizer.getCellSize();

        // Изменяем размер
        visualizer.setSize(900, 700);

        // Получаем ComponentListener
        ComponentListener listener = visualizer.getComponentListeners()[0];

        // Создаем событие изменения размера
        ComponentEvent resizeEvent = new ComponentEvent(visualizer, ComponentEvent.COMPONENT_RESIZED);

        // Вызываем обработчик вручную
        listener.componentResized(resizeEvent);

        // Проверяем, что размер клетки изменился
        assertNotEquals(initialCellSize, visualizer.getCellSize());

        // Проверяем новые расчеты
        int expectedCellSize = Math.min(900 / 15, 700 / 15);
        assertEquals(expectedCellSize, visualizer.getCellSize());
        assertEquals(15 * expectedCellSize, visualizer.getGridWidth());
        assertEquals(15 * expectedCellSize, visualizer.getGridHeight());

        // Проверяем центрирование
        int expectedXOffset = (900 - 15 * expectedCellSize) / 2;
        int expectedYOffset = (700 - 15 * expectedCellSize) / 2;
        assertEquals(expectedXOffset, visualizer.getXOffset());
        assertEquals(expectedYOffset, visualizer.getYOffset());
    }
}