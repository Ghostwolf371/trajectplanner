package org.example.trajectplanner.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DialogUtils {
    public static Stage showDialog(String fxmlPath, String title, Window owner) throws Exception {
        var url = DialogUtils.class.getResource(fxmlPath);
        if (url != null) {
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(owner);
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            
            stage.showAndWait();
            return stage;
        }
        throw new IllegalArgumentException("Could not find FXML file: " + fxmlPath);
    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
