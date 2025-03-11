module org.example.trajectplanner {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.trajectplanner to javafx.fxml;
    exports org.example.trajectplanner;
}