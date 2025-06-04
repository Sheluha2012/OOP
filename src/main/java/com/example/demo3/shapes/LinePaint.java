package com.example.demo3.shapes;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.Map;

public class LinePaint extends ShapePaint {
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private Color color;
    private double strokeWidth;


    public LinePaint() {
        this.color = Color.BLACK;
        this.strokeWidth = 1.0;
        Line line = new Line();
        setJavaFXShape(line);
        addHandlers();
    }
    @Override
    public void setColor(Color color) {
        this.color = color;
        if (this.javafxShape != null) {
            this.javafxShape.setStroke(color);
        }
    }
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("startX", startX);
        map.put("startY", startY);
        map.put("endX", endX);
        map.put("endY", endY);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);
        this.startX = ((Number) map.get("startX")).doubleValue();
        this.startY = ((Number) map.get("startY")).doubleValue();
        this.endX = ((Number) map.get("endX")).doubleValue();
        this.endY = ((Number) map.get("endY")).doubleValue();
        this.color = Color.web((String) map.get("strokeColor"));
        this.strokeWidth = ((Number) map.get("strokeWidth")).doubleValue();
        draw();
    }
    @Override
    protected String getShapeType() {
        return "Line";
    }
    @Override
    public Shape draw() {
        Line line = (Line) this.javafxShape;
        line.setStartX(this.startX);
        line.setStartY(this.startY);
        line.setEndX(this.endX);
        line.setEndY(this.endY);
        line.setStrokeWidth(this.strokeWidth);
        line.setStroke(this.color);
        return line;
    }
    @Override
    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
        if (this.javafxShape != null) {
            this.javafxShape.setStrokeWidth(strokeWidth);
        }
    }
    @Override
    public void setStart(double x, double y) {
        this.startX = x;
        this.startY = y;
        draw();
    }

    @Override
    public void reset() {

    }

    public void setEnd(double x, double y) {
        this.endX = x;
        this.endY = y;
        draw();
    }

    @Override
    public void updateShape(double x, double y) {
        setEnd(x, y); // Обновляем конечную точку линии
    }

    @Override
    public void finalizeShape(double x, double y) {
        setEnd(x, y); // Завершаем рисование, устанавливая конечную точку
    }

    private void addHandlers() {
        this.javafxShape.setOnMousePressed(event -> {
            event.consume(); // Предотвращаем всплытие события
            this.onMousePressed(event);
        });
        this.javafxShape.setOnMouseDragged(event -> {
            event.consume(); // Предотвращаем всплытие события
            this.onMouseDragged(event);
        });
        this.javafxShape.setOnMouseReleased(event -> {
            event.consume(); // Предотвращаем всплытие события
            this.onMouseReleased(event);
        });
    }

    private void onMousePressed(MouseEvent event) {
        setStart(event.getX(), event.getY());
    }

    private void onMouseDragged(MouseEvent event) {
        updateShape(event.getX(), event.getY());
    }

    private void onMouseReleased(MouseEvent event) {
        finalizeShape(event.getX(), event.getY());
    }
}
