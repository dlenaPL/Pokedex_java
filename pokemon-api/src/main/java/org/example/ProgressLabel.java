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
        setOpaque(false); // to ensure custom painting is visible
    }

    public void setFillWidth(int fillWidth) {
        this.fillWidth = fillWidth;
        repaint(); // repaint the label to show the updated fill width
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        repaint(); // repaint the label to show the updated fill color
    }

    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;

        // Fill the entire background with the default background color
        graphics.setColor(Color.decode("#F0F0F0"));
        graphics.fillRect(0, 0, width, height);

        // Fill the specified width with the fill color
        graphics.setColor(fillColor);
        graphics.fillRect(0, 0, fillWidth, height);

        // Draw the text
        super.paintComponent(g);
    }

}