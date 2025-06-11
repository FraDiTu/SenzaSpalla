package com.catring.viewfx;

import com.catring.controller.MenuController;
import com.catring.model.Ricetta;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * VISTA PER LA GESTIONE DEL RICETTARIO
 * Gestisce l'interfaccia per creare, visualizzare ed eliminare ricette
 */
public class RicettarioView {
    
    private MenuController controller;
    private VBox layoutPrincipale;
    
    // Componenti per la creazione ricette
    private TextField campoNomeRicetta;
    private TextField campoDescrizioneRicetta;
    private TextField campoTempoPreparazione;
    private TextField campoAutoreRicetta;
    private ComboBox<String> comboStatoRicetta;
    private Button bottoneInserisciRicetta;
    private Button bottoneConsultaRicettario;
    private Button bottoneEliminaRicetta;
    
    // Lista ricette esistenti
    private ListView<Ricetta> listaRicette;
    
    public RicettarioView(MenuController controller) {
        this.controller = controller;
        creaInterfaccia();
        collegaController();
    }
    
    /**
     * Crea l'interfaccia per la gestione del ricettario
     */
    private void creaInterfaccia() {
        layoutPrincipale = new VBox();
        layoutPrincipale.setSpacing(15);
        layoutPrincipale.setStyle("-fx-padding: 15px;");
        
        // Intestazione
        Label titolo = new Label("Gestione Ricette");
        titolo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        
        // Pulsante per aggiornare il ricettario
        HBox pannelloAzioni = creaPannelloAzioni();
        
        // Contenuto principale
        HBox contenutoPrincipale = creaContenutoPrincipale();
        
        layoutPrincipale.getChildren().addAll(titolo, pannelloAzioni, contenutoPrincipale);
    }
    
    /**
     * Crea il pannello con le azioni principali
     */
    private HBox creaPannelloAzioni() {
        HBox pannello = new HBox();
        pannello.setSpacing(10);
        
        bottoneConsultaRicettario = new Button("Aggiorna Ricettario");
        bottoneConsultaRicettario.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-padding: 8px 16px;");
        
        pannello.getChildren().add(bottoneConsultaRicettario);
        return pannello;
    }
    
    /**
     * Crea il contenuto principale con form e lista
     */
    private HBox creaContenutoPrincipale() {
        HBox contenuto = new HBox();
        contenuto.setSpacing(15);
        
        // Pannello sinistro - Creazione ricette
        VBox pannelloCreazione = creaPannelloCreazione();
        
        // Pannello destro - Lista ricette con eliminazione
        VBox pannelloLista = creaPannelloListaRicette();
        
        contenuto.getChildren().addAll(pannelloCreazione, pannelloLista);
        return contenuto;
    }
    
    /**
     * Crea il pannello per inserire nuove ricette
     */
    private VBox creaPannelloCreazione() {
        VBox pannello = new VBox();
        pannello.setSpacing(15);
        pannello.setPrefWidth(450);
        
        Label etichetta = new Label("Crea Nuova Ricetta");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        VBox sezioneForm = creaFormRicetta();
        
        pannello.getChildren().addAll(etichetta, sezioneForm);
        return pannello;
    }
    
    /**
     * Crea il form per l'inserimento dei dati della ricetta
     */
    private VBox creaFormRicetta() {
        VBox sezione = new VBox();
        sezione.setSpacing(12);
        sezione.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15px; -fx-border-radius: 8px;");
        
        GridPane griglia = new GridPane();
        griglia.setHgap(12);
        griglia.setVgap(12);
        
        // Nome ricetta
        Label labelNome = new Label("Nome ricetta:");
        labelNome.setStyle("-fx-text-fill: #2c3e50;");
        campoNomeRicetta = new TextField();
        campoNomeRicetta.setPromptText("Es: Pasta al pomodoro");
        
        // Descrizione
        Label labelDescrizione = new Label("Descrizione:");
        labelDescrizione.setStyle("-fx-text-fill: #2c3e50;");
        campoDescrizioneRicetta = new TextField();
        campoDescrizioneRicetta.setPromptText("Breve descrizione");
        
        // Tempo preparazione
        Label labelTempo = new Label("Tempo (minuti):");
        labelTempo.setStyle("-fx-text-fill: #2c3e50;");
        campoTempoPreparazione = new TextField();
        campoTempoPreparazione.setPromptText("30");
        
        // Autore
        Label labelAutore = new Label("Chef responsabile:");
        labelAutore.setStyle("-fx-text-fill: #2c3e50;");
        campoAutoreRicetta = new TextField();
        campoAutoreRicetta.setPromptText("Nome del chef");
        
        // Stato
        Label labelStato = new Label("Stato:");
        labelStato.setStyle("-fx-text-fill: #2c3e50;");
        comboStatoRicetta = new ComboBox<>();
        comboStatoRicetta.getItems().addAll("bozza", "pubblicata");
        comboStatoRicetta.setValue("bozza");
        
        // Aggiungi componenti alla griglia
        griglia.add(labelNome, 0, 0);
        griglia.add(campoNomeRicetta, 1, 0);
        griglia.add(labelDescrizione, 0, 1);
        griglia.add(campoDescrizioneRicetta, 1, 1);
        griglia.add(labelTempo, 0, 2);
        griglia.add(campoTempoPreparazione, 1, 2);
        griglia.add(labelAutore, 0, 3);
        griglia.add(campoAutoreRicetta, 1, 3);
        griglia.add(labelStato, 0, 4);
        griglia.add(comboStatoRicetta, 1, 4);
        
        // Pulsante per inserire la ricetta
        bottoneInserisciRicetta = new Button("Aggiungi Ricetta al Ricettario");
        bottoneInserisciRicetta.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-padding: 10px 20px;");
        
        // Testo informativo
        Label testoInfo = new Label("Tutti i campi sono validati automaticamente");
        testoInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        sezione.getChildren().addAll(griglia, bottoneInserisciRicetta, testoInfo);
        return sezione;
    }
    
