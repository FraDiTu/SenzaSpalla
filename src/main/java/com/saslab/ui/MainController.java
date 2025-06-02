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
import com.saslab.model.Menu;
import com.saslab.model.Event;
import com.saslab.model.Task;
import com.saslab.model.User;
import com.saslab.service.SessionManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller principale dell'applicazione Cat & Ring
 * CORRETTO: Risolve dipendenze circolari e inizializzazione errata
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

    // Controllers - CORRETTI: Inizializzazione ordinata per evitare dipendenze circolari
    private RecipeController recipeController;
    private MenuController menuController;
    private EventController eventController;
    private TaskController taskController;
    private UserController userController;
    private ShiftController shiftController;
    private final SessionManager sessionManager = SessionManager.getInstance();
    
    // Stato corrente
    private User currentUser;
    private String currentSessionId;

    @FXML
    public void initialize() {
        setupTables();
        initializeControllers(); // NUOVO: Inizializzazione ordinata
    }
    
    /**
     * NUOVO: Inizializzazione corretta dei controller per evitare dipendenze circolari
     */
    private void initializeControllers() {
        // 1. Prima i controller senza dipendenze
        this.userController = new UserController();
        this.recipeController = new RecipeController();
        this.eventController = new EventController();
        this.taskController = new TaskController();
        this.shiftController = new ShiftController();
        
        // 2. Poi i controller con dipendenze (MenuController dipende da RecipeController)
        this.menuController = new MenuController(recipeController);
        
        // 3. RIMOSSO: setUserController(UserController) - metodo ricorsivo senza senso
        // this.shiftController.setUserController(userController); // ERRATO nel file originale
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
        // CORRETTO: Rimozione sicura dei tab con controllo esistenza
        if (currentUser.getRole() == User.UserRole.SERVICE_STAFF) {
            // Trova e rimuovi tab specifici in modo sicuro
            mainTabPane.getTabs().removeIf(tab -> {
                String tabText = tab.getText();
                return "Ricette".equals(tabText) || 
                       "Menù".equals(tabText) || 
                       "Compiti".equals(tabText);
            });
        }
        
        // Abilita/disabilita controlli in base al ruolo
        switch (currentUser.getRole()) {
            case ORGANIZER:
                // Gli organizzatori possono fare tutto
                break;
                
            case CHEF:
                // Gli chef non possono creare eventi ma possono gestire menu e ricette
                setEventControlsEnabled(false);
                break;
                
            case COOK:
                // I cuochi possono solo vedere ricette e compiti
                setMenuControlsEnabled(false);
                setEventControlsEnabled(false);
                assignTasksButton.setDisable(true);
                break;
                
            case SERVICE_STAFF:
                // Accesso limitato già gestito con rimozione tab
                break;
        }
    }
    
    private void setEventControlsEnabled(boolean enabled) {
        if (newEventButton != null) newEventButton.setDisable(!enabled);
        if (editEventButton != null) editEventButton.setDisable(!enabled);
        if (deleteEventButton != null) deleteEventButton.setDisable(!enabled);
    }
    
    private void setMenuControlsEnabled(boolean enabled) {
        if (newMenuButton != null) newMenuButton.setDisable(!enabled);
        if (editMenuButton != null) editMenuButton.setDisable(!enabled);
        if (deleteMenuButton != null) deleteMenuButton.setDisable(!enabled);
    }
    
    private void setupTables() {
        // Setup tabella ricette
        if (recipeNameColumn != null) {
            recipeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
        if (recipeStateColumn != null) {
            recipeStateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        }
        if (recipeOwnerColumn != null) {
            recipeOwnerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        }
        if (recipeTableView != null) {
            recipeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
        
        // Setup tabella menù
        if (menuNameColumn != null) {
            menuNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
        if (menuStateColumn != null) {
            menuStateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        }
        if (menuChefColumn != null) {
            menuChefColumn.setCellValueFactory(new PropertyValueFactory<>("chefId"));
        }
        if (menuTableView != null) {
            menuTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }
        
        // Setup tabella eventi
        if (eventNameColumn != null) {
            eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
        if (eventStateColumn != null) {
            eventStateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        }
        if (eventOrganizerColumn != null) {
            eventOrganizerColumn.setCellValueFactory(new PropertyValueFactory<>("organizerId"));
        }
        if (eventDateColumn != null) {
            eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        }
        if (eventTableView != null) {
            eventTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }
        
        // Setup tabella compiti
        if (taskDescriptionColumn != null) {
            taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        }
        if (taskTypeColumn != null) {
            taskTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        }
        if (taskStatusColumn != null) {
            taskStatusColumn.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatusDescription()));
        }
        if (taskTableView != null) {
            taskTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }
    }
    
    private void loadInitialData() {
        refreshRecipeTable();
        refreshMenuTable();
        refreshEventTable();
        refreshTaskTable();
    }
    
    private void refreshRecipeTable() {
        if (recipeController == null || recipeTableView == null) return;
        
        List<Recipe> recipes;
        if (currentUser.getRole() == User.UserRole.COOK || currentUser.getRole() == User.UserRole.CHEF) {
            recipes = recipeController.getRecipesByOwner(currentUser.getId());
            recipes.addAll(recipeController.getPublishedRecipes());
            // Rimuovi duplicati usando stream distinct
            recipes = recipes.stream().distinct().collect(java.util.stream.Collectors.toList());
        } else {
            recipes = RecipeBook.getInstance().getAllRecipes();
        }
        
        ObservableList<Recipe> data = FXCollections.observableArrayList(recipes);
        recipeTableView.setItems(data);
    }
    
    private void refreshMenuTable() {
        if (menuController == null || menuTableView == null) return;
        
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
        if (eventController == null || eventTableView == null) return;
        
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
        if (taskController == null || taskTableView == null) return;
        
        List<Task> tasks = taskController.getAllTasks();
        ObservableList<Task> data = FXCollections.observableArrayList(tasks);
        taskTableView.setItems(data);
    }

    // === HANDLER PER RICETTE ===
    
    @FXML
    private void handleNewRecipe(ActionEvent event) {
        if (recipeController == null) {
            showError("Sistema non inizializzato correttamente");
            return;
        }
        
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
        } catch (Exception e) {
            showError("Errore imprevisto: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleEditRecipe(ActionEvent event) {
        if (recipeTableView == null) return;
        
        Recipe selected = recipeTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleziona una ricetta da modificare");
            return;
        }
        
        if (!recipeController.canUserModifyRecipe(selected.getId(), currentUser.getId())) {
            showWarning("Non hai i permessi per modificare questa ricetta");
            return;
        }
        
        // TODO: Implementa modifica ricetta
        showInfo("Funzionalità di modifica ricetta da implementare");
    }
    
    @FXML
    private void handleDeleteRecipe(ActionEvent event) {
        if (recipeTableView == null) return;
        
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
        if (menuController == null) {
            showError("Sistema non inizializzato correttamente");
            return;
        }
        
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
        } catch (Exception e) {
            showError("Errore imprevisto: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleEditMenu(ActionEvent event) {
        if (menuTableView == null) return;
        
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
        if (menuTableView == null) return;
        
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
        if (eventController == null || userController == null) {
            showError("Sistema non inizializzato correttamente");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EventEditView.fxml"));
            Parent root = loader.load();
            
            EventEditController controller = loader.getController();
            controller.setEventController(eventController);
            controller.setUserController(userController);
            controller.setCurrentUser(currentUser);
            controller.setMainController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Nuovo Evento");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            showError("Errore nell'apertura della finestra: " + e.getMessage());
        } catch (Exception e) {
            showError("Errore imprevisto: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleEditEvent(ActionEvent event) {
        if (eventTableView == null) return;
        
        Event selected = eventTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleziona un evento da modificare");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EventEditView.fxml"));
            Parent root = loader.load();
            
            EventEditController controller = loader.getController();
            controller.setEventController(eventController);
            controller.setUserController(userController);
            controller.setCurrentUser(currentUser);
            controller.setMainController(this);
            controller.setEventForEditing(selected);
            
            Stage stage = new Stage();
            stage.setTitle("Modifica Evento: " + selected.getName());
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            showError("Errore nell'apertura della finestra: " + e.getMessage());
        } catch (Exception e) {
            showError("Errore imprevisto: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDeleteEvent(ActionEvent event) {
        if (eventTableView == null) return;
        
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
        if (taskController == null) {
            showError("Sistema non inizializzato correttamente");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TaskManagementView.fxml"));
            Parent root = loader.load();
            
            TaskManagementController controller = loader.getController();
            controller.setTaskController(taskController);
            controller.setUserController(userController);
            controller.setCurrentUser(currentUser);
            controller.setMainController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Gestione Compiti");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            showError("Errore nell'apertura della finestra: " + e.getMessage());
        } catch (Exception e) {
            showError("Errore imprevisto: " + e.getMessage());
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
    
    // CORRETTO: Getters per i controller per evitare null reference
    public RecipeController getRecipeController() {
        return recipeController;
    }
    
    public MenuController getMenuController() {
        return menuController;
    }
    
    public EventController getEventController() {
        return eventController;
    }
    
    public TaskController getTaskController() {
        return taskController;
    }
    
    public UserController getUserController() {
        return userController;
    }
}