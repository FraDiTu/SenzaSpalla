// File: src/main/java/it/catering/catring/view/RicettarioView.java
package it.catering.catring.view;

import it.catering.catring.controller.ApplicationController;
import it.catering.catring.controller.AuthController;
import it.catering.catring.model.entities.*;
import it.catering.catring.model.managers.RicettarioManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RicettarioView {
    private RicettarioManager ricettarioManager;
    private AuthController authController;
    private VBox mainContent;
    private TabPane tabPane;
    
    // Tab Ricette
    private ListView<Ricetta> ricetteListView;
    private VBox ricettaDetailsPane;
    private Ricetta selectedRicetta;
    
    // Tab Preparazioni
    private ListView<Preparazione> preparazioniListView;
    private VBox preparazioneDetailsPane;
    private Preparazione selectedPreparazione;
    
    // Filtri
    private TextField searchField;
    private ComboBox<String> tagFilterCombo;
    private CheckBox showOnlyMineCheckBox;
    
    public RicettarioView() {
        this.ricettarioManager = RicettarioManager.getInstance();
        this.authController = ApplicationController.getInstance().getAuthController();
        createView();
        loadData();
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
        
        // Filtri
        HBox filtersBox = createFiltersBox();
        
        // TabPane per Ricette e Preparazioni
        tabPane = createTabPane();
        
        mainContent.getChildren().addAll(header, new Separator(), filtersBox, tabPane);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Ricettario");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Button refreshButton = new Button("Aggiorna");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        refreshButton.setOnAction(e -> loadData());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleLabel, spacer, refreshButton);
        return header;
    }
    
    private HBox createFiltersBox() {
        HBox filtersBox = new HBox(15);
        filtersBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        filtersBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label searchLabel = new Label("Cerca:");
        searchLabel.setStyle("-fx-font-weight: bold;");
        
        searchField = new TextField();
        searchField.setPromptText("Nome ricetta o preparazione...");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        
        Label tagLabel = new Label("Tag:");
        tagLabel.setStyle("-fx-font-weight: bold;");
        
        tagFilterCombo = new ComboBox<>();
        tagFilterCombo.setPromptText("Tutti i tag");
        tagFilterCombo.setPrefWidth(150);
        tagFilterCombo.setOnAction(e -> applyFilters());
        
        showOnlyMineCheckBox = new CheckBox("Solo le mie");
        showOnlyMineCheckBox.setOnAction(e -> applyFilters());
        
        filtersBox.getChildren().addAll(searchLabel, searchField, tagLabel, tagFilterCombo, showOnlyMineCheckBox);
        return filtersBox;
    }
    
    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        
        // Tab Ricette
        Tab ricetteTab = new Tab("Ricette");
        ricetteTab.setClosable(false);
        ricetteTab.setContent(createRicetteTab());
        
        // Tab Preparazioni
        Tab preparazioniTab = new Tab("Preparazioni");
        preparazioniTab.setClosable(false);
        preparazioniTab.setContent(createPreparazioniTab());
        
        tabPane.getTabs().addAll(ricetteTab, preparazioniTab);
        return tabPane;
    }
    
    private HBox createRicetteTab() {
        HBox content = new HBox(20);
        content.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        
        // Lista ricette
        VBox ricetteListPane = createRicetteListPane();
        
        // Dettagli ricetta
        ricettaDetailsPane = createRicettaDetailsPane();
        
        content.getChildren().addAll(ricetteListPane, ricettaDetailsPane);
        return content;
    }
    
    private HBox createPreparazioniTab() {
        HBox content = new HBox(20);
        content.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        
        // Lista preparazioni
        VBox preparazioniListPane = createPreparazioniListPane();
        
        // Dettagli preparazione
        preparazioneDetailsPane = createPreparazioneDetailsPane();
        
        content.getChildren().addAll(preparazioniListPane, preparazioneDetailsPane);
        return content;
    }
    
    private VBox createRicetteListPane() {
        VBox pane = new VBox(10);
        pane.setPrefWidth(300);
        pane.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label listTitle = new Label("Ricette");
        listTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Button newRicettaButton = new Button("Nuova Ricetta");
        newRicettaButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3;");
        newRicettaButton.setOnAction(e -> showCreateRicettaDialog());
        
        headerBox.getChildren().addAll(listTitle, newRicettaButton);
        
        ricetteListView = new ListView<>();
        ricetteListView.setPrefHeight(400);
        ricetteListView.getSelectionModel().selectedItemProperty().addListener((obs, oldRicetta, newRicetta) -> {
            selectedRicetta = newRicetta;
            updateRicettaDetails();
        });
        
        pane.getChildren().addAll(headerBox, ricetteListView);
        return pane;
    }
    
    private VBox createPreparazioniListPane() {
        VBox pane = new VBox(10);
        pane.setPrefWidth(300);
        pane.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label listTitle = new Label("Preparazioni");
        listTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Button newPreparazioneButton = new Button("Nuova Preparazione");
        newPreparazioneButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3;");
        newPreparazioneButton.setOnAction(e -> showCreatePreparazioneDialog());
        
        headerBox.getChildren().addAll(listTitle, newPreparazioneButton);
        
        preparazioniListView = new ListView<>();
        preparazioniListView.setPrefHeight(400);
        preparazioniListView.getSelectionModel().selectedItemProperty().addListener((obs, oldPrep, newPrep) -> {
            selectedPreparazione = newPrep;
            updatePreparazioneDetails();
        });
        
        pane.getChildren().addAll(headerBox, preparazioniListView);
        return pane;
    }
    
    private VBox createRicettaDetailsPane() {
        VBox pane = new VBox(15);
        pane.setPrefWidth(600);
        pane.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label detailsTitle = new Label("Dettagli Ricetta");
        detailsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label noSelectionLabel = new Label("Seleziona una ricetta dalla lista per visualizzare i dettagli");
        noSelectionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        pane.getChildren().addAll(detailsTitle, noSelectionLabel);
        return pane;
    }
    
    private VBox createPreparazioneDetailsPane() {
        VBox pane = new VBox(15);
        pane.setPrefWidth(600);
        pane.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label detailsTitle = new Label("Dettagli Preparazione");
        detailsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label noSelectionLabel = new Label("Seleziona una preparazione dalla lista per visualizzare i dettagli");
        noSelectionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        pane.getChildren().addAll(detailsTitle, noSelectionLabel);
        return pane;
    }
    
    private void updateRicettaDetails() {
        ricettaDetailsPane.getChildren().clear();
        
        Label detailsTitle = new Label("Dettagli Ricetta");
        detailsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        ricettaDetailsPane.getChildren().add(detailsTitle);
        
        if (selectedRicetta == null) {
            Label noSelectionLabel = new Label("Seleziona una ricetta dalla lista per visualizzare i dettagli");
            noSelectionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            ricettaDetailsPane.getChildren().add(noSelectionLabel);
            return;
        }
        
        // Informazioni ricetta
        VBox infoBox = new VBox(10);
        infoBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label nomeLabel = new Label("Nome: " + selectedRicetta.getNome());
        nomeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        infoBox.getChildren().add(nomeLabel);
        
        if (selectedRicetta.getDescrizione() != null && !selectedRicetta.getDescrizione().trim().isEmpty()) {
            Label descLabel = new Label("Descrizione: " + selectedRicetta.getDescrizione());
            descLabel.setWrapText(true);
            infoBox.getChildren().add(descLabel);
        }
        
        Label proprietarioLabel = new Label("Proprietario: " + selectedRicetta.getProprietario().getNomeCompleto());
        infoBox.getChildren().add(proprietarioLabel);
        
        if (selectedRicetta.getAutore() != null) {
            Label autoreLabel = new Label("Autore: " + selectedRicetta.getAutore());
            infoBox.getChildren().add(autoreLabel);
        }
        
        Label portateLabel = new Label("Porzioni: " + selectedRicetta.getNumeroPortate());
        infoBox.getChildren().add(portateLabel);
        
        Label tempoLabel = new Label("Tempo preparazione: " + selectedRicetta.getTempoPreparazione() + " minuti");
        infoBox.getChildren().add(tempoLabel);
        
        Label statoLabel = new Label("Stato: " + selectedRicetta.getStateName());
        statoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + 
                           (selectedRicetta.isPubblicata() ? "#27ae60" : "#e67e22") + ";");
        infoBox.getChildren().add(statoLabel);
        
        // Tags
        if (!selectedRicetta.getTags().isEmpty()) {
            Label tagsLabel = new Label("Tag: " + String.join(", ", selectedRicetta.getTags()));
            infoBox.getChildren().add(tagsLabel);
        }
        
        // Ingredienti
        if (!selectedRicetta.getIngredienti().isEmpty()) {
            Label ingredientiTitle = new Label("Ingredienti:");
            ingredientiTitle.setStyle("-fx-font-weight: bold;");
            infoBox.getChildren().add(ingredientiTitle);
            
            for (Dose dose : selectedRicetta.getIngredienti()) {
                Label ingredienteLabel = new Label("• " + dose.toString());
                infoBox.getChildren().add(ingredienteLabel);
            }
        }
        
        // Istruzioni
        VBox istruzioniBox = new VBox(10);
        istruzioniBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5;");
        
        if (!selectedRicetta.getIstruzioniAnticipo().isEmpty()) {
            Label anticipoTitle = new Label("Istruzioni da eseguire in anticipo:");
            anticipoTitle.setStyle("-fx-font-weight: bold;");
            istruzioniBox.getChildren().add(anticipoTitle);
            
            for (int i = 0; i < selectedRicetta.getIstruzioniAnticipo().size(); i++) {
                Label istruzioneLabel = new Label((i + 1) + ". " + selectedRicetta.getIstruzioniAnticipo().get(i));
                istruzioneLabel.setWrapText(true);
                istruzioniBox.getChildren().add(istruzioneLabel);
            }
        }
        
        if (!selectedRicetta.getIstruzioniUltimo().isEmpty()) {
            Label ultimoTitle = new Label("Istruzioni da eseguire sul posto:");
            ultimoTitle.setStyle("-fx-font-weight: bold;");
            istruzioniBox.getChildren().add(ultimoTitle);
            
            for (int i = 0; i < selectedRicetta.getIstruzioniUltimo().size(); i++) {
                Label istruzioneLabel = new Label((i + 1) + ". " + selectedRicetta.getIstruzioniUltimo().get(i));
                istruzioneLabel.setWrapText(true);
                istruzioniBox.getChildren().add(istruzioneLabel);
            }
        }
        
        // Bottoni azione
        HBox buttonBox = createRicettaActionButtons();
        
        ricettaDetailsPane.getChildren().addAll(infoBox, istruzioniBox, buttonBox);
    }
    
    private void updatePreparazioneDetails() {
        preparazioneDetailsPane.getChildren().clear();
        
        Label detailsTitle = new Label("Dettagli Preparazione");
        detailsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        preparazioneDetailsPane.getChildren().add(detailsTitle);
        
        if (selectedPreparazione == null) {
            Label noSelectionLabel = new Label("Seleziona una preparazione dalla lista per visualizzare i dettagli");
            noSelectionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            preparazioneDetailsPane.getChildren().add(noSelectionLabel);
            return;
        }
        
        // Simile alla ricetta ma con quantità risultante
        VBox infoBox = new VBox(10);
        infoBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label nomeLabel = new Label("Nome: " + selectedPreparazione.getNome());
        nomeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        infoBox.getChildren().add(nomeLabel);
        
        if (selectedPreparazione.getDescrizione() != null && !selectedPreparazione.getDescrizione().trim().isEmpty()) {
            Label descLabel = new Label("Descrizione: " + selectedPreparazione.getDescrizione());
            descLabel.setWrapText(true);
            infoBox.getChildren().add(descLabel);
        }
        
        Label proprietarioLabel = new Label("Proprietario: " + selectedPreparazione.getProprietario().getNomeCompleto());
        infoBox.getChildren().add(proprietarioLabel);
        
        if (selectedPreparazione.getAutore() != null) {
            Label autoreLabel = new Label("Autore: " + selectedPreparazione.getAutore());
            infoBox.getChildren().add(autoreLabel);
        }
        
        if (selectedPreparazione.getQuantitaRisultante() > 0) {
            Label quantitaLabel = new Label("Quantità risultante: " + selectedPreparazione.getQuantitaRisultante() + 
                                          " " + selectedPreparazione.getUnitaMisuraRisultato());
            infoBox.getChildren().add(quantitaLabel);
        }
        
        Label tempoLabel = new Label("Tempo preparazione: " + selectedPreparazione.getTempoPreparazione() + " minuti");
        infoBox.getChildren().add(tempoLabel);
        
        Label statoLabel = new Label("Stato: " + selectedPreparazione.getStateName());
        statoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + 
                           (selectedPreparazione.isPubblicata() ? "#27ae60" : "#e67e22") + ";");
        infoBox.getChildren().add(statoLabel);
        
        // Tags
        if (!selectedPreparazione.getTags().isEmpty()) {
            Label tagsLabel = new Label("Tag: " + String.join(", ", selectedPreparazione.getTags()));
            infoBox.getChildren().add(tagsLabel);
        }
        
        // Resto simile alla ricetta...
        HBox buttonBox = createPreparazioneActionButtons();
        
        preparazioneDetailsPane.getChildren().addAll(infoBox, buttonBox);
    }
    
    private HBox createRicettaActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        User currentUser = authController.getCurrentUser();
        boolean isOwner = selectedRicetta.getProprietario().equals(currentUser);
        boolean canModify = ricettarioManager.canModify(selectedRicetta, currentUser);
        boolean canDelete = ricettarioManager.canDelete(selectedRicetta, currentUser);
        
        if (canModify) {
            Button editButton = new Button("Modifica");
            editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
            editButton.setOnAction(e -> showEditRicettaDialog());
            buttonBox.getChildren().add(editButton);
        }
        
        if (isOwner) {
            if (selectedRicetta.isPubblicata()) {
                Button ritiraButton = new Button("Ritira dalla Pubblicazione");
                ritiraButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
                ritiraButton.setOnAction(e -> ritiraRicetta());
                buttonBox.getChildren().add(ritiraButton);
            } else {
                Button pubblicaButton = new Button("Pubblica");
                pubblicaButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
                pubblicaButton.setOnAction(e -> pubblicaRicetta());
                buttonBox.getChildren().add(pubblicaButton);
            }
        }
        
        Button copyButton = new Button("Copia");
        copyButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        copyButton.setOnAction(e -> showCopyRicettaDialog());
        buttonBox.getChildren().add(copyButton);
        
        if (canDelete) {
            Button deleteButton = new Button("Elimina");
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
            deleteButton.setOnAction(e -> deleteRicetta());
            buttonBox.getChildren().add(deleteButton);
        }
        
        return buttonBox;
    }
    
    private HBox createPreparazioneActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        User currentUser = authController.getCurrentUser();
        boolean isOwner = selectedPreparazione.getProprietario().equals(currentUser);
        boolean canModify = ricettarioManager.canModify(selectedPreparazione, currentUser);
        boolean canDelete = ricettarioManager.canDelete(selectedPreparazione, currentUser);
        
        if (canModify) {
            Button editButton = new Button("Modifica");
            editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
            editButton.setOnAction(e -> showEditPreparazioneDialog());
            buttonBox.getChildren().add(editButton);
        }
        
        if (isOwner) {
            if (selectedPreparazione.isPubblicata()) {
                Button ritiraButton = new Button("Ritira dalla Pubblicazione");
                ritiraButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
                ritiraButton.setOnAction(e -> ritiraPreparazione());
                buttonBox.getChildren().add(ritiraButton);
            } else {
                Button pubblicaButton = new Button("Pubblica");
                pubblicaButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
                pubblicaButton.setOnAction(e -> pubblicaPreparazione());
                buttonBox.getChildren().add(pubblicaButton);
            }
        }
        
        Button copyButton = new Button("Copia");
        copyButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        copyButton.setOnAction(e -> showCopyPreparazioneDialog());
        buttonBox.getChildren().add(copyButton);
        
        if (canDelete) {
            Button deleteButton = new Button("Elimina");
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
            deleteButton.setOnAction(e -> deletePreparazione());
            buttonBox.getChildren().add(deleteButton);
        }
        
        return buttonBox;
    }
    
    // Metodi di azione
    private void showCreateRicettaDialog() {
        // Implementazione dialog creazione ricetta
        showAlert("Info", "Funzionalità creazione ricetta - Da implementare");
    }
    
    private void showCreatePreparazioneDialog() {
        // Implementazione dialog creazione preparazione
        showAlert("Info", "Funzionalità creazione preparazione - Da implementare");
    }
    
    private void showEditRicettaDialog() {
        // Implementazione dialog modifica ricetta
        showAlert("Info", "Funzionalità modifica ricetta - Da implementare");
    }
    
    private void showEditPreparazioneDialog() {
        // Implementazione dialog modifica preparazione
        showAlert("Info", "Funzionalità modifica preparazione - Da implementare");
    }
    
    private void showCopyRicettaDialog() {
        TextInputDialog dialog = new TextInputDialog(selectedRicetta.getNome() + " - Copia");
        dialog.setTitle("Copia Ricetta");
        dialog.setHeaderText("Inserisci il nome per la copia della ricetta");
        dialog.setContentText("Nome:");
        
        dialog.showAndWait().ifPresent(nome -> {
            if (!nome.trim().isEmpty()) {
                try {
                    ricettarioManager.copyRicetta(selectedRicetta, authController.getCurrentUser(), nome.trim());
                    loadData();
                    showAlert("Successo", "Ricetta copiata con successo!");
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                }
            }
        });
    }
    
    private void showCopyPreparazioneDialog() {
        TextInputDialog dialog = new TextInputDialog(selectedPreparazione.getNome() + " - Copia");
        dialog.setTitle("Copia Preparazione");
        dialog.setHeaderText("Inserisci il nome per la copia della preparazione");
        dialog.setContentText("Nome:");
        
        dialog.showAndWait().ifPresent(nome -> {
            if (!nome.trim().isEmpty()) {
                try {
                    ricettarioManager.copyPreparazione(selectedPreparazione, authController.getCurrentUser(), nome.trim());
                    loadData();
                    showAlert("Successo", "Preparazione copiata con successo!");
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                }
            }
        });
    }
    
    private void pubblicaRicetta() {
        try {
            ricettarioManager.pubblicaRicetta(selectedRicetta);
            loadData();
            updateRicettaDetails();
            showAlert("Successo", "Ricetta pubblicata!");
        } catch (Exception e) {
            showAlert("Errore", e.getMessage());
        }
    }
    
    private void ritiraRicetta() {
        try {
            ricettarioManager.ritiraRicettaDallaPubblicazione(selectedRicetta);
            loadData();
            updateRicettaDetails();
            showAlert("Successo", "Ricetta ritirata dalla pubblicazione!");
        } catch (Exception e) {
            showAlert("Errore", e.getMessage());
        }
    }
    
    private void pubblicaPreparazione() {
        try {
            ricettarioManager.pubblicaPreparazione(selectedPreparazione);
            loadData();
            updatePreparazioneDetails();
            showAlert("Successo", "Preparazione pubblicata!");
        } catch (Exception e) {
            showAlert("Errore", e.getMessage());
        }
    }
    
    private void ritiraPreparazione() {
        try {
            ricettarioManager.ritiraPreparazioneDallaPubblicazione(selectedPreparazione);
            loadData();
            updatePreparazioneDetails();
            showAlert("Successo", "Preparazione ritirata dalla pubblicazione!");
        } catch (Exception e) {
            showAlert("Errore", e.getMessage());
        }
    }
    
    private void deleteRicetta() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma Eliminazione");
        confirmAlert.setHeaderText("Eliminare la ricetta selezionata?");
        confirmAlert.setContentText("Questa azione non può essere annullata.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    ricettarioManager.deleteRicetta(selectedRicetta);
                    loadData();
                    selectedRicetta = null;
                    updateRicettaDetails();
                    showAlert("Successo", "Ricetta eliminata!");
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                }
            }
        });
    }
    
    private void deletePreparazione() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma Eliminazione");
        confirmAlert.setHeaderText("Eliminare la preparazione selezionata?");
        confirmAlert.setContentText("Questa azione non può essere annullata.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    ricettarioManager.deletePreparazione(selectedPreparazione);
                    loadData();
                    selectedPreparazione = null;
                    updatePreparazioneDetails();
                    showAlert("Successo", "Preparazione eliminata!");
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                }
            }
        });
    }
    
    private void applyFilters() {
        loadRicette();
        loadPreparazioni();
    }
    
    private void loadData() {
        loadTags();
        loadRicette();
        loadPreparazioni();
    }
    
    private void loadTags() {
        Set<String> allTags = ricettarioManager.getAllTags();
        ObservableList<String> tagsList = FXCollections.observableArrayList(allTags);
        tagsList.add(0, "Tutti i tag");
        tagFilterCombo.setItems(tagsList);
        tagFilterCombo.setValue("Tutti i tag");
    }
    
    private void loadRicette() {
        User currentUser = authController.getCurrentUser();
        List<Ricetta> ricette;
        
        if (showOnlyMineCheckBox.isSelected()) {
            ricette = ricettarioManager.getRicetteByProprietario(currentUser);
        } else {
            ricette = ricettarioManager.getRicetteVisibili(currentUser);
        }
        
        // Applica filtri
        String searchText = searchField.getText().toLowerCase();
        String selectedTag = tagFilterCombo.getValue();
        
        if (searchText != null && !searchText.trim().isEmpty()) {
            ricette = ricette.stream()
                .filter(r -> r.getNome().toLowerCase().contains(searchText))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        if (selectedTag != null && !selectedTag.equals("Tutti i tag")) {
            ricette = ricette.stream()
                .filter(r -> r.hasTag(selectedTag))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        ObservableList<Ricetta> ricetteList = FXCollections.observableArrayList(ricette);
        ricetteListView.setItems(ricetteList);
    }
    
    private void loadPreparazioni() {
        User currentUser = authController.getCurrentUser();
        List<Preparazione> preparazioni;
        
        if (showOnlyMineCheckBox.isSelected()) {
            preparazioni = ricettarioManager.getPreparazioniByProprietario(currentUser);
        } else {
            preparazioni = ricettarioManager.getPreparazioniVisibili(currentUser);
        }
        
        // Applica filtri
        String searchText = searchField.getText().toLowerCase();
        String selectedTag = tagFilterCombo.getValue();
        
        if (searchText != null && !searchText.trim().isEmpty()) {
            preparazioni = preparazioni.stream()
                .filter(p -> p.getNome().toLowerCase().contains(searchText))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        if (selectedTag != null && !selectedTag.equals("Tutti i tag")) {
            preparazioni = preparazioni.stream()
                .filter(p -> p.hasTag(selectedTag))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        ObservableList<Preparazione> preparazioniList = FXCollections.observableArrayList(preparazioni);
        preparazioniListView.setItems(preparazioniList);
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