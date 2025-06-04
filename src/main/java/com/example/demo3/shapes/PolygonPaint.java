package com.example.demo3.shapes;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.Map;
import java.util.Optional;

public class PolygonPaint extends ShapePaint {
    private double centerX;
    private double centerY;
    private double radius;
    private int sides;
    private Color color;
    private double strokeWidth;
    private static int defaultSides = 0;

    public PolygonPaint() {
        this.color = Color.BLACK;
        this.strokeWidth = 1.0;
        this.sides = defaultSides;
        Polygon polygon = new Polygon();
        setJavaFXShape(polygon);
        addHandlers();
    }
    private static boolean showSidesDialogIfNeeded() {
        if (defaultSides == 0) {
            TextInputDialog dialog = new TextInputDialog("5");
            dialog.setTitle("Polygon Settings");
            dialog.setHeaderText("Enter number of sides");
            dialog.setContentText("Sides (3-20):");

            try {
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    int sides = Integer.parseInt(result.get());
                    if (sides < 3 || sides > 20) {
                        showAlert("Number of sides must be between 3 and 20");
                        return false;
                    } else {
                        defaultSides = sides;
                        return true;
                    }
                }
            } catch (NumberFormatException e) {
                showAlert("Please enter a valid number");
                return false;
            }
            return false;
        }
        return true;
    }
    @Override
    protected String getShapeType() {
        return "Polygon";
    }
    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static void setDefaultSides(int sides) {
        defaultSides = Math.max(3, Math.min(20, sides));
    }

    @Override
    public Shape draw() {
        if (!showSidesDialogIfNeeded()) {
            return null;
        }

        Polygon polygon = (Polygon) this.javafxShape;
        polygon.getPoints().clear();
        double initialAngle = -Math.PI / 2;

        for (int i = 0; i < sides; i++) {
            double angle = initialAngle + 2 * Math.PI * i / sides;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            polygon.getPoints().addAll(x, y);
        }

        polygon.setFill(this.getFillColor());
        polygon.setStrokeWidth(strokeWidth);
        polygon.setStroke(color);
        return polygon;
    }
    @Override
    public void setColor(Color color) {
        this.color = color;
        if (this.javafxShape != null) {
            this.javafxShape.setStroke(color);
        }
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
        this.centerX = x;
        this.centerY = y;
        this.radius = 0;
        draw();
    }

    @Override
    public void reset() {
        this.radius = 0;
        draw();
    }

    @Override
    public void updateShape(double x, double y) {
        this.radius = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        draw();
    }

    @Override
    public void finalizeShape(double x, double y) {
        this.radius = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        draw();
    }

    private void addHandlers() {
        this.javafxShape.setOnMousePressed(event -> {
            event.consume();
            this.onMousePressed(event);
        });
        this.javafxShape.setOnMouseDragged(event -> {
            event.consume();
            this.onMouseDragged(event);
        });
        this.javafxShape.setOnMouseReleased(event -> {
            event.consume();
            this.onMouseReleased(event);
        });
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("centerX", centerX);
        map.put("centerY", centerY);
        map.put("radius", radius);
        map.put("sides", sides);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        super.fromMap(map);
        this.centerX = ((Number) map.get("centerX")).doubleValue();
        this.centerY = ((Number) map.get("centerY")).doubleValue();
        this.radius = ((Number) map.get("radius")).doubleValue();
        this.sides = ((Number) map.get("sides")).intValue();
        this.color = Color.web((String) map.get("strokeColor"));
        this.strokeWidth = ((Number) map.get("strokeWidth")).doubleValue();
        draw();
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