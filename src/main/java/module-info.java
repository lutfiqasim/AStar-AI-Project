module com.example.astarproj {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.astarproj to javafx.fxml;
    exports com.example.astarproj;
}