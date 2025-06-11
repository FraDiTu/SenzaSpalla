package com.catring.viewfx;

import com.catring.controller.MenuController;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * VISTA PER LA FINALIZZAZIONE DEI MENU
 * Gestisce le operazioni finali: modifica titolo, note, TXT, bacheca, eliminazione
 */
public class FinaleView {
    
    private MenuController controller;
    private VBox layoutPrincipale;
    
    // Componenti per la modifica del menu
    private TextField campoNuovoTitolo;
    private TextArea areaNuoveNote;
    private Button bottoneAggiornaTitolo;
    private Button bottoneAggiungiAnnotazione;
    
    // Componenti per le azioni finali
    private Button bottoneGeneraTXT;
    private Button bottonePubblicaBacheca;
    private Button bottoneEliminaMenu;
    
    // Area di stato
    private Label labelStato;
    
    public FinaleView(MenuController controller) {
        this.controller = controller;
        creaInterfaccia();
        collegaController();
    }
    
    /**
     * Crea l'interfaccia per la finalizzazione
     */
    private void creaInterfaccia() {
        layoutPrincipale = new VBox();
        layoutPrincipale.setSpacing(20);
        layoutPrincipale.setStyle("-fx-padding: 20px;");
        
        // Intestazione
        Label titolo = new Label("Finalizzazione e Condivisione Menu");
        titolo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        
        // Contenuto principale
        HBox contenutoPrincipale = creaContenutoPrincipale();
        
        // Separatore
        Separator separatore = new Separator();
        
        // Area informazioni di stato
        VBox areaStato = creaAreaStato();
        
        layoutPrincipale.getChildren().addAll(titolo, contenutoPrincipale, separatore, areaStato);
    }
    
    /**
     * Crea il contenuto principale con modifica e azioni
     */
    private HBox creaContenutoPrincipale() {
        HBox contenuto = new HBox();
        contenuto.setSpacing(20);
        
        // Pannello sinistro - Personalizzazione menu
        VBox pannelloPersonalizzazione = creaPannelloPersonalizzazione();
        
        // Pannello destro - Azioni finali
        VBox pannelloAzioni = creaPannelloAzioni();
        
        contenuto.getChildren().addAll(pannelloPersonalizzazione, pannelloAzioni);
        return contenuto;
    }
    
    /**
     * Crea il pannello per personalizzare il menu
     */
    private VBox creaPannelloPersonalizzazione() {
        VBox pannello = new VBox();
        pannello.setSpacing(15);
        pannello.setPrefWidth(450);
        
        Label etichetta = new Label("Personalizza Menu Selezionato");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        VBox sezioneModifica = creaSezioneModificaMenu();
        
        pannello.getChildren().addAll(etichetta, sezioneModifica);
        return pannello;
    }
    
    /**
     * Crea la sezione per modificare il menu
     */
    private VBox creaSezioneModificaMenu() {
        VBox sezione = new VBox();
        sezione.setSpacing(12);
        sezione.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15px; -fx-border-radius: 8px;");
        
        // Modifica titolo
        Label labelTitolo = new Label("Modifica titolo del menu:");
        labelTitolo.setStyle("-fx-text-fill: #2c3e50;");
        
        HBox pannelloTitolo = new HBox();
        pannelloTitolo.setSpacing(10);
        
        campoNuovoTitolo = new TextField();
        campoNuovoTitolo.setPromptText("Nuovo titolo del menu");
        HBox.setHgrow(campoNuovoTitolo, Priority.ALWAYS);
        
        bottoneAggiornaTitolo = new Button("Aggiorna");
        bottoneAggiornaTitolo.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        pannelloTitolo.getChildren().addAll(campoNuovoTitolo, bottoneAggiornaTitolo);
        
        // Aggiungi note
        Label labelNote = new Label("Aggiungi note speciali:");
        labelNote.setStyle("-fx-text-fill: #2c3e50;");
        
        areaNuoveNote = new TextArea();
        areaNuoveNote.setPromptText("Inserisci note o informazioni aggiuntive...");
        areaNuoveNote.setPrefRowCount(4);
        
        bottoneAggiungiAnnotazione = new Button("Salva Annotazioni");
        bottoneAggiungiAnnotazione.setStyle("-fx-background-color: #16a085; -fx-text-fill: white; -fx-padding: 8px 16px;");
        
        sezione.getChildren().addAll(
            labelTitolo, pannelloTitolo,
            labelNote, areaNuoveNote, bottoneAggiungiAnnotazione
        );
        
        return sezione;
    }
    
