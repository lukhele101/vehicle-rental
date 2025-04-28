module com.example.tutorial_test {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.tutorial_test to javafx.fxml;
    exports com.example.tutorial_test;
    exports com.example.tutorial2;
    opens com.example.tutorial2 to javafx.fxml;
}