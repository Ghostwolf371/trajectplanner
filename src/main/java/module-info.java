module org.example.trajectplanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires javafx.graphics;

    opens org.example.trajectplanner to javafx.fxml;
    opens org.example.trajectplanner.Modal to com.fasterxml.jackson.databind;
    exports org.example.trajectplanner;
    exports org.example.trajectplanner.Modal;
}
