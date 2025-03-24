package org.example.trajectplanner.controllers;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

import org.example.trajectplanner.API.DeleteMethods;
import org.example.trajectplanner.Modal.Tentamen;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.Alert;

public class TentamenItemController implements Initializable {
    @FXML
    private Label id;
    @FXML
    private Label cursusNaam;
    @FXML
    private Label semester;
    @FXML
    private Label datum;
    @FXML
    private Label type;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private String tentamenId;

    public void setData(Tentamen tentamen){
        this.tentamenId = tentamen.getId();
        id.setText(tentamenId);
        cursusNaam.setText(tentamen.getCursesNaam());
        semester.setText(tentamen.getSemester());
        datum.setText(tentamen.getDatum());
        type.setText(tentamen.getType());

        editButton.setOnAction(event -> navigateToEdit());
        deleteButton.setOnAction(event -> deleteTentamen());
    }

    private void deleteTentamen() {
        try {
            DeleteMethods deleteMethods = new DeleteMethods();
            HttpResponse<String> response = deleteMethods.deleteTentamen(tentamenId);

            if (response != null) {
                switch (response.statusCode()) {
                    case 200:
                        this.id.getParent().setVisible(false);
                        break;
                    case 400:
                        showError("Delete Failed", "Invalid examination ID format or missing required parameters");
                        break;
                    case 404:
                        showError("Delete Failed", "Examination not found");
                        break;
                    default:
                        showError("Delete Failed", "Unexpected error (Status " + response.statusCode() + ")");
                }
            } else {
                showError("Delete Failed", "Could not connect to server");
            }
        } catch (Exception e) {
            showError("Error", "Failed to delete examination: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateToEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/trajectplanner/edit-tentamen.fxml"));
            Parent root = loader.load();

            EditTentamenController editController = loader.getController();
            editController.setTentamenId(tentamenId);

            // Create a new stage for the edit window
            Stage editStage = new Stage();
            editStage.initModality(Modality.APPLICATION_MODAL); // Make it modal
            editStage.initOwner(editButton.getScene().getWindow()); // Set the parent window
            
            Scene scene = new Scene(root);
            editStage.setScene(scene);
            editStage.setTitle("Edit Examination");
            
            // Show the window and wait for it to close
            editStage.showAndWait();
            
            // Optionally refresh the main window's data when edit window closes
            // You might want to call a refresh method here
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public String getCursusNaam() {
        return cursusNaam.getText();
    }
}
