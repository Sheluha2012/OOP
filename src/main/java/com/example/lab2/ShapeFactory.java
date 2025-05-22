package com.example.lab2;

import com.example.lab2.shapes.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ShapeFactory {
    private static final Map<String, Supplier<ShapePaint>> registry = new HashMap<>();

    static {
        registry.put("Line", LinePaint::new);
        registry.put("Ellipse", EllipsePaint::new);
        registry.put("Rectangle", RectanglePaint::new);
        registry.put("Polygon", PolygonPaint::new);
        registry.put("Polyline", PolylinePaint::new);
    }

    public static ShapePaint create(String shapeType) {
        Supplier<ShapePaint> supplier = registry.get(shapeType);
        if (supplier != null) {
            return supplier.get();
        }
        throw new IllegalArgumentException("Unknown shape type: " + shapeType);
    }
}
