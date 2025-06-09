package it.catering.catring.view;

import it.catering.catring.controller.ApplicationController;
import it.catering.catring.controller.CompitoController;
import it.catering.catring.controller.AuthController;
import it.catering.catring.model.entities.*;
import it.catering.catring.model.states.StatoCompito;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class CompitoManagementView {
    private CompitoController compitoController;
    private AuthController authController;
    private VBox mainContent;
    private TableView<Compito> compitiTable;
    private ComboBox<String> ordinamentoComboBox;
    
    public CompitoManagementView() {
        this.compitoController = ApplicationController.getInstance().getCompitoController();
        this.authController = ApplicationController.getInstance().getAuthController();
        createView();
        loadCompiti();
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
        
        // Filters and controls
        HBox controlsBox = createControlsBox();
        
        // Table
        compitiTable = createCompitiTable();
        
        // Action buttons
        HBox actionButtons = createActionButtons();
        
        mainContent.getChildren().addAll(header, new Separator(), controlsBox, compitiTable, actionButtons);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Gestione Compiti della Cucina");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Button refreshButton = new Button("Aggiorna");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        refreshButton.setOnAction(e -> loadCompiti());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleLabel, spacer, refreshButton);
        return header;
    }
    
    private HBox createControlsBox() {
        HBox controlsBox = new HBox(15);
        controlsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        controlsBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label sortLabel = new Label("Ordina per:");
        sortLabel.setStyle("-fx-font-weight: bold;");
        
        ordinamentoComboBox = new ComboBox<>();
        ordinamentoComboBox.setItems(FXCollections.observableArrayList(
            "Stato",
            "Difficoltà (Tempo)",
            "Cuoco"
        ));
        ordinamentoComboBox.setValue("Stato");
        ordinamentoComboBox.setOnAction(e -> {
            String selected = ordinamentoComboBox.getValue();
            if (selected != null) {
                String tipo = switch (selected) {
                    case "Stato" -> "stato";
                    case "Difficoltà (Tempo)" -> "difficolta";
                    case "Cuoco" -> "cuoco";
                    default -> "stato";
                };
                compitoController.setOrdinamentoCompiti(tipo);
                loadCompiti();
            }
        });
        
        controlsBox.getChildren().addAll(sortLabel, ordinamentoComboBox);
        return controlsBox;
    }
    
    private TableView<Compito> createCompitiTable() {
        TableView<Compito> table = new TableView<>();
        table.setPrefHeight(400);
        
        // Colonne
        TableColumn<Compito, String> preparazioneCol = new TableColumn<>("Preparazione");
        preparazioneCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getPreparazione().getNome()));
        preparazioneCol.setPrefWidth(200);
        
        TableColumn<Compito, String> cuocoCol = new TableColumn<>("Cuoco");
        cuocoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getCuoco().getNomeCompleto()));
        cuocoCol.setPrefWidth(150);
        
        TableColumn<Compito, String> tempoCol = new TableColumn<>("Tempo Stimato");
        tempoCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getTempoStimato() + " min"));
        tempoCol.setPrefWidth(100);
        
        TableColumn<Compito, String> quantitaCol = new TableColumn<>("Quantità");
        quantitaCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getQuantita())));
        quantitaCol.setPrefWidth(80);
        
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
        noteCol.setPrefWidth(200);
        
        table.getColumns().addAll(preparazioneCol, cuocoCol, tempoCol, quantitaCol, statoCol, noteCol);
        
        return table;
    }
    
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button assignButton = new Button("Assegna Nuovo Compito");
        assignButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5;");
        assignButton.setOnAction(e -> showAssignCompitoDialog());
        
        Button modifyButton = new Button("Modifica Assegnazione");
        modifyButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5;");
        modifyButton.setOnAction(e -> showModifyCompitoDialog());
        modifyButton.disableProperty().bind(compitiTable.getSelectionModel().selectedItemProperty().isNull());
        
        buttonBox.getChildren().addAll(assignButton, modifyButton);
        return buttonBox;
    }
    
    private void showAssignCompitoDialog() {
        Dialog<Compito> dialog = new Dialog<>();
        dialog.setTitle("Assegna Nuovo Compito");
        dialog.setHeaderText("Assegna un compito a un cuoco");
        
        ButtonType assignButtonType = new ButtonType("Assegna", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // ComboBox per preparazioni (simulate)
        ComboBox<String> preparazioneCombo = new ComboBox<>();
        preparazioneCombo.setItems(FXCollections.observableArrayList(
            "Pasta al Pomodoro", "Risotto ai Funghi", "Tiramisù", "Salsa Béchamel"
        ));
        
        // ComboBox per cuochi
        ComboBox<User> cuocoCombo = new ComboBox<>();
        List<User> allUsers = authController.getAllUsers();
        List<User> cuochi = allUsers.stream()
            .filter(u -> u instanceof Cuoco)
            .toList();
        cuocoCombo.setItems(FXCollections.observableArrayList(cuochi));
        cuocoCombo.setConverter(new javafx.util.StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getNomeCompleto() : "";
            }
            
            @Override
            public User fromString(String string) {
                return null;
            }
        });
        
        TextField tempoField = new TextField();
        tempoField.setPromptText("Tempo in minuti");
        
        TextField quantitaField = new TextField();
        quantitaField.setPromptText("Quantità");
        
        grid.add(new Label("Preparazione:"), 0, 0);
        grid.add(preparazioneCombo, 1, 0);
        grid.add(new Label("Cuoco:"), 0, 1);
        grid.add(cuocoCombo, 1, 1);
        grid.add(new Label("Tempo (min):"), 0, 2);
        grid.add(tempoField, 1, 2);
        grid.add(new Label("Quantità:"), 0, 3);
        grid.add(quantitaField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == assignButtonType) {
                try {
                    // Simulated assignment - in a real implementation, you would use the actual preparazione objects
                    showAlert("Successo", "Compito assegnato con successo!\n(Funzionalità simulata)");
                    return null;
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(compito -> loadCompiti());
    }
    
    private void showModifyCompitoDialog() {
        Compito selectedCompito = compitiTable.getSelectionModel().getSelectedItem();
        if (selectedCompito == null) return;
        
        if (selectedCompito.getStato() == StatoCompito.COMPLETATO) {
            showAlert("Errore", "Non è possibile modificare un compito completato");
            return;
        }
        
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Modifica Compito");
        dialog.setHeaderText("Modifica assegnazione del compito");
        
        ButtonType saveButtonType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Display current preparation (read-only)
        TextField preparazioneField = new TextField(selectedCompito.getPreparazione().getNome());
        preparazioneField.setEditable(false);
        
        // ComboBox per cuochi
        ComboBox<User> cuocoCombo = new ComboBox<>();
        List<User> allUsers = authController.getAllUsers();
        List<User> cuochi = allUsers.stream()
            .filter(u -> u instanceof Cuoco)
            .toList();
        cuocoCombo.setItems(FXCollections.observableArrayList(cuochi));
        cuocoCombo.setValue(selectedCompito.getCuoco());
        cuocoCombo.setConverter(new javafx.util.StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getNomeCompleto() : "";
            }
            
            @Override
            public User fromString(String string) {
                return null;
            }
        });
        
        TextField tempoField = new TextField(String.valueOf(selectedCompito.getTempoStimato()));
        TextField quantitaField = new TextField(String.valueOf(selectedCompito.getQuantita()));
        
        grid.add(new Label("Preparazione:"), 0, 0);
        grid.add(preparazioneField, 1, 0);
        grid.add(new Label("Cuoco:"), 0, 1);
        grid.add(cuocoCombo, 1, 1);
        grid.add(new Label("Tempo (min):"), 0, 2);
        grid.add(tempoField, 1, 2);
        grid.add(new Label("Quantità:"), 0, 3);
        grid.add(quantitaField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int nuovoTempo = Integer.parseInt(tempoField.getText());
                    double nuovaQuantita = Double.parseDouble(quantitaField.getText());
                    Cuoco nuovoCuoco = (Cuoco) cuocoCombo.getValue();
                    
                    compitoController.modificaAssegnazione(selectedCompito, nuovoCuoco, nuovoTempo, nuovaQuantita);
                    return true;
                } catch (NumberFormatException e) {
                    showAlert("Errore", "Inserire valori numerici validi");
                    return false;
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                    return false;
                }
            }
            return false;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            if (result) {
                loadCompiti();
            }
        });
    }
    
    private void loadCompiti() {
        try {
            List<Compito> compiti = compitoController.getTuttiICompiti();
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