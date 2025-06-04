package com.example.demo3.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import java.util.*;

public class PolylinePaint extends ShapePaint {
    private final List<Line> lines = new ArrayList<>();
    private double lastX, lastY;
    private boolean isFirstPoint = true;
    private Color color;
    private double strokeWidth;
    private boolean isFinalized = false;

    public PolylinePaint() {
        this.color = Color.BLACK;
        this.strokeWidth = 1.0;
    }

    @Override
    public Shape draw() {
        return null; // Polyline состоит из множества Line, поэтому возвращаем null
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        List<Map<String, Double>> points = new ArrayList<>();

        if (!lines.isEmpty()) {
            // Сохраняем первую точку
            Map<String, Double> firstPoint = new HashMap<>();
            firstPoint.put("x", lines.get(0).getStartX());
            firstPoint.put("y", lines.get(0).getStartY());
            points.add(firstPoint);

            // Сохраняем все последующие точки
            for (Line line : lines) {
                Map<String, Double> point = new HashMap<>();
                point.put("x", line.getEndX());
                point.put("y", line.getEndY());
                points.add(point);
            }
        }

        map.put("points", points);
        map.put("isFinalized", isFinalized);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        // Обработка базовых свойств с проверкой на null
        if (map == null) return;

        try {
            // Обработка цвета с проверкой на null
            Object strokeColorObj = map.get("strokeColor");
            if (strokeColorObj instanceof String) {
                this.color = Color.web((String) strokeColorObj);
            }

            // Обработка толщины линии с проверкой на null
            Object strokeWidthObj = map.get("strokeWidth");
            if (strokeWidthObj instanceof Number) {
                this.strokeWidth = ((Number) strokeWidthObj).doubleValue();
            }

            // Обработка флага завершения
            Object isFinalizedObj = map.get("isFinalized");
            if (isFinalizedObj instanceof Boolean) {
                this.isFinalized = (Boolean) isFinalizedObj;
            }

            // Очищаем текущие данные
            lines.clear();
            isFirstPoint = true;

            // Восстанавливаем точки
            Object pointsObj = map.get("points");
            if (pointsObj instanceof List<?> pointsList) {

                for (Object pointObj : pointsList) {
                    if (pointObj instanceof Map<?, ?> pointMap) {

                        Double x = getDoubleValue(pointMap, "x");
                        Double y = getDoubleValue(pointMap, "y");

                        if (x != null && y != null) {
                            if (isFirstPoint) {
                                lastX = x;
                                lastY = y;
                                isFirstPoint = false;
                            } else {
                                Line line = createLine(lastX, lastY, x, y);
                                lines.add(line);
                                lastX = x;
                                lastY = y;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading PolylineShape: " + e.getMessage());
            e.printStackTrace();
            // В случае ошибки очищаем данные
            clearAll();
        }
    }

    // Вспомогательный метод для безопасного получения double значения
    private Double getDoubleValue(Map<?, ?> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }

    // Остальные методы класса остаются без изменений
    public void clearAll() {
        lines.clear();
        lastX = 0;
        lastY = 0;
        isFirstPoint = true;
        isFinalized = false;
    }

    public void addPoint(double x, double y) {
        if (isFinalized) return;

        if (isFirstPoint) {
            lastX = x;
            lastY = y;
            isFirstPoint = false;
        } else {
            Line newLine = createLine(lastX, lastY, x, y);
            lines.add(newLine);
            lastX = x;
            lastY = y;
        }
    }

    private Line createLine(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(color);
        line.setStrokeWidth(strokeWidth);
        return line;
    }

    public Line createPreviewLine(double endX, double endY) {
        if (isFirstPoint || isFinalized) {
            return null;
        }
        return createLine(lastX, lastY, endX, endY);
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        for (Line line : lines) {
            line.setStroke(color);
        }
    }

    @Override
    protected String getShapeType() {
        return "Polyline";
    }

    @Override
    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
        for (Line line : lines) {
            line.setStrokeWidth(strokeWidth);
        }
    }

    @Override
    public void updateShape(double x, double y) {}

    @Override
    public void finalizeShape(double x, double y) {
        this.isFinalized = true;
    }

    @Override
    public void setStart(double x, double y) {
        addPoint(x, y);
    }

    @Override
    public void reset() {
        clearAll();
    }

    public boolean isFinalized() {
        return isFinalized;
    }

    public List<Line> getLines() {
        return new ArrayList<>(lines);
    }

    public boolean hasLines() {
        return !lines.isEmpty();
    }
}