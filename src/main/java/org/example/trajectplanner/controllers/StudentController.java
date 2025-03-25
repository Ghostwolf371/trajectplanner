package org.example.trajectplanner.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.example.trajectplanner.model.Student;

public class StudentController {
    private static final String API_URL = "https://trajectplannerapi.dulamari.com/students";
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> studentNumberColumn;
    @FXML private TableColumn<Student, String> firstNameColumn;
    @FXML private TableColumn<Student, String> lastNameColumn;
    @FXML private TableColumn<Student, String> majorColumn;
    @FXML private TableColumn<Student, Integer> cohortColumn;
    @FXML private TableColumn<Student, String> genderColumn;
    @FXML private TableColumn<Student, String> birthdateColumn;
    @FXML private TableColumn<Student, Integer> totalEcColumn;
    @FXML private TextField searchField;

    private ObservableList<Student> students;
    private FilteredList<Student> filteredStudents;

    @FXML
    public void initialize() {
        try {
            mapper.registerModule(new JavaTimeModule());
            
            // Initialize table columns
            studentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));
            firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
            cohortColumn.setCellValueFactory(new PropertyValueFactory<>("cohort"));
            genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
            birthdateColumn.setCellValueFactory(cellData -> {
                LocalDate date = cellData.getValue().getBirthdate();
                return new javafx.beans.property.SimpleStringProperty(date != null ? date.format(dateFormatter) : "");
            });
            totalEcColumn.setCellValueFactory(new PropertyValueFactory<>("totalEc"));

            // Initialize data
            loadStudents();

            // Setup search functionality
            if (searchField != null) {
                searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filterStudents(newValue);
                });
            }

        } catch (Exception e) {
            showError("Error", "Failed to initialize student management: " + e.getMessage());
        }
    }

    private void loadStudents() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            try {
                                List<Student> studentList = mapper.readValue(
                                    response.body(),
                                    mapper.getTypeFactory().constructCollectionType(List.class, Student.class)
                                );
                                
                                Platform.runLater(() -> {
                                    students = FXCollections.observableArrayList(studentList);
                                    filteredStudents = new FilteredList<>(students, b -> true);
                                    studentTable.setItems(filteredStudents);
                                });
                            } catch (Exception e) {
                                Platform.runLater(() -> 
                                    showError("Error", "Failed to parse student data: " + e.getMessage()));
                            }
                        } else {
                            Platform.runLater(() -> 
                                showError("Error", "Failed to load students. Status code: " + response.statusCode()));
                        }
                    })
                    .exceptionally(e -> {
                        Platform.runLater(() -> 
                            showError("Error", "Failed to load students: " + e.getMessage()));
                        return null;
                    });
        } catch (Exception e) {
            showError("Error", "Failed to load students: " + e.getMessage());
        }
    }

    private void filterStudents(String searchText) {
        if (filteredStudents != null) {
            filteredStudents.setPredicate(student -> {
                if (searchText == null || searchText.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = searchText.toLowerCase();
                return student.getStudentNumber().toLowerCase().contains(lowerCaseFilter) ||
                       student.getFirstName().toLowerCase().contains(lowerCaseFilter) ||
                       student.getLastName().toLowerCase().contains(lowerCaseFilter) ||
                       student.getMajor().toLowerCase().contains(lowerCaseFilter);
            });
        }
    }

    @FXML
    private void handleAddStudent(ActionEvent event) {
        showStudentDialog(null);
    }

    @FXML
    private void handleEditStudent(ActionEvent event) {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showError("Error", "Please select a student to edit");
            return;
        }

        showStudentDialog(selectedStudent);
    }

    @FXML
    private void handleDeleteStudent(ActionEvent event) {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showError("Error", "Please select a student to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Student");
        alert.setContentText("Are you sure you want to delete student " + selectedStudent.getFullName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteStudent(selectedStudent);
            }
        });
    }

    private void showStudentDialog(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student-dialog.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            Stage dialogStage = new Stage();
            dialogStage.setTitle(student == null ? "Add New Student" : "Edit Student");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(studentTable.getScene().getWindow());
            dialogStage.setScene(scene);

            StudentDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setStudentController(this);
            if (student != null) {
                controller.setStudent(student);
            }

            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                loadStudents();
            }
        } catch (IOException e) {
            showError("Error", "Failed to show dialog: " + e.getMessage());
        }
    }

    private void deleteStudent(Student student) {
        try {
            String deleteUrl = API_URL + "?student_number=" + student.getStudentNumber();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(deleteUrl))
                    .DELETE()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            Platform.runLater(() -> {
                                loadStudents();
                                showInfo("Success", "Student deleted successfully");
                            });
                        } else {
                            Platform.runLater(() -> 
                                showError("Error", "Failed to delete student. Status code: " + response.statusCode()));
                        }
                    })
                    .exceptionally(e -> {
                        Platform.runLater(() -> 
                            showError("Error", "Failed to delete student: " + e.getMessage()));
                        return null;
                    });
        } catch (Exception e) {
            showError("Error", "Failed to delete student: " + e.getMessage());
        }
    }

    @FXML
    private void navigateToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard-view.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("Navigation Error", "Failed to navigate to dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void navigateToStudents(ActionEvent event) {
        // Already on students page
    }

    @FXML
    private void navigateToExams(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/exam-view.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("Navigation Error", "Failed to navigate to exams: " + e.getMessage());
        }
    }

    @FXML
    private void navigateToCourses(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/course-view.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("Navigation Error", "Failed to navigate to courses: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login_scherm.fxml"));
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("Logout Error", "Failed to logout: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 