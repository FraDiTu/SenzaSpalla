package com.catring.viewfx;

import com.catring.controller.MenuController;
import com.catring.controller.EventoController;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * VISTA PRINCIPALE DELL'APPLICAZIONE
 * Gestisce l'interfaccia principale con le tab per diverse funzionalità
 */
public class MainView {
    
    private Stage primaryStage;
    private Scene scene;
    private TabPane tabPane;
    
    // View specifiche per ogni funzionalità
    private EventiView eventiView;
    private MenuView menuView;
    private RicettarioView ricettarioView;
    private BachecaView bachecaView;
    
    // Controller
    private MenuController menuController;
    private EventoController eventoController;
    
    public MainView(Stage stage) {
        this.primaryStage = stage;
        this.menuController = new MenuController();
        this.eventoController = new EventoController();
        
        creaInterfaccia();
        configuraStage();
    }
    
    /**
     * Crea l'interfaccia principale
     */
    private void creaInterfaccia() {
        // Layout principale
        BorderPane layoutPrincipale = new BorderPane();
        
        // Intestazione
        VBox intestazione = creaIntestazione();
        layoutPrincipale.setTop(intestazione);
        
        // Contenuto principale con tab
        tabPane = creaTabPane();
        layoutPrincipale.setCenter(tabPane);
        
        // Footer
        HBox footer = creaFooter();
        layoutPrincipale.setBottom(footer);
        
        // Crea la scena
        scene = new Scene(layoutPrincipale, 1400, 900);
    }
    
    /**
     * Crea l'intestazione dell'applicazione
     */
    private VBox creaIntestazione() {
        VBox intestazione = new VBox();
        intestazione.setSpacing(10);
        intestazione.setStyle("-fx-padding: 15px; -fx-background-color: #ecf0f1;");
        
        Label titolo = new Label("Cat & Ring - Sistema di Gestione Catering");
        titolo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label sottotitolo = new Label("Gestisci eventi, menu e ricette per il tuo servizio di catering");
        sottotitolo.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        Separator separatore = new Separator();
        
        intestazione.getChildren().addAll(titolo, sottotitolo, separatore);
        return intestazione;
    }
    
    /**
     * Crea il pannello con le tab principali
     */
    private TabPane creaTabPane() {
        TabPane tabs = new TabPane();
        
        // Tab Eventi
        Tab tabEventi = new Tab("Eventi Assegnati");
        tabEventi.setClosable(false);
        eventiView = new EventiView(eventoController);
        tabEventi.setContent(eventiView.getView());
        
        // Tab Menu Unificata (Gestione Menu)
        Tab tabMenu = new Tab("Gestione Menu");
        tabMenu.setClosable(false);
        menuView = new MenuView(menuController);
        tabMenu.setContent(menuView.getView());
        
        // Tab Ricettario
        Tab tabRicettario = new Tab("Ricettario");
        tabRicettario.setClosable(false);
        ricettarioView = new RicettarioView(menuController);
        tabRicettario.setContent(ricettarioView.getView());
        
        // Tab Bacheca
        Tab tabBacheca = new Tab("Bacheca Menu");
        tabBacheca.setClosable(false);
        bachecaView = new BachecaView(menuController);
        tabBacheca.setContent(bachecaView.getView());
        
        tabs.getTabs().addAll(tabEventi, tabMenu, tabRicettario, tabBacheca);
        return tabs;
    }
    
    /**
     * Crea il footer dell'applicazione
     */
    private HBox creaFooter() {
        HBox footer = new HBox();
        footer.setStyle("-fx-padding: 10px 15px; -fx-background-color: #f8f9fa;");
        
        // Spazio vuoto per spingere il testo a destra
        Region spazioVuoto = new Region();
        HBox.setHgrow(spazioVuoto, Priority.ALWAYS);
        
        Label copyright = new Label("Cat & Ring (c) 2024 - Sistema di Gestione Catering");
        copyright.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 10px;");
        
        footer.getChildren().addAll(spazioVuoto, copyright);
        return footer;
    }
    
    /**
     * Configura lo stage principale
     */
    private void configuraStage() {
        primaryStage.setTitle("Cat & Ring - Sistema di Gestione Catering");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
    }
    
    /**
     * Mostra la finestra principale
     */
    public void mostra() {
        primaryStage.show();
    }
    
    /**
     * Restituisce lo stage principale
     */
    public Stage getStage() {
        return primaryStage;
    }
    
    /**
     * Restituisce la scena principale
     */
    public Scene getScene() {
        return scene;
    }
}