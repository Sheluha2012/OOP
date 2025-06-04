    package com.example.demo3;

    import com.example.demo3.shapes.*;
    import com.example.demo3.serialize.ShapeHistoryManager;
    import com.example.demo3.serialize.SerializeShape;
    import com.example.demo3.plugin.ShapePlugin;
    import javafx.application.Platform;
    import javafx.scene.control.*;
    import javafx.scene.layout.Pane;
    import javafx.scene.paint.Color;
    import javafx.scene.shape.Line;
    import javafx.scene.shape.Shape;

    import java.io.*;
    import java.net.URL;
    import java.net.URLClassLoader;
    import java.util.*;
    import java.util.function.BiFunction;
    import javafx.scene.input.MouseButton;

    public class Controller {
        private static ShapePaint selectedShape = null;
        protected static final ShapeHistoryManager historyManager = new ShapeHistoryManager();
        private static ChoiceBox<String> shapeChoiceBox;
        private static ColorPicker rootColorPicker;
        private static ColorPicker fillColorPicker;
        private static ScrollBar depthScrollBar;
        private static final Map<String, BiFunction<Double, Double, ShapePaint>> shapeMap = new HashMap<>();
        private static final Map<String, ShapePlugin> plugins = new HashMap<>();
        static final List<ShapePaint> shapesList = new ArrayList<>();
        private static ShapePaint currentShape = null;
        private static boolean isDrawingPolyline = false;

        static {
            shapeMap.put("Line", (x, y) -> new LinePaint());
            shapeMap.put("Ellipse", (x, y) -> new EllipsePaint());
            shapeMap.put("Polygon", (x, y) -> new PolygonPaint());
            shapeMap.put("Rectangle", (x, y) -> new RectanglePaint());
            shapeMap.put("Polyline", (x, y) -> new PolylinePaint());
        }

        public static void initializeUIComponents(ChoiceBox<String> shapeCB, ColorPicker rootCP,
                                                  ColorPicker fillCP, ScrollBar depthSB) {
            shapeChoiceBox = shapeCB;
            rootColorPicker = rootCP;
            fillColorPicker = fillCP;
            depthScrollBar = depthSB;
        }

        public static List<ShapePaint> getShapesFromPane(Pane drawingPane) {
            return new ArrayList<>(shapesList);
        }

        public static void addShapesToPane(List<ShapePaint> shapes, Pane drawingPane) {
            for (ShapePaint shape : shapes) {
                Shape drawnShape = shape.draw();
                if (drawnShape != null) {
                    drawingPane.getChildren().add(drawnShape);
                    shapesList.add(shape);
                    historyManager.saveState(getShapesFromPane(drawingPane));
                }
            }

        }
        public static void loadPlugin(ShapePlugin plugin) {
            String shapeName = plugin.getShapeName();

            if (plugins.containsKey(shapeName)) {
                System.out.println("Plugin already loaded: " + shapeName);
                return;
            }

            plugins.put(shapeName, plugin);
            shapeMap.put(shapeName, (x, y) -> plugin.createShape());
            SerializeShape.registerPlugin(plugin);

            Platform.runLater(() -> {
                if (!shapeChoiceBox.getItems().contains(shapeName)) {
                    shapeChoiceBox.getItems().add(shapeName);
                    System.out.println("Added shape to ChoiceBox: " + shapeName);
                }
            });

            System.out.println("Successfully loaded plugin: " + shapeName);
        }
        public static void loadPluginFromJar(String jarPath) {
            try {
                File jarFile = new File(jarPath);
                URL jarUrl = jarFile.toURI().toURL();

                URLClassLoader pluginLoader = new URLClassLoader(
                        new URL[]{jarUrl},
                        Controller.class.getClassLoader()
                );

                String serviceFile = "META-INF/services/com.example.demo3.plugin.ShapePlugin";
                try (InputStream is = pluginLoader.getResourceAsStream(serviceFile)) {
                    if (is == null) {
                        throw new RuntimeException("Service file not found in JAR: " + serviceFile);
                    }

                    String pluginClassName = new BufferedReader(new InputStreamReader(is))
                            .lines()
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Empty service file"));

                    Class<?> pluginClass = pluginLoader.loadClass(pluginClassName);
                    ShapePlugin plugin = (ShapePlugin) pluginClass.getDeclaredConstructor().newInstance();
                    loadPlugin(plugin);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to load plugin from: " + jarPath, e);
            }
        }
        public static void setSelectedShape(ShapePaint shape) {
            if (selectedShape != null) {
                selectedShape.setSelected(false);
            }
            selectedShape = shape;
            if (selectedShape != null) {
                selectedShape.setSelected(true);
            }
        }

        public static ShapePaint getSelectedShape() {
            return selectedShape;
        }

        public static void addFigureDoubleClick(Pane drawingPane) {
            drawingPane.setOnMousePressed(event -> {
                String selectedShape = shapeChoiceBox.getValue();
                if (shapeMap.containsKey(selectedShape)) {
                    currentShape = createShape(selectedShape, event.getX(), event.getY(),
                            rootColorPicker.getValue(), depthScrollBar.getValue());
                    currentShape.setFillColor(fillColorPicker.getValue());
                    shapesList.add(currentShape);
                    currentShape.setStart(event.getX(), event.getY());

                    if (Objects.equals(shapeChoiceBox.getValue(), "Polyline")) {
                        isDrawingPolyline = true;
                        setupPolylineHandlers(drawingPane, (PolylinePaint) currentShape);
                    } else {
                        setupOtherShapeHandlers(drawingPane, currentShape);
                    }

                    saveCurrentState();
                }
            });
        }

        private static ShapePaint createShape(String shapeType, double x, double y,
                                              Color color, double strokeWidth) {
            ShapePaint shape = shapeMap.get(shapeType).apply(x, y);
            shape.setColor(color);
            shape.setStrokeWidth(strokeWidth);
            return shape;
        }

        private static void setupPolylineHandlers(Pane drawingPane, PolylinePaint shape) {
            resetDrawingPaneHandlers(drawingPane);
            Line[] previewLine = {null};

            drawingPane.setOnMouseMoved(e -> {
                if (previewLine[0] != null) {
                    drawingPane.getChildren().remove(previewLine[0]);
                }
                previewLine[0] = (Line) shape.createPreviewLine(e.getX(), e.getY());
                if (previewLine[0] != null) {
                    drawingPane.getChildren().add(previewLine[0]);
                }
            });

            drawingPane.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    shape.addPoint(e.getX(), e.getY());
                    redrawPolyline(drawingPane, shape);
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    if (shape.hasLines()) {
                        shape.finalizeShape(0, 0);
                        isDrawingPolyline = false;
                        currentShape = null;
                        if (previewLine[0] != null) {
                            drawingPane.getChildren().remove(previewLine[0]);
                            previewLine[0] = null;
                        }
                        resetDrawingPaneHandlers(drawingPane);
                        saveCurrentState();
                        addFigureDoubleClick(drawingPane);
                    }
                }
            });

            drawingPane.requestFocus();
        }

        private static void redrawPolyline(Pane drawingPane, PolylinePaint shape) {
            drawingPane.getChildren().removeIf(node -> shape.getLines().contains(node));
            for (Line line : shape.getLines()) {
                drawingPane.getChildren().add(line);
            }
        }

        private static void setupOtherShapeHandlers(Pane drawingPane, ShapePaint shape) {
            drawingPane.setOnMouseDragged(e -> {
                shape.updateShape(e.getX(), e.getY());
                updateDrawingPane(drawingPane, shape);
            });

            drawingPane.setOnMouseReleased(e -> {
                shape.finalizeShape(e.getX(), e.getY());
                shape.finishCreation();
                saveCurrentState();
            });
        }

        private static void updateDrawingPane(Pane drawingPane, ShapePaint shape) {
            drawingPane.getChildren().removeIf(node -> node == shape.draw());
            Shape drawnShape = shape.draw();
            drawingPane.getChildren().add(drawnShape);
        }

        private static void resetDrawingPaneHandlers(Pane drawingPane) {
            drawingPane.setOnMousePressed(null);
            drawingPane.setOnMouseDragged(null);
            drawingPane.setOnMouseReleased(null);
            drawingPane.setOnKeyPressed(null);
        }

        public static void addUndoButton(MenuItem undoButton, Pane drawingPane) {
            undoButton.setOnAction(event -> {
                if (historyManager.canUndo()) {
                    List<ShapePaint> previousState = historyManager.undo();
                    shapesList.clear();
                    shapesList.addAll(previousState);
                    redrawAllShapes(drawingPane);
                }
            });
        }

        public static void addRedoButton(MenuItem redoButton, Pane drawingPane) {
            redoButton.setOnAction(event -> {
                if (historyManager.canRedo()) {
                    List<ShapePaint> nextState = historyManager.redo();
                    shapesList.clear();
                    shapesList.addAll(nextState);
                    redrawAllShapes(drawingPane);
                }
            });
        }

        private static void saveCurrentState() {
            if (!isDrawingPolyline || (((PolylinePaint) currentShape).isFinalized())) {
                historyManager.saveState(new ArrayList<>(shapesList));
            }
        }

        private static void redrawAllShapes(Pane drawingPane) {
            drawingPane.getChildren().clear();
            for (ShapePaint shape : shapesList) {
                Shape drawnShape = shape.draw();
                if (drawnShape != null) {
                    drawingPane.getChildren().add(drawnShape);
                }
            }
        }
    }
