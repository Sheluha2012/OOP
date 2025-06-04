package com.example.demo3.shapes;

import com.example.demo3.Controller;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.HashMap;
import java.util.Map;

public abstract class ShapePaint {
    protected Shape javafxShape;
    private Color fillColor = Color.TRANSPARENT;
    private Color strokeColor = Color.BLACK; // Добавлено поле для цвета обводки
    private double strokeWidth = 1.0; // Добавлено поле для толщины обводки
    private boolean resizing = false;
    private boolean moving = false;
    private boolean isBeingCreated = true;
    private double mouseX;
    private double mouseY;
    private double initialX;
    private double initialY;
    private double initialWidth;
    private double initialHeight;

    public ShapePaint() {
    }

    public void setSelected(boolean selected) {
        if (this.javafxShape != null) {
            if (selected) {
                this.javafxShape.setStrokeWidth(this.javafxShape.getStrokeWidth() * 2);
            } else {
                this.javafxShape.setStrokeWidth(this.strokeWidth); // Восстанавливаем исходную толщину
            }
        }
    }
    public boolean isPluginShape() {
        return false;
    }
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", getShapeType());
        map.put("fillColor", fillColor);
        map.put("strokeColor", strokeColor);
        map.put("strokeWidth", strokeWidth);
        return map;
    }

    public void fromMap(Map<String, Object> map) {
        this.fillColor = Color.web((String) map.get("fillColor"));
        this.strokeColor = Color.web((String) map.get("strokeColor"));
        this.strokeWidth = ((Number) map.get("strokeWidth")).doubleValue();

        if (javafxShape != null) {
            updateShapeProperties();
        }
    }
    protected abstract String getShapeType();

    protected void updateShapeProperties() {
        javafxShape.setFill(fillColor);
        javafxShape.setStroke(strokeColor);
        javafxShape.setStrokeWidth(strokeWidth);
    }
    private String colorToString(Color color) {
        if (color == null) {
            return colorToString(Color.TRANSPARENT);
        }
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private Color stringToColor(String colorStr) {
        try {
            return Color.web(colorStr);
        } catch (IllegalArgumentException e) {
            return Color.TRANSPARENT;
        }
    }

    public Color getFillColor() {
        return this.fillColor;
    }

    public Color getStrokeColor() {
        return this.strokeColor;
    }

    public void applyFill(Color fillColor) {
        this.fillColor = fillColor != null ? fillColor : Color.TRANSPARENT;
        if (this.javafxShape != null) {
            this.javafxShape.setFill(this.fillColor);
        }
    }


    public abstract Shape draw();

    public void setFillColor(Color color) {
        this.fillColor = color != null ? color : Color.TRANSPARENT;
        if (this.javafxShape != null) {
            this.javafxShape.setFill(this.fillColor);
        }
    }

    public void setColor(Color color) {
        this.strokeColor = color != null ? color : Color.BLACK;
        if (this.javafxShape != null) {
            this.javafxShape.setStroke(this.strokeColor);
        }
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = Math.max(0, strokeWidth);
        if (this.javafxShape != null) {
            this.javafxShape.setStrokeWidth(this.strokeWidth);
        }
    }

    private void addHandlers() {
        if (this.javafxShape == null) return;

        this.javafxShape.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                ShapePaint prevSelected = Controller.getSelectedShape();
                if (prevSelected != null) {
                    prevSelected.setSelected(false);
                }

                this.setSelected(true);
                Controller.setSelectedShape(this);
                event.consume();
            }
        });

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
            this.resizing = false;
            this.moving = false;
            this.isBeingCreated = false;
        });
    }

    private void onMousePressed(MouseEvent event) {
        if (this.javafxShape == null) return;

        this.mouseX = event.getSceneX();
        this.mouseY = event.getSceneY();
        this.initialX = this.javafxShape.getTranslateX();
        this.initialY = this.javafxShape.getTranslateY();
        this.initialWidth = this.javafxShape.getBoundsInLocal().getWidth();
        this.initialHeight = this.javafxShape.getBoundsInLocal().getHeight();

        if (event.isShiftDown()) {
            this.moving = true;
        } else if (this.isBeingCreated || this.isOnBorder(event)) {
            this.resizing = true;
        }
    }

    private void onMouseDragged(MouseEvent event) {
        if (this.javafxShape == null) return;

        if (this.resizing && this.isBeingCreated) {
            resize(event);
        } else if (this.moving) {
            double deltaX = event.getSceneX() - this.mouseX;
            double deltaY = event.getSceneY() - this.mouseY;
            this.javafxShape.setTranslateX(this.initialX + deltaX);
            this.javafxShape.setTranslateY(this.initialY + deltaY);
        }
    }

    private boolean isOnBorder(MouseEvent event) {
        if (this.javafxShape == null) return false;

        double border = 10.0;
        double x = event.getX();
        double y = event.getY();
        return x <= border || x >= this.javafxShape.getBoundsInLocal().getWidth() - border ||
                y <= border || y >= this.javafxShape.getBoundsInLocal().getHeight() - border;
    }

    protected void setJavaFXShape(Shape shape) {
        this.javafxShape = shape;
        if (this.javafxShape != null) {
            this.javafxShape.setFill(this.fillColor);
            this.javafxShape.setStroke(this.strokeColor);
            this.javafxShape.setStrokeWidth(this.strokeWidth);
            this.addHandlers();
        }
    }

    private void resize(MouseEvent event) {
        if (this.javafxShape == null) return;

        double deltaX = event.getSceneX() - this.mouseX;
        double deltaY = event.getSceneY() - this.mouseY;

        double newWidth = Math.max(0, this.initialWidth + deltaX);
        double newHeight = Math.max(0, this.initialHeight + deltaY);

        this.javafxShape.setScaleX(newWidth / this.initialWidth);
        this.javafxShape.setScaleY(newHeight / this.initialHeight);
    }

    public void finishCreation() {
        this.isBeingCreated = false;
    }

    public abstract void updateShape(double x, double y);
    public abstract void finalizeShape(double x, double y);
    public abstract void setStart(double x, double y);
    public abstract void reset();
}