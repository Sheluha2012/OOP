package com.example.lab2.shapes;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class LinePaint extends ShapePaint {
    @Override
    public Shape createShape(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        applyStyle(line);
        return line;
    }
}
