package org.example.trajectplanner.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

import org.example.trajectplanner.api.GetMethods;
import org.example.trajectplanner.model.Course;
import org.example.trajectplanner.model.Score;
import org.example.trajectplanner.model.Semester;
import org.example.trajectplanner.model.Tentamen;

import java.util.List;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.event.ActionEvent;

public class DashboardController implements Initializable {
    private static final String STUDENT_NUMBER = "SE/1145/47";
    private final ObjectMapper mapper = new ObjectMapper();
    private final GetMethods getMethods = new GetMethods();

    // Tables
    @FXML private TableView<Course> coursesTable;
    @FXML private TableView<Tentamen> examsTable;
    @FXML private TableView<Score> scoresTable;

    // Course Table Columns
    @FXML private TableColumn<Course, String> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> courseSemesterColumn;
    @FXML private TableColumn<Course, String> courseBlockColumn;
    @FXML private TableColumn<Course, Integer> courseEcColumn;

    // Exam Table Columns
    @FXML private TableColumn<Tentamen, String> examIdColumn;
    @FXML private TableColumn<Tentamen, String> examCourseColumn;
    @FXML private TableColumn<Tentamen, String> examSemesterColumn;
    @FXML private TableColumn<Tentamen, String> examDateColumn;
    @FXML private TableColumn<Tentamen, String> examTypeColumn;

    // Scores Table Columns
    @FXML private TableColumn<Score, String> scoresCourseCodeColumn;
    @FXML private TableColumn<Score, String> scoresCourseColumn;
    @FXML private TableColumn<Score, String> scoresValueColumn;
    @FXML private TableColumn<Score, String> scoresDateColumn;

    // Other UI elements
    @FXML private TextField searchField;
    @FXML private TextField examSearchField;
    @FXML private TextField scoreSearchField;
    @FXML private Label studentNameLabel;
    @FXML private Label totalEcLabel;
    @FXML private Label averageGradeLabel;
    @FXML private Label upcomingExamsLabel;
    @FXML private GridPane semesterGrid;
    @FXML private ComboBox<String> semesterSelector;
    @FXML private Label studentNumberLabel;
    @FXML private Label birthdateLabel;

    private String studentNumber;

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
        
        // Fetch and load all student data
        fetchStudentData(studentNumber);
        
        // Load exams for the student
        loadExams();
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            mapper.registerModule(new JavaTimeModule());
            
            initializeCoursesTable();
            initializeExamsTable();
            initializeScoresTable();
            
            // Set default student number and load data
            this.studentNumber = STUDENT_NUMBER;
            fetchStudentData(studentNumber);
            
            // Load all data
            loadCourses();
            loadExams();
            loadSemesters();
            
