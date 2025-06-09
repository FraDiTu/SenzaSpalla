package it.catering.catring.view;

import it.catering.catring.controller.ApplicationController;
import it.catering.catring.controller.CompitoController;
import it.catering.catring.model.entities.Compito;
import it.catering.catring.model.states.StatoCompito;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class MieiCompitiView {
    private CompitoController compitoController;
    private VBox mainContent;
    private TableView<Compito> compitiTable;
    
    public MieiCompitiView() {
        this.compitoController = ApplicationController.getInstance().getCompitoController();
        createView();
        loadMieiCompiti();
    }
    
    public ScrollPane getView() {
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }
    
    private void createView() {
        mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: white;");
        
        // Header
        HBox header = createHeader();
        
        // Table
        compitiTable = createCompitiTable();
        
        // Action buttons
        HBox actionButtons = createActionButtons();
        
        mainContent.getChildren().addAll(header, new Separator(), compitiTable, actionButtons);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("I Miei Compiti");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Button refreshButton = new Button("Aggiorna");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        refreshButton.setOnAction(e -> loadMieiCompiti());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleLabel, spacer, refreshButton);
        return header;
    }
    
    private TableView<Compito> createCompitiTable() {
        TableView<Compito> table = new TableView<>();
        table.setPrefHeight(400);
        
        // Colonne
        TableColumn<Compito, String> preparazioneCol = new TableColumn<>("Preparazione");
        preparazioneCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getPreparazione().getNome()));
        preparazioneCol.setPrefWidth(250);
        
        TableColumn<Compito, String> tempoCol = new TableColumn<>("Tempo Stimato");
        tempoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getTempoStimato() + " min"));
        tempoCol.setPrefWidth(120);
        
        TableColumn<Compito, String> quantitaCol = new TableColumn<>("Quantità");
        quantitaCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getQuantita())));
        quantitaCol.setPrefWidth(100);
        
        TableColumn<Compito, String> statoCol = new TableColumn<>("Stato");
        statoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getStato().toString()));
        statoCol.setPrefWidth(120);
        
        // Custom cell factory per colorare le righe in base allo stato
        statoCol.setCellFactory(column -> new TableCell<Compito, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    TableRow<Compito> row = getTableRow();
                    if (row != null && row.getItem() != null) {
                        StatoCompito stato = row.getItem().getStato();
                        switch (stato) {
                            case COMPLETATO -> row.setStyle("-fx-background-color: #d4edda;");
                            case IN_CORSO -> row.setStyle("-fx-background-color: #fff3cd;");
                            case PROBLEMA -> row.setStyle("-fx-background-color: #f8d7da;");
                            default -> row.setStyle("");
                        }
                    }
                }
            }
        });
        
        TableColumn<Compito, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getNote() != null ? data.getValue().getNote() : ""));
        noteCol.setPrefWidth(300);
        
        table.getColumns().addAll(preparazioneCol, tempoCol, quantitaCol, statoCol, noteCol);
        
        return table;
    }
    
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button startButton = new Button("Inizia");
        startButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5;");
        startButton.setOnAction(e -> iniziaCompito());
        startButton.disableProperty().bind(compitiTable.getSelectionModel().selectedItemProperty().isNull());
        
        Button completeButton = new Button("Completa");
        completeButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5;");
        completeButton.setOnAction(e -> completaCompito());
        completeButton.disableProperty().bind(compitiTable.getSelectionModel().selectedItemProperty().isNull());
        
        Button problemButton = new Button("Segnala Problema");
        problemButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5;");
        problemButton.setOnAction(e -> segnalaProblema());
        problemButton.disableProperty().bind(compitiTable.getSelectionModel().selectedItemProperty().isNull());
        
        buttonBox.getChildren().addAll(startButton, completeButton, problemButton);
        return buttonBox;
    }
    
    private void iniziaCompito() {
        Compito selectedCompito = compitiTable.getSelectionModel().getSelectedItem();
        if (selectedCompito == null) return;
        
        if (selectedCompito.getStato() != StatoCompito.ASSEGNATO) {
            showAlert("Errore", "Il compito deve essere nello stato 'Assegnato' per essere iniziato");
            return;
        }
        
        try {
            compitoController.iniziaCompito(selectedCompito);
            loadMieiCompiti();
            showAlert("Successo", "Compito iniziato!");
        } catch (Exception e) {
            showAlert("Errore", e.getMessage());
        }
    }
    
    private void completaCompito() {
        Compito selectedCompito = compitiTable.getSelectionModel().getSelectedItem();
        if (selectedCompito == null) return;
        
        if (selectedCompito.getStato() != StatoCompito.IN_CORSO) {
            showAlert("Errore", "Il compito deve essere nello stato 'In Corso' per essere completato");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma Completamento");
        confirmAlert.setHeaderText("Sei sicuro di aver completato questo compito?");
        confirmAlert.setContentText("Preparazione: " + selectedCompito.getPreparazione().getNome());
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    compitoController.completaCompito(selectedCompito);
                    loadMieiCompiti();
                    showAlert("Successo", "Compito completato!");
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                }
            }
        });
    }
    
    private void segnalaProblema() {
        Compito selectedCompito = compitiTable.getSelectionModel().getSelectedItem();
        if (selectedCompito == null) return;
        
        if (selectedCompito.getStato() == StatoCompito.COMPLETATO) {
            showAlert("Errore", "Non è possibile segnalare problemi su un compito completato");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Segnala Problema");
        dialog.setHeaderText("Descrivi il problema riscontrato");
        dialog.setContentText("Note:");
        
        dialog.showAndWait().ifPresent(note -> {
            if (!note.trim().isEmpty()) {
                try {
                    compitoController.segnalaProblema(selectedCompito, note.trim());
                    loadMieiCompiti();
                    showAlert("Successo", "Problema segnalato!");
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                }
            }
        });
    }
    
    private void loadMieiCompiti() {
        try {
            List<Compito> compiti = compitoController.getCompitiPerCuocoCorrente();
            ObservableList<Compito> compitiList = FXCollections.observableArrayList(compiti);
            compitiTable.setItems(compitiList);
        } catch (Exception e) {
            showAlert("Errore", "Impossibile caricare i compiti: " + e.getMessage());
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
