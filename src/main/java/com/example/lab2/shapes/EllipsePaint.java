package com.example.lab2.shapes;

import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

public class EllipsePaint extends ShapePaint {
    @Override
    public Shape createShape(double startX, double startY, double endX, double endY) {
        double centerX = (startX + endX) / 2;
        double centerY = (startY + endY) / 2;
        double radiusX = Math.abs(endX - startX) / 2;
        double radiusY = Math.abs(endY - startY) / 2;
        Ellipse ellipse = new Ellipse(centerX, centerY, radiusX, radiusY);
        applyStyle(ellipse);
        return ellipse;
    }
}
