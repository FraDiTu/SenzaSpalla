package com.saslab.ui;

import com.saslab.controller.TaskController;
import com.saslab.controller.UserController;
import com.saslab.model.Task;
import com.saslab.model.TaskAssignment;
import com.saslab.model.User;
import com.saslab.model.Cook;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

/**
 * Controller per la gestione dei compiti della cucina
 * Implementa pattern Controller (GRASP) per gestire l'interfaccia utente
 */
public class TaskManagementController {
    
    // Gestione task extra
    @FXML private TextArea taskDescriptionArea;
    @FXML private ComboBox<Task.TaskType> taskTypeComboBox;
    @FXML private ComboBox<Task.TaskPriority> taskPriorityComboBox;
    @FXML private Button addTaskButton;
    
    // Lista task disponibili
    @FXML private TableView<Task> availableTasksTable;
    @FXML private TableColumn<Task, String> taskDescriptionColumn;
    @FXML private TableColumn<Task, Task.TaskType> taskTypeColumn;
    @FXML private TableColumn<Task, Task.TaskPriority> taskPriorityColumn;
    @FXML private TableColumn<Task, String> taskStatusColumn;
    @FXML private Button splitTaskButton;
    @FXML private Spinner<Integer> splitPartsSpinner;
    
    // Assegnazione compiti
    @FXML private ComboBox<Cook> cookComboBox;
    @FXML private TextField shiftIdField;
    @FXML private Spinner<Integer> timeEstimateSpinner;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button assignTaskButton;
    
    // Visualizzazione assegnazioni
    @FXML private TableView<TaskAssignment> assignmentsTable;
    @FXML private TableColumn<TaskAssignment, String> assignmentTaskColumn;
    @FXML private TableColumn<TaskAssignment, String> assignmentCookColumn;
    @FXML private TableColumn<TaskAssignment, String> assignmentShiftColumn;
    @FXML private TableColumn<TaskAssignment, Integer> assignmentTimeColumn;
    @FXML private TableColumn<TaskAssignment, String> assignmentStatusColumn;
    @FXML private Button reassignTaskButton;
    @FXML private Button markCompletedButton;
    @FXML private Button reportIssueButton;
    
    // Gestione piano
    @FXML private Label planStatusLabel;
    @FXML private Button confirmPlanButton;
    @FXML private Button resetPlanButton;
    
    // Statistiche
    @FXML private Label totalTasksLabel;
    @FXML private Label assignedTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label tasksWithIssuesLabel;
    @FXML private ProgressBar completionProgressBar;
    
    // Controlli
    @FXML private Button refreshButton;
    @FXML private Button closeButton;
    @FXML private Label errorLabel;
    @FXML private Label statusLabel;

    private TaskController taskController;
    private UserController userController;
    @FXML
    public void initialize() {
        setupComboBoxes();
        setupSpinners();
        setupTables();
        errorLabel.setVisible(false);
        statusLabel.setVisible(false);
    }
    
    private void setupComboBoxes() {
        taskTypeComboBox.setItems(FXCollections.observableArrayList(Task.TaskType.values()));
        taskTypeComboBox.getSelectionModel().select(Task.TaskType.EXTRA);
        
        taskPriorityComboBox.setItems(FXCollections.observableArrayList(Task.TaskPriority.values()));
        taskPriorityComboBox.getSelectionModel().select(Task.TaskPriority.NORMAL);
    }
    
