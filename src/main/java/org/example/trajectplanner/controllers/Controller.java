package org.example.trajectplanner.controllers;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ResourceBundle;
import java.time.LocalDate;

import org.example.trajectplanner.api.GetMethods;
import org.example.trajectplanner.model.Tentamen;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Controller implements Initializable {
    @FXML
    private VBox tentamenLayout;
    @FXML
    private Button addButton;
    @FXML
    private TextField searchField;
    @FXML
    private Button scoresButton;
    @FXML
    private Button studentsButton;

    private ObservableList<GridPane> examItems = FXCollections.observableArrayList();
    private FilteredList<GridPane> filteredExams;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filteredExams = new FilteredList<>(examItems, p -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredExams.setPredicate(examItem -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                TentamenItemController controller = (TentamenItemController) examItem.getUserData();
                return controller.getCursusNaam().toLowerCase().contains(lowerCaseFilter);
            });
            
            updateExamList();
        });

        loadExams();
        
        addButton.setOnAction(event -> navigateToAdd());
        scoresButton.setOnAction(event -> navigateToScores());
        studentsButton.setOnAction(event -> navigateToStudents());
    }

    private void loadExams() {
        try {
            GetMethods getMethods = new GetMethods();
            HttpResponse<String> response = getMethods.getExams();

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                List<Tentamen> tentamens = mapper.readValue(
                    response.body(), 
                    mapper.getTypeFactory().constructCollectionType(List.class, Tentamen.class)
                );

                tentamens.sort((t1, t2) -> {
                    LocalDate date1 = LocalDate.parse(t1.getDate());
                    LocalDate date2 = LocalDate.parse(t2.getDate());
                    return date1.compareTo(date2);
                });

                examItems.clear();
                for (Tentamen tentamen : tentamens) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/fxml/Tentamen_item.fxml"));
                    GridPane gridPane = fxmlLoader.load();
                    TentamenItemController tic = fxmlLoader.getController();
                    tic.setData(tentamen);
                    gridPane.setUserData(tic);
                    examItems.add(gridPane);
                }
                updateExamList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateExamList() {
        tentamenLayout.getChildren().clear();
        tentamenLayout.getChildren().addAll(filteredExams);
    }

    private void navigateToAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddExamDialog.fxml"));
            Parent root = loader.load();
            
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(addButton.getScene().getWindow());
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            popupStage.setScene(scene);
            popupStage.setTitle("Add Examination");
            popupStage.setMinWidth(800);
            popupStage.setMinHeight(600);
            
            popupStage.showAndWait();
            
            loadExams();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToScores() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/scores-view.fxml"));
            Parent root = loader.load();
            Scene scene = scoresButton.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToStudents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student-view.fxml"));
            Parent root = loader.load();
            Scene scene = studentsButton.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
