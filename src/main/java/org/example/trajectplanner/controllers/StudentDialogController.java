package org.example.trajectplanner.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.trajectplanner.model.Student;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDate;

public class StudentDialogController {
    private static final String API_URL = "https://trajectplannerapi.dulamari.com/students";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML private TextField studentNumberField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker birthdatePicker;
    @FXML private PasswordField passwordField;

    private Stage dialogStage;
    private Student student;
    private StudentController studentController;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        genderComboBox.getItems().addAll("M", "F");
        setupValidation();
    }

    private void setupValidation() {
        // Add listeners for real-time validation
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateField(firstNameField, newValue, "First name");
        });

        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateField(lastNameField, newValue, "Last name");
        });

        studentNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateField(studentNumberField, newValue, "Student number");
        });
    }

    private void validateField(TextField field, String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            field.setPromptText(fieldName + " is required");
        } else {
            field.setStyle("");
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setStudent(Student student) {
        this.student = student;
        populateFields();
    }

    private void populateFields() {
        if (student != null) {
            studentNumberField.setText(student.getStudentNumber());
            firstNameField.setText(student.getFirstName());
            lastNameField.setText(student.getLastName());
            passwordField.setText(student.getPassword());
            genderComboBox.setValue(student.getGender());
            
            if (student.getBirthdate() != null && !student.getBirthdate().isEmpty()) {
                try {
                    birthdatePicker.setValue(LocalDate.parse(student.getBirthdate()));
                } catch (Exception e) {
                    showError("Error", "Invalid date format in student birthdate");
                }
            }
        }
    }

    public void setStudentController(StudentController controller) {
        this.studentController = controller;
    }

    @FXML
    private void handleSave() {
        if (!isInputValid()) {
            return;
        }

        try {
            ObjectNode requestBody = createRequestBody();
            sendUpdateRequest(requestBody);
        } catch (Exception e) {
            showError("Error", "Failed to update student: " + e.getMessage());
        }
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();

        if (isNullOrEmpty(studentNumberField.getText())) {
            errorMessage.append("Student number is required!\n");
        }
        if (isNullOrEmpty(firstNameField.getText())) {
            errorMessage.append("First name is required!\n");
        }
        if (isNullOrEmpty(lastNameField.getText())) {
            errorMessage.append("Last name is required!\n");
        }
        if (genderComboBox.getValue() == null) {
            errorMessage.append("Gender is required!\n");
        }
        if (birthdatePicker.getValue() == null) {
            errorMessage.append("Birthdate is required!\n");
        }
        // Add password validation
        if (isNullOrEmpty(passwordField.getText())) {
            errorMessage.append("Password is required!\n");
        } else if (passwordField.getText().length() < 8) {
            errorMessage.append("Password must be at least 8 characters long!\n");
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showError("Invalid Fields", errorMessage.toString());
            return false;
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private ObjectNode createRequestBody() {
        ObjectNode requestBody = mapper.createObjectNode()
            .put("student_number", studentNumberField.getText().trim())
            .put("first_name", firstNameField.getText().trim())
            .put("last_name", lastNameField.getText().trim())
            .put("gender", genderComboBox.getValue())
            .put("birthdate", birthdatePicker.getValue().toString());

        // Only include password in request if it has been changed
        String password = passwordField.getText().trim();
        if (!password.isEmpty()) {
            requestBody.put("password", password);
        }

        return requestBody;
    }

    private void sendUpdateRequest(ObjectNode requestBody) {
        String updateUrl = API_URL + "/" + student.getStudentNumber().replace("/", "-");
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(updateUrl))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> {
                if (response.statusCode() == 200) {
                    handleSuccessfulUpdate();
                } else {
                    handleFailedUpdate(response.statusCode());
                }
            })
            .exceptionally(e -> {
                handleUpdateError(e);
                return null;
            });
    }

    private void handleSuccessfulUpdate() {
        okClicked = true;
        javafx.application.Platform.runLater(() -> {
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Student information updated successfully!");
            alert.showAndWait();
            
            dialogStage.close();
            if (studentController != null) {
                studentController.refreshStudents();
            }
        });
    }

    private void handleFailedUpdate(int statusCode) {
        showError("Error", "Failed to update student. Status code: " + statusCode);
    }

    private void handleUpdateError(Throwable e) {
        showError("Error", "Failed to update student: " + e.getMessage());
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public boolean isOkClicked() {
        return okClicked;
    }
} 
