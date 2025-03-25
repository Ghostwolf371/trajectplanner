package org.example.trajectplanner.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.trajectplanner.api.GetMethods;
import org.example.trajectplanner.api.PutMethods;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Arrays;

public class EditTentamenController {
    @FXML
    private Label tentamenIdLabel;
    
    @FXML
    private ComboBox<String> courseCodeComboBox; // Changed from TextField to ComboBox
    
    @FXML
    private ComboBox<String> typeComboBox;
    
    @FXML
    private DatePicker datePicker;
    
    private String tentamenId;
    private String courseId;
    
    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("Regulier", "Her");
        
        // Initialize course code combo box with all course codes
        List<String> courseCodes = Arrays.asList(
            "SP1121BB1", "SP1121BB2", "SP1121BB3a", "SP1121BB3b", "SP1121BB4",
            "SP1121BB5a", "SP1121BB5b", "SP1121BB6a", "SP1121BB6b", "SP1121BB7",
            "21-ID1-T", "21-ID2-T", "SP1121ID3", "SP1121ID4", "SP1121ID5",
            "SP1121ID6", "SH11121MS1", "SH11121MS2", "SH11121MS3", "SH11121MS4",
            "SH11121MS5", "SH11121MS6", "SH1120SE1", "SH1120SE2", "SH1120SE3",
            "SH1120SE4", "SH1120SE5", "SH1120SE6", "SH21119CS1", "SH21119CS2",
            "SH21119CS3", "SH21119CS4", "SH21119CS5", "SH21119CS6", "SH21119PM1",
            "SH21119PM2", "SH21119PM3", "SH21119PM4", "SH21119PM5", "SH21119PM6",
            "7-PP1-T", "7-PP2-T", "7-PP3A-T", "7-PP3B-T", "7-PP4-T",
            "7-PP5A-T", "7-PP5B-T", "7-PP6-T", "7-PP7A-T", "7-PP7B-T",
            "8-AO1-T", "8-AO2-T", "8-AO3-T"
        );
        courseCodeComboBox.getItems().addAll(courseCodes);
    }
    
    public void setTentamenId(String id) {
        this.tentamenId = id;
        tentamenIdLabel.setText("Editing examination " + id);
        loadExamData();
    }
    
    private void loadExamData() {
        try {
            GetMethods getMethods = new GetMethods();
            HttpResponse<String> examResponse = getMethods.getExamById(tentamenId);
            
            if (examResponse != null && examResponse.statusCode() == 200) {
                String responseBody = examResponse.body();
                
                ObjectMapper mapper = new ObjectMapper();
                ArrayNode arrayNode = (ArrayNode) mapper.readTree(responseBody);
                
                if (arrayNode.size() > 0) {
                    ObjectNode examData = (ObjectNode) arrayNode.get(0);
                    
                    // Get course_id and fetch course details
                    if (examData.has("course_id")) {
                        this.courseId = examData.get("course_id").asText();
                        loadCourseData(this.courseId);
                    }
                    
                    // Set type
                    if (examData.has("type")) {
                        String examType = examData.get("type").asText();
                        typeComboBox.setValue(examType);
                    }
                    
                    // Set date
                    if (examData.has("date")) {
                        String dateStr = examData.get("date").asText();
                        if (dateStr != null && !dateStr.isEmpty()) {
                            datePicker.setValue(LocalDate.parse(dateStr));
                        }
                    }
                } else {
                    showError("Error", "No examination data found for ID: " + tentamenId);
                }
            } else {
                String errorMsg = "Failed to load examination data. Status code: " + 
                    (examResponse != null ? examResponse.statusCode() : "null");
                showError("Error", errorMsg);
            }
        } catch (Exception e) {
            showError("Error", "Failed to load examination data: " + e.getMessage());
        }
    }
    
    private void loadCourseData(String courseId) {
        try {
            GetMethods getMethods = new GetMethods();
            HttpResponse<String> courseResponse = getMethods.getCourseById(courseId);
            
            if (courseResponse != null && courseResponse.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                ArrayNode arrayNode = (ArrayNode) mapper.readTree(courseResponse.body());
                
                if (arrayNode.size() > 0) {
                    ObjectNode courseData = (ObjectNode) arrayNode.get(0);
                    
                    // Set course code
                    if (courseData.has("code")) {
                        String courseCode = courseData.get("code").asText();
                        courseCodeComboBox.setValue(courseCode);
                    }
                }
            } else {
                System.err.println("Failed to load course data. Status code: " + 
                    (courseResponse != null ? courseResponse.statusCode() : "null"));
            }
        } catch (Exception e) {
            System.err.println("Error loading course data: " + e.getMessage());
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
            
            // Required field - ensure it's a valid integer
            try {
                int examIdInt = Integer.parseInt(tentamenId);
                requestBody.put("exam_id", examIdInt);
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Invalid examination ID format");
                return;
            }

            // Optional fields
            String courseCode = courseCodeComboBox.getValue();
            if (courseCode != null && !courseCode.isEmpty()) {
                requestBody.put("code", courseCode);
            }

            String selectedType = typeComboBox.getValue();
            if (selectedType != null && !selectedType.isEmpty()) {
                requestBody.put("exam_type", selectedType);
            }
            
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                String formattedDate = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
                requestBody.put("exam_date", formattedDate);
            }

            PutMethods putMethods = new PutMethods();
            HttpResponse<String> response = putMethods.putExam(requestBody.toString());

            if (response != null) {
                handleUpdateResponse(response);
            } else {
                showError("Error", "Failed to connect to server");
            }
        } catch (Exception e) {
            showError("Error", "Failed to update examination: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleUpdateResponse(HttpResponse<String> response) {
        if (response != null) {
            String responseBody = response.body();
            switch (response.statusCode()) {
                case 200:
                    closeDialog();
                    break;
                case 400:
                    String errorMessage = "Please check your input values.";
                    if (responseBody != null && !responseBody.isEmpty()) {
                        errorMessage += "\nServer message: " + responseBody;
                    }
                    showError("Invalid Input", errorMessage);
                    break;
                case 404:
                    showError("Not Found", "The examination could not be found.");
                    break;
                case 500:
                    String dbErrorMessage = "A database error occurred.";
                    if (responseBody != null && !responseBody.isEmpty()) {
                        dbErrorMessage += "\nServer message: " + responseBody;
                    }
                    showError("Server Error", dbErrorMessage);
                    break;
                default:
                    showError("Update Failed", "Unexpected status code: " + response.statusCode() + 
                        "\nResponse: " + responseBody);
            }
        } else {
            showError("Connection Error", "Failed to connect to the server");
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) courseCodeComboBox.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private boolean validateInputs() {
        if (courseCodeComboBox.getValue() == null || courseCodeComboBox.getValue().trim().isEmpty()) {
            showError("Validation Error", "Course code is required");
            return false;
        }
        
        if (typeComboBox.getValue() == null) {
            showError("Validation Error", "Exam type must be selected");
            return false;
        }
        
        if (datePicker.getValue() == null) {
            showError("Validation Error", "Date must be selected");
            return false;
        }
        
        return true;
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
