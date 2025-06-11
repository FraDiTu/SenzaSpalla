package com.catring.viewfx;

import com.catring.controller.MenuController;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class FinaleView {
    
    private MenuController controller;
    private VBox layoutPrincipale;

    private TextField campoNuovoTitolo;
    private TextArea areaNuoveNote;
    private Button bottoneAggiornaTitolo;
    private Button bottoneAggiungiAnnotazione;

    private Button bottoneGeneraTXT;
    private Button bottonePubblicaBacheca;
    private Button bottoneEliminaMenu;

    private Label labelStato;
    
    public FinaleView(MenuController controller) {
        this.controller = controller;
        creaInterfaccia();
        collegaController();
    }

    private void creaInterfaccia() {
        layoutPrincipale = new VBox();
        layoutPrincipale.setSpacing(20);
        layoutPrincipale.setStyle("-fx-padding: 20px;");

        Label titolo = new Label("Finalizzazione e Condivisione Menu");
        titolo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        HBox contenutoPrincipale = creaContenutoPrincipale();

        Separator separatore = new Separator();

        VBox areaStato = creaAreaStato();
        
        layoutPrincipale.getChildren().addAll(titolo, contenutoPrincipale, separatore, areaStato);
    }

    private HBox creaContenutoPrincipale() {
        HBox contenuto = new HBox();
        contenuto.setSpacing(20);

        VBox pannelloPersonalizzazione = creaPannelloPersonalizzazione();

        VBox pannelloAzioni = creaPannelloAzioni();
        
        contenuto.getChildren().addAll(pannelloPersonalizzazione, pannelloAzioni);
        return contenuto;
    }

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

    private VBox creaSezioneModificaMenu() {
        VBox sezione = new VBox();
        sezione.setSpacing(12);
        sezione.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15px; -fx-border-radius: 8px;");

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

    private void collegaController() {

        controller.setComponentiFinale(
            campoNuovoTitolo, areaNuoveNote, labelStato
        );

        bottoneAggiornaTitolo.setOnAction(e -> controller.handleAggiornaTitolo());
        bottoneAggiungiAnnotazione.setOnAction(e -> controller.handleAggiungiAnnotazione());
        bottoneGeneraTXT.setOnAction(e -> controller.handleGeneraTXT());
        bottonePubblicaBacheca.setOnAction(e -> controller.handlePubblicaBacheca());
        bottoneEliminaMenu.setOnAction(e -> controller.handleEliminaMenu());
    }

    public Node getView() {
        return layoutPrincipale;
    }

    public void pulisciCampi() {
        campoNuovoTitolo.clear();
        areaNuoveNote.clear();
    }

    public void aggiornaStato(String messaggio) {
        if (labelStato != null) {
            labelStato.setText(messaggio);
        }
    }

    public TextField getCampoNuovoTitolo() { return campoNuovoTitolo; }
    public TextArea getAreaNuoveNote() { return areaNuoveNote; }
    public Label getLabelStato() { return labelStato; }
}