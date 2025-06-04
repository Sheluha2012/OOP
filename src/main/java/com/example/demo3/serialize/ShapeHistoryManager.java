package com.example.demo3.serialize;

import com.example.demo3.shapes.PolylinePaint;
import com.example.demo3.shapes.ShapePaint;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ShapeHistoryManager {
    private final Deque<List<ShapePaint>> undoStack = new ArrayDeque<>();
    private final Deque<List<ShapePaint>> redoStack = new ArrayDeque<>();
    private List<ShapePaint> currentState = new ArrayList<>();

    public void saveState(List<ShapePaint> shapes) {
        List<ShapePaint> filteredState = filterFinalizedShapes(shapes);

        undoStack.push(new ArrayList<>(currentState));
        currentState = new ArrayList<>(filteredState);
        redoStack.clear();
    }
    public void loadState(List<ShapePaint> shapes) {
        undoStack.push(new ArrayList<>(currentState));
        currentState = new ArrayList<>(filterFinalizedShapes(shapes));
        redoStack.clear();
    }



    public List<ShapePaint> undo() {
        if (canUndo()) {
            redoStack.push(new ArrayList<>(currentState));
            currentState = undoStack.pop();
            return new ArrayList<>(currentState);
        }
        return currentState;
    }

    public List<ShapePaint> redo() {
        if (canRedo()) {
            undoStack.push(new ArrayList<>(currentState));
            currentState = redoStack.pop();
            return new ArrayList<>(currentState);
        }
        return currentState;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
        currentState.clear();
    }

    private List<ShapePaint> filterFinalizedShapes(List<ShapePaint> shapes) {
        List<ShapePaint> result = new ArrayList<>();
        for (ShapePaint shape : shapes) {
            if (!(shape instanceof PolylinePaint) || ((PolylinePaint) shape).isFinalized()) {
                result.add(shape);
            }
        }
        return result;
    }
}