package com.saslab.ui;

import com.saslab.controller.EventController;
import com.saslab.controller.UserController;
import com.saslab.model.Event;
import com.saslab.model.Service;
import com.saslab.model.User;
import com.saslab.model.Chef;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller per la creazione e modifica degli eventi
 * Implementa pattern Controller (GRASP) per gestire l'interfaccia utente
 */
public class EventEditController {
    
    @FXML private TextField eventNameField;
    @FXML private TextField locationField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> expectedGuestsSpinner;
    @FXML private TextArea notesArea;
    @FXML private TextField clientIdField;
    
    // Assegnazione Chef
    @FXML private ComboBox<Chef> chefComboBox;
    @FXML private Button assignChefButton;
    @FXML private Label assignedChefLabel;
    
    // Gestione Servizi
    @FXML private DatePicker serviceDatePicker;
    @FXML private Spinner<Integer> serviceStartHourSpinner;
    @FXML private Spinner<Integer> serviceStartMinuteSpinner;
    @FXML private Spinner<Integer> serviceEndHourSpinner;
    @FXML private Spinner<Integer> serviceEndMinuteSpinner;
    @FXML private ComboBox<Service.ServiceType> serviceTypeComboBox;
    @FXML private Spinner<Integer> serviceGuestsSpinner;
    @FXML private TextArea serviceNotesArea;
    @FXML private Button addServiceButton;
    
    // Tabella servizi
    @FXML private TableView<Service> servicesTable;
    @FXML private TableColumn<Service, LocalDate> serviceDateColumn;
    @FXML private TableColumn<Service, String> serviceTimeColumn;
    @FXML private TableColumn<Service, Service.ServiceType> serviceTypeColumn;
    @FXML private TableColumn<Service, Integer> serviceGuestsColumn;
    @FXML private Button removeServiceButton;
    
    // Controlli
    @FXML private Button saveButton;
    @FXML private Button startEventButton;
    @FXML private Button completeEventButton;
    @FXML private Button cancelEventButton;
    @FXML private Button cancelButton;
    @FXML private Label statusLabel;
    @FXML private Label errorLabel;
    
    private EventController eventController;
    private UserController userController;
    private User currentUser;
    private MainController mainController;
    private Event currentEvent;
    private boolean isEditMode = false;
    
    @FXML
    public void initialize() {
        setupSpinners();
        setupComboBoxes();
        setupTable();
        setupDatePickers();
        errorLabel.setVisible(false);
        statusLabel.setVisible(false);
    }
    
