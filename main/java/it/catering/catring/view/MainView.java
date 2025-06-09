// File: src/main/java/it/catering/catring/view/MainView.java
package it.catering.catring.view;

import it.catering.catring.controller.ApplicationController;
import it.catering.catring.model.entities.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainView {
    private Stage stage;
    private User currentUser;
    private ApplicationController appController;
    private BorderPane root;
    
    public MainView(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.appController = ApplicationController.getInstance();
    }
    
    public void show() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #ecf0f1;");
        
        // Header
        VBox header = createHeader();
        root.setTop(header);
        
        // Navigation Menu
        VBox navigationMenu = createNavigationMenu();
        root.setLeft(navigationMenu);
        
        // Default content
        showDefaultContent();
        
        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Cat & Ring - " + currentUser.getNomeCompleto() + " (" + currentUser.getClass().getSimpleName() + ")");
    }
    
    private VBox createHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: #2c3e50; -fx-padding: 15;");
        
        HBox headerContent = new HBox();
        headerContent.setSpacing(20);
        
        Label titleLabel = new Label("Cat & Ring - Sistema Catering");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userLabel = new Label("Benvenuto, " + currentUser.getNomeCompleto());
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");
        logoutButton.setOnAction(e -> logout());
        
        headerContent.getChildren().addAll(titleLabel, spacer, userLabel, logoutButton);
        header.getChildren().add(headerContent);
        
        return header;
    }
    
    private VBox createNavigationMenu() {
        VBox menu = new VBox(10);
        menu.setStyle("-fx-background-color: #34495e; -fx-padding: 20; -fx-min-width: 200;");
        
        Label menuTitle = new Label("Menu Principale");
        menuTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        menu.getChildren().add(menuTitle);
        menu.getChildren().add(new Separator());
        
        // Menu items based on user type
        if (currentUser instanceof Chef) {
            addMenuButton(menu, "Gestione Menu", this::showMenuManagement);
            addMenuButton(menu, "Gestione Compiti", this::showCompitoManagement);
            addMenuButton(menu, "Ricettario", this::showRicettario);
        } else if (currentUser instanceof Cuoco) {
            addMenuButton(menu, "I Miei Compiti", this::showMieiCompiti);
            addMenuButton(menu, "Ricettario", this::showRicettario);
        } else if (currentUser instanceof Organizzatore) {
            addMenuButton(menu, "Supervisione Cucina", this::showSupervisione);
            addMenuButton(menu, "Ricettario", this::showRicettario);
        }
        
        return menu;
    }
    
    private void addMenuButton(VBox menu, String text, Runnable action) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5; -fx-min-width: 160;");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> action.run());
        
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5; -fx-min-width: 160;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 5; -fx-min-width: 160;"));
        
        menu.getChildren().add(button);
    }
    
    private void showDefaultContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        
        Label welcomeLabel = new Label("Benvenuto nel Sistema Cat & Ring");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label roleLabel = new Label("Ruolo: " + currentUser.getClass().getSimpleName());
        roleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        
        // Descrizione funzionalità per ruolo
        VBox functionsBox = new VBox(10);
        functionsBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label functionsTitle = new Label("Funzionalità disponibili:");
        functionsTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        functionsBox.getChildren().add(functionsTitle);
        
        if (currentUser instanceof Chef) {
            functionsBox.getChildren().addAll(
                new Label("• Gestione Menu: Crea, modifica ed esporta menu per eventi"),
                new Label("• Gestione Compiti: Assegna compiti ai cuochi e monitora l'avanzamento"),
                new Label("• Ricettario: Gestisci ricette e preparazioni con sistema di tag"),
                new Label("• Export PDF: Esporta menu in formato PDF per clienti")
            );
        } else if (currentUser instanceof Cuoco) {
            functionsBox.getChildren().addAll(
                new Label("• I Miei Compiti: Visualizza e gestisci i compiti assegnati"),
                new Label("• Ricettario: Consulta ricette pubblicate e crea le tue"),
                new Label("• Segnalazione Problemi: Comunica eventuali difficoltà al chef")
            );
        } else if (currentUser instanceof Organizzatore) {
            functionsBox.getChildren().addAll(
                new Label("• Supervisione Cucina: Monitora l'avanzamento di tutti i compiti"),
                new Label("• Ricettario: Accesso completo al ricettario aziendale"),
                new Label("• Statistiche: Visualizza report e analisi delle performance")
            );
        }
        
        Label instructionLabel = new Label("Utilizza il menu a sinistra per navigare nelle funzionalità disponibili.");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        // Info sistema
        VBox systemInfoBox = new VBox(5);
        systemInfoBox.setStyle("-fx-background-color: #e8f5e8; -fx-padding: 10; -fx-background-radius: 5;");
        
        Label systemTitle = new Label("Novità del Sistema:");
        systemTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");
        systemInfoBox.getChildren().add(systemTitle);
        
        systemInfoBox.getChildren().addAll(
            new Label("✓ Sistema di persistenza dati in JSON"),
            new Label("✓ Gestione stati avanzata per ricette"),
            new Label("✓ Esportazione PDF con pattern Visitor"),
            new Label("✓ Copia e modifica menu esistenti"),
            new Label("✓ Sistema di tag per organizzazione ricette")
        );
        
        content.getChildren().addAll(welcomeLabel, roleLabel, functionsBox, instructionLabel, systemInfoBox);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        root.setCenter(scrollPane);
    }
    
    private void showMenuManagement() {
        MenuManagementView menuView = new MenuManagementView();
        root.setCenter(menuView.getView());
    }
    
    private void showCompitoManagement() {
        CompitoManagementView compitoView = new CompitoManagementView();
        root.setCenter(compitoView.getView());
    }
    
    private void showRicettario() {
        RicettarioView ricettarioView = new RicettarioView();
        root.setCenter(ricettarioView.getView());
    }
    
    private void showMieiCompiti() {
        MieiCompitiView mieiCompitiView = new MieiCompitiView();
        root.setCenter(mieiCompitiView.getView());
    }
    
    private void showSupervisione() {
        SupervisioneView supervisioneView = new SupervisioneView();
        root.setCenter(supervisioneView.getView());
    }
    
    private void logout() {
        appController.getAuthController().logout();
        LoginView loginView = new LoginView(stage);
        loginView.show();
    }
}