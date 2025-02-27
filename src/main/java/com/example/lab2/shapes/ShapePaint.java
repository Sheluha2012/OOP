package com.example.lab2.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class ShapePaint {
    private Color color;
    private double width;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public abstract void draw(GraphicsContext gc);
}
