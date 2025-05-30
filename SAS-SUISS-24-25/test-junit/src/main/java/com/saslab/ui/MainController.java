package com.saslab.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import com.saslab.Recipe;
import com.saslab.RecipeBook;
import com.saslab.controller.RecipeController;
import com.saslab.controller.MenuController;
import com.saslab.controller.EventController;
import com.saslab.controller.TaskController;

import java.time.LocalDate;

public class MainController {

    @FXML private MenuItem esciMenuItem;
    @FXML private TabPane tabPane;

    @FXML private TableView<Recipe> recipeTableView;
    @FXML private TableColumn<Recipe, String> nameColumn;
    @FXML private TableColumn<Recipe, Recipe.RecipeState> stateColumn;

    @FXML private Button newRecipeButton;
    @FXML private Button newMenuButton;
    @FXML private Button newEventButton;
    @FXML private Button assignTasksButton;

    private final RecipeController recipeController = new RecipeController();
    private final RecipeBook recipeBook           = RecipeBook.getInstance();
    private final MenuController menuController   = new MenuController(recipeController);
    private final EventController eventController = new EventController();
    private final TaskController taskController   = new TaskController();

    @FXML
    public void initialize() {
        // Allineo le colonne ai nomi dei getter getName() e getState()
        nameColumn .setCellValueFactory(new PropertyValueFactory<>("name"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));

        // Carico la lista iniziale
        ObservableList<Recipe> data =
            FXCollections.observableArrayList(recipeBook.getAllRecipes());
        recipeTableView.setItems(data);
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) tabPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleNewRecipe(ActionEvent event) {
        // firma: createRecipe(String ownerId, String name, String description, int preparationTime)
        recipeController.createRecipe("system", "Esempio Ricetta", "Descrizione demo", 15);
        recipeTableView.setItems(FXCollections.observableArrayList(
            recipeBook.getAllRecipes()
        ));
    }

    @FXML
    private void handleNewMenu(ActionEvent event) {
        // firma: createMenu(String chefId, String name)
        menuController.createMenu("chef1", "Esempio Menu");
    }

    @FXML
    private void handleNewEvent(ActionEvent event) {
        // firma: createEvent(String organizerId, String eventName, String location, LocalDate startDate, int expectedGuests)
        eventController.createEvent("organizer1", "Esempio Evento", "Luogo Demo",
                                    LocalDate.now(), 50);
    }

    @FXML
    private void handleAssignTasks(ActionEvent event) {
        // firma: assignTaskToCook(String taskID, String cookID, String shiftID, int timeEstimate, int quantity)
        taskController.assignTaskToCook("task1", "cook1", "shift1", 30, 1);
    }
}
