//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

module com.example.demo {
    uses com.example.demo3.plugin.ShapePlugin;
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    exports com.example.demo3;

    opens com.example.demo3 to
            javafx.fxml;
    exports com.example.demo3.plugin;
    opens com.example.demo3.plugin to javafx.fxml;
    exports com.example.demo3.shapes;
    opens com.example.demo3.shapes to javafx.fxml;
}