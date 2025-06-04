package com.example.demo3;

import com.example.demo3.serialize.SerializeShape;
import com.example.demo3.shapes.ShapePaint;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

import static com.example.demo3.Controller.historyManager;
import static com.example.demo3.Controller.shapesList;

public class HelloApplication extends Application {
    @FXML public ColorPicker fillColorPicker;
    @FXML private ChoiceBox<String> shapeChoiceBox;
    @FXML private ScrollBar depthScrollBar;
    @FXML public ColorPicker rootColorPicker;
    @FXML private Pane drawingPane;
    @FXML private MenuItem undoButton;
    @FXML private MenuItem redoButton;
    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem saveMenuItem;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Pane root = (Pane) fxmlLoader.load();
        Scene scene = new Scene(root, 800, 550);
        stage.setTitle("Figures");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        this.shapeChoiceBox.getItems().addAll("Line", "Ellipse", "Polygon", "Rectangle", "Polyline");
        this.shapeChoiceBox.setValue("Line");
        this.depthScrollBar.setMin(1.0);
        this.depthScrollBar.setMax(20.0);
        this.depthScrollBar.setValue(1.0);

        Controller.initializeUIComponents(shapeChoiceBox, rootColorPicker, fillColorPicker, depthScrollBar);
        Controller.addFigureDoubleClick(this.drawingPane);
        Controller.addUndoButton(this.undoButton, this.drawingPane);
        Controller.addRedoButton(this.redoButton, this.drawingPane);

        saveMenuItem.setOnAction(event -> saveShapesToFile());
        openMenuItem.setOnAction(event -> loadShapesFromFile());
    }

    @FXML
    private void saveShapesToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить фигуры");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(drawingPane.getScene().getWindow());

        if (file != null) {
            List<ShapePaint> shapes = Controller.getShapesFromPane(drawingPane);
            SerializeShape.saveToFile(shapes, file);

            historyManager.saveState(shapes);
        }
    }


    @FXML
    private void loadShapesFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(drawingPane.getScene().getWindow());

        if (file != null) {
            try {
                drawingPane.getChildren().clear();
                shapesList.clear();
                historyManager.clear();
                List<ShapePaint> shapes = SerializeShape.loadFromFile(file);
                Controller.addShapesToPane(shapes, drawingPane);

                historyManager.loadState(shapes);

            } catch (SerializeShape.MissingPluginException e) {
                showErrorAlert("Не удалось загрузить фигуры: " + e.getMessage() +
                        "\nПожалуйста, сначала загрузите необходимые плагины.");
            } catch (Exception e) {
                showErrorAlert("Ошибка при загрузке файла: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void addPlugins() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Plugin JAR File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAR Files", "*.jar"));

        File pluginsDir = new File("plugins");
        if (pluginsDir.exists()) {
            fileChooser.setInitialDirectory(pluginsDir);
        }

        File selectedFile = fileChooser.showOpenDialog(drawingPane.getScene().getWindow());

        if (selectedFile != null) {
            try {
                Controller.loadPluginFromJar(selectedFile.getAbsolutePath());
            } catch (Exception e) {
                showErrorAlert("Error loading plugin: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static void main(String[] args) {
        launch();
    }
}