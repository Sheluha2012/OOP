package com.example.demo3.plugin;

import com.example.demo3.shapes.ShapePaint;
import javafx.scene.control.MenuItem;

public class TrapezoidPlugin implements ShapePlugin {
    @Override
    public String getShapeName() {
        return "Trapezoid";
    }

    @Override
    public ShapePaint createShape() {
        return new TrapezoidShape.Trapezoid();
    }

    @Override
    public MenuItem getMenuItem() {
        return new MenuItem(getShapeName());
    }
}