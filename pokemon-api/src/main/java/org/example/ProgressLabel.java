package org.example;

import javax.swing.*;
import java.awt.*;

public class ProgressLabel extends JLabel {
    private int fillWidth;
    private Color fillColor;

    public ProgressLabel(String text, int fillWidth, Color fillColor) {
        super(text);
        this.fillWidth = fillWidth;
        this.fillColor = fillColor;
        setOpaque(false);
    }

    public void setFillWidth(int fillWidth) {
        this.fillWidth = fillWidth;
        repaint();
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;

        graphics.setColor(Color.decode("#F0F0F0"));
        graphics.fillRect(0, 0, width, height);

        graphics.setColor(fillColor);
        graphics.fillRect(0, 0, fillWidth, height);

        super.paintComponent(g);
    }

}