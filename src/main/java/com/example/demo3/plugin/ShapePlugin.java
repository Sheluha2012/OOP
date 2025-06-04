package com.example.demo3.plugin;

import com.example.demo3.shapes.ShapePaint;
import javafx.scene.control.MenuItem;

public interface ShapePlugin {
    String getShapeName();
    ShapePaint createShape();
    MenuItem getMenuItem();
}