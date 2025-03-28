package org.example.trajectplanner.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.example.trajectplanner.services.ScoreService;
import org.example.trajectplanner.utils.DialogUtils;
import java.time.LocalDate;

public class EditScoreController {
    @FXML private Label studentNumberField;
    @FXML private Label courseNameField;
    @FXML private TextField scoreField;
    @FXML private DatePicker datePicker;
    
    private String scoreId;
    
    public void setScoreId(String id) {
        this.scoreId = id;
        loadScoreData();
    }
    
    private void loadScoreData() {
        try {
            var response = ScoreService.getById(scoreId);
            if (response == null) {
                DialogUtils.showError("Error", "Failed to connect to server");
                return;
            }
            
            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                var scores = mapper.readTree(response.body());
                
                if (scores.isArray() && scores.size() > 0) {
                    var score = scores.get(0);
                    
                    if (score.has("student_number")) {
                        studentNumberField.setText(score.get("student_number").asText());
                    }
                    
                    if (score.has("course_name")) {
                        courseNameField.setText(score.get("course_name").asText());
                    }
                    
                    // Changed from "score" to "score_value"
                    if (score.has("score_value")) {
                        var scoreValue = score.get("score_value");
                        if (scoreValue.isNumber()) {
                            scoreField.setText(String.format("%.1f", scoreValue.asDouble()));
                        } else {
                            scoreField.setText(scoreValue.asText());
                        }
                    }
                    
                    if (score.has("score_datetime")) {
                        String dateStr = score.get("score_datetime").asText();
                        if (dateStr != null && !dateStr.isEmpty()) {
                            try {
                                LocalDate date = LocalDate.parse(dateStr.split(" ")[0]);
                                datePicker.setValue(date);
                            } catch (Exception e) {
                                DialogUtils.showError("Error", "Invalid date format in data");
                            }
                        }
                    }
                }
            } else {
                DialogUtils.showError("Error", "Failed to load score data. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Error", "Failed to load score data: " + e.getMessage());
            closeDialog();
        }
    }

    @FXML
    private void handleUpdate() {
        if (!validateInputs()) {
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();

            requestBody.put("score_id", scoreId);
            requestBody.put("score_value", Double.parseDouble(scoreField.getText().trim()));
            
            if (datePicker.getValue() != null) {
                requestBody.put("score_date", datePicker.getValue().toString());
            }

            var response = ScoreService.update(requestBody.toString());

            if (response != null) {
                switch (response.statusCode()) {
                    case 200 -> closeDialog();
                    case 400 -> DialogUtils.showError("Validation Error", "Please check your input values");
                    case 404 -> DialogUtils.showError("Error", "Score not found");
                    default -> DialogUtils.showError("Error", "Server returned status code: " + response.statusCode());
                }
            } else {
                DialogUtils.showError("Error", "Failed to connect to server");
            }
        } catch (Exception e) {
            DialogUtils.showError("Error", "Failed to update score: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private boolean validateInputs() {
        if (scoreField.getText() == null || scoreField.getText().trim().isEmpty()) {
            DialogUtils.showError("Validation Error", "Score is required");
            return false;
        }

        try {
            double score = Double.parseDouble(scoreField.getText().trim());
            if (score < 0.0 || score > 10.0) {
                DialogUtils.showError("Validation Error", "Score must be between 0.0 and 10.0");
                return false;
            }
        } catch (NumberFormatException e) {
            DialogUtils.showError("Validation Error", "Score must be a valid number");
            return false;
        }

        return true;
    }

    private void closeDialog() {
        ((Stage) scoreField.getScene().getWindow()).close();
    }
}
