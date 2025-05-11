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
    private BufferedImage playerSprite;
    private int fixedCols = 15;
    private int fixedRows = 15;
    private int cellSize;
    private int gridWidth;
    private int gridHeight;
    private int xOffset;
    private int yOffset;

    public GameVisualizer() {
        this.gameModel = new Movements();
        gameModel.updateGridSize(fixedCols, fixedRows);

        try {
            playerSprite = ImageIO.read(getClass().getResourceAsStream("/resources/character.png"));
        } catch (IOException e) {
            e.printStackTrace();
            playerSprite = null;
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateGridDimensions();
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                gameModel.handleKeyPress(e);
                repaint();
            }
        });

        setDoubleBuffered(true);
        setFocusable(true);
        requestFocusInWindow();
    }

    void calculateGridDimensions() {
        int availableWidth = getWidth();
        int availableHeight = getHeight();

        // Рассчитываем максимально возможный размер клетки
        int maxCellWidth = availableWidth / fixedCols;
        int maxCellHeight = availableHeight / fixedRows;
        cellSize = Math.min(maxCellWidth, maxCellHeight);

        // Гарантируем, что клетки не будут меньше минимального размера
        cellSize = Math.max(cellSize, 10);

        // Рассчитываем общий размер сетки
        gridWidth = fixedCols * cellSize;
        gridHeight = fixedRows * cellSize;

        // Центрируем сетку, если она меньше доступной области
        xOffset = Math.max(0, (availableWidth - gridWidth) / 2);
        yOffset = Math.max(0, (availableHeight - gridHeight) / 2);

        // Корректируем размеры, если сетка выходит за границы
        if (gridWidth > availableWidth) {
            cellSize = availableWidth / fixedCols;
            gridWidth = fixedCols * cellSize;
            xOffset = 0;
        }

        if (gridHeight > availableHeight) {
            cellSize = availableHeight / fixedRows;
            gridHeight = fixedRows * cellSize;
            yOffset = 0;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Пересчитываем размеры при каждой отрисовке
        calculateGridDimensions();

        // Отрисовка клетчатого фона
        drawGrid(g2d);

        // Отрисовка игрока
        drawPlayer(g2d);
    }

    void drawGrid(Graphics2D g) {
        // Заливаем фон белым цветом
        g.setColor(Color.WHITE);
        g.fillRect(xOffset, yOffset, gridWidth, gridHeight);

        // Рисуем сетку
        g.setColor(Color.LIGHT_GRAY);

        // Вертикальные линии
        for (int i = 0; i <= fixedCols; i++) {
            int x = xOffset + i * cellSize;
            g.drawLine(x, yOffset, x, yOffset + gridHeight);
        }

        // Горизонтальные линии
        for (int i = 0; i <= fixedRows; i++) {
            int y = yOffset + i * cellSize;
            g.drawLine(xOffset, y, xOffset + gridWidth, y);
        }
    }

    void drawPlayer(Graphics2D g) {
        int x = xOffset + gameModel.getPlayerX() * cellSize + 2;
        int y = yOffset + gameModel.getPlayerY() * cellSize + 2;
        int spriteSize = cellSize - 4;

        if (playerSprite != null) {
            BufferedImage scaledSprite = scaleImage(playerSprite, spriteSize, spriteSize);
            g.drawImage(scaledSprite, x, y, null);
        } else {
            g.setColor(new Color(0, 128, 255, 200));
            g.fillRect(x, y, spriteSize, spriteSize);
            g.setColor(Color.BLUE);
            g.drawRect(x, y, spriteSize, spriteSize);
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
        int baseCellSize = 40;
        return new Dimension(fixedCols * baseCellSize, fixedRows * baseCellSize);
    }

    public int getFixedCols() { return fixedCols; }
    public int getFixedRows() { return fixedRows; }
    public int getCellSize() { return cellSize; }
    public int getGridWidth() { return gridWidth; }
    public int getGridHeight() { return gridHeight; }
    public int getXOffset() { return xOffset; }
    public int getYOffset() { return yOffset; }

    static GameVisualizer createForTest(int cellSize, int gridWidth,
                                        int gridHeight, int xOffset, int yOffset) {
        GameVisualizer instance = new GameVisualizer();
        instance.cellSize = cellSize;
        instance.gridWidth = gridWidth;
        instance.gridHeight = gridHeight;
        instance.xOffset = xOffset;
        instance.yOffset = yOffset;
        return instance;
    }
}