    /**
     * Crea il pannello con le azioni finali
     */
    private VBox creaPannelloAzioni() {
        VBox pannello = new VBox();
        pannello.setSpacing(15);
        pannello.setPrefWidth(300);
        
        Label etichetta = new Label("Azioni Finali");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        VBox sezioneAzioni = creaSezioneAzioniFinali();
        
        pannello.getChildren().addAll(etichetta, sezioneAzioni);
        return pannello;
    }
    
    /**
     * Crea la sezione con i pulsanti delle azioni finali
     */
    private VBox creaSezioneAzioniFinali() {
        VBox sezione = new VBox();
        sezione.setSpacing(12);
        sezione.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15px; -fx-border-radius: 8px;");
        
        Label labelCondivisione = new Label("Condividi il tuo menu:");
        labelCondivisione.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        
        bottoneGeneraTXT = new Button("Genera TXT per Stampa");
        bottoneGeneraTXT.setPrefWidth(200);
        bottoneGeneraTXT.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-padding: 10px;");
        
        bottonePubblicaBacheca = new Button("Pubblica su Bacheca");
        bottonePubblicaBacheca.setPrefWidth(200);
        bottonePubblicaBacheca.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-padding: 10px;");
        
        Separator separatoreAzioni = new Separator();
        
        Label labelGestione = new Label("Gestione menu:");
        labelGestione.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        
        bottoneEliminaMenu = new Button("Elimina Menu");
        bottoneEliminaMenu.setPrefWidth(200);
        bottoneEliminaMenu.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px;");
        
        sezione.getChildren().addAll(
            labelCondivisione,
            bottoneGeneraTXT,
            bottonePubblicaBacheca,
            separatoreAzioni,
            labelGestione,
            bottoneEliminaMenu
        );
        
        return sezione;
    }
    
    /**
     * Crea l'area per mostrare lo stato del sistema
     */
    private VBox creaAreaStato() {
        VBox area = new VBox();
        area.setSpacing(8);
        area.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15px; -fx-border-radius: 8px;");
        
        Label labelTitolo = new Label("Stato del Sistema:");
        labelTitolo.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        labelStato = new Label("Sistema pronto per la gestione di eventi e menu");
        labelStato.setStyle("-fx-text-fill: #27ae60;");
        
        area.getChildren().addAll(labelTitolo, labelStato);
        return area;
    }
    
    /**
     * Collega i componenti dell'interfaccia al controller
     */
    private void collegaController() {
        // Imposta i componenti nel controller
        controller.setComponentiFinale(
            campoNuovoTitolo, areaNuoveNote, labelStato
        );
        
        // Collega i pulsanti agli eventi del controller
        bottoneAggiornaTitolo.setOnAction(e -> controller.handleAggiornaTitolo());
        bottoneAggiungiAnnotazione.setOnAction(e -> controller.handleAggiungiAnnotazione());
        bottoneGeneraTXT.setOnAction(e -> controller.handleGeneraTXT());
        bottonePubblicaBacheca.setOnAction(e -> controller.handlePubblicaBacheca());
        bottoneEliminaMenu.setOnAction(e -> controller.handleEliminaMenu());
    }
    
    /**
     * Restituisce il nodo principale della vista
     */
    public Node getView() {
        return layoutPrincipale;
    }
    
    /**
     * Pulisce i campi di input
     */
    public void pulisciCampi() {
        campoNuovoTitolo.clear();
        areaNuoveNote.clear();
    }
    
    /**
     * Aggiorna il messaggio di stato
     */
    public void aggiornaStato(String messaggio) {
        if (labelStato != null) {
            labelStato.setText(messaggio);
        }
    }
    
    /**
     * Restituisce i componenti per l'accesso dal controller
     */
    public TextField getCampoNuovoTitolo() { return campoNuovoTitolo; }
    public TextArea getAreaNuoveNote() { return areaNuoveNote; }
    public Label getLabelStato() { return labelStato; }
}