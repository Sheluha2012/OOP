package com.example.lab2;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import java.util.ArrayList;
import java.util.List;

public class ShapeHistoryManager {
    private final List<Node> undoList = new ArrayList<>();
    private final List<Node> redoList = new ArrayList<>();

    public void addShape(Node shape, Pane pane) {
        undoList.add(shape);
        redoList.clear();
        if (!pane.getChildren().contains(shape)) {
            pane.getChildren().add(shape);
        }
    }

    public void undo(Pane pane) {
        if (!undoList.isEmpty()) {
            Node shapeToUndo = undoList.removeLast();
            redoList.add(shapeToUndo);
            pane.getChildren().remove(shapeToUndo);
        }
    }

    public void redo(Pane pane) {
        if (!redoList.isEmpty()) {
            Node shapeToRedo = redoList.removeLast();
            undoList.add(shapeToRedo);
            pane.getChildren().add(shapeToRedo);
        }
    }

    public void clear(Pane drawPane) {
        undoList.clear();
        redoList.clear();
        drawPane.getChildren().clear();
    }

}
