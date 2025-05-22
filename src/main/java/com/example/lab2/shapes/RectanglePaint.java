package com.example.lab2.shapes;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RectanglePaint extends ShapePaint {
    @Override
    public Shape createShape(double startX, double startY, double endX, double endY) {
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);
        Rectangle rectangle = new Rectangle(x, y, width, height);
        applyStyle(rectangle);
        return rectangle;
    }
}