    /**
     * Crea il pannello con la lista delle ricette esistenti
     */
    private VBox creaPannelloListaRicette() {
        VBox pannello = new VBox();
        pannello.setSpacing(15);
        HBox.setHgrow(pannello, Priority.ALWAYS);
        
        Label etichetta = new Label("Ricette Disponibili");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        listaRicette = new ListView<>();
        listaRicette.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5px;");
        VBox.setVgrow(listaRicette, Priority.ALWAYS);
        
        // Personalizza la visualizzazione delle ricette
        listaRicette.setCellFactory(listView -> new ListCell<Ricetta>() {
            @Override
            protected void updateItem(Ricetta ricetta, boolean empty) {
                super.updateItem(ricetta, empty);
                if (empty || ricetta == null) {
                    setText(null);
                } else {
                    setText(ricetta.getNome() + " - " + ricetta.getAutore() + " (" + ricetta.getTempoPreparazione() + " min)");
                }
            }
        });
        
        // Pannello pulsanti per gestione ricette
        HBox pannelloPulsanti = new HBox();
        pannelloPulsanti.setSpacing(10);
        
        bottoneEliminaRicetta = new Button("Elimina Ricetta Selezionata");
        bottoneEliminaRicetta.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8px 16px;");
        
        pannelloPulsanti.getChildren().add(bottoneEliminaRicetta);
        
        Label testoInfo = new Label("Seleziona una ricetta dalla lista per eliminarla");
        testoInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        pannello.getChildren().addAll(etichetta, listaRicette, pannelloPulsanti, testoInfo);
        return pannello;
    }
    
    /**
     * Collega i componenti dell'interfaccia al controller
     */
    private void collegaController() {
        // Imposta i componenti nel controller
        controller.setComponentiRicettario(
            campoNomeRicetta, campoDescrizioneRicetta, campoTempoPreparazione,
            campoAutoreRicetta, comboStatoRicetta, listaRicette
        );
        
        // Collega i pulsanti agli eventi del controller
        bottoneConsultaRicettario.setOnAction(e -> controller.handleConsultaRicettario());
        bottoneInserisciRicetta.setOnAction(e -> controller.handleInserisciRicetta());
        bottoneEliminaRicetta.setOnAction(e -> controller.handleEliminaRicettaDalRicettario());
    }
    
    /**
     * Restituisce il nodo principale della vista
     */
    public Node getView() {
        return layoutPrincipale;
    }
    
    /**
     * Pulisce i campi di input della ricetta
     */
    public void pulisciCampiRicetta() {
        campoNomeRicetta.clear();
        campoDescrizioneRicetta.clear();
        campoTempoPreparazione.clear();
        campoAutoreRicetta.clear();
        comboStatoRicetta.setValue("bozza");
    }
    
    /**
     * Restituisce i componenti per l'accesso dal controller
     */
    public TextField getCampoNomeRicetta() { return campoNomeRicetta; }
    public TextField getCampoDescrizioneRicetta() { return campoDescrizioneRicetta; }
    public TextField getCampoTempoPreparazione() { return campoTempoPreparazione; }
    public TextField getCampoAutoreRicetta() { return campoAutoreRicetta; }
    public ComboBox<String> getComboStatoRicetta() { return comboStatoRicetta; }
    public ListView<Ricetta> getListaRicette() { return listaRicette; }
}