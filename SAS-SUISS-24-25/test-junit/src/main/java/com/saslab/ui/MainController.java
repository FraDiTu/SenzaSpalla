package com.saslab.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import com.saslab.Recipe;
import com.saslab.RecipeBook;
import com.saslab.controller.*;
import com.saslab.model.*;
import com.saslab.model.Menu;
import com.saslab.model.MenuItem;
import com.saslab.service.SessionManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller principale dell'applicazione Cat & Ring
 */
public class MainController {

    // Menu e controlli generali
    @FXML private MenuItem logoutMenuItem;
    @FXML private Label userInfoLabel;
    @FXML private TabPane mainTabPane;

    // Tab Ricette
    @FXML private TableView<Recipe> recipeTableView;
    @FXML private TableColumn<Recipe, String> recipeNameColumn;
    @FXML private TableColumn<Recipe, Recipe.RecipeState> recipeStateColumn;
    @FXML private TableColumn<Recipe, String> recipeOwnerColumn;
    @FXML private Button newRecipeButton;
    @FXML private Button editRecipeButton;
    @FXML private Button deleteRecipeButton;

    // Tab Menù
    @FXML private TableView<Menu> menuTableView;
    @FXML private TableColumn<Menu, String> menuNameColumn;
    @FXML private TableColumn<Menu, Menu.MenuState> menuStateColumn;
    @FXML private TableColumn<Menu, String> menuChefColumn;
    @FXML private Button newMenuButton;
    @FXML private Button editMenuButton;
    @FXML private Button deleteMenuButton;

    // Tab Eventi
    @FXML private TableView<Event> eventTableView;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, Event.EventState> eventStateColumn;
    @FXML private TableColumn<Event, String> eventOrganizerColumn;
    @FXML private TableColumn<Event, LocalDate> eventDateColumn;
    @FXML private Button newEventButton;
    @FXML private Button editEventButton;
    @FXML private Button deleteEventButton;

    // Tab Compiti
    @FXML private TableView<Task> taskTableView;
    @FXML private TableColumn<Task, String> taskDescriptionColumn;
    @FXML private TableColumn<Task, Task.TaskType> taskTypeColumn;
    @FXML private TableColumn<Task, String> taskStatusColumn;
    @FXML private Button assignTasksButton;
    @FXML private Button viewTaskProgressButton;

    // Controllers
    private final RecipeController recipeController = new RecipeController();
    private final RecipeBook recipeBook = RecipeBook.getInstance();
    private final MenuController menuController = new MenuController(recipeController);
    private final EventController eventController = new EventController();
    private final TaskController taskController = new TaskController();
    private final UserController userController = new UserController();
    private final SessionManager sessionManager = SessionManager.getInstance();
    
    // Stato corrente
    private User currentUser;
    private String currentSessionId;

    @FXML
    public void initialize() {
        setupRecipeTable();
        setupMenuTable();
        setupEventTable();
        setupTaskTable();
    }
    
    /**
     * Inizializza il controller con l'utente corrente
     */
    public void initializeWithUser(User user, String sessionId) {
        this.currentUser = user;
        this.currentSessionId = sessionId;
        
        updateUserInterface();
        loadInitialData();
        setupUserSpecificUI();
    }
    
    private void updateUserInterface() {
        userInfoLabel.setText(String.format("%s (%s)", 
                             currentUser.getName(), 
                             currentUser.getRole().getDisplayName()));
    }
    
    private void setupUserSpecificUI() {
        // Abilita/disabilita controlli in base al ruolo
        switch (currentUser.getRole()) {
            case ORGANIZER:
                // Gli organizzatori possono fare tutto
                break;
                
            case CHEF:
                // Gli chef non possono creare eventi
                newEventButton.setDisable(true);
                editEventButton.setDisable(true);
                deleteEventButton.setDisable(true);
                break;
                
            case COOK:
                // I cuochi possono solo vedere ricette e compiti
                newMenuButton.setDisable(true);
                editMenuButton.setDisable(true);
                deleteMenuButton.setDisable(true);
                newEventButton.setDisable(true);
                editEventButton.setDisable(true);
                deleteEventButton.setDisable(true);
                assignTasksButton.setDisable(true);
                break;
                
            case SERVICE_STAFF:
                // Il personale di servizio ha accesso limitato
                mainTabPane.getTabs().removeIf(tab -> 
                    tab.getText().equals("Ricette") || 
                    tab.getText().equals("Menù") || 
                    tab.getText().equals("Compiti"));
                break;
        }
    }
    
