package gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GameVisualizerTest {

    private GameVisualizer gameVisualizer;

    @BeforeEach
    void setUp() {
        gameVisualizer = new GameVisualizer();
        gameVisualizer.setSize(300, 300);
        gameVisualizer.setVisible(true);
    }

    @Test
    void testRobotDoesNotMoveOutsideBounds() {
        gameVisualizer.setTargetPosition(new Point(400, 400));

        gameVisualizer.onModelUpdateEvent();

        double robotX = gameVisualizer.m_robotPositionX;
        double robotY = gameVisualizer.m_robotPositionY;

        assertTrue(robotX >= 0 && robotX <= gameVisualizer.getWidth() - 1);
        assertTrue(robotY >= 0 && robotY <= gameVisualizer.getHeight() - 1);
    }
}