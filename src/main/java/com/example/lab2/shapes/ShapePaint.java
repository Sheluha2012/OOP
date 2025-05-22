package com.example.lab2.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public abstract class ShapePaint {
    protected Color strokeColor;
    protected Color fillColor;
    protected double strokeWidth;

    public abstract Shape createShape(double startX, double startY, double endX, double endY);

    public void applyStyle(Shape shape) {
        shape.setFill(fillColor);
        shape.setStroke(strokeColor);
        shape.setStrokeWidth(strokeWidth);
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(double width) {
        this.strokeWidth = width;
    }

}