package org.example.trajectplanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import org.example.trajectplanner.API.GetMethods;
import org.example.trajectplanner.Modal.Score;
import org.example.trajectplanner.utils.DialogUtils;

public class ScoresController {
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button examinationsButton;
    @FXML private VBox scoresLayout;

    private ObservableList<GridPane> scoreItems;
    private FilteredList<GridPane> filteredScores;
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        scoreItems = FXCollections.observableArrayList();
        filteredScores = new FilteredList<>(scoreItems, p -> true);
        setupSearch();
        setupButtons();
        loadScores();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredScores.setPredicate(scoreItem -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                ScoreItemController controller = (ScoreItemController) scoreItem.getUserData();
                
                return (controller.getStudentNumber().toLowerCase().contains(lowerCaseFilter)) ||
                       (controller.getCourseName().toLowerCase().contains(lowerCaseFilter));
            });
            
            updateScoreList();
        });
    }

    private void setupButtons() {
        addButton.setOnAction(event -> navigateToAddScore());
        examinationsButton.setOnAction(event -> navigateToExaminations());
    }

    public void loadScores() {
        try {
            GetMethods getMethods = new GetMethods();
            var response = getMethods.getScores();

            if (response != null && response.statusCode() == 200) {
                List<Score> scores = mapper.readValue(
                    response.body(), 
                    mapper.getTypeFactory().constructCollectionType(List.class, Score.class)
                );

                scoreItems.clear();
                scores.stream()
                    .filter(this::isValidScore)
                    .forEach(this::addScoreItem);
                updateScoreList();
            }
        } catch (Exception e) {
            DialogUtils.showError("Error", "Failed to load scores: " + e.getMessage());
        }
    }

    private boolean isValidScore(Score score) {
        return score.getId() != null && score.getStudentNumber() != null;
    }

    private void addScoreItem(Score score) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Score_item.fxml"));
            GridPane gridPane = loader.load();
            ScoreItemController controller = loader.getController();
            controller.setData(score);
            gridPane.setUserData(controller);
            scoreItems.add(gridPane);
        } catch (IOException e) {
            DialogUtils.showError("Error", "Failed to load score item: " + e.getMessage());
        }
    }

    private void updateScoreList() {
        scoresLayout.getChildren().clear();
        scoresLayout.getChildren().addAll(filteredScores);
    }

    private void navigateToAddScore() {
        try {
            DialogUtils.showDialog(
                "/org/example/trajectplanner/AddScoreDialog.fxml",
                "Add New Score",
                addButton.getScene().getWindow()
            );
            loadScores();
        } catch (Exception e) {
            DialogUtils.showError("Error", "Failed to open add score dialog: " + e.getMessage());
        }
    }

    private void navigateToExaminations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            examinationsButton.getScene().setRoot(loader.load());
        } catch (IOException e) {
            DialogUtils.showError("Error", "Failed to navigate to examinations: " + e.getMessage());
        }
    }
}
