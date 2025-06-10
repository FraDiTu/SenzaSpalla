package it.catering.catring.view;

import it.catering.catring.controller.ApplicationController;
import it.catering.catring.controller.TurnoController;
import it.catering.catring.model.entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TurnoManagementView {
    private TurnoController turnoController;
    private VBox mainContent;
    private TabPane tabPane;
    private TableView<TurnoPreparatorio> turniPrepTable;
    private TableView<TurnoServizio> turniServTable;
    
    public TurnoManagementView() {
        this.turnoController = new TurnoController();
        this.turnoController.setCurrentUser(
            ApplicationController.getInstance().getAuthController().getCurrentUser());
        createView();
        loadTurni();
    }
    
    public ScrollPane getView() {
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }
    
    private void createView() {
        mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: white;");
        
        // Header
        HBox header = createHeader();
        
        // TabPane per separare turni preparatori e di servizio
        tabPane = createTabPane();
        
        mainContent.getChildren().addAll(header, new Separator(), tabPane);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Gestione Turni");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Button refreshButton = new Button("Aggiorna");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        refreshButton.setOnAction(e -> loadTurni());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleLabel, spacer, refreshButton);
        return header;
    }
    
    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        
        // Tab Turni Preparatori
        Tab turniPrepTab = new Tab("Turni Preparatori");
        turniPrepTab.setClosable(false);
        turniPrepTab.setContent(createTurniPreparatoriTab());
        
        // Tab Turni Servizio
        Tab turniServTab = new Tab("Turni Servizio");
        turniServTab.setClosable(false);
        turniServTab.setContent(createTurniServizioTab());
        
        tabPane.getTabs().addAll(turniPrepTab, turniServTab);
        return tabPane;
    }
    
    private VBox createTurniPreparatoriTab() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        // Buttons
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button addTurnoButton = new Button("Nuovo Turno");
        addTurnoButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        addTurnoButton.setOnAction(e -> showAddTurnoPreparatoriDialog());
        
        Button addRicorrenteButton = new Button("Turni Ricorrenti");
        addRicorrenteButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        addRicorrenteButton.setOnAction(e -> showAddTurniRicorrentiDialog());
        
        Button deleteButton = new Button("Elimina Turno");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        deleteButton.setOnAction(e -> deleteTurnoPreparatorio());
        deleteButton.disableProperty().bind(turniPrepTable != null ? 
            turniPrepTable.getSelectionModel().selectedItemProperty().isNull() : 
            javafx.beans.binding.Bindings.createBooleanBinding(() -> true));
        
        buttonsBox.getChildren().addAll(addTurnoButton, addRicorrenteButton, deleteButton);
        
        // Table
        turniPrepTable = createTurniPreparatoriTable();
        
        content.getChildren().addAll(buttonsBox, turniPrepTable);
        return content;
    }
    
    private VBox createTurniServizioTab() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        // Table
        turniServTable = createTurniServizioTable();
        
        content.getChildren().add(turniServTable);
        return content;
    }
    
    private TableView<TurnoPreparatorio> createTurniPreparatoriTable() {
        TableView<TurnoPreparatorio> table = new TableView<>();
        table.setPrefHeight(400);
        
        TableColumn<TurnoPreparatorio, String> dataCol = new TableColumn<>("Data");
        dataCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getData().toString()));
        dataCol.setPrefWidth(100);
        
        TableColumn<TurnoPreparatorio, String> orarioCol = new TableColumn<>("Orario");
        orarioCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getOrarioInizio() + " - " + data.getValue().getOrarioFine()));
        orarioCol.setPrefWidth(120);
        
        TableColumn<TurnoPreparatorio, String> luogoCol = new TableColumn<>("Luogo");
        luogoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getLuogo()));
        luogoCol.setPrefWidth(150);
        
        TableColumn<TurnoPreparatorio, String> statoCol = new TableColumn<>("Stato");
        statoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getStato().toString()));
        statoCol.setPrefWidth(100);
        
        TableColumn<TurnoPreparatorio, String> cuochiCol = new TableColumn<>("Cuochi Assegnati");
        cuochiCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(data.getValue().getCuochiAssegnati().size())));
        cuochiCol.setPrefWidth(120);
        
        TableColumn<TurnoPreparatorio, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getNote() != null ? data.getValue().getNote() : ""));
        noteCol.setPrefWidth(200);
        
        table.getColumns().addAll(dataCol, orarioCol, luogoCol, statoCol, cuochiCol, noteCol);
        
        return table;
    }
    
    private TableView<TurnoServizio> createTurniServizioTable() {
        TableView<TurnoServizio> table = new TableView<>();
        table.setPrefHeight(400);
        
        TableColumn<TurnoServizio, String> dataCol = new TableColumn<>("Data");
        dataCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getData().toString()));
        dataCol.setPrefWidth(100);
        
        TableColumn<TurnoServizio, String> orarioCol = new TableColumn<>("Orario");
        orarioCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getOrarioInizio() + " - " + data.getValue().getOrarioFine()));
        orarioCol.setPrefWidth(120);
        
        TableColumn<TurnoServizio, String> eventoCol = new TableColumn<>("Evento");
        eventoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getEvento() != null ? data.getValue().getEvento().getNome() : ""));
        eventoCol.setPrefWidth(150);
        
        TableColumn<TurnoServizio, String> servizioCol = new TableColumn<>("Servizio");
        servizioCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getServizio() != null ? data.getValue().getServizio().getTipo() : ""));
        servizioCol.setPrefWidth(100);
        
        TableColumn<TurnoServizio, String> luogoCol = new TableColumn<>("Luogo");
        luogoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getLuogo()));
        luogoCol.setPrefWidth(150);
        
        TableColumn<TurnoServizio, String> personaleCol = new TableColumn<>("Personale");
        personaleCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(data.getValue().getPersonaleAssegnato().size())));
        personaleCol.setPrefWidth(100);
        
        table.getColumns().addAll(dataCol, orarioCol, eventoCol, servizioCol, luogoCol, personaleCol);
        
        return table;
    }
    
    private void showAddTurnoPreparatoriDialog() {
        Dialog<TurnoPreparatorio> dialog = new Dialog<>();
        dialog.setTitle("Nuovo Turno Preparatorio");
        dialog.setHeaderText("Crea un nuovo turno preparatorio");
        
        ButtonType createButtonType = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        DatePicker datePicker = new DatePicker(LocalDate.now());
        
        Spinner<Integer> oraInizioSpin = new Spinner<>(0, 23, 8);
        Spinner<Integer> minInizioSpin = new Spinner<>(0, 59, 0, 15);
        
        Spinner<Integer> oraFineSpin = new Spinner<>(0, 23, 17);
        Spinner<Integer> minFineSpin = new Spinner<>(0, 59, 0, 15);
        
        TextField luogoField = new TextField("Cucina");
        
        grid.add(new Label("Data:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Ora inizio:"), 0, 1);
        grid.add(new HBox(5, oraInizioSpin, new Label(":"), minInizioSpin), 1, 1);
        grid.add(new Label("Ora fine:"), 0, 2);
        grid.add(new HBox(5, oraFineSpin, new Label(":"), minFineSpin), 1, 2);
        grid.add(new Label("Luogo:"), 0, 3);
        grid.add(luogoField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    LocalDate data = datePicker.getValue();
                    LocalTime inizio = LocalTime.of(oraInizioSpin.getValue(), minInizioSpin.getValue());
                    LocalTime fine = LocalTime.of(oraFineSpin.getValue(), minFineSpin.getValue());
                    String luogo = luogoField.getText();
                    
                    if (fine.isBefore(inizio)) {
                        showAlert("Errore", "L'orario di fine deve essere successivo a quello di inizio");
                        return null;
                    }
                    
                    return turnoController.createTurnoPreparatorio(data, inizio, fine, luogo);
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(turno -> {
            loadTurni();
            showAlert("Successo", "Turno creato con successo!");
        });
    }
    
    private void showAddTurniRicorrentiDialog() {
        Dialog<List<TurnoPreparatorio>> dialog = new Dialog<>();
        dialog.setTitle("Turni Ricorrenti");
        dialog.setHeaderText("Crea turni preparatori ricorrenti");
        
        ButtonType createButtonType = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        DatePicker dataInizioPicker = new DatePicker(LocalDate.now());
        DatePicker dataFinePicker = new DatePicker(LocalDate.now().plusMonths(3));
        
        ComboBox<java.time.DayOfWeek> giornoCombo = new ComboBox<>();
        giornoCombo.setItems(FXCollections.observableArrayList(java.time.DayOfWeek.values()));
        giornoCombo.setValue(java.time.DayOfWeek.MONDAY);
        
        Spinner<Integer> oraInizioSpin = new Spinner<>(0, 23, 8);
        Spinner<Integer> minInizioSpin = new Spinner<>(0, 59, 0, 15);
        
        Spinner<Integer> oraFineSpin = new Spinner<>(0, 23, 17);
        Spinner<Integer> minFineSpin = new Spinner<>(0, 59, 0, 15);
        
        TextField luogoField = new TextField("Cucina");
        
        grid.add(new Label("Data inizio:"), 0, 0);
        grid.add(dataInizioPicker, 1, 0);
        grid.add(new Label("Data fine:"), 0, 1);
        grid.add(dataFinePicker, 1, 1);
        grid.add(new Label("Giorno settimana:"), 0, 2);
        grid.add(giornoCombo, 1, 2);
        grid.add(new Label("Ora inizio:"), 0, 3);
        grid.add(new HBox(5, oraInizioSpin, new Label(":"), minInizioSpin), 1, 3);
        grid.add(new Label("Ora fine:"), 0, 4);
        grid.add(new HBox(5, oraFineSpin, new Label(":"), minFineSpin), 1, 4);
        grid.add(new Label("Luogo:"), 0, 5);
        grid.add(luogoField, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    LocalDate dataInizio = dataInizioPicker.getValue();
                    LocalDate dataFine = dataFinePicker.getValue();
                    java.time.DayOfWeek giorno = giornoCombo.getValue();
                    LocalTime inizio = LocalTime.of(oraInizioSpin.getValue(), minInizioSpin.getValue());
                    LocalTime fine = LocalTime.of(oraFineSpin.getValue(), minFineSpin.getValue());
                    String luogo = luogoField.getText();
                    
                    if (dataFine.isBefore(dataInizio)) {
                        showAlert("Errore", "La data fine deve essere successiva alla data inizio");
                        return null;
                    }
                    
                    if (fine.isBefore(inizio)) {
                        showAlert("Errore", "L'orario di fine deve essere successivo a quello di inizio");
                        return null;
                    }
                    
                    return turnoController.createTurniPreparatoriRicorrenti(
                        dataInizio, dataFine, giorno, inizio, fine, luogo);
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(turni -> {
            loadTurni();
            showAlert("Successo", "Creati " + turni.size() + " turni ricorrenti!");
        });
    }
    
    private void deleteTurnoPreparatorio() {
        TurnoPreparatorio selectedTurno = turniPrepTable.getSelectionModel().getSelectedItem();
        if (selectedTurno == null) return;
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma Eliminazione");
        confirmAlert.setHeaderText("Eliminare il turno selezionato?");
        confirmAlert.setContentText("Questa azione non può essere annullata.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    turnoController.deleteTurnoPreparatorio(selectedTurno);
                    loadTurni();
                    showAlert("Successo", "Turno eliminato!");
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                }
            }
        });
    }
    
    private void loadTurni() {
        try {
            // Load turni preparatori
            List<TurnoPreparatorio> turniPrep = turnoController.getAllTurniPreparatori();
            ObservableList<TurnoPreparatorio> turniPrepList = FXCollections.observableArrayList(turniPrep);
            if (turniPrepTable != null) {
                turniPrepTable.setItems(turniPrepList);
            }
            
            // Load turni servizio
            List<TurnoServizio> turniServ = turnoController.getAllTurniServizio();
            ObservableList<TurnoServizio> turniServList = FXCollections.observableArrayList(turniServ);
            if (turniServTable != null) {
                turniServTable.setItems(turniServList);
            }
        } catch (Exception e) {
            showAlert("Errore", "Impossibile caricare i turni: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (title.equals("Errore")) {
            alert.setAlertType(Alert.AlertType.ERROR);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}