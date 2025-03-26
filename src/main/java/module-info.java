module org.example.trajectplanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens org.example.trajectplanner to javafx.fxml;
    opens org.example.trajectplanner.controllers to javafx.fxml, javafx.graphics, javafx.base;
    opens org.example.trajectplanner.model to com.fasterxml.jackson.databind;
    opens org.example.trajectplanner.api to com.fasterxml.jackson.databind;
    
    exports org.example.trajectplanner;
    exports org.example.trajectplanner.controllers;
    exports org.example.trajectplanner.model;
    exports org.example.trajectplanner.api;
}
