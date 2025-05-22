package com.example.lab2.shapes;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class PolygonPaint extends ShapePaint {
    private int sides = 5;

    public void setSides(int sides) {
        if (sides >= 3) {
            this.sides = sides;
        }
    }

    @Override
    public Shape createShape(double startX, double startY, double endX, double endY) {
        double centerX = (startX + endX) / 2;
        double centerY = (startY + endY) / 2;
        double radius = Math.min(Math.abs(endX - startX), Math.abs(endY - startY)) / 2;

        Polygon polygon = new Polygon();
        for (int i = 0; i < sides; i++) {
            double angle = 2 * Math.PI * i / sides - Math.PI / 2;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            polygon.getPoints().addAll(x, y);
        }
        applyStyle(polygon);
        return polygon;
    }
}
