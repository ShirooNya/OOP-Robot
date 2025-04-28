package gui;

import mechanics.Movements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;

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
        // Тест инициализации
        assertNotNull(gameVisualizer);
        assertTrue(gameVisualizer.isFocusable());
        assertTrue(gameVisualizer.isDoubleBuffered());
    }

    @Test
    void testKeyPressHandling() {
        // Тест обработки нажатий клавиш
        when(mockMovements.getCols()).thenReturn(10);
        when(mockMovements.getRows()).thenReturn(10);

        KeyEvent rightKeyEvent = new KeyEvent(gameVisualizer, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, 'D');

        gameVisualizer.getKeyListeners()[0].keyPressed(rightKeyEvent);

        verify(mockMovements).handleKeyPress(rightKeyEvent);
    }

    @Test
    void testPaintComponent() {
        // Тест отрисовки компонента
        // Настраиваем мок
        when(mockMovements.getPlayerX()).thenReturn(2);
        when(mockMovements.getPlayerY()).thenReturn(3);
        when(mockMovements.getCols()).thenReturn(20);
        when(mockMovements.getRows()).thenReturn(15);

        // Создаем реальный Graphics объект для тестирования
        BufferedImage bi = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();

        try {
            gameVisualizer.paintComponent(g2d);

            // Проверяем, что игрок отрисован в правильной позиции
            // Можно проверить пиксели в ожидаемой позиции
            int expectedX = 2 * gameVisualizer.getCellSize() + 2;
            int expectedY = 3 * gameVisualizer.getCellSize() + 2;

            // Простая проверка, что что-то нарисовано в этой области
            assertNotEquals(0, bi.getRGB(expectedX, expectedY));
        } finally {
            g2d.dispose();
        }
    }

    @Test
    void testGridSizeUpdate() {
        // Тест обновления размеров сетки
        when(mockMovements.getCols()).thenReturn(10);
        when(mockMovements.getRows()).thenReturn(10);

        gameVisualizer.setSize(400, 300);

        BufferedImage bi = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();

        try {
            gameVisualizer.paintComponent(g2d);

            int expectedCols = 400 / gameVisualizer.getCellSize();
            int expectedRows = 300 / gameVisualizer.getCellSize();
            verify(mockMovements).updateGridSize(expectedCols, expectedRows);
        } finally {
            g2d.dispose();
        }
    }

    @Test
    void testPreferredSize() {
        // тест размеров по умолчанию
        Dimension preferredSize = gameVisualizer.getPreferredSize();
        assertEquals(600, preferredSize.width);
        assertEquals(600, preferredSize.height);
    }

    @Test
    void testDrawGrid() throws Exception {
        // Тест отрисовки сетки

        // Создаем наследник для тестирования protected методов
        class TestGameVisualizer extends GameVisualizer {
            void publicDrawGrid(Graphics2D g, int cols, int rows) {
                super.drawGrid(g, cols, rows);
            }
        }

        TestGameVisualizer testVisualizer = new TestGameVisualizer();
        BufferedImage bi = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();

        try {
            testVisualizer.publicDrawGrid(g2d, 5, 5);

            // Проверяем, что фон белый
            assertEquals(Color.WHITE.getRGB(), bi.getRGB(10, 10));
        } finally {
            g2d.dispose();
        }
    }

    @Test
    void testDrawPlayer() throws Exception {
        // Тест отрисовки игрока

        // Создаем наследник для тестирования protected методов
        class TestGameVisualizer extends GameVisualizer {
            void publicDrawPlayer(Graphics2D g) {
                super.drawPlayer(g);
            }
        }

        TestGameVisualizer testVisualizer = new TestGameVisualizer();

        // Подменяем gameModel
        Field field = GameVisualizer.class.getDeclaredField("gameModel");
        field.setAccessible(true);
        Movements mockModel = mock(Movements.class);
        when(mockModel.getPlayerX()).thenReturn(1);
        when(mockModel.getPlayerY()).thenReturn(1);
        field.set(testVisualizer, mockModel);

        BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();

        try {
            testVisualizer.publicDrawPlayer(g2d);

            int expectedX = 1 * testVisualizer.getCellSize() + 2;
            int expectedY = 1 * testVisualizer.getCellSize() + 2;

            // Проверяем, что что-то нарисовано в этой позиции
            assertNotEquals(0, bi.getRGB(expectedX, expectedY));
        } finally {
            g2d.dispose();
        }
    }
}