    private void setupRecipeTable() {
        recipeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        recipeStateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        recipeOwnerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        
        // Abilita selezione multipla per eliminazione
        recipeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    private void setupMenuTable() {
        menuNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        menuStateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        menuChefColumn.setCellValueFactory(new PropertyValueFactory<>("chefId"));
        
        menuTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    private void setupEventTable() {
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eventStateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        eventOrganizerColumn.setCellValueFactory(new PropertyValueFactory<>("organizerId"));
        eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        
        eventTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    private void setupTaskTable() {
        taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        taskTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        taskStatusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatusDescription()));
        
        taskTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    private void loadInitialData() {
        refreshRecipeTable();
        refreshMenuTable();
        refreshEventTable();
        refreshTaskTable();
    }
    
    private void refreshRecipeTable() {
        List<Recipe> recipes;
        if (currentUser.getRole() == User.UserRole.COOK || currentUser.getRole() == User.UserRole.CHEF) {
            recipes = recipeController.getRecipesByOwner(currentUser.getId());
            recipes.addAll(recipeController.getPublishedRecipes());
        } else {
            recipes = recipeBook.getAllRecipes();
        }
        
        ObservableList<Recipe> data = FXCollections.observableArrayList(recipes);
        recipeTableView.setItems(data);
    }
    
    private void refreshMenuTable() {
        List<Menu> menus;
        if (currentUser.getRole() == User.UserRole.CHEF) {
            menus = menuController.getMenusByChef(currentUser.getId());
        } else {
            menus = menuController.getPublishedMenus();
        }
        
        ObservableList<Menu> data = FXCollections.observableArrayList(menus);
        menuTableView.setItems(data);
    }
    
    private void refreshEventTable() {
        List<Event> events;
        if (currentUser.getRole() == User.UserRole.ORGANIZER) {
            events = eventController.getEventsByOrganizer(currentUser.getId());
        } else if (currentUser.getRole() == User.UserRole.CHEF) {
            events = eventController.getEventsByChef(currentUser.getId());
        } else {
            events = eventController.getActiveEvents();
        }
        
        ObservableList<Event> data = FXCollections.observableArrayList(events);
        eventTableView.setItems(data);
    }
    
    private void refreshTaskTable() {
        List<Task> tasks = taskController.getAllTasks();
        ObservableList<Task> data = FXCollections.observableArrayList(tasks);
        taskTableView.setItems(data);
    }

    // === HANDLER PER RICETTE ===
    
    @FXML
    private void handleNewRecipe(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecipeEditView.fxml"));
            Parent root = loader.load();
            
            RecipeEditController controller = loader.getController();
            controller.setRecipeController(recipeController);
            controller.setCurrentUser(currentUser);
            controller.setMainController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Nuova Ricetta");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            showError("Errore nell'apertura della finestra: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleEditRecipe(ActionEvent event) {
        Recipe selected = recipeTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleziona una ricetta da modificare");
            return;
        }
        
        if (!recipeController.canUserModifyRecipe(selected.getId(), currentUser.getId())) {
            showWarning("Non hai i permessi per modificare questa ricetta");
            return;
        }
        
        // Implementa modifica ricetta
        showInfo("Funzionalità di modifica ricetta da implementare");
    }
    
    @FXML
    private void handleDeleteRecipe(ActionEvent event) {
        Recipe selected = recipeTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleziona una ricetta da eliminare");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Eliminare la ricetta selezionata?");
        alert.setContentText("Questa operazione non può essere annullata.");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean deleted = recipeController.deleteRecipe(selected.getId(), currentUser.getId());
                if (deleted) {
                    refreshRecipeTable();
                    showInfo("Ricetta eliminata con successo");
                } else {
                    showError("Impossibile eliminare la ricetta");
                }
            } catch (Exception e) {
                showError("Errore nell'eliminazione: " + e.getMessage());
            }
        }
    }

    // === HANDLER PER MENÙ ===
    
    @FXML
    private void handleNewMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MenuEditView.fxml"));
            Parent root = loader.load();
            
            MenuEditController controller = loader.getController();
            controller.setControllers(menuController, recipeController);
            controller.setCurrentUser(currentUser);
            controller.setMainController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Nuovo Menù");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            showError("Errore nell'apertura della finestra: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleEditMenu(ActionEvent event) {
        Menu selected = menuTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleziona un menù da modificare");
            return;
        }
        
        if (!selected.getChefId().equals(currentUser.getId()) && currentUser.getRole() != User.UserRole.ORGANIZER) {
            showWarning("Non hai i permessi per modificare questo menù");
            return;
        }
        
        showInfo("Funzionalità di modifica menù da implementare");
    }
    
    @FXML
    private void handleDeleteMenu(ActionEvent event) {
        Menu selected = menuTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleziona un menù da eliminare");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Eliminare il menù selezionato?");
        alert.setContentText("Questa operazione non può essere annullata.");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean deleted = menuController.deleteMenu(selected.getId(), currentUser.getId());
                if (deleted) {
                    refreshMenuTable();
                    showInfo("Menù eliminato con successo");
                } else {
                    showError("Impossibile eliminare il menù");
                }
            } catch (Exception e) {
                showError("Errore nell'eliminazione: " + e.getMessage());
            }
        }
    }

    // === HANDLER PER EVENTI ===
    
    @FXML
    private void handleNewEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EventEditView.fxml"));
            Parent root = loader.load();
            
            EventEditController controller = loader.getController();
            controller.setEventController(eventController);
            controller.setCurrentUser(currentUser);
            controller.setMainController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Nuovo Evento");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            showError("Errore nell'apertura della finestra: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleEditEvent(ActionEvent event) {
        Event selected = eventTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleziona un evento da modificare");
            return;
        }
        
        showInfo("Funzionalità di modifica evento da implementare");
    }
    
    @FXML
    private void handleDeleteEvent(ActionEvent event) {
        Event selected = eventTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleziona un evento da eliminare");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Eliminare l'evento selezionato?");
        alert.setContentText("Questa operazione non può essere annullata.");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean deleted = eventController.deleteEvent(selected.getId(), currentUser.getId());
                if (deleted) {
                    refreshEventTable();
                    showInfo("Evento eliminato con successo");
                } else {
                    showError("Impossibile eliminare l'evento");
                }
            } catch (Exception e) {
                showError("Errore nell'eliminazione: " + e.getMessage());
            }
        }
    }

    // === HANDLER PER COMPITI ===
    
    @FXML
    private void handleAssignTasks(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TaskManagementView.fxml"));
            Parent root = loader.load();
            
            TaskManagementController controller = loader.getController();
            controller.setTaskController(taskController);
            controller.setCurrentUser(currentUser);
            controller.setMainController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Gestione Compiti");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            showError("Errore nell'apertura della finestra: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleViewTaskProgress(ActionEvent event) {
        showInfo("Visualizzazione progresso compiti da implementare");
    }

    // === HANDLER GENERALI ===
    
    @FXML
    private void handleLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma logout");
        alert.setHeaderText("Vuoi disconnetterti?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Termina sessione
            sessionManager.endSession(currentSessionId);
            
            // Chiudi finestra principale
            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            stage.close();
            
            // Riapri login
            openLoginWindow();
        }
    }
    
    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            Stage loginStage = new Stage();
            loginStage.setTitle("Cat & Ring - Login");
            loginStage.setScene(new Scene(root));
            loginStage.setResizable(false);
            loginStage.show();
            
        } catch (IOException e) {
            System.err.println("Errore nell'apertura del login: " + e.getMessage());
            System.exit(1);
        }
    }
    
    @FXML
    private void handleExit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma uscita");
        alert.setHeaderText("Vuoi chiudere l'applicazione?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            sessionManager.endSession(currentSessionId);
            System.exit(0);
        }
    }
    
    @FXML
    private void handleAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazioni");
        alert.setHeaderText("Cat & Ring - Sistema di Gestione Catering");
        alert.setContentText("Versione 1.0\n\nSistema per la gestione completa di una società di catering,\n" +
                           "dalla creazione delle ricette all'organizzazione degli eventi.\n\n" +
                           "Sviluppato per il corso di Sviluppo Applicazioni Software");
        alert.showAndWait();
    }

    // === METODI DI UTILITÀ ===
    
    public void refreshAllData() {
        refreshRecipeTable();
        refreshMenuTable();
        refreshEventTable();
        refreshTaskTable();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText("Si è verificato un errore");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attenzione");
        alert.setHeaderText("Attenzione");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazione");
        alert.setHeaderText("Informazione");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // === GETTERS PER I CONTROLLER FIGLI ===
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public String getCurrentSessionId() {
        return currentSessionId;
    }
}