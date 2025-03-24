package org.example.trajectplanner.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.trajectplanner.API.ScoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AddScoreController {
    @FXML private TextField studentNumberField;
    @FXML private TextField examIdField;
    @FXML private TextField scoreField;
    @FXML private DatePicker datePicker;

    @FXML
    private void handleAdd() {
        if (!validateInputs()) {
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            
            requestBody.put("student_number", studentNumberField.getText().trim());
            requestBody.put("exam_id", Integer.parseInt(examIdField.getText().trim()));
            requestBody.put("score_value", Double.parseDouble(scoreField.getText().trim()));
            
            if (datePicker.getValue() != null) {
                requestBody.put("score_date", datePicker.getValue().toString());
            }

            var response = ScoreService.create(requestBody.toString());

            if (response != null) {
                switch (response.statusCode()) {
                    case 201:
                        closeDialog();
                        break;
                    case 400:
                        showError("Validation Error", "Please check your input values");
                        break;
                    case 409:
                        showError("Conflict", "This score already exists");
                        break;
                    default:
                        showError("Error", "Server returned status code: " + response.statusCode());
                }
            } else {
                showError("Error", "Failed to connect to server");
            }
        } catch (Exception e) {
            showError("Error", "Failed to add score: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();

        if (isNullOrEmpty(studentNumberField.getText())) {
            errorMessage.append("Student Number is required\n");
        }

        if (isNullOrEmpty(examIdField.getText())) {
            errorMessage.append("Exam ID is required\n");
        } else {
            try {
                Integer.parseInt(examIdField.getText().trim());
            } catch (NumberFormatException e) {
                errorMessage.append("Exam ID must be a valid number\n");
            }
        }

        if (isNullOrEmpty(scoreField.getText())) {
            errorMessage.append("Score is required\n");
        } else {
            try {
                double score = Double.parseDouble(scoreField.getText().trim());
                if (score < 0.0 || score > 10.0) {
                    errorMessage.append("Score must be between 0.0 and 10.0\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Score must be a valid number\n");
            }
        }

        if (errorMessage.length() > 0) {
            showError("Validation Error", errorMessage.toString());
            return false;
        }
        
        return true;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeDialog() {
        ((Stage) studentNumberField.getScene().getWindow()).close();
    }
}
