package org.example.trajectplanner.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.trajectplanner.model.Student;
import org.example.trajectplanner.api.GetMethods;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

public class StudentController {
    private static final String API_URL = "https://trajectplannerapi.dulamari.com/students";
    private final ObjectMapper mapper = new ObjectMapper();
    private final GetMethods getMethods = new GetMethods();
    private final HttpClient client = HttpClient.newHttpClient();
    
    @FXML private VBox studentsLayout;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button scoresButton;
    @FXML private Button examsButton;

    private ObservableList<Student> students;
    private FilteredList<Student> filteredStudents;

    @FXML
    private void initialize() {
        students = FXCollections.observableArrayList();
        filteredStudents = new FilteredList<>(students, p -> true);
        
        setupSearch();
        setupButtons();
        loadStudents();
    }

    private void setupButtons() {
        addButton.setOnAction(event -> handleAddStudent());
        if (scoresButton != null) {
            scoresButton.setOnAction(event -> navigateToScores(event));
        }
        if (examsButton != null) {
            examsButton.setOnAction(event -> navigateToExams(event));
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = newValue.toLowerCase();
            filteredStudents.setPredicate(student -> {
                if (searchText == null || searchText.isEmpty()) {
                    return true;
                }
                
                return student.getStudentNumber().toLowerCase().contains(searchText) ||
                       student.getFirstName().toLowerCase().contains(searchText) ||
                       student.getLastName().toLowerCase().contains(searchText);
            });
            
            updateStudentList();
        });
    }

    private void loadStudents() {
        try {
            HttpResponse<String> response = getMethods.getStudents();
            
            if (response != null && response.statusCode() == 200) {
                List<Student> studentList = mapper.readValue(
                    response.body(),
                    new TypeReference<List<Student>>() {}
                );

                Platform.runLater(() -> {
                    students.clear();
                    students.addAll(studentList);
                    updateStudentList();
                });
            } else {
                showError("Error", "Failed to load students");
            }
        } catch (Exception e) {
            showError("Error", "Failed to load students: " + e.getMessage());
        }
    }

    private void updateStudentList() {
        studentsLayout.getChildren().clear();
        
        if (filteredStudents.isEmpty()) {
            Label noDataLabel = new Label("No students found");
            noDataLabel.setStyle("-fx-padding: 20; -fx-font-size: 14;");
            studentsLayout.getChildren().add(noDataLabel);
            return;
        }

        for (Student student : filteredStudents) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Student_item.fxml"));
                Node studentNode = loader.load();
                StudentItemController controller = loader.getController();
                controller.setStudent(student);
                controller.setParentController(this);
                studentNode.setUserData(controller);
                studentsLayout.getChildren().add(studentNode);
            } catch (IOException e) {
                System.err.println("Error loading student item: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddStudentDialog.fxml"));
            Parent root = loader.load();
            
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(addButton.getScene().getWindow());
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            popupStage.setScene(scene);
            popupStage.setTitle("Add Student");
            popupStage.setMinWidth(900);
            popupStage.setMinHeight(600);
            popupStage.setResizable(true);
            
            popupStage.showAndWait();
            loadStudents(); // Refresh the list after adding
        } catch (Exception e) {
            showError("Error", "Could not open add student dialog: " + e.getMessage());
        }
    }

    @FXML
    private void navigateToScores(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/scores-view.fxml"));
            Parent root = loader.load();
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("Error", "Could not navigate to scores view: " + e.getMessage());
        }
    }

    @FXML
    private void navigateToExams(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/hello-view.fxml"));
            Parent root = loader.load();
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("Error", "Could not navigate to exams view: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void refreshStudents() {
        loadStudents();
    }
} 