    private void setupSpinners() {
        splitPartsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 10, 2));
        timeEstimateSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 480, 60, 15));
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    }
    
    private void setupTables() {
        // Tabella task disponibili
        taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        taskTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        taskPriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        taskStatusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatusDescription()));
        
        availableTasksTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Tabella assegnazioni
        assignmentTaskColumn.setCellValueFactory(cellData -> {
            Task task = taskController.getTask(cellData.getValue().getTaskId());
            return new javafx.beans.property.SimpleStringProperty(
                task != null ? task.getDescription() : "Sconosciuto");
        });
        
        assignmentCookColumn.setCellValueFactory(cellData -> {
            User cook = userController.getUser(cellData.getValue().getCookId());
            return new javafx.beans.property.SimpleStringProperty(
                cook != null ? cook.getName() : "Sconosciuto");
        });
        
        assignmentShiftColumn.setCellValueFactory(new PropertyValueFactory<>("shiftId"));
        assignmentTimeColumn.setCellValueFactory(new PropertyValueFactory<>("timeEstimate"));
        assignmentStatusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        
        assignmentsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    public void setTaskController(TaskController taskController) {
        this.taskController = taskController;
        refreshData();
    }
    
    public void setUserController(UserController userController) {
        this.userController = userController;
        loadAvailableCooks();
    }
    
    public void setCurrentUser(User currentUser) {
    }
    
    public void setMainController(MainController mainController) {
    }
    
    private void loadAvailableCooks() {
        if (userController == null) return;
        
        List<Cook> cooks = userController.getAvailableCooks();
        ObservableList<Cook> cookList = FXCollections.observableArrayList(cooks);
        cookComboBox.setItems(cookList);
        
        // Imposta il display del combo box
        cookComboBox.setCellFactory(listView -> new ListCell<Cook>() {
            @Override
            protected void updateItem(Cook cook, boolean empty) {
                super.updateItem(cook, empty);
                if (empty || cook == null) {
                    setText(null);
                } else {
                    setText(cook.getName() + " (" + cook.getSkillLevel() + ")");
                }
            }
        });
        
        cookComboBox.setButtonCell(new ListCell<Cook>() {
            @Override
            protected void updateItem(Cook cook, boolean empty) {
                super.updateItem(cook, empty);
                if (empty || cook == null) {
                    setText(null);
                } else {
                    setText(cook.getName() + " (" + cook.getSkillLevel() + ")");
                }
            }
        });
    }
    
    @FXML
    private void handleAddTask(ActionEvent event) {
        String description = taskDescriptionArea.getText().trim();
        if (description.isEmpty()) {
            showError("Inserire la descrizione del compito");
            return;
        }
        
        try {
            String taskId = taskController.addExtraTask(description);
            Task task = taskController.getTask(taskId);
            
            // Imposta priorità se diversa da NORMAL
            Task.TaskPriority priority = taskPriorityComboBox.getSelectionModel().getSelectedItem();
            if (priority != Task.TaskPriority.NORMAL) {
                task.setPriority(priority);
            }
            
            taskDescriptionArea.clear();
            refreshData();
            showSuccess("Compito aggiunto con successo");
            
        } catch (Exception e) {
            showError("Errore nell'aggiunta del compito: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSplitTask(ActionEvent event) {
        Task selectedTask = availableTasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showError("Seleziona un compito da suddividere");
            return;
        }
        
        if (selectedTask.isAssigned()) {
            showError("Non è possibile suddividere un compito già assegnato");
            return;
        }
        
        int parts = splitPartsSpinner.getValue();
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma suddivisione");
        alert.setHeaderText("Suddividere il compito in " + parts + " parti?");
        alert.setContentText("Compito: " + selectedTask.getDescription());
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                List<String> partIds = taskController.splitPreparation(selectedTask.getId(), parts);
                refreshData();
                showSuccess("Compito suddiviso in " + partIds.size() + " parti");
                
            } catch (Exception e) {
                showError("Errore nella suddivisione: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleAssignTask(ActionEvent event) {
        Task selectedTask = availableTasksTable.getSelectionModel().getSelectedItem();
        Cook selectedCook = cookComboBox.getSelectionModel().getSelectedItem();
        String shiftId = shiftIdField.getText().trim();
        
        if (selectedTask == null) {
            showError("Seleziona un compito da assegnare");
            return;
        }
        
        if (selectedCook == null) {
            showError("Seleziona un cuoco");
            return;
        }
        
        if (shiftId.isEmpty()) {
            showError("Inserire l'ID del turno");
            return;
        }
        
        if (selectedTask.isAssigned()) {
            showError("Il compito è già assegnato");
            return;
        }
        
        try {
            int timeEstimate = timeEstimateSpinner.getValue();
            int quantity = quantitySpinner.getValue();
            
            taskController.assignTaskToCook(selectedTask.getId(), selectedCook.getId(), 
                                          shiftId, timeEstimate, quantity);
            
            refreshData();
            showSuccess("Compito assegnato con successo");
            
        } catch (Exception e) {
            showError("Errore nell'assegnazione: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleReassignTask(ActionEvent event) {
        TaskAssignment selectedAssignment = assignmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAssignment == null) {
            showError("Seleziona un'assegnazione da modificare");
            return;
        }
        
        if (selectedAssignment.isCompleted()) {
            showError("Non è possibile riassegnare un compito completato");
            return;
        }
        
        Cook newCook = cookComboBox.getSelectionModel().getSelectedItem();
        String newShiftId = shiftIdField.getText().trim();
        
        if (newCook == null) {
            showError("Seleziona il nuovo cuoco");
            return;
        }
        
        if (newShiftId.isEmpty()) {
            showError("Inserire il nuovo ID del turno");
            return;
        }
        
        try {
            taskController.reassignTask(selectedAssignment.getTaskId(), newCook.getId(), newShiftId);
            refreshData();
            showSuccess("Compito riassegnato con successo");
            
        } catch (Exception e) {
            showError("Errore nella riassegnazione: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleMarkCompleted(ActionEvent event) {
        TaskAssignment selectedAssignment = assignmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAssignment == null) {
            showError("Seleziona un'assegnazione da completare");
            return;
        }
        
        if (selectedAssignment.isCompleted()) {
            showError("Il compito è già completato");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma completamento");
        alert.setHeaderText("Segnare il compito come completato?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                taskController.markTaskCompleted(selectedAssignment.getTaskId(), selectedAssignment.getCookId());
                refreshData();
                showSuccess("Compito segnato come completato");
                
            } catch (Exception e) {
                showError("Errore nel completamento: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleReportIssue(ActionEvent event) {
        TaskAssignment selectedAssignment = assignmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAssignment == null) {
            showError("Seleziona un'assegnazione per segnalare un problema");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Segnala Problema");
        dialog.setHeaderText("Segnala un problema sul compito");
        dialog.setContentText("Descrizione del problema:");
        
        dialog.showAndWait().ifPresent(issue -> {
            try {
                taskController.reportTaskIssue(selectedAssignment.getTaskId(), 
                                             selectedAssignment.getCookId(), issue);
                refreshData();
                showSuccess("Problema segnalato");
                
            } catch (Exception e) {
                showError("Errore nella segnalazione: " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void handleConfirmPlan(ActionEvent event) {
        if (taskController.isPlanConfirmed()) {
            showError("Il piano è già confermato");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Piano");
        alert.setHeaderText("Confermare il piano dei compiti?");
        alert.setContentText("Una volta confermato, il piano non potrà più essere modificato.");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                taskController.confirmTaskPlan();
                refreshData();
                showSuccess("Piano confermato con successo");
                
            } catch (Exception e) {
                showError("Errore nella conferma: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleResetPlan(ActionEvent event) {
        if (!taskController.isPlanConfirmed()) {
            showError("Il piano non è confermato");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Piano");
        alert.setHeaderText("Resettare il piano dei compiti?");
        alert.setContentText("Il piano tornerà in modalità modifica.");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                taskController.resetPlan();
                refreshData();
                showSuccess("Piano resettato");
                
            } catch (Exception e) {
                showError("Errore nel reset: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        refreshData();
        showSuccess("Dati aggiornati");
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    private void refreshData() {
        if (taskController == null) return;
        
        // Aggiorna tabella task
        List<Task> tasks = taskController.getAllTasks();
        ObservableList<Task> taskList = FXCollections.observableArrayList(tasks);
        availableTasksTable.setItems(taskList);
        
        // Aggiorna tabella assegnazioni
        List<TaskAssignment> assignments = taskController.getAllAssignments();
        ObservableList<TaskAssignment> assignmentList = FXCollections.observableArrayList(assignments);
        assignmentsTable.setItems(assignmentList);
        
        // Aggiorna stato piano
        updatePlanStatus();
        
        // Aggiorna statistiche
        updateStatistics();
    }
    
    private void updatePlanStatus() {
        boolean isPlanConfirmed = taskController.isPlanConfirmed();
        planStatusLabel.setText("Piano: " + (isPlanConfirmed ? "CONFERMATO" : "IN MODIFICA"));
        planStatusLabel.setStyle(isPlanConfirmed ? "-fx-text-fill: green;" : "-fx-text-fill: orange;");
        
        confirmPlanButton.setDisable(isPlanConfirmed);
        resetPlanButton.setDisable(!isPlanConfirmed);
        
        // Disabilita controlli di modifica se il piano è confermato
        boolean canModify = !isPlanConfirmed;
        addTaskButton.setDisable(!canModify);
        splitTaskButton.setDisable(!canModify);
        assignTaskButton.setDisable(!canModify);
        reassignTaskButton.setDisable(!canModify);
    }
    
    private void updateStatistics() {
        Map<String, Integer> stats = taskController.getTaskStatistics();
        
        totalTasksLabel.setText("Totale: " + stats.get("total"));
        assignedTasksLabel.setText("Assegnati: " + stats.get("assigned"));
        completedTasksLabel.setText("Completati: " + stats.get("completed"));
        tasksWithIssuesLabel.setText("Con problemi: " + stats.get("withIssues"));
        
        // Calcola percentuale completamento
        int total = stats.get("total");
        int completed = stats.get("completed");
        double completionPercentage = total > 0 ? (double) completed / total : 0;
        
        completionProgressBar.setProgress(completionPercentage);
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(true);
    }
    
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setVisible(true);
        
        // Nascondi il messaggio dopo 3 secondi
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), e -> statusLabel.setVisible(false))
        );
        timeline.play();
    }
}