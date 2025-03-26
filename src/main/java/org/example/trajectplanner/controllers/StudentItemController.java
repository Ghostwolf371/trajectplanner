package org.example.trajectplanner.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.trajectplanner.model.Student;
import org.example.trajectplanner.services.StudentService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class StudentItemController {
    private static final String API_URL = "https://trajectplannerapi.dulamari.com/students";
    private final HttpClient client = HttpClient.newHttpClient();

    @FXML private Label studentNumber;
    @FXML private Label firstName;
    @FXML private Label lastName;
    @FXML private Label gender;
    @FXML private Label birthdate;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private Student student;
    private StudentController parentController;

    @FXML
    private void initialize() {
        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());
    }

    public void setStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        this.student = student;
        updateLabels();
    }

    private void updateLabels() {
        studentNumber.setText(student.getStudentNumber());
        firstName.setText(student.getFirstName());
        lastName.setText(student.getLastName());
        gender.setText(student.getGender());
        birthdate.setText(student.getBirthdate() != null ? student.getBirthdate().toString() : "");
    }

    public void setParentController(StudentController controller) {
        this.parentController = controller;
    }

    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student-dialog.fxml"));
            Parent root = loader.load();

            StudentDialogController dialogController = loader.getController();
            dialogController.setStudent(student);
            dialogController.setStudentController(parentController);

            Stage dialogStage = createDialogStage(root);
            dialogController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

        } catch (IOException e) {
            showError("Error", "Could not load the edit dialog: " + e.getMessage());
        }
    }

    private Stage createDialogStage(Parent root) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(editButton.getScene().getWindow());
        dialogStage.setTitle("Edit Student");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogStage.setScene(scene);
        dialogStage.setMinWidth(800);
        dialogStage.setMinHeight(600);
        dialogStage.setResizable(true);

        return dialogStage;
    }

    private void handleDelete() {
        if (student == null) {
            showError("Delete Failed", "No student selected for deletion");
            return;
        }

        if (showDeleteConfirmation()) {
            deleteThroughApi();
        }
    }

    private boolean showDeleteConfirmation() {
        String message = String.format("Are you sure you want to delete %s %s?", 
            student.getFirstName(), student.getLastName());
            
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Student");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void deleteThroughApi() {
        try {
            var response = StudentService.delete(student.getStudentNumber());
            
            if (response != null) {
                switch (((HttpResponse<String>) response).statusCode()) {
                    case 200, 204 -> {
                        studentNumber.getParent().setVisible(false);
                        refreshParentController();
                    }
                    case 400 -> showError("Delete Failed", "Invalid student number format");
                    case 404 -> showError("Delete Failed", "Student not found");
                    default -> showError("Delete Failed", 
                        String.format("Server error occurred (Status: %d)", response.statusCode()));
                }
            } else {
                showError("Delete Failed", "Could not connect to server");
            }
        } catch (Exception e) {
            showError("Error", "Failed to delete student: " + e.getMessage());
        }
    }

    private void refreshParentController() {
        if (parentController != null) {
            parentController.refreshStudents();
        }
    }

    private void showError(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}