            // Setup search listeners
            if (searchField != null) {
                searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filterCourses(newValue);
                });
            }
            
            if (examSearchField != null) {
                examSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filterExams(newValue);
                });
            }

            if (scoreSearchField != null) {
                scoreSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filterScores(newValue);
                });
            }
            
            // Setup semester selector if needed for other purposes
            if (semesterSelector != null) {
                semesterSelector.getItems().addAll("All Semesters", "Semester 1", "Semester 2", "Semester 3", "Semester 4");
                semesterSelector.setValue("All Semesters");
                semesterSelector.setOnAction(e -> filterBySemester());
            }
            
        } catch (Exception e) {
            showError("Error", "Failed to initialize dashboard: " + e.getMessage());
        }
    }

    private void initializeCoursesTable() {
        if (coursesTable == null) {
            return;
        }

        // Initialize table columns with proper cell value factories
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        courseSemesterColumn.setCellValueFactory(new PropertyValueFactory<>("semesterName"));
        courseBlockColumn.setCellValueFactory(new PropertyValueFactory<>("block"));
        courseEcColumn.setCellValueFactory(new PropertyValueFactory<>("ec"));
        
        // Set column widths
        courseCodeColumn.setPrefWidth(150);
        courseNameColumn.setPrefWidth(400);
        courseSemesterColumn.setPrefWidth(150);
        courseBlockColumn.setPrefWidth(120);
        courseEcColumn.setPrefWidth(120);
        
        // Ensure consistent cell alignment - ALL CENTER
        courseCodeColumn.setStyle("-fx-alignment: CENTER;");
        courseNameColumn.setStyle("-fx-alignment: CENTER;");
        courseSemesterColumn.setStyle("-fx-alignment: CENTER;");
        courseBlockColumn.setStyle("-fx-alignment: CENTER;");
        courseEcColumn.setStyle("-fx-alignment: CENTER;");
    }

    private void loadCourses() {
        try {
            HttpResponse<String> response = getMethods.getCourses();
            
            if (response != null && response.statusCode() == 200) {
                List<Course> courses = mapper.readValue(
                    response.body(), 
                    new TypeReference<List<Course>>() {}
                );
                coursesTable.setItems(FXCollections.observableArrayList(courses));
            } else {
                showError("Error", "Failed to load courses");
            }
        } catch (Exception e) {
            showError("Error", "Failed to load courses: " + e.getMessage());
        }
    }

    private void filterCourses(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            loadCourses();
            return;
        }

        try {
            HttpResponse<String> response = getMethods.getCourses();
            if (response != null && response.statusCode() == 200) {
                List<Course> allCourses = mapper.readValue(
                    response.body(), 
                    new TypeReference<List<Course>>() {}
                );
                
                List<Course> filteredCourses = allCourses.stream()
                    .filter(course -> 
                        course.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                        course.getCode().toLowerCase().contains(searchText.toLowerCase()) ||
                        course.getSemesterName().toLowerCase().contains(searchText.toLowerCase())
                    )
                    .toList();
                
                coursesTable.setItems(FXCollections.observableArrayList(filteredCourses));
            }
        } catch (Exception e) {
            showError("Error", "Failed to filter courses: " + e.getMessage());
        }
    }

    private void loadStudentInfo() {
        try {
            if (studentNumber == null) {
                return;
            }
            
            // Get student scores
            String formattedStudentNumber = studentNumber.replace("/", "-");
            HttpResponse<String> response = getMethods.getScoresByStudentNumber(formattedStudentNumber);
            
            if (response != null && response.statusCode() == 200) {
                List<Score> scores = mapper.readValue(
                    response.body(),
                    mapper.getTypeFactory().constructCollectionType(List.class, Score.class)
                );
                
                // Calculate average grade
                double averageGrade = scores.stream()
                    .mapToDouble(score -> Double.parseDouble(score.getScoreValue()))
                    .average()
                    .orElse(0.0);
                
                // Count total EC (assuming each course gives EC)
                long totalEC = scores.stream()
                    .map(Score::getCourseCode)
                    .distinct()
                    .count() * 5; // Assuming each course is 5 EC
                
                // Update UI
                studentNameLabel.setText("Student " + studentNumber);
                totalEcLabel.setText(totalEC + "/60 EC");
                averageGradeLabel.setText(String.format("%.1f", averageGrade));
            } else {
                showError("Error", "Failed to load student scores");
            }
            
            // Load upcoming exams count
            loadUpcomingExamsCount();
        } catch (Exception e) {
            showError("Error", "Failed to load student information: " + e.getMessage());
        }
    }

    private void loadUpcomingExamsCount() {
        try {
            HttpResponse<String> response = getMethods.getExams();
            if (response != null && response.statusCode() == 200) {
                List<Tentamen> exams = mapper.readValue(
                    response.body(),
                    mapper.getTypeFactory().constructCollectionType(List.class, Tentamen.class)
                );
                
                // Filter exams in the next 30 days
                LocalDate now = LocalDate.now();
                LocalDate thirtyDaysFromNow = now.plusDays(30);
                
                long upcomingCount = exams.stream()
                    .filter(exam -> {
                        String examDateStr = exam.getDate(); // Changed from getDatum()
                        if (examDateStr == null || examDateStr.trim().isEmpty()) {
                            return false;
                        }
                        try {
                            LocalDate examDate = LocalDate.parse(examDateStr);
                            return !examDate.isBefore(now) && !examDate.isAfter(thirtyDaysFromNow);
                        } catch (Exception e) {
                            // Log invalid date format if needed
                            return false;
                        }
                    })
                    .count();
                
                upcomingExamsLabel.setText(String.valueOf(upcomingCount));
            } else {
                upcomingExamsLabel.setText("0");
                showError("Error", "Failed to load upcoming exams");
            }
        } catch (Exception e) {
            upcomingExamsLabel.setText("0");
            showError("Error", "Failed to load upcoming exams: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void initializeExamsTable() {
        if (examsTable == null) {
            return;
        }

        examIdColumn.setVisible(false);
        
        examCourseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        examSemesterColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty("Semester " + cellData.getValue().getSemester()));
        examDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        examTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Set column widths
        examCourseColumn.setPrefWidth(400);
        examSemesterColumn.setPrefWidth(150);
        examDateColumn.setPrefWidth(150);
        examTypeColumn.setPrefWidth(150);
        
        // Ensure consistent cell alignment - ALL CENTER
        examCourseColumn.setStyle("-fx-alignment: CENTER;");
        examSemesterColumn.setStyle("-fx-alignment: CENTER;");
        examDateColumn.setStyle("-fx-alignment: CENTER;");
        examTypeColumn.setStyle("-fx-alignment: CENTER;");

        // Format the date display
        examDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null || date.trim().isEmpty()) {
                    setText(null);
                } else {
                    try {
                        LocalDate localDate = LocalDate.parse(date);
                        setText(localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    } catch (Exception e) {
                        setText(date);
                    }
                }
                setAlignment(javafx.geometry.Pos.CENTER);
            }
        });

        examSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterExams(newValue);
        });
    }

    private void loadExams() {
        try {
            HttpResponse<String> response = getMethods.getExams();
            
            if (response != null && response.statusCode() == 200) {
                List<Tentamen> exams = mapper.readValue(
                    response.body(),
                    mapper.getTypeFactory().constructCollectionType(List.class, Tentamen.class)
                );
                
                if (exams.isEmpty()) {
                    return;
                }

                // Filter for upcoming exams only
                LocalDate today = LocalDate.now();
                List<Tentamen> upcomingExams = exams.stream()
                    .filter(exam -> {
                        try {
                            LocalDate examDate = LocalDate.parse(exam.getDate());
                            return !examDate.isBefore(today);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .sorted((exam1, exam2) -> {
                        try {
                            LocalDate date1 = LocalDate.parse(exam1.getDate());
                            LocalDate date2 = LocalDate.parse(exam2.getDate());
                            return date1.compareTo(date2);
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .collect(Collectors.toList());
                
                examsTable.setItems(FXCollections.observableArrayList(upcomingExams));
                examsTable.refresh();
            } else {
                showError("Error", "Failed to load exams");
            }
        } catch (Exception e) {
            showError("Error", "Failed to load exams: " + e.getMessage());
        }
    }

    private void filterExams(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            // If search is empty, show all exams
            loadExams();
            return;
        }

        // Filter exams based on search text
        List<Tentamen> filteredExams = examsTable.getItems().stream()
            .filter(exam -> 
                exam.getCourseName().toLowerCase().contains(searchText.toLowerCase()) ||
                String.valueOf(exam.getSemester()).contains(searchText) ||
                exam.getType().toLowerCase().contains(searchText.toLowerCase())
            )
            .collect(Collectors.toList());

        examsTable.setItems(FXCollections.observableArrayList(filteredExams));
    }

    private void loadSemesters() {
        try {
            HttpResponse<String> response = getMethods.getSemesters();
            if (response != null && response.statusCode() == 200) {
                List<Semester> semesters = mapper.readValue(
                    response.body(),
                    mapper.getTypeFactory().constructCollectionType(List.class, Semester.class)
                );
                displaySemesters(semesters);
            }
        } catch (Exception e) {
            showError("Error", "Failed to load semesters: " + e.getMessage());
        }
    }

    private void displaySemesters(List<Semester> semesters) {
        semesterGrid.getChildren().clear();
        semesterGrid.getColumnConstraints().clear();
        semesterGrid.getRowConstraints().clear();
        
        // Set column constraints to make columns evenly spaced
        for (int i = 0; i < 4; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.SOMETIMES);
            columnConstraints.setFillWidth(true);
            columnConstraints.setPercentWidth(25); // 4 columns of equal width
            semesterGrid.getColumnConstraints().add(columnConstraints);
        }
        
        int row = 0;
        int col = 0;

        for (Semester semester : semesters) {
            VBox semesterBox = createSemesterBox(semester);
            semesterGrid.add(semesterBox, col, row);
            GridPane.setMargin(semesterBox, new Insets(10));
            
            col++;
            if (col > 3) {  // 4 semesters per row
                col = 0;
                row++;
            }
        }
    }

    private VBox createSemesterBox(Semester semester) {
        // Main container with consistent size
        VBox box = new VBox(10);
        box.getStyleClass().add("semester-card");
        box.setPadding(new Insets(15));
        box.setMinWidth(280);
        box.setMaxWidth(320);
        box.setPrefHeight(280);
        
        // Header with semester information
        Label titleLabel = new Label(String.format("Semester %d", semester.getSemester()));
        titleLabel.getStyleClass().add("semester-title");
        
        Label subtitleLabel = new Label(semester.getSemesterName());
        subtitleLabel.getStyleClass().add("semester-subtitle");
        
        // Stat container with EC count and course count
        HBox statsContainer = new HBox(20);
        statsContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        statsContainer.setPadding(new Insets(0, 0, 8, 0));
        
        // Calculate total EC
        int totalEC = 0;
        for (var course : semester.getCourses()) {
            totalEC += course.getEc();
        }
        
        // EC display 
        Label ecLabel = new Label(totalEC + " EC");
        ecLabel.getStyleClass().add("semester-stats-value");
        
        // Course count display
        Label courseCountLabel = new Label(semester.getCourses().size() + " Courses");
        courseCountLabel.getStyleClass().add("semester-stats-value");
        
        statsContainer.getChildren().addAll(ecLabel, courseCountLabel);
        
        // Course list container
        VBox courseContainer = new VBox(6);
        courseContainer.setPadding(new Insets(0, 0, 0, 0));
        
        // Course list header
        Label coursesHeaderLabel = new Label("Courses");
        coursesHeaderLabel.getStyleClass().add("semester-courses-header");
        courseContainer.getChildren().add(coursesHeaderLabel);
        
        // Create a VBox to hold all courses
        VBox courseList = new VBox(5);
        courseList.getStyleClass().add("course-list");
        
        // Add each course as a clean card-like item
        for (var course : semester.getCourses()) {
            HBox courseItem = new HBox();
            courseItem.getStyleClass().add("course-item");
            courseItem.setPrefHeight(34);
            courseItem.setMinHeight(34);
            courseItem.setMaxHeight(34);
            courseItem.setPadding(new Insets(0, 5, 0, 10));
            courseItem.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            courseItem.setSpacing(15); // Add spacing between elements
            
            Label codeLabel = new Label(course.getCourseCode());
            codeLabel.getStyleClass().add("course-code");
            codeLabel.setMinWidth(70); // Increase minimum width
            codeLabel.setPrefWidth(70); // Set preferred width
            
            Label nameLabel = new Label(course.getCourseName());
            nameLabel.getStyleClass().add("course-name");
            nameLabel.setWrapText(false);
            nameLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            nameLabel.setMaxWidth(170);
            
            HBox.setHgrow(nameLabel, Priority.ALWAYS);
            courseItem.getChildren().addAll(codeLabel, nameLabel);
            courseList.getChildren().add(courseItem);
        }
        
        // Add course list to a scroll pane
        ScrollPane scrollPane = new ScrollPane(courseList);
        scrollPane.getStyleClass().add("course-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefHeight(140);
        scrollPane.setMinHeight(140);
        scrollPane.setMaxHeight(140);
        
        // Add the scroll pane to the course container
        courseContainer.getChildren().add(scrollPane);
        
        // Add all components to the main container
        box.getChildren().addAll(titleLabel, subtitleLabel, statsContainer, courseContainer);
        
        return box;
    }

    private void initializeScoresTable() {
        scoresCourseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        scoresCourseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        scoresValueColumn.setCellValueFactory(new PropertyValueFactory<>("scoreValue"));
        scoresDateColumn.setCellValueFactory(new PropertyValueFactory<>("scoreDateTime"));
        
        // Set column widths
        scoresCourseCodeColumn.setPrefWidth(150);
        scoresCourseColumn.setPrefWidth(400);
        scoresValueColumn.setPrefWidth(120);
        scoresDateColumn.setPrefWidth(150);
        
        // Ensure consistent cell alignment - ALL CENTER
        scoresCourseCodeColumn.setStyle("-fx-alignment: CENTER;");
        scoresCourseColumn.setStyle("-fx-alignment: CENTER;");
        scoresValueColumn.setStyle("-fx-alignment: CENTER;");
        scoresDateColumn.setStyle("-fx-alignment: CENTER;");

        // Format the date display
        scoresDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null || date.trim().isEmpty()) {
                    setText(null);
                } else {
                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(date, 
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        setText(dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    } catch (Exception e) {
                        setText(date);
                    }
                }
                setAlignment(javafx.geometry.Pos.CENTER);
            }
        });

        // Set default sorting by date (most recent first)
        scoresDateColumn.setSortType(TableColumn.SortType.DESCENDING);
        scoresTable.getSortOrder().add(scoresDateColumn);
    }

    private void loadScores() {
        try {
            String formattedStudentNumber = studentNumber.replace("/", "-");
            HttpResponse<String> response = getMethods.getScoresByStudentNumber(formattedStudentNumber);
            
            if (response != null && response.statusCode() == 200) {
                String responseBody = response.body();
                List<Score> scores = new java.util.ArrayList<>();
                
                // Check if response is an array or an object
                JsonNode jsonNode = mapper.readTree(responseBody);
                if (jsonNode.isArray()) {
                    // Handle array response (list of scores)
                    scores = mapper.readValue(
                        responseBody,
                        mapper.getTypeFactory().constructCollectionType(List.class, Score.class)
                    );
                } else if (jsonNode.isObject()) {
                    // Check if it's an error message
                    if (jsonNode.has("status") && jsonNode.get("status").asText().equals("error")) {
                        // Clear the table and show no data
                        scoresTable.setItems(FXCollections.observableArrayList());
                        scoresTable.setPlaceholder(new Label("You don't have any scores recorded yet"));
                        return;
                    }
                    
                    // Check if this is actually a score object and add it if so
                    if (jsonNode.has("courseCode") && jsonNode.has("courseName")) {
                        Score singleScore = mapper.readValue(responseBody, Score.class);
                        scores.add(singleScore);
                    }
                }
                
                if (scores.isEmpty()) {
                    scoresTable.setItems(FXCollections.observableArrayList());
                    scoresTable.setPlaceholder(new Label("You don't have any scores recorded yet"));
                    return;
                }
                
                // Sort scores by date (most recent first)
                scores.sort((score1, score2) -> {
                    try {
                        LocalDateTime date1 = LocalDateTime.parse(score1.getScoreDateTime(), 
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime date2 = LocalDateTime.parse(score2.getScoreDateTime(), 
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        return date2.compareTo(date1); // Reverse order for most recent first
                    } catch (Exception e) {
                        return 0;
                    }
                });
                
                scoresTable.setItems(FXCollections.observableArrayList(scores));
                scoresTable.refresh();
            } else {
                scoresTable.setItems(FXCollections.observableArrayList());
                scoresTable.setPlaceholder(new Label("Unable to connect to the grading system"));
            }
        } catch (Exception e) {
            scoresTable.setItems(FXCollections.observableArrayList());
            scoresTable.setPlaceholder(new Label("Unable to load your scores at this time"));
        }
    }

    private void filterScores(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            loadScores(); // Reset to all scores
            return;
        }

        try {
            String formattedStudentNumber = studentNumber.replace("/", "-");
            HttpResponse<String> response = getMethods.getScoresByStudentNumber(formattedStudentNumber);
            
            if (response != null && response.statusCode() == 200) {
                String responseBody = response.body();
                List<Score> allScores = new java.util.ArrayList<>();
                
                // Check if response is an array or an object
                JsonNode jsonNode = mapper.readTree(responseBody);
                if (jsonNode.isArray()) {
                    // Handle array response (list of scores)
                    allScores = mapper.readValue(
                        responseBody,
                        mapper.getTypeFactory().constructCollectionType(List.class, Score.class)
                    );
                } else if (jsonNode.isObject()) {
                    // Check if it's an error message
                    if (jsonNode.has("status") && jsonNode.get("status").asText().equals("error")) {
                        // Clear the table and show no data
                        scoresTable.setItems(FXCollections.observableArrayList());
                        scoresTable.setPlaceholder(new Label("You don't have any scores recorded yet"));
                        return;
                    }
                    
                    // Check if this is actually a score object and add it if so
                    if (jsonNode.has("courseCode") && jsonNode.has("courseName")) {
                        Score singleScore = mapper.readValue(responseBody, Score.class);
                        allScores.add(singleScore);
                    }
                }
                
                if (allScores.isEmpty()) {
                    scoresTable.setItems(FXCollections.observableArrayList());
                    scoresTable.setPlaceholder(new Label("You don't have any scores recorded yet"));
                    return;
                }
                
                List<Score> filteredScores = allScores.stream()
                    .filter(score -> 
                        score.getCourseCode().toLowerCase().contains(searchText.toLowerCase()) ||
                        score.getCourseName().toLowerCase().contains(searchText.toLowerCase()) ||
                        score.getScoreValue().toLowerCase().contains(searchText.toLowerCase()) ||
                        (score.getDate() != null && score.getDate().toLowerCase().contains(searchText.toLowerCase()))
                    )
                    .toList();
                
                scoresTable.setItems(FXCollections.observableArrayList(filteredScores));
                
                if (filteredScores.isEmpty()) {
                    scoresTable.setPlaceholder(new Label("No results found for \"" + searchText + "\""));
                }
            } else {
                scoresTable.setItems(FXCollections.observableArrayList());
                scoresTable.setPlaceholder(new Label("Unable to connect to the grading system"));
            }
        } catch (Exception e) {
            scoresTable.setItems(FXCollections.observableArrayList());
            scoresTable.setPlaceholder(new Label("Unable to load your scores at this time"));
        }
    }

    private void calculateAndDisplayAverageGrade() {
        try {
            String formattedStudentNumber = studentNumber.replace("/", "-");
            HttpResponse<String> response = getMethods.getScoresByStudentNumber(formattedStudentNumber);
            
            if (response != null && response.statusCode() == 200) {
                String responseBody = response.body();
                List<Score> scores = new java.util.ArrayList<>();
                
                // Check if response is an array or an object
                JsonNode jsonNode = mapper.readTree(responseBody);
                if (jsonNode.isArray()) {
                    // Handle array response (list of scores)
                    scores = mapper.readValue(
                        responseBody,
                        mapper.getTypeFactory().constructCollectionType(List.class, Score.class)
                    );
                } else if (jsonNode.isObject()) {
                    // Check if it's an error message
                    if (jsonNode.has("status") && jsonNode.get("status").asText().equals("error")) {
                        // Display N/A for average grade
                        averageGradeLabel.setText("N/A");
                        return;
                    }
                    
                    // Check if this is actually a score object and add it if so
                    if (jsonNode.has("courseCode") && jsonNode.has("courseName")) {
                        Score singleScore = mapper.readValue(responseBody, Score.class);
                        scores.add(singleScore);
                    }
                }
                
                if (!scores.isEmpty()) {
                    double sum = 0;
                    int count = 0;
                    
                    for (Score score : scores) {
                        try {
                            double value = Double.parseDouble(score.getScoreValue());
                            sum += value;
                            count++;
                        } catch (NumberFormatException e) {
                            // Skip non-numeric scores
                        }
                    }
                    
                    if (count > 0) {
                        double average = sum / count;
                        averageGradeLabel.setText(String.format("%.1f", average));
                    } else {
                        averageGradeLabel.setText("N/A");
                    }
                } else {
                    averageGradeLabel.setText("N/A");
                }
            } else {
                averageGradeLabel.setText("N/A");
            }
        } catch (Exception e) {
            showError("Error", "Failed to calculate average grade: " + e.getMessage());
            averageGradeLabel.setText("N/A");
        }
    }

    private void filterBySemester() {
        String selectedSemester = semesterSelector.getValue();
        if (selectedSemester == null || selectedSemester.equals("All Semesters")) {
            loadExams();
            return;
        }

        try {
            HttpResponse<String> response = getMethods.getExams();
            if (response != null && response.statusCode() == 200) {
                List<Tentamen> allExams = mapper.readValue(
                    response.body(),
                    new TypeReference<List<Tentamen>>() {}
                );
                List<Tentamen> filteredExams = allExams.stream()
                    .filter(exam -> String.valueOf(exam.getSemester()).equals(selectedSemester))
                    .toList();
                
                examsTable.setItems(FXCollections.observableArrayList(filteredExams));
            }
        } catch (Exception e) {
            showError("Error", "Failed to filter exams by semester: " + e.getMessage());
        }
    }

    private void fetchStudentData(String studentNumber) {
        try {
            if (studentNumber == null) {
                return;
            }
            
            // Format student number correctly with dashes for API call (SE/1145/47 -> SE-1145-47)
            String formattedStudentNumber = studentNumber.replace("/", "-");
            
            HttpResponse<String> response = getMethods.getStudentByNumber(formattedStudentNumber);
            
            if (response != null && response.statusCode() == 200) {
                String responseBody = response.body();
                
                // The API returns an array with a single student object
                JsonNode studentsArray = mapper.readTree(responseBody);
                if (studentsArray.isArray() && studentsArray.size() > 0) {
                    JsonNode studentNode = studentsArray.get(0);
                    
                    // Update student name label with full name from API
                    if (studentNameLabel != null) {
                        String firstName = studentNode.has("first_name") ? studentNode.get("first_name").asText() : "";
                        String lastName = studentNode.has("last_name") ? studentNode.get("last_name").asText() : "";
                        String fullName = firstName + " " + lastName;
                        studentNameLabel.setText(fullName.trim());
                    }
                    
                    // Update student number
                    if (studentNumberLabel != null) {
                        studentNumberLabel.setText(studentNumber);
                    }
                    
                    // Update birthdate with properly formatted date
                    if (birthdateLabel != null && studentNode.has("birthdate")) {
                        String birthDate = studentNode.get("birthdate").asText();
                        try {
                            // Format the date from API (assuming format is YYYY-MM-DD)
                            LocalDate date = LocalDate.parse(birthDate);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            birthdateLabel.setText(date.format(formatter));
                        } catch (Exception e) {
                            birthdateLabel.setText(birthDate); // Use as-is if parsing fails
                        }
                    }
                    
                    // Update total EC if available
                    if (totalEcLabel != null && studentNode.has("total_ec")) {
                        int totalEC = studentNode.get("total_ec").asInt();
                        totalEcLabel.setText(totalEC + " EC");
                    }
                } else {
                    setDefaultValues(studentNumber);
                }
                
                // Also load scores to calculate average grade
                loadScores();
                calculateAndDisplayAverageGrade();
            } else {
                setDefaultValues(studentNumber);
            }
        } catch (Exception e) {
            showError("Error", "Failed to fetch student data: " + e.getMessage());
        }
    }
    
    private void setDefaultValues(String studentNumber) {
        // Set default values if data can't be fetched
        if (studentNameLabel != null) {
            studentNameLabel.setText("Student");
        }
        if (studentNumberLabel != null) {
            studentNumberLabel.setText(studentNumber);
        }
        if (birthdateLabel != null) {
            birthdateLabel.setText("--");
        }
        if (totalEcLabel != null) {
            totalEcLabel.setText("0 EC");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Get the current stage
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            
            // Load the login screen FXML - corrected path
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_scherm.fxml"));
            Scene loginScene = new Scene(loader.load());
            
            // Set the login scene on the current stage
            currentStage.setScene(loginScene);
            currentStage.setTitle("TrajectPlanner - Login");
            currentStage.show();
            
            // Clear any stored user data/session if needed
            // ...
            
        } catch (IOException e) {
            showError("Navigation Error", "Could not load login screen: " + e.getMessage());
        }
    }
}
