package org.example.trajectplanner.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DialogUtils {
    public static Stage showDialog(String fxmlPath, String title, Window owner) throws Exception {
        var url = DialogUtils.class.getResource(fxmlPath);
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        
        if (loader.getLocation() == null) {
            throw new IllegalArgumentException("Could not find FXML file: " + fxmlPath);
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setScene(new Scene(loader.load()));
        dialog.setTitle(title);
        
        // Store the controller for later use
        dialog.setUserData(loader.getController());
        
        dialog.showAndWait();
        return dialog;
    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
