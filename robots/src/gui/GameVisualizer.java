package gui;

import mechanics.Movements;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GameVisualizer extends JPanel {
    private final Movements gameModel;
    private int cellSize = 40;
    private BufferedImage playerSprite;

    public GameVisualizer() {
        this.gameModel = new Movements();

        // Загрузка спрайта
        try {
            // Пытаемся загрузить из ресурсов
            playerSprite = ImageIO.read(getClass().getResourceAsStream("/resources/character.png"));

            if (playerSprite != null) {
                playerSprite = scaleImage(playerSprite, cellSize - 4, cellSize - 4);
            } else {
                System.err.println("Не удалось загрузить спрайт игрока. Будет использован стандартный прямоугольник.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            playerSprite = null;
        }

        // Обработчик клавиатуры
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                gameModel.handleKeyPress(e);
                repaint();
            }
        });

        setDoubleBuffered(true);
        setFocusable(true);
        requestFocusInWindow(); // Добавляем фокус на панель
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Рассчитываем количество клеток
        int cols = getWidth() / cellSize;
        int rows = getHeight() / cellSize;
        gameModel.updateGridSize(cols, rows);

        // Отрисовка клетчатого фона
        drawGrid(g2d, cols, rows);

        // Отрисовка игрока
        drawPlayer(g2d);
    }

    void drawGrid(Graphics2D g, int cols, int rows) {
        // Заливаем фон белым цветом
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, cols * cellSize, rows * cellSize);

        // Рисуем сетку
        g.setColor(Color.LIGHT_GRAY);

        // Вертикальные линии
        for (int i = 0; i <= cols; i++) {
            int x = i * cellSize;
            g.drawLine(x, 0, x, rows * cellSize);
        }

        // Горизонтальные линии
        for (int i = 0; i <= rows; i++) {
            int y = i * cellSize;
            g.drawLine(0, y, cols * cellSize, y);
        }
    }

    void drawPlayer(Graphics2D g) {
        int x = gameModel.getPlayerX() * cellSize + 2;
        int y = gameModel.getPlayerY() * cellSize + 2;

        if (playerSprite != null) {
            g.drawImage(playerSprite, x, y, null);
        } else {
            // Fallback - рисуем прямоугольник
            g.setColor(new Color(0, 128, 255, 200));
            g.fillRect(x, y, cellSize - 4, cellSize - 4);
            g.setColor(Color.BLUE);
            g.drawRect(x, y, cellSize - 4, cellSize - 4);
        }
    }

    BufferedImage scaleImage(BufferedImage original, int width, int height) {
        if (original == null) return null;

        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();
        return scaled;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }

    public int getCellSize() {
        return cellSize;
    }
}