    private void setupSpinners() {
        expectedGuestsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 50));
        serviceGuestsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 50));
        
        serviceStartHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        serviceStartMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15));
        serviceEndHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 15));
        serviceEndMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 15));
    }
    
    private void setupComboBoxes() {
        // Setup service type combo box
        serviceTypeComboBox.setItems(FXCollections.observableArrayList(Service.ServiceType.values()));
        serviceTypeComboBox.getSelectionModel().selectFirst();
        
        // Chef combo box verrà popolato quando viene impostato il UserController
    }
    
    private void setupTable() {
        serviceDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        serviceTimeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTimeSlot()));
        serviceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        serviceGuestsColumn.setCellValueFactory(new PropertyValueFactory<>("expectedGuests"));
        
        servicesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    private void setupDatePickers() {
        startDatePicker.setValue(LocalDate.now().plusDays(1));
        endDatePicker.setValue(LocalDate.now().plusDays(1));
        serviceDatePicker.setValue(LocalDate.now().plusDays(1));
        
        // Listener per aggiornare la data di fine quando cambia quella di inizio
        startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null && (endDatePicker.getValue() == null || endDatePicker.getValue().isBefore(newDate))) {
                endDatePicker.setValue(newDate);
            }
            if (newDate != null) {
                serviceDatePicker.setValue(newDate);
            }
        });
    }
    
    public void setEventController(EventController eventController) {
        this.eventController = eventController;
    }
    
    public void setUserController(UserController userController) {
        this.userController = userController;
        loadAvailableChefs();
    }
    
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    public void setEventForEditing(Event event) {
        this.currentEvent = event;
        this.isEditMode = true;
        loadEventData();
        updateUIForEditMode();
    }
    
    private void loadAvailableChefs() {
        if (userController == null) return;
        
        List<Chef> chefs = userController.getAllChefs();
        ObservableList<Chef> chefList = FXCollections.observableArrayList(chefs);
        chefComboBox.setItems(chefList);
        
        // Imposta il display del combo box per mostrare nome e specializzazione
        chefComboBox.setCellFactory(listView -> new ListCell<Chef>() {
            @Override
            protected void updateItem(Chef chef, boolean empty) {
                super.updateItem(chef, empty);
                if (empty || chef == null) {
                    setText(null);
                } else {
                    setText(chef.getName() + " - " + chef.getSpecialization());
                }
            }
        });
        
        chefComboBox.setButtonCell(new ListCell<Chef>() {
            @Override
            protected void updateItem(Chef chef, boolean empty) {
                super.updateItem(chef, empty);
                if (empty || chef == null) {
                    setText(null);
                } else {
                    setText(chef.getName() + " - " + chef.getSpecialization());
                }
            }
        });
    }
    
    private void loadEventData() {
        if (currentEvent == null) return;
        
        eventNameField.setText(currentEvent.getName());
        locationField.setText(currentEvent.getLocation());
        startDatePicker.setValue(currentEvent.getStartDate());
        endDatePicker.setValue(currentEvent.getEndDate());
        expectedGuestsSpinner.getValueFactory().setValue(currentEvent.getExpectedGuests());
        notesArea.setText(currentEvent.getNotes());
        clientIdField.setText(currentEvent.getClientId());
        
        // Carica chef assegnato
        if (currentEvent.getAssignedChefId() != null) {
            Chef assignedChef = (Chef) userController.getUser(currentEvent.getAssignedChefId());
            if (assignedChef != null) {
                chefComboBox.getSelectionModel().select(assignedChef);
                assignedChefLabel.setText("Chef assegnato: " + assignedChef.getName());
                assignedChefLabel.setVisible(true);
            }
        }
        
        // Carica servizi
        ObservableList<Service> services = FXCollections.observableArrayList(currentEvent.getServices());
        servicesTable.setItems(services);
        
        updateStatusLabel();
    }
    
    private void updateUIForEditMode() {
        if (currentEvent != null) {
            boolean canModify = currentEvent.canBeModified();
            
            eventNameField.setDisable(!canModify);
            locationField.setDisable(!canModify);
            startDatePicker.setDisable(!canModify);
            endDatePicker.setDisable(!canModify);
            expectedGuestsSpinner.setDisable(!canModify);
            clientIdField.setDisable(!canModify);
            
            // I servizi possono essere aggiunti se l'evento non è completato o annullato
            boolean canAddServices = currentEvent.getState() != Event.EventState.COMPLETED && 
                                   currentEvent.getState() != Event.EventState.CANCELLED;
            
            serviceDatePicker.setDisable(!canAddServices);
            serviceStartHourSpinner.setDisable(!canAddServices);
            serviceStartMinuteSpinner.setDisable(!canAddServices);
            serviceEndHourSpinner.setDisable(!canAddServices);
            serviceEndMinuteSpinner.setDisable(!canAddServices);
            serviceTypeComboBox.setDisable(!canAddServices);
            serviceGuestsSpinner.setDisable(!canAddServices);
            serviceNotesArea.setDisable(!canAddServices);
            addServiceButton.setDisable(!canAddServices);
            removeServiceButton.setDisable(!canAddServices);
            
            // Lo chef può essere assegnato se l'evento è in bozza o in corso
            boolean canAssignChef = currentEvent.getState() == Event.EventState.DRAFT || 
                                   currentEvent.getState() == Event.EventState.IN_PROGRESS;
            chefComboBox.setDisable(!canAssignChef);
            assignChefButton.setDisable(!canAssignChef);
            
            // Abilita pulsanti in base allo stato
            startEventButton.setDisable(currentEvent.getState() != Event.EventState.DRAFT);
            completeEventButton.setDisable(currentEvent.getState() != Event.EventState.IN_PROGRESS);
            cancelEventButton.setDisable(currentEvent.getState() == Event.EventState.COMPLETED);
        }
    }
    
    @FXML
    private void handleAssignChef(ActionEvent event) {
        Chef selectedChef = chefComboBox.getSelectionModel().getSelectedItem();
        if (selectedChef == null) {
            showError("Seleziona uno chef da assegnare");
            return;
        }
        
        try {
            if (isEditMode && currentEvent != null) {
                eventController.assignChefToEvent(currentEvent.getId(), selectedChef.getId(), currentUser.getId());
                assignedChefLabel.setText("Chef assegnato: " + selectedChef.getName());
                assignedChefLabel.setVisible(true);
                showSuccess("Chef assegnato con successo");
            } else {
                showInfo("Salva prima l'evento per assegnare uno chef");
            }
        } catch (Exception e) {
            showError("Errore nell'assegnazione dello chef: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAddService(ActionEvent event) {
        if (!validateServiceInput()) {
            return;
        }
        
        try {
            LocalDate serviceDate = serviceDatePicker.getValue();
            LocalTime startTime = LocalTime.of(serviceStartHourSpinner.getValue(), serviceStartMinuteSpinner.getValue());
            LocalTime endTime = LocalTime.of(serviceEndHourSpinner.getValue(), serviceEndMinuteSpinner.getValue());
            Service.ServiceType serviceType = serviceTypeComboBox.getSelectionModel().getSelectedItem();
            
            if (isEditMode && currentEvent != null) {
                String serviceId = eventController.addServiceToEvent(currentEvent.getId(), serviceDate, 
                                                                   startTime, endTime, serviceType);
                
                // Aggiorna il servizio con informazioni aggiuntive
                Service newService = currentEvent.getServiceById(serviceId);
                if (newService != null) {
                    newService.setExpectedGuests(serviceGuestsSpinner.getValue());
                    String notes = serviceNotesArea.getText().trim();
                    if (!notes.isEmpty()) {
                        newService.setNotes(notes);
                    }
                }
                
                loadEventData(); // Ricarica per aggiornare la tabella
                clearServiceFields();
                showSuccess("Servizio aggiunto con successo");
            } else {
                showInfo("Salva prima l'evento per aggiungere servizi");
            }
            
        } catch (Exception e) {
            showError("Errore nell'aggiunta del servizio: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRemoveService(ActionEvent event) {
        Service selectedService = servicesTable.getSelectionModel().getSelectedItem();
        if (selectedService == null) {
            showError("Seleziona un servizio da rimuovere");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma rimozione");
        alert.setHeaderText("Rimuovere il servizio selezionato?");
        alert.setContentText("Servizio: " + selectedService.getType().getDisplayName() + 
                           " del " + selectedService.getDate());
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (isEditMode && currentEvent != null) {
                    boolean removed = currentEvent.removeService(selectedService.getId());
                    if (removed) {
                        loadEventData();
                        showSuccess("Servizio rimosso con successo");
                    }
                }
            } catch (Exception e) {
                showError("Errore nella rimozione: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInput()) {
            return;
        }
        
        try {
            if (isEditMode) {
                updateCurrentEvent();
                showSuccess("Evento aggiornato con successo");
            } else {
                String eventId = createNewEvent();
                showSuccess("Evento creato con successo! ID: " + eventId);
            }
            
            updateStatusLabel();
            updateUIForEditMode();
            
            if (mainController != null) {
                mainController.refreshAllData();
            }
            
        } catch (Exception e) {
            showError("Errore nel salvataggio: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleStartEvent(ActionEvent event) {
        if (currentEvent == null) {
            showError("Nessun evento da avviare");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma avvio evento");
        alert.setHeaderText("Avviare l'evento?");
        alert.setContentText("Una volta avviato, l'evento non potrà più essere eliminato.");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                eventController.startEvent(currentEvent.getId(), currentUser.getId());
                loadEventData();
                updateUIForEditMode();
                showSuccess("Evento avviato con successo");
                
                if (mainController != null) {
                    mainController.refreshAllData();
                }
                
            } catch (Exception e) {
                showError("Errore nell'avvio dell'evento: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleCompleteEvent(ActionEvent event) {
        if (currentEvent == null) {
            showError("Nessun evento da completare");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Completa Evento");
        dialog.setHeaderText("Completa l'evento");
        dialog.setContentText("Note finali (opzionali):");
        
        dialog.showAndWait().ifPresent(finalNotes -> {
            try {
                eventController.completeEvent(currentEvent.getId(), currentUser.getId(), finalNotes);
                loadEventData();
                updateUIForEditMode();
                showSuccess("Evento completato con successo");
                
                if (mainController != null) {
                    mainController.refreshAllData();
                }
                
            } catch (Exception e) {
                showError("Errore nel completamento dell'evento: " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void handleCancelEvent(ActionEvent event) {
        if (currentEvent == null) {
            showError("Nessun evento da annullare");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Annulla Evento");
        dialog.setHeaderText("Annulla l'evento");
        dialog.setContentText("Motivo dell'annullamento:");
        
        dialog.showAndWait().ifPresent(reason -> {
            try {
                eventController.cancelEvent(currentEvent.getId(), currentUser.getId(), reason);
                loadEventData();
                updateUIForEditMode();
                showSuccess("Evento annullato");
                
                if (mainController != null) {
                    mainController.refreshAllData();
                }
                
            } catch (Exception e) {
                showError("Errore nell'annullamento dell'evento: " + e.getMessage());
            }
        });
    }
    
    private String createNewEvent() {
        String name = eventNameField.getText().trim();
        String location = locationField.getText().trim();
        LocalDate startDate = startDatePicker.getValue();
        int expectedGuests = expectedGuestsSpinner.getValue();
        
        String eventId = eventController.createEvent(currentUser.getId(), name, location, startDate, expectedGuests);
        Event event = eventController.getEvent(eventId);
        
        // Imposta data di fine
        event.setEndDate(endDatePicker.getValue());
        
        // Imposta client ID se specificato
        String clientId = clientIdField.getText().trim();
        if (!clientId.isEmpty()) {
            event.setClientId(clientId);
        }
        
        // Imposta note
        String notes = notesArea.getText().trim();
        if (!notes.isEmpty()) {
            event.setNotes(notes);
        }
        
        // Imposta come evento corrente e modalità modifica
        this.currentEvent = event;
        this.isEditMode = true;
        
        return eventId;
    }
    
    private void updateCurrentEvent() {
        if (currentEvent == null || !currentEvent.canBeModified()) return;
        
        // Aggiorna campi modificabili
        currentEvent.setName(eventNameField.getText().trim());
        currentEvent.setLocation(locationField.getText().trim());
        currentEvent.setEndDate(endDatePicker.getValue());
        currentEvent.setExpectedGuests(expectedGuestsSpinner.getValue());
        currentEvent.setNotes(notesArea.getText().trim());
        
        String clientId = clientIdField.getText().trim();
        if (!clientId.isEmpty()) {
            currentEvent.setClientId(clientId);
        }
    }
    
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        
        if (eventNameField.getText().trim().isEmpty()) {
            errors.append("Il nome dell'evento è obbligatorio\n");
        }
        
        if (locationField.getText().trim().isEmpty()) {
            errors.append("La location è obbligatoria\n");
        }
        
        if (startDatePicker.getValue() == null) {
            errors.append("La data di inizio è obbligatoria\n");
        } else if (startDatePicker.getValue().isBefore(LocalDate.now())) {
            errors.append("La data di inizio non può essere nel passato\n");
        }
        
        if (endDatePicker.getValue() == null) {
            errors.append("La data di fine è obbligatoria\n");
        } else if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            errors.append("La data di fine non può essere precedente a quella di inizio\n");
        }
        
        if (expectedGuestsSpinner.getValue() <= 0) {
            errors.append("Il numero di ospiti deve essere positivo\n");
        }
        
        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }
        
        return true;
    }
    
    private boolean validateServiceInput() {
        StringBuilder errors = new StringBuilder();
        
        if (serviceDatePicker.getValue() == null) {
            errors.append("La data del servizio è obbligatoria\n");
        } else if (serviceDatePicker.getValue().isBefore(startDatePicker.getValue()) || 
                   serviceDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            errors.append("La data del servizio deve essere nell'intervallo dell'evento\n");
        }
        
        LocalTime startTime = LocalTime.of(serviceStartHourSpinner.getValue(), serviceStartMinuteSpinner.getValue());
        LocalTime endTime = LocalTime.of(serviceEndHourSpinner.getValue(), serviceEndMinuteSpinner.getValue());
        
        if (!endTime.isAfter(startTime)) {
            errors.append("L'orario di fine deve essere successivo a quello di inizio\n");
        }
        
        if (serviceTypeComboBox.getSelectionModel().getSelectedItem() == null) {
            errors.append("Il tipo di servizio è obbligatorio\n");
        }
        
        if (serviceGuestsSpinner.getValue() <= 0) {
            errors.append("Il numero di ospiti del servizio deve essere positivo\n");
        }
        
        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }
        
        return true;
    }
    
    private void clearServiceFields() {
        serviceStartHourSpinner.getValueFactory().setValue(12);
        serviceStartMinuteSpinner.getValueFactory().setValue(0);
        serviceEndHourSpinner.getValueFactory().setValue(15);
        serviceEndMinuteSpinner.getValueFactory().setValue(0);
        serviceTypeComboBox.getSelectionModel().selectFirst();
        serviceGuestsSpinner.getValueFactory().setValue(expectedGuestsSpinner.getValue());
        serviceNotesArea.clear();
    }
    
    private void updateStatusLabel() {
        if (currentEvent != null) {
            statusLabel.setText("Stato: " + currentEvent.getState().getDisplayName());
            statusLabel.setVisible(true);
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(true);
    }
    
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: green;");
        errorLabel.setVisible(true);
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazione");
        alert.setHeaderText("Informazione");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void clearError() {
        errorLabel.setVisible(false);
    }
}