package com.example.lab2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class HelloController {
    @FXML
    Ellipse buttonEllipse;
    @FXML
    Line buttonLine;
    @FXML
    Rectangle buttonRectangle;
    @FXML
    Polygon buttonPolygon;

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
    }

    @FXML
    void onEllipseClick() {
        unselectButtons();
        buttonEllipse.setStrokeWidth(5);
        buttonEllipse.setStroke(Color.BLUE);
    }

    @FXML
    void onLineClick() {
        unselectButtons();
        buttonLine.setStrokeWidth(7);
        buttonLine.setStroke(Color.ORANGE);
    }

    @FXML
    void onPolygonClick() {
        unselectButtons();
        buttonPolygon.setStrokeWidth(5);
        buttonPolygon.setStroke(Color.GREEN);
    }

    @FXML
    void onRectangleClick() {
        unselectButtons();
        buttonRectangle.setStrokeWidth(5);
        buttonRectangle.setStroke(Color.RED);
    }
}