package mechanics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.awt.event.KeyEvent;
import static org.junit.jupiter.api.Assertions.*;

class MovementsTest {
    private Movements movements;
    private Component dummyComponent = new Panel(); // Фиктивный компонент

    @BeforeEach
    void setUp() {
        movements = new Movements();
    }

    @Test
    void testInitialPosition() {
        assertEquals(0, movements.getPlayerX());
        assertEquals(0, movements.getPlayerY());
    }

    @Test
    void testMoveRight() {
        movements.handleKeyPress(new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, 'D'));
        assertEquals(1, movements.getPlayerX());
        assertEquals(0, movements.getPlayerY());
    }

    @Test
    void testMoveLeft() {
        movements.handleKeyPress(new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, 'A'));
        assertEquals(0, movements.getPlayerX());
        assertEquals(0, movements.getPlayerY());
    }

    @Test
    void testMoveDown() {
        movements.handleKeyPress(new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, 'S'));
        assertEquals(0, movements.getPlayerX());
        assertEquals(1, movements.getPlayerY());
    }

    @Test
    void testMoveUp() {
        // Сначала двигаемся вниз
        movements.handleKeyPress(new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, 'S'));
        // Затем вверх
        movements.handleKeyPress(new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_UP, 'W'));
        assertEquals(0, movements.getPlayerX());
        assertEquals(0, movements.getPlayerY());
    }

    @Test
    void testMoveBeyondBoundaries() {
        // Пытаемся выйти за верхнюю/левую границу
        for (int i = 0; i < 5; i++) {
            movements.handleKeyPress(new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                    System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, 'A'));
            movements.handleKeyPress(new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                    System.currentTimeMillis(), 0, KeyEvent.VK_UP, 'W'));
        }
        assertEquals(0, movements.getPlayerX());
        assertEquals(0, movements.getPlayerY());

        // Пытаемся выйти за правую/нижнюю границу
        movements.updateGridSize(5, 5); // Уменьшаем сетку
        for (int i = 0; i < 10; i++) {
            movements.handleKeyPress(new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                    System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, 'D'));
            movements.handleKeyPress(new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                    System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, 'S'));
        }
        assertEquals(4, movements.getPlayerX());
        assertEquals(4, movements.getPlayerY());
    }

    @Test
    void testGridResize() {
        // Двигаем игрока
        movements.handleKeyPress(new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, 'D'));
        movements.handleKeyPress(new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, 'S'));

        // Уменьшаем сетку так, что игрок оказывается за границей
        movements.updateGridSize(1, 1);
        assertEquals(0, movements.getPlayerX());
        assertEquals(0, movements.getPlayerY());

        // Увеличиваем сетку - позиция должна сохраниться
        movements.updateGridSize(10, 10);
        assertEquals(0, movements.getPlayerX());
        assertEquals(0, movements.getPlayerY());
    }

    @Test
    void testAlternativeKeyBindings() {
        // Двигаем вниз, чтобы можно было двигаться вверх
        movements.handleKeyPress(createKeyEvent(KeyEvent.VK_S));
        assertEquals(0, movements.getPlayerX());
        assertEquals(1, movements.getPlayerY());

        // Теперь двигаем вверх
        movements.handleKeyPress(createKeyEvent(KeyEvent.VK_W));
        assertEquals(0, movements.getPlayerX());
        assertEquals(0, movements.getPlayerY()); // Ожидаем 0, а не -1

        // Аналогично для других направлений
        movements.handleKeyPress(createKeyEvent(KeyEvent.VK_D));
        assertEquals(1, movements.getPlayerX());
        assertEquals(0, movements.getPlayerY());

        movements.handleKeyPress(createKeyEvent(KeyEvent.VK_A));
        assertEquals(0, movements.getPlayerX()); // Ожидаем 0, а не -1
        assertEquals(0, movements.getPlayerY());
    }

    private KeyEvent createKeyEvent(int keyCode) {
        return new KeyEvent(dummyComponent, KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, keyCode, (char)keyCode);
    }
}