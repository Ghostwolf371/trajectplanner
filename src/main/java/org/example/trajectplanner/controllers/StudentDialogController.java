package org.example.trajectplanner.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.example.trajectplanner.model.Student;

public class StudentDialogController {
    private static final String API_URL = "https://trajectplannerapi.dulamari.com/students";
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> majorComboBox;
    @FXML private ComboBox<Integer> cohortComboBox;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker birthdatePicker;
    @FXML private PasswordField passwordField;

    private Stage dialogStage;
    private Student student;
    private boolean okClicked = false;
    private StudentController studentController;

    @FXML
    private void initialize() {
        // Initialize major options
        majorComboBox.getItems().addAll("SE", "BI", "NE");
        
        // Initialize cohort options (1101-1199)
        List<Integer> cohorts = new ArrayList<>();
        for (int i = 1101; i <= 1199; i++) {
            cohorts.add(i);
        }
        cohortComboBox.getItems().addAll(cohorts);
        
        // Initialize gender options
        genderComboBox.getItems().addAll("M", "F");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setStudent(Student student) {
        this.student = student;
        
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        majorComboBox.setValue(student.getMajor());
        cohortComboBox.setValue(student.getCohort());
        genderComboBox.setValue(student.getGender());
        birthdatePicker.setValue(student.getBirthdate());
        passwordField.setText(student.getPassword());
    }

    public void setStudentController(StudentController studentController) {
        this.studentController = studentController;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            try {
                if (student == null) {
                    // Create new student
                    createStudent();
                } else {
                    // Update existing student
                    updateStudent();
                }
            } catch (Exception e) {
                showError("Error", "Failed to save student: " + e.getMessage());
            }
        }
    }

    private void createStudent() throws Exception {
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("first_name", firstNameField.getText());
        requestBody.put("last_name", lastNameField.getText());
        requestBody.put("major", majorComboBox.getValue());
        requestBody.put("cohort", cohortComboBox.getValue());
        requestBody.put("gender", genderComboBox.getValue());
        requestBody.put("birthdate", birthdatePicker.getValue().toString());
        requestBody.put("password", passwordField.getText());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 201) {
                        okClicked = true;
                        dialogStage.close();
                    } else {
                        showError("Error", "Failed to create student. Status code: " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    showError("Error", "Failed to create student: " + e.getMessage());
                    return null;
                });
    }

    private void updateStudent() throws Exception {
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("student_number", student.getStudentNumber());
        requestBody.put("first_name", firstNameField.getText());
        requestBody.put("last_name", lastNameField.getText());
        requestBody.put("major", majorComboBox.getValue());
        requestBody.put("cohort", cohortComboBox.getValue());
        requestBody.put("gender", genderComboBox.getValue());
        requestBody.put("birthdate", birthdatePicker.getValue().toString());
        requestBody.put("password", passwordField.getText());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        okClicked = true;
                        dialogStage.close();
                    } else {
                        showError("Error", "Failed to update student. Status code: " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    showError("Error", "Failed to update student: " + e.getMessage());
                    return null;
                });
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
            errorMessage += "First name is required!\n";
        }
        if (lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) {
            errorMessage += "Last name is required!\n";
        }
        if (majorComboBox.getValue() == null) {
            errorMessage += "Major is required!\n";
        }
        if (cohortComboBox.getValue() == null) {
            errorMessage += "Cohort is required!\n";
        }
        if (genderComboBox.getValue() == null) {
            errorMessage += "Gender is required!\n";
        }
        if (birthdatePicker.getValue() == null) {
            errorMessage += "Birthdate is required!\n";
        }
        if (passwordField.getText() == null || passwordField.getText().trim().isEmpty()) {
            errorMessage += "Password is required!\n";
        } else if (passwordField.getText().length() < 4) {
            errorMessage += "Password must be at least 4 characters long!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showError("Invalid Fields", errorMessage);
            return false;
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 