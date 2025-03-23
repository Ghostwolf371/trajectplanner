package org.example.trajectplanner;

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
            HttpResponse<String> response = deleteMethods.deleteExam(tentamenId);

            if (response != null && response.statusCode() == 200) {
                this.id.getParent().setVisible(false);
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    private void navigateToEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-tentamen.fxml"));
            Parent root = loader.load();

            EditTentamenController editController = loader.getController();
            editController.setTentamenId(tentamenId);

            // Get the current stage
            Stage stage = (Stage) editButton.getScene().getWindow();
            // Replace the current scene
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Tentamen");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
