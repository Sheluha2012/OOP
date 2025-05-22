package com.example.lab2;

import com.example.lab2.shapes.*;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML
    public MenuItem undoButton;
    @FXML
    public MenuItem redoButton;
    @FXML
    Ellipse buttonEllipse;
    @FXML
    Line buttonLine;
    @FXML
    Rectangle buttonRectangle;
    @FXML
    Polygon buttonPolygon;
    @FXML
    Polyline buttonPolyline;
    @FXML
    Pane drawPane;
    @FXML
    ColorPicker strokePicker, fillPicker;
    @FXML
    private Spinner<Integer> sidesAmountSpinner;
    @FXML
    private Spinner<Double> strokeWidthSpinner;
    @FXML
    private Label sidesLabel;

    private String currShape = null;
    double startX;
    double startY;
    Shape previewShape = null;
    ShapePaint currentShapeTool;
    List<Shape> shapes = new ArrayList<>();
    boolean drawingPolyline = false;
    PolylinePaint polylineTool = null;
    ShapeHistoryManager shapeHistoryManager = new ShapeHistoryManager();

    @FXML
    void unselectButtons() {
        buttonEllipse.setStrokeWidth(1);
        buttonEllipse.setStroke(Color.BLACK);
        buttonLine.setStrokeWidth(3);
        buttonLine.setStroke(Color.BLACK);
        buttonPolygon.setStrokeWidth(1);
        buttonPolygon.setStroke(Color.BLACK);
        buttonRectangle.setStrokeWidth(1);
        buttonRectangle.setStroke(Color.BLACK);
        buttonPolyline.setStrokeWidth(3);
        buttonPolyline.setStroke(Color.BLACK);
        drawingPolyline = false;
        sidesLabel.setVisible(false);
        sidesAmountSpinner.setVisible(false);
    }

    @FXML
    void onEllipseClick() {
        unselectButtons();
        buttonEllipse.setStrokeWidth(5);
        buttonEllipse.setStroke(Color.BLUE);
        currShape = "Ellipse";
    }

    @FXML
    void onLineClick() {
        unselectButtons();
        buttonLine.setStrokeWidth(7);
        buttonLine.setStroke(Color.ORANGE);
        currShape = "Line";
    }

    @FXML
    void onPolygonClick() {
        unselectButtons();
        buttonPolygon.setStrokeWidth(5);
        buttonPolygon.setStroke(Color.GREEN);
        currShape = "Polygon";
        sidesLabel.setVisible(true);
        sidesAmountSpinner.setVisible(true);

    }

    @FXML
    void onRectangleClick() {
        unselectButtons();
        buttonRectangle.setStrokeWidth(5);
        buttonRectangle.setStroke(Color.RED);
        currShape = "Rectangle";
    }

    @FXML
    void onPolylineClick() {
        unselectButtons();
        buttonPolyline.setStrokeWidth(5);
        buttonPolyline.setStroke(Color.PURPLE);
        currShape = "Polyline";
    }

    @FXML
    void onPaneMousePressed(MouseEvent event) {
        if (currShape == null) return;
        startX = event.getX();
        startY = event.getY();
        if ("Polyline".equals(currShape)) {
            if (!drawingPolyline) {
                polylineTool = new PolylinePaint();
                polylineTool.setStrokeColor(strokePicker.getValue());
                polylineTool.setFillColor(fillPicker.getValue());
                polylineTool.setStrokeWidth(strokeWidthSpinner.getValue());
                polylineTool.addPoint(event.getX(), event.getY());
                previewShape = polylineTool.createShape(event.getX(), event.getY(), event.getX(), event.getY());
                drawPane.getChildren().add(previewShape);

                drawingPolyline = true;
            } else {
                polylineTool.addPoint(event.getX(), event.getY());
                Line newLine = polylineTool.getLines().get(polylineTool.getLines().size() - 1);
                drawPane.getChildren().add(newLine);
                if (event.isSecondaryButtonDown()) {
                    drawPane.getChildren().remove(previewShape);
                    previewShape = null;

                    Group polylineGroup = new Group();

                    for (Line line : polylineTool.getLines()) {
                        drawPane.getChildren().remove(line);
                        polylineGroup.getChildren().add(line);
                    }

                    drawPane.getChildren().add(polylineGroup);
                    shapeHistoryManager.addShape(polylineGroup, drawPane);

                    polylineTool = null;
                    drawingPolyline = false;
                }

            }
            return;
        }
        currentShapeTool = ShapeFactory.create(currShape);
        currentShapeTool.setStrokeColor(strokePicker.getValue());
        currentShapeTool.setFillColor(fillPicker.getValue());
        currentShapeTool.setStrokeWidth(strokeWidthSpinner.getValue());

        if (currentShapeTool instanceof PolygonPaint polygonPaint) {
            Integer sides = sidesAmountSpinner.getValue();
            if (sides != null) polygonPaint.setSides(sides);
        }
        previewShape = currentShapeTool.createShape(startX, startY, startX, startY);
        drawPane.getChildren().add(previewShape);
    }

    @FXML
    void onPaneMouseDragged(MouseEvent event) {
        if (!drawPane.contains(event.getX(), event.getY())) return;
        if ("Polyline".equals(currShape) && drawingPolyline) {
            if (previewShape != null) {
                drawPane.getChildren().remove(previewShape);
            }
            previewShape = polylineTool.createShape(
                    polylineTool.getLastX(), polylineTool.getLastY(),
                    event.getX(), event.getY()
            );
            drawPane.getChildren().add(previewShape);
            return;
        }
        if (currentShapeTool != null) {
            if (currentShapeTool instanceof PolygonPaint polygonPaint) {
                Integer sides = sidesAmountSpinner.getValue();
                if (sides != null) polygonPaint.setSides(sides);
            }
            if (previewShape != null) {
                drawPane.getChildren().remove(previewShape);
            }
            previewShape = currentShapeTool.createShape(startX, startY, event.getX(), event.getY());
            drawPane.getChildren().add(previewShape);
        }
    }

    @FXML
    void onMouseReleased(MouseEvent event) {
        if ("Polyline".equals(currShape)) return;
        if (currentShapeTool != null && previewShape != null) {
            drawPane.getChildren().remove(previewShape);
            Shape finalShape = currentShapeTool.createShape(startX, startY, event.getX(), event.getY());

            shapeHistoryManager.addShape(finalShape, drawPane);
            previewShape = null;
        }
    }

    @FXML
    void handleUndo() {
        shapeHistoryManager.undo(drawPane);
    }

    @FXML
    void handleRedo() {
        shapeHistoryManager.redo(drawPane);

    }

    @FXML
    void handleClear() {
        shapeHistoryManager.clear(drawPane);
    }

}
