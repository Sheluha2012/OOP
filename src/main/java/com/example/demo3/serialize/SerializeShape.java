package com.example.demo3.serialize;

import com.example.demo3.shapes.ShapePaint;
import com.example.demo3.shapes.*;
import com.example.demo3.plugin.ShapePlugin;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SerializeShape {
    private static final ObjectMapper objectMapper = createObjectMapper();
    private static final Map<String, Class<? extends ShapePaint>> pluginShapes = new HashMap<>();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Color.class, new SerializeColor());
        module.addDeserializer(Color.class, new DeserializeColor());
        mapper.registerModule(module);
        return mapper;
    }

    public static void registerPlugin(ShapePlugin plugin) {
        pluginShapes.put(plugin.getShapeName(), plugin.createShape().getClass());
    }

    public static void saveToFile(List<ShapePaint> shapes, File file) {
        try {
            List<Map<String, Object>> shapeData = shapes.stream()
                    .map(ShapePaint::toMap)
                    .collect(Collectors.toList());
            objectMapper.writeValue(file, shapeData);

        } catch (IOException e) {
            showErrorAlert("Error saving file: " + e.getMessage());
        }
    }

    public static List<ShapePaint> loadFromFile(File file) {
        try {
            List<Map<String, Object>> shapeData = objectMapper.readValue(
                    file,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            List<ShapePaint> result = new ArrayList<>();
            List<String> missingPlugins = new ArrayList<>();

            for (Map<String, Object> data : shapeData) {
                String type = (String) data.get("type");
                try {
                    ShapePaint shape = createShapeFromType(type);
                    shape.fromMap(data);
                    result.add(shape);
                } catch (IllegalArgumentException e) {
                    if (isPluginShape(type)) {
                        missingPlugins.add(type);
                    } else {
                        throw e;
                    }
                }
            }

            if (!missingPlugins.isEmpty()) {
                throw new MissingPluginException("Missing plugins for shapes: " + String.join(", ", missingPlugins));
            }

            return result;
        } catch (IOException e) {
            showErrorAlert("Error loading file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static boolean isPluginShape(String type) {
        return pluginShapes.containsKey(type);
    }

    private static ShapePaint createShapeFromType(String type) {
        switch (type) {
            case "Ellipse": return new EllipsePaint();
            case "Line": return new LinePaint();
            case "Polyline": return new PolylinePaint();
            case "Rectangle": return new RectanglePaint();
            case "Polygon": return new PolygonPaint();
        }
        Class<? extends ShapePaint> shapeClass = pluginShapes.get(type);
        if (shapeClass != null) {
            try {
                return shapeClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create shape instance: " + type, e);
            }
        }

        throw new IllegalArgumentException("Unknown shape type: " + type);
    }

    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class MissingPluginException extends RuntimeException {
        public MissingPluginException(String message) {
            super(message);
        }
    }
}