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
            addMenuButton(menu, "I Miei Eventi", this::showEventiManagement);
        } else if (currentUser instanceof Cuoco) {
            addMenuButton(menu, "I Miei Compiti", this::showMieiCompiti);
            addMenuButton(menu, "Ricettario", this::showRicettario);
            addMenuButton(menu, "Turni Disponibili", this::showTurniDisponibili);
        } else if (currentUser instanceof Organizzatore) {
            addMenuButton(menu, "Gestione Eventi", this::showEventiManagement);
            addMenuButton(menu, "Gestione Personale", this::showPersonaleManagement);
            addMenuButton(menu, "Gestione Turni", this::showTurnoManagement);
            addMenuButton(menu, "Supervisione Cucina", this::showSupervisione);
            addMenuButton(menu, "Ricettario", this::showRicettario);
        } else if (currentUser instanceof PersonaleServizio) {
            addMenuButton(menu, "Turni Disponibili", this::showTurniDisponibili);
            addMenuButton(menu, "I Miei Turni", this::showMieiTurni);
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
                new Label("• I Miei Eventi: Visualizza eventi assegnati e gestisci menu")
            );
        } else if (currentUser instanceof Cuoco) {
            functionsBox.getChildren().addAll(
                new Label("• I Miei Compiti: Visualizza e gestisci i compiti assegnati"),
                new Label("• Ricettario: Consulta ricette pubblicate e crea le tue"),
                new Label("• Turni Disponibili: Dichiara disponibilità per turni preparatori")
            );
        } else if (currentUser instanceof Organizzatore) {
            functionsBox.getChildren().addAll(
                new Label("• Gestione Eventi: Crea eventi, assegna chef e gestisci servizi"),
                new Label("• Gestione Personale: Aggiungi e gestisci tutto il personale"),
                new Label("• Gestione Turni: Crea turni preparatori e di servizio"),
                new Label("• Supervisione Cucina: Monitora l'avanzamento di tutti i compiti")
            );
        } else if (currentUser instanceof PersonaleServizio) {
            functionsBox.getChildren().addAll(
                new Label("• Turni Disponibili: Dichiara disponibilità per turni di servizio"),
                new Label("• I Miei Turni: Visualizza turni assegnati")
            );
        }
        
        Label instructionLabel = new Label("Utilizza il menu a sinistra per navigare nelle funzionalità disponibili.");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        // Info sistema aggiornate
        VBox systemInfoBox = new VBox(5);
        systemInfoBox.setStyle("-fx-background-color: #e8f5e8; -fx-padding: 10; -fx-background-radius: 5;");
        
        Label systemTitle = new Label("Funzionalità del Sistema:");
        systemTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");
        systemInfoBox.getChildren().add(systemTitle);
        
        systemInfoBox.getChildren().addAll(
            new Label("✓ Gestione completa eventi e servizi"),
            new Label("✓ Sistema turni preparatori e di servizio"),
            new Label("✓ Gestione personale e disponibilità"),
            new Label("✓ Assegnazione compiti con compatibilità turni"),
            new Label("✓ Ricettario avanzato con tag e stati"),
            new Label("✓ Export PDF menu con pattern Visitor"),
            new Label("✓ Persistenza dati JSON completa")
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
    
    // Nuovi metodi per le nuove funzionalità
    private void showEventiManagement() {
        EventoManagementView eventoView = new EventoManagementView();
        root.setCenter(eventoView.getView());
    }
    
    private void showPersonaleManagement() {
        PersonaleManagementView personaleView = new PersonaleManagementView();
        root.setCenter(personaleView.getView());
    }
    
    private void showTurnoManagement() {
        TurnoManagementView turnoView = new TurnoManagementView();
        root.setCenter(turnoView.getView());
    }
    
    private void showTurniDisponibili() {
        // Implementazione semplificata - mostra messaggio
        showInfoMessage("Turni Disponibili", 
            "Funzionalità per visualizzare e dichiarare disponibilità ai turni.\n" +
            "In una implementazione completa includerebbe:\n" +
            "• Lista turni disponibili per il proprio ruolo\n" +
            "• Possibilità di dichiarare/ritirare disponibilità\n" +
            "• Calendario turni con filtri per data e tipo");
    }
    
    private void showMieiTurni() {
        // Implementazione semplificata - mostra messaggio
        showInfoMessage("I Miei Turni", 
            "Funzionalità per visualizzare i propri turni assegnati.\n" +
            "In una implementazione completa includerebbe:\n" +
            "• Lista turni confermati\n" +
            "• Dettagli servizi e eventi\n" +
            "• Check-in/check-out per turni di servizio");
    }
    
    private void showInfoMessage(String title, String message) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        content.getChildren().addAll(titleLabel, messageLabel);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        root.setCenter(scrollPane);
    }
    
    private void logout() {
        appController.getAuthController().logout();
        LoginView loginView = new LoginView(stage);
        loginView.show();
    }
}