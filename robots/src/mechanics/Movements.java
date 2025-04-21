package mechanics;

import java.awt.event.KeyEvent;

public class Movements {
    private int playerX = 0;
    private int playerY = 0;
    private int cols = 15;
    private int rows = 15;

    public void updateGridSize(int newCols, int newRows) {
        // Корректируем позицию игрока при уменьшении сетки
        if (playerX >= newCols) playerX = newCols - 1;
        if (playerY >= newRows) playerY = newRows - 1;

        this.cols = newCols;
        this.rows = newRows;
    }

    public void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:    movePlayer(0, -1); break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:  movePlayer(0, 1); break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:  movePlayer(-1, 0); break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT: movePlayer(1, 0); break;
        }
    }

    private void movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;

        if (newX >= 0 && newX < cols && newY >= 0 && newY < rows) {
            playerX = newX;
            playerY = newY;
        }
    }

    // Геттеры
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
}