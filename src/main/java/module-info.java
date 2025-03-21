module org.example.trajectplanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;


    opens org.example.trajectplanner to javafx.fxml;
    exports org.example.trajectplanner;
}