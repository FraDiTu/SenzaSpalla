// File: src/main/java/it/catering/catring/view/BachecaView.java
package it.catering.catring.view;

import it.catering.catring.controller.ApplicationController;
import it.catering.catring.controller.AuthController;
import it.catering.catring.model.entities.*;
import it.catering.catring.model.entities.Menu;
import it.catering.catring.model.managers.MenuManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BachecaView {
    private MenuManager menuManager;
    private AuthController authController;
    private VBox mainContent;
    private ListView<Menu> bachecaListView;
    private VBox menuDetailsPane;
    private Menu selectedMenu;
    
    public BachecaView() {
        this.menuManager = MenuManager.getInstance();
        this.authController = ApplicationController.getInstance().getAuthController();
        createView();
        loadMenuPubblici();
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
        
        // Content area
        HBox contentArea = new HBox(20);
        contentArea.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        
        // Menu list
        VBox menuListPane = createMenuListPane();
        
        // Menu details
        menuDetailsPane = createMenuDetailsPane();
        
        contentArea.getChildren().addAll(menuListPane, menuDetailsPane);
        
        mainContent.getChildren().addAll(header, new Separator(), contentArea);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("📋 Bacheca Menu Pubblici");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Button refreshButton = new Button("Aggiorna");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
        refreshButton.setOnAction(e -> loadMenuPubblici());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Info per ruolo
        Label roleInfo = new Label();
        User currentUser = authController.getCurrentUser();
        if (currentUser instanceof Chef) {
            roleInfo.setText("👨‍🍳 Pubblica i tuoi menu per renderli visibili a tutti");
        } else if (currentUser instanceof Organizzatore) {
            roleInfo.setText("👔 Menu disponibili per i tuoi eventi");
        } else {
            roleInfo.setText("👀 Menu pubblici disponibili");
        }
        roleInfo.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        header.getChildren().addAll(titleLabel, spacer, roleInfo, refreshButton);
        return header;
    }
    
    private VBox createMenuListPane() {
        VBox pane = new VBox(10);
        pane.setPrefWidth(350);
        pane.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label listTitle = new Label("📚 Menu Pubblici");
        listTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        bachecaListView = new ListView<>();
        bachecaListView.setPrefHeight(500);
        bachecaListView.setCellFactory(listView -> new MenuPublicoListCell());
        bachecaListView.getSelectionModel().selectedItemProperty().addListener((obs, oldMenu, newMenu) -> {
            selectedMenu = newMenu;
            updateMenuDetails();
        });
        
        pane.getChildren().addAll(listTitle, bachecaListView);
        return pane;
    }
    
    private VBox createMenuDetailsPane() {
        VBox pane = new VBox(15);
        pane.setPrefWidth(650);
        pane.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label detailsTitle = new Label("Dettagli Menu");
        detailsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label noSelectionLabel = new Label("Seleziona un menu dalla bacheca per visualizzare i dettagli");
        noSelectionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        pane.getChildren().addAll(detailsTitle, noSelectionLabel);
        return pane;
    }
    
    private void updateMenuDetails() {
        menuDetailsPane.getChildren().clear();
        
        Label detailsTitle = new Label("📋 Dettagli Menu");
        detailsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        menuDetailsPane.getChildren().add(detailsTitle);
        
        if (selectedMenu == null) {
            Label noSelectionLabel = new Label("Seleziona un menu dalla bacheca per visualizzare i dettagli");
            noSelectionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            menuDetailsPane.getChildren().add(noSelectionLabel);
            return;
        }
        
        // Menu info
        VBox menuInfo = new VBox(12);
        menuInfo.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        // Titolo e chef
        Label menuTitleLabel = new Label("🍽 " + selectedMenu.getTitolo());
        menuTitleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        menuInfo.getChildren().add(menuTitleLabel);
        
        Label chefLabel = new Label("👨‍🍳 Chef: " + selectedMenu.getChef().getNomeCompleto());
        chefLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e; -fx-font-weight: bold;");
        menuInfo.getChildren().add(chefLabel);
        
        // Data pubblicazione
        if (selectedMenu.getDataCreazione() != null) {
            Label dataLabel = new Label("📅 Pubblicato: " + 
                LocalDateTime.ofInstant(selectedMenu.getDataCreazione().toInstant(), 
                java.time.ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            dataLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
            menuInfo.getChildren().add(dataLabel);
        }
        
        // Descrizione/Caratteristiche
        if (selectedMenu.getDescrizione() != null && !selectedMenu.getDescrizione().trim().isEmpty()) {
            VBox descBox = new VBox(8);
            descBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
            
            Label descTitleLabel = new Label("📝 Caratteristiche:");
            descTitleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            descBox.getChildren().add(descTitleLabel);
            
            String[] sections = selectedMenu.getDescrizione().split("\n\n");
            for (String section : sections) {
                if (!section.trim().isEmpty()) {
                    Label sectionLabel = new Label(section.trim());
                    sectionLabel.setWrapText(true);
                    sectionLabel.setStyle("-fx-text-fill: #495057; -fx-font-size: 13px;");
                    descBox.getChildren().add(sectionLabel);
                }
            }
            
            menuInfo.getChildren().add(descBox);
        }
        
        // Statistiche
        VBox statsBox = new VBox(5);
        statsBox.setStyle("-fx-background-color: #e8f5e8; -fx-padding: 10; -fx-background-radius: 5;");
        Label statsTitle = new Label("📊 Informazioni:");
        statsTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");
        statsBox.getChildren().add(statsTitle);
        
        int totalPortate = selectedMenu.getTutteLeRicette().stream()
            .mapToInt(Ricetta::getNumeroPortate)
            .sum();
        
        statsBox.getChildren().add(new Label("🍽 Sezioni: " + selectedMenu.getSezioni().size()));
        statsBox.getChildren().add(new Label("📋 Ricette totali: " + selectedMenu.getTutteLeRicette().size()));
        statsBox.getChildren().add(new Label("👥 Portate totali: " + totalPortate));
        
        menuInfo.getChildren().add(statsBox);
        
        // Sezioni e ricette
        VBox sezioniArea = new VBox(10);
        sezioniArea.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);");
        
        Label sezioniTitle = new Label("🍴 Menu Completo:");
        sezioniTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        sezioniArea.getChildren().add(sezioniTitle);
        
        if (selectedMenu.getSezioni().isEmpty()) {
            Label noSezioniLabel = new Label("Menu in fase di definizione...");
            noSezioniLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            sezioniArea.getChildren().add(noSezioniLabel);
        } else {
            for (SezioneMenu sezione : selectedMenu.getSezioni()) {
                VBox sezioneBox = new VBox(5);
                sezioneBox.setStyle("-fx-border-color: #e9ecef; -fx-border-width: 1; -fx-padding: 12; -fx-background-radius: 5; -fx-background-color: #fafafa;");
                
                Label sezioneLabel = new Label("📂 " + sezione.getNome());
                sezioneLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #495057; -fx-font-size: 13px;");
                sezioneBox.getChildren().add(sezioneLabel);
                
                if (sezione.getVoci().isEmpty()) {
                    Label noVociLabel = new Label("    In preparazione...");
                    noVociLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic; -fx-font-size: 11px;");
                    sezioneBox.getChildren().add(noVociLabel);
                } else {
                    for (VoceMenu voce : sezione.getVoci()) {
                        HBox voceBox = new HBox(8);
                        voceBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                        
                        Label voceLabel = new Label("🍴 " + voce.getNomeVoce());
                        voceLabel.setStyle("-fx-font-size: 12px;");
                        
                        if (!voce.getNomeVoce().equals(voce.getRicetta().getNome())) {
                            Label originalLabel = new Label("(" + voce.getRicetta().getNome() + ")");
                            originalLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-size: 10px; -fx-font-style: italic;");
                            voceBox.getChildren().addAll(voceLabel, originalLabel);
                        } else {
                            voceBox.getChildren().add(voceLabel);
                        }
                        
                        // Mostra numero portate
                        Label portateLabel = new Label("👥 " + voce.getRicetta().getNumeroPortate());
                        portateLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 10px; -fx-font-weight: bold;");
                        
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);
                        voceBox.getChildren().addAll(spacer, portateLabel);
                        
                        sezioneBox.getChildren().add(voceBox);
                    }
                }
                
                sezioniArea.getChildren().add(sezioneBox);
            }
        }
        
        // Action buttons per chef
        HBox actionBox = createActionButtons();
        
        menuDetailsPane.getChildren().addAll(menuInfo, sezioniArea, actionBox);
    }
    
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        User currentUser = authController.getCurrentUser();
        
        if (selectedMenu != null && currentUser instanceof Chef && 
            selectedMenu.getChef().equals(currentUser)) {
            
            Button ritiraButton = new Button("📤 Ritira dalla Bacheca");
            ritiraButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
            ritiraButton.setOnAction(e -> ritiraDallaBacheca());
            buttonBox.getChildren().add(ritiraButton);
        }
        
        if (selectedMenu != null && currentUser instanceof Organizzatore) {
            Button useButton = new Button("📋 Utilizza per Evento");
            useButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
            useButton.setOnAction(e -> showAlert("Info", "Per utilizzare questo menu, vai nella gestione eventi e assegnalo a un servizio."));
            buttonBox.getChildren().add(useButton);
        }
        
        return buttonBox;
    }
    
    private void ritiraDallaBacheca() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Ritira Menu");
        confirmAlert.setHeaderText("Confermare il ritiro del menu dalla bacheca?");
        confirmAlert.setContentText("Il menu non sarà più visibile agli altri utenti.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Rimuovi il flag di pubblicazione pubblica
                    // (Implementazione semplificata: imposta come non utilizzato)
                    selectedMenu.setUtilizzato(false);
                    menuManager.updateMenu(selectedMenu);
                    
                    loadMenuPubblici();
                    selectedMenu = null;
                    updateMenuDetails();
                    showAlert("Successo", "Menu ritirato dalla bacheca!");
                } catch (Exception e) {
                    showAlert("Errore", e.getMessage());
                }
            }
        });
    }
    
    private void loadMenuPubblici() {
        try {
            // Carica tutti i menu disponibili (non utilizzati in eventi)
            // In una implementazione più completa, ci sarebbe un campo specifico per la pubblicazione pubblica
            List<Menu> menuPubblici = menuManager.getMenusDisponibili().stream()
                .filter(menu -> !menu.getSezioni().isEmpty()) // Solo menu con contenuto
                .collect(Collectors.toList());
            
            ObservableList<Menu> menuList = FXCollections.observableArrayList(menuPubblici);
            bachecaListView.setItems(menuList);
        } catch (Exception e) {
            showAlert("Errore", "Impossibile caricare i menu pubblici: " + e.getMessage());
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
    
    // Custom ListCell per menu pubblici
    private static class MenuPublicoListCell extends ListCell<Menu> {
        @Override
        protected void updateItem(Menu menu, boolean empty) {
            super.updateItem(menu, empty);
            
            if (empty || menu == null) {
                setText(null);
                setGraphic(null);
                setStyle("");
            } else {
                VBox content = new VBox(4);
                content.setPadding(new Insets(8));
                
                Label titleLabel = new Label("🍽 " + menu.getTitolo());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2c3e50;");
                
                Label chefLabel = new Label("👨‍🍳 " + menu.getChef().getNomeCompleto());
                chefLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
                
                Label statsLabel = new Label("📊 " + menu.getSezioni().size() + " sezioni, " + 
                    menu.getTutteLeRicette().size() + " ricette");
                statsLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");
                
                // Data pubblicazione
                if (menu.getDataCreazione() != null) {
                    Label dataLabel = new Label("📅 " + 
                        LocalDateTime.ofInstant(menu.getDataCreazione().toInstant(), 
                        java.time.ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    dataLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #999;");
                    content.getChildren().addAll(titleLabel, chefLabel, statsLabel, dataLabel);
                } else {
                    content.getChildren().addAll(titleLabel, chefLabel, statsLabel);
                }
                
                setGraphic(content);
                setText(null);
                
                // Stile della cella
                setStyle("-fx-background-color: #ffffff; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0;");
                
                // Hover effect
                setOnMouseEntered(e -> setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #3498db; -fx-border-width: 0 0 1 0;"));
                setOnMouseExited(e -> setStyle("-fx-background-color: #ffffff; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0;"));
            }
        }
    }
}