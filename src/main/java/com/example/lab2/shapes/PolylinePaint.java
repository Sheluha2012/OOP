package com.example.lab2.shapes;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class PolylinePaint extends ShapePaint {
    private final List<Line> lines = new ArrayList<>();
    private double lastX, lastY;
    private boolean isFirstPoint = true;

    @Override
    public Shape createShape(double startX, double startY, double endX, double endY) {
        Line previewLine = new Line(startX, startY, endX, endY);
        applyStyle(previewLine);
        return previewLine;
    }

    public void addPoint(double x, double y) {
        if (isFirstPoint) {
            // Первая точка - просто запоминаем
            lastX = x;
            lastY = y;
            isFirstPoint = false;
        } else {
            Line newLine = new Line(lastX, lastY, x, y);
            applyStyle(newLine);
            lines.add(newLine);
            lastX = x;
            lastY = y;
        }
    }

    public Line createPreviewLine(double endX, double endY) {
        if (isFirstPoint) {
            return null;
        }
        Line preview = new Line(lastX, lastY, endX, endY);
        applyStyle(preview);
        return preview;
    }

    public List<Line> getLines() {
        return lines;
    }

    public boolean hasLines() {
        return !lines.isEmpty();
    }

    public void reset() {
        lines.clear();
        isFirstPoint = true;
    }

    public boolean isFirstPoint() {
        return isFirstPoint;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastY() {
        return lastY;
    }
}