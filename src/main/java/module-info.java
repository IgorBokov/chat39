module com.example.chat39 {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.chat39 to javafx.fxml;
    exports com.example.chat39;
}