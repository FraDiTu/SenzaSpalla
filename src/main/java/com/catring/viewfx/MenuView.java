package com.catring.viewfx;

import com.catring.controller.MenuController;
import com.catring.model.*;
import com.catring.model.Menu;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * VISTA UNIFICATA PER LA GESTIONE COMPLETA DEI MENU - VERSIONE AGGIORNATA
 */
public class MenuView {
    
    private MenuController controller;
    private VBox layoutPrincipale;
    private Menu menuSelezionato;
    
    // Componenti per la creazione menu
    private TextField campoNomeMenu;
    private TextField campoDescrizioneMenu;
    private TextArea areaNoteMenu;
    private Button bottoneCreaMenu;
    private Button bottoneDettagliMenu;
    private Button bottoneDuplicaMenu;
    
    // Componenti per le sezioni
    private TextField campoTitoloSezione;
    private Button bottoneAggiungiSezione;
    private Button bottoneRimuoviSezione;
    
    // Componenti per le ricette - TUTTE LE COMBO BOX
    private ComboBox<String> comboSezioni; // Per aggiungere ricette
    private ComboBox<Ricetta> comboRicette; // Ricette disponibili da aggiungere
    
    // Per spostare ricette
    private ComboBox<VoceMenu> comboRicetteDaSpostare; // Ricette nel menu da spostare
    private ComboBox<String> comboSezioniOrigine; // Sezione di origine
    private ComboBox<String> comboSezioniDestinazione; // Sezione di destinazione
    
    // Per rimuovere ricette
    private ComboBox<VoceMenu> comboRicetteDaRimuovere; // Ricette nel menu da rimuovere
    
    // Pulsanti
    private Button bottoneAggiungiRicetta;
    private Button bottoneSpostaRicetta;
    private Button bottoneEliminaRicetta;
    
    // Tabella menu
    private TableView<Menu> tabellaMenu;
    
    // Liste per visualizzare contenuto menu
    private ListView<SezioniMenu> listaSezioni;
    private ListView<VoceMenu> listaVoci;
    
    // Componenti per finalizzazione
    private TextField campoNuovoTitolo;
    private TextArea areaNuoveNote;
    private Button bottoneAggiornaTitolo;
    private Button bottoneAggiungiAnnotazione;
    private Button bottoneGeneraPDF;
    private Button bottonePubblicaBacheca;
    private Button bottoneEliminaMenu;
    
    // Area di stato
    private Label labelStato;
    
    public MenuView(MenuController controller) {
        this.controller = controller;
        creaInterfaccia();
        collegaController();
    }
    
    /**
     * Crea l'interfaccia unificata per la gestione menu
     */
    private void creaInterfaccia() {
        layoutPrincipale = new VBox();
        layoutPrincipale.setSpacing(15);
        layoutPrincipale.setStyle("-fx-padding: 15px;");
        
        // Intestazione
        Label titolo = new Label("Gestione Menu Completa");
        titolo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        
        // ScrollPane per contenere tutto il contenuto
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        VBox contenutoCompleto = creaContenutoCompleto();
        scrollPane.setContent(contenutoCompleto);
        
        // Area di stato
        labelStato = new Label("Sistema pronto per la gestione menu");
        labelStato.setStyle("-fx-text-fill: #27ae60; -fx-padding: 10px; -fx-background-color: #f8f9fa;");
        
        layoutPrincipale.getChildren().addAll(titolo, scrollPane, labelStato);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }
    
    /**
     * Crea tutto il contenuto della vista unificata
     */
    private VBox creaContenutoCompleto() {
        VBox contenuto = new VBox();
        contenuto.setSpacing(20);
        
        // Sezione 1: Creazione e gestione menu
        TitledPane sezioneCreazione = new TitledPane("1. Creazione e Gestione Menu", creaSezioneCreazione());
        sezioneCreazione.setExpanded(true);
        
        // Sezione 2: Gestione contenuto menu
        TitledPane sezioneContenuto = new TitledPane("2. Gestione Contenuto Menu", creaSezioneContenuto());
        sezioneContenuto.setExpanded(true);
        
        // Sezione 3: Finalizzazione e condivisione
        TitledPane sezioneFinalizzazione = new TitledPane("3. Finalizzazione e Condivisione", creaSezioneFinale());
        sezioneFinalizzazione.setExpanded(true);
        
        contenuto.getChildren().addAll(sezioneCreazione, sezioneContenuto, sezioneFinalizzazione);
        return contenuto;
    }
    
    /**
     * Crea la sezione per creazione e gestione menu
     */
    private HBox creaSezioneCreazione() {
        HBox sezione = new HBox();
        sezione.setSpacing(15);
        
        // Pannello creazione menu
        VBox pannelloCreazione = creaPannelloCreazione();
        
        // Pannello lista menu
        VBox pannelloLista = creaPannelloListaMenu();
        
        sezione.getChildren().addAll(pannelloCreazione, pannelloLista);
        return sezione;
    }
    
    /**
     * Crea il pannello per la creazione di menu
     */
    private VBox creaPannelloCreazione() {
        VBox pannello = new VBox();
        pannello.setSpacing(12);
        pannello.setPrefWidth(400);
        pannello.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15px; -fx-border-radius: 8px;");
        
        Label etichetta = new Label("Crea Nuovo Menu");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        GridPane griglia = new GridPane();
        griglia.setHgap(10);
        griglia.setVgap(10);
        
        Label labelNome = new Label("Nome:");
        campoNomeMenu = new TextField();
        campoNomeMenu.setPromptText("Es: Menu Matrimonio");
        
        Label labelDescrizione = new Label("Descrizione:");
        campoDescrizioneMenu = new TextField();
        campoDescrizioneMenu.setPromptText("Breve descrizione del menu");
        
        Label labelNote = new Label("Note:");
        areaNoteMenu = new TextArea();
        areaNoteMenu.setPrefRowCount(2);
        areaNoteMenu.setPromptText("Note aggiuntive (opzionale)");
        
        griglia.add(labelNome, 0, 0);
        griglia.add(campoNomeMenu, 1, 0);
        griglia.add(labelDescrizione, 0, 1);
        griglia.add(campoDescrizioneMenu, 1, 1);
        griglia.add(labelNote, 0, 2);
        griglia.add(areaNoteMenu, 1, 2);
        
        HBox pannelloPulsanti = new HBox();
        pannelloPulsanti.setSpacing(10);
        
        bottoneCreaMenu = new Button("Crea Menu");
        bottoneCreaMenu.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8px 16px;");
        
        bottoneDettagliMenu = new Button("Dettagli");
        bottoneDettagliMenu.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 8px 16px;");
        
        bottoneDuplicaMenu = new Button("Duplica Menu");
        bottoneDuplicaMenu.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 8px 16px;");
        
        pannelloPulsanti.getChildren().addAll(bottoneCreaMenu, bottoneDettagliMenu, bottoneDuplicaMenu);
        
        pannello.getChildren().addAll(etichetta, griglia, pannelloPulsanti);
        return pannello;
    }
    
    /**
     * Crea il pannello con la lista dei menu
     */
    private VBox creaPannelloListaMenu() {
        VBox pannello = new VBox();
        pannello.setSpacing(10);
        pannello.setPrefWidth(350);
        
        Label etichetta = new Label("Menu Esistenti");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        tabellaMenu = new TableView<>();
        tabellaMenu.setPrefHeight(200);
        tabellaMenu.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5px;");
        
        TableColumn<Menu, String> colonnaNome = new TableColumn<>("Nome");
        colonnaNome.setPrefWidth(150);
        colonnaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        TableColumn<Menu, String> colonnaDescrizione = new TableColumn<>("Descrizione");
        colonnaDescrizione.setPrefWidth(180);
        colonnaDescrizione.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        
        tabellaMenu.getColumns().add(colonnaNome);
        tabellaMenu.getColumns().add(colonnaDescrizione);
        
        pannello.getChildren().addAll(etichetta, tabellaMenu);
        return pannello;
    }
    
    /**
     * Crea la sezione per gestione contenuto menu
     */
    private HBox creaSezioneContenuto() {
        HBox sezione = new HBox();
        sezione.setSpacing(15);
        
        // Pannello gestione sezioni e ricette
        VBox pannelloGestione = creaPannelloGestioneContenuto();
        
        // Pannello visualizzazione contenuto
        VBox pannelloVisualizzazione = creaPannelloVisualizzazioneContenuto();
        
        sezione.getChildren().addAll(pannelloGestione, pannelloVisualizzazione);
        return sezione;
    }
    
    /**
     * Crea il pannello per gestire sezioni e ricette
     */
    private VBox creaPannelloGestioneContenuto() {
        VBox pannello = new VBox();
        pannello.setSpacing(15);
        pannello.setPrefWidth(450);
        pannello.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15px; -fx-border-radius: 8px;");
        
        Label etichetta = new Label("Gestione Sezioni e Ricette");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        // Gestione sezioni
        VBox sezioneSezioni = creaSezioneGestioneSezioni();
        
        // Gestione ricette
        VBox sezioneRicette = creaSezioneGestioneRicette();
        
        pannello.getChildren().addAll(etichetta, sezioneSezioni, sezioneRicette);
        return pannello;
    }
    
    /**
     * Crea la sezione per gestire le sezioni del menu
     */
    private VBox creaSezioneGestioneSezioni() {
        VBox sezione = new VBox();
        sezione.setSpacing(8);
        
        Label label = new Label("Gestione Sezioni:");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        HBox pannelloSezione = new HBox();
        pannelloSezione.setSpacing(10);
        
        campoTitoloSezione = new TextField();
        campoTitoloSezione.setPromptText("Es: Antipasti, Primi, Dolci...");
        HBox.setHgrow(campoTitoloSezione, Priority.ALWAYS);
        
        bottoneAggiungiSezione = new Button("Aggiungi");
        bottoneAggiungiSezione.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
        
        bottoneRimuoviSezione = new Button("Rimuovi");
        bottoneRimuoviSezione.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        pannelloSezione.getChildren().addAll(campoTitoloSezione, bottoneAggiungiSezione, bottoneRimuoviSezione);
        
        sezione.getChildren().addAll(label, pannelloSezione);
        return sezione;
    }
    
    /**
     * Crea la sezione per gestire le ricette - VERSIONE CON ORIGINE E DESTINAZIONE
     */
    private VBox creaSezioneGestioneRicette() {
        VBox sezione = new VBox();
        sezione.setSpacing(12);
        
        Label label = new Label("Gestione Ricette:");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // SEZIONE 1: Aggiungere ricette
        Label labelAggiungi = new Label("Aggiungi ricetta:");
        labelAggiungi.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 12px;");
        
        HBox pannelloAggiungi = new HBox();
        pannelloAggiungi.setSpacing(8);
        
        comboSezioni = new ComboBox<>();
        comboSezioni.setPromptText("Sezione");
        comboSezioni.setPrefWidth(120);
        
        comboRicette = new ComboBox<>();
        comboRicette.setPromptText("Ricetta");
        comboRicette.setPrefWidth(160);
        
        bottoneAggiungiRicetta = new Button("Aggiungi");
        bottoneAggiungiRicetta.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        
        pannelloAggiungi.getChildren().addAll(comboSezioni, comboRicette, bottoneAggiungiRicetta);
        
        // SEZIONE 2: Spostare ricette
        Label labelSposta = new Label("Sposta ricetta:");
        labelSposta.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 12px;");
        
        // Prima riga: selezione ricetta e sezione origine
        HBox pannelloSposta1 = new HBox();
        pannelloSposta1.setSpacing(8);
        
        Label labelRicetta = new Label("Ricetta:");
        comboRicetteDaSpostare = new ComboBox<>();
        comboRicetteDaSpostare.setPromptText("Scegli ricetta");
        comboRicetteDaSpostare.setPrefWidth(140);
        
        Label labelDa = new Label("Da:");
        comboSezioniOrigine = new ComboBox<>();
        comboSezioniOrigine.setPromptText("Sezione origine");
        comboSezioniOrigine.setPrefWidth(120);
        
        pannelloSposta1.getChildren().addAll(labelRicetta, comboRicetteDaSpostare, labelDa, comboSezioniOrigine);
        
        // Seconda riga: sezione destinazione e pulsante
        HBox pannelloSposta2 = new HBox();
        pannelloSposta2.setSpacing(8);
        
        Label labelA = new Label("A:");
        comboSezioniDestinazione = new ComboBox<>();
        comboSezioniDestinazione.setPromptText("Sezione destinazione");
        comboSezioniDestinazione.setPrefWidth(140);
        
        bottoneSpostaRicetta = new Button("Sposta Ricetta");
        bottoneSpostaRicetta.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        
        pannelloSposta2.getChildren().addAll(labelA, comboSezioniDestinazione, bottoneSpostaRicetta);
        
        // SEZIONE 3: Rimuovere ricette
        Label labelRimuovi = new Label("Rimuovi ricetta:");
        labelRimuovi.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 12px;");
        
        HBox pannelloRimuovi = new HBox();
        pannelloRimuovi.setSpacing(8);
        
        comboRicetteDaRimuovere = new ComboBox<>();
        comboRicetteDaRimuovere.setPromptText("Scegli ricetta da rimuovere");
        comboRicetteDaRimuovere.setPrefWidth(200);
        
        bottoneEliminaRicetta = new Button("Rimuovi");
        bottoneEliminaRicetta.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        pannelloRimuovi.getChildren().addAll(comboRicetteDaRimuovere, bottoneEliminaRicetta);
        
        // Configura le combo box delle ricette
        configuraComboPRicette();
        
        sezione.getChildren().addAll(
            label,
            labelAggiungi, pannelloAggiungi,
            labelSposta, pannelloSposta1, pannelloSposta2,
            labelRimuovi, pannelloRimuovi
        );
        return sezione;
    }
    
    /**
     * Configura la visualizzazione delle combo box delle ricette
     */
    private void configuraComboPRicette() {
        // Combo ricette per aggiungere
        comboRicette.setCellFactory(listView -> new ListCell<Ricetta>() {
            @Override
            protected void updateItem(Ricetta ricetta, boolean empty) {
                super.updateItem(ricetta, empty);
                if (empty || ricetta == null) {
                    setText(null);
                } else {
                    setText(ricetta.getNome() + " (" + ricetta.getTempoPreparazione() + " min)");
                }
            }
        });
        
        comboRicette.setButtonCell(new ListCell<Ricetta>() {
            @Override
            protected void updateItem(Ricetta ricetta, boolean empty) {
                super.updateItem(ricetta, empty);
                if (empty || ricetta == null) {
                    setText(null);
                } else {
                    setText(ricetta.getNome() + " (" + ricetta.getTempoPreparazione() + " min)");
                }
            }
        });
        
        // Combo ricette per spostare
        comboRicetteDaSpostare.setCellFactory(listView -> new ListCell<VoceMenu>() {
            @Override
            protected void updateItem(VoceMenu voce, boolean empty) {
                super.updateItem(voce, empty);
                if (empty || voce == null) {
                    setText(null);
                } else {
                    setText(voce.getNomeVisuale());
                }
            }
        });
        
        comboRicetteDaSpostare.setButtonCell(new ListCell<VoceMenu>() {
            @Override
            protected void updateItem(VoceMenu voce, boolean empty) {
                super.updateItem(voce, empty);
                if (empty || voce == null) {
                    setText(null);
                } else {
                    setText(voce.getNomeVisuale());
                }
            }
        });
        
        // Combo ricette per rimuovere (uguale a quella per spostare)
        comboRicetteDaRimuovere.setCellFactory(listView -> new ListCell<VoceMenu>() {
            @Override
            protected void updateItem(VoceMenu voce, boolean empty) {
                super.updateItem(voce, empty);
                if (empty || voce == null) {
                    setText(null);
                } else {
                    setText(voce.getNomeVisuale());
                }
            }
        });
        
        comboRicetteDaRimuovere.setButtonCell(new ListCell<VoceMenu>() {
            @Override
            protected void updateItem(VoceMenu voce, boolean empty) {
                super.updateItem(voce, empty);
                if (empty || voce == null) {
                    setText(null);
                } else {
                    setText(voce.getNomeVisuale());
                }
            }
        });
    }
    
    /**
     * Crea il pannello per visualizzare il contenuto del menu
     */
    private VBox creaPannelloVisualizzazioneContenuto() {
        VBox pannello = new VBox();
        pannello.setSpacing(10);
        pannello.setPrefWidth(350);
        
        Label etichetta = new Label("Contenuto Menu Selezionato");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        Label labelSezioni = new Label("Sezioni:");
        labelSezioni.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        listaSezioni = new ListView<>();
        listaSezioni.setPrefHeight(120);
        listaSezioni.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5px;");
        
        listaSezioni.setCellFactory(listView -> new ListCell<SezioniMenu>() {
            @Override
            protected void updateItem(SezioniMenu sezione, boolean empty) {
                super.updateItem(sezione, empty);
                if (empty || sezione == null) {
                    setText(null);
                } else {
                    setText(sezione.getTitolo() + " (" + sezione.getVoci().size() + " ricette)");
                }
            }
        });
        
        Label labelRicette = new Label("Ricette nella sezione:");
        labelRicette.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        listaVoci = new ListView<>();
        listaVoci.setPrefHeight(120);
        listaVoci.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5px;");
        
        listaVoci.setCellFactory(listView -> new ListCell<VoceMenu>() {
            @Override
            protected void updateItem(VoceMenu voce, boolean empty) {
                super.updateItem(voce, empty);
                if (empty || voce == null) {
                    setText(null);
                } else {
                    setText(voce.getNomeVisuale());
                }
            }
        });
        
        pannello.getChildren().addAll(etichetta, labelSezioni, listaSezioni, labelRicette, listaVoci);
        return pannello;
    }
    
    /**
     * Crea la sezione per finalizzazione e condivisione
     */
    private VBox creaSezioneFinale() {
        VBox sezione = new VBox();
        sezione.setSpacing(15);
        
        HBox contenutoFinale = new HBox();
        contenutoFinale.setSpacing(15);
        
        // Pannello personalizzazione
        VBox pannelloPersonalizzazione = creaPannelloPersonalizzazione();
        
        // Pannello azioni finali
        VBox pannelloAzioni = creaPannelloAzioniFinali();
        
        contenutoFinale.getChildren().addAll(pannelloPersonalizzazione, pannelloAzioni);
        sezione.getChildren().add(contenutoFinale);
        
        return sezione;
    }
    
    /**
     * Crea il pannello per personalizzare il menu
     */
    private VBox creaPannelloPersonalizzazione() {
        VBox pannello = new VBox();
        pannello.setSpacing(12);
        pannello.setPrefWidth(400);
        pannello.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15px; -fx-border-radius: 8px;");
        
        Label etichetta = new Label("Personalizza Menu");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        // Modifica titolo
        Label labelTitolo = new Label("Nuovo titolo:");
        HBox pannelloTitolo = new HBox();
        pannelloTitolo.setSpacing(10);
        
        campoNuovoTitolo = new TextField();
        campoNuovoTitolo.setPromptText("Nuovo titolo del menu");
        HBox.setHgrow(campoNuovoTitolo, Priority.ALWAYS);
        
        bottoneAggiornaTitolo = new Button("Aggiorna");
        bottoneAggiornaTitolo.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        pannelloTitolo.getChildren().addAll(campoNuovoTitolo, bottoneAggiornaTitolo);
        
        // Aggiungi note
        Label labelNote = new Label("Note aggiuntive:");
        areaNuoveNote = new TextArea();
        areaNuoveNote.setPromptText("Inserisci note speciali...");
        areaNuoveNote.setPrefRowCount(3);
        
        bottoneAggiungiAnnotazione = new Button("Salva Note");
        bottoneAggiungiAnnotazione.setStyle("-fx-background-color: #16a085; -fx-text-fill: white;");
        
        pannello.getChildren().addAll(etichetta, labelTitolo, pannelloTitolo, labelNote, areaNuoveNote, bottoneAggiungiAnnotazione);
        return pannello;
    }
    
    /**
     * Crea il pannello con le azioni finali
     */
    private VBox creaPannelloAzioniFinali() {
        VBox pannello = new VBox();
        pannello.setSpacing(12);
        pannello.setPrefWidth(300);
        pannello.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15px; -fx-border-radius: 8px;");
        
        Label etichetta = new Label("Azioni Finali");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        Label labelCondivisione = new Label("Condividi menu:");
        labelCondivisione.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        
        bottoneGeneraPDF = new Button("Genera PDF");
        bottoneGeneraPDF.setPrefWidth(200);
        bottoneGeneraPDF.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-padding: 10px;");
        
        bottonePubblicaBacheca = new Button("Pubblica su Bacheca");
        bottonePubblicaBacheca.setPrefWidth(200);
        bottonePubblicaBacheca.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-padding: 10px;");
        
        Separator separatore = new Separator();
        
        Label labelGestione = new Label("Gestione:");
        labelGestione.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        
        bottoneEliminaMenu = new Button("Elimina Menu");
        bottoneEliminaMenu.setPrefWidth(200);
        bottoneEliminaMenu.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px;");
        
        pannello.getChildren().addAll(etichetta, labelCondivisione, bottoneGeneraPDF, bottonePubblicaBacheca, separatore, labelGestione, bottoneEliminaMenu);
        return pannello;
    }
    
    /**
     * Collega i componenti dell'interfaccia al controller - VERSIONE COMPLETA
     */
    private void collegaController() {
        // Imposta i componenti nel controller
        controller.setComponentiMenu(
            campoNomeMenu, campoDescrizioneMenu, areaNoteMenu,
            campoTitoloSezione, comboSezioni, comboRicette,
            tabellaMenu, listaSezioni, listaVoci
        );
        
        controller.setComponentiFinale(
            campoNuovoTitolo, areaNuoveNote, labelStato
        );
        
        // Collega eventi creazione menu
        bottoneCreaMenu.setOnAction(e -> controller.handleCreaMenu());
        bottoneDettagliMenu.setOnAction(e -> controller.handleSelezionaMenu());
        bottoneDuplicaMenu.setOnAction(e -> controller.handleDuplicaMenu());
        
        // Collega eventi gestione contenuto
        bottoneAggiungiSezione.setOnAction(e -> controller.handleAggiungiSezione());
        bottoneRimuoviSezione.setOnAction(e -> controller.handleRimuoviSezione());
        bottoneAggiungiRicetta.setOnAction(e -> controller.handleAggiungiRicetta());
        
        // SPOSTA RICETTA - con origine e destinazione
        bottoneSpostaRicetta.setOnAction(e -> handleSpostaRicetta());
        
        // ELIMINA RICETTA - dalla combo box
        bottoneEliminaRicetta.setOnAction(e -> handleEliminaRicettaDaCombo());
        
        // Collega eventi finalizzazione
        bottoneAggiornaTitolo.setOnAction(e -> controller.handleAggiornaTitolo());
        bottoneAggiungiAnnotazione.setOnAction(e -> controller.handleAggiungiAnnotazione());
        bottoneGeneraPDF.setOnAction(e -> controller.handleGeneraPDF());
        bottonePubblicaBacheca.setOnAction(e -> controller.handlePubblicaBacheca());
        bottoneEliminaMenu.setOnAction(e -> controller.handleEliminaMenu());
        
        // Listener per selezioni
        tabellaMenu.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    if (newSelection != null) {
        menuSelezionato = newSelection;
        controller.handleSelezionaMenuDaTabella(newSelection);
        
        // IMPORTANTE: Aggiorna TUTTE le combo box quando si seleziona un menu
        aggiornaComboBoxSezioni(newSelection);
        aggiornaComboBoxRicette(newSelection);
        
        System.out.println("Menu selezionato: " + newSelection.getNome()); // Debug
        System.out.println("Sezioni trovate: " + newSelection.getSezioni().size()); // Debug
        
        // Conta le ricette totali per debug
        int totalRicette = 0;
        for (SezioniMenu sezione : newSelection.getSezioni()) {
            totalRicette += sezione.getVoci().size();
        }
        System.out.println("Ricette totali nel menu: " + totalRicette); // Debug
    }
});
        
        listaSezioni.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                controller.handleSelezionaSezione(newSelection);
            }
        });
        
        // Listener per aggiornare sezione origine quando si seleziona una ricetta
        comboRicetteDaSpostare.setOnAction(e -> aggiornaSezioneOrigine());
    }
    
    /**
     * Gestisce lo spostamento della ricetta
     */
    private void handleSpostaRicetta() {
        VoceMenu ricettaSelezionata = comboRicetteDaSpostare.getValue();
        String sezioneOrigine = comboSezioniOrigine.getValue();
        String sezioneDestinazione = comboSezioniDestinazione.getValue();
        
        if (ricettaSelezionata == null) {
            mostraErrore("Ricetta non selezionata", "Scegli una ricetta da spostare");
            return;
        }
        
        if (sezioneOrigine == null) {
            mostraErrore("Sezione origine non selezionata", "Scegli la sezione di origine");
            return;
        }
        
        if (sezioneDestinazione == null) {
            mostraErrore("Sezione destinazione non selezionata", "Scegli la sezione di destinazione");
            return;
        }
        
        if (sezioneOrigine.equals(sezioneDestinazione)) {
            mostraErrore("Sezioni uguali", "La sezione di origine e destinazione devono essere diverse");
            return;
        }
        
        // Chiama il controller per spostare la ricetta
        controller.handleSpostaRicettaConSezioni(sezioneOrigine, sezioneDestinazione, ricettaSelezionata);
        
        // Aggiorna le combo box
        aggiornaComboBoxRicette(menuSelezionato);
        
        // Pulisce le selezioni
        comboRicetteDaSpostare.setValue(null);
        comboSezioniOrigine.setValue(null);
        comboSezioniDestinazione.setValue(null);
    }
    
    /**
     * Gestisce l'eliminazione della ricetta dalla combo box
     */
    private void handleEliminaRicettaDaCombo() {
        VoceMenu ricettaSelezionata = comboRicetteDaRimuovere.getValue();
        
        if (ricettaSelezionata == null) {
            mostraErrore("Ricetta non selezionata", "Scegli una ricetta da rimuovere");
            return;
        }
        
        if (confermaAzione("Conferma rimozione", "Rimuovere '" + ricettaSelezionata.getNomeVisuale() + "' dal menu?")) {
            controller.handleEliminaRicetta(ricettaSelezionata);
            aggiornaComboBoxRicette(menuSelezionato);
            comboRicetteDaRimuovere.setValue(null);
        }
    }
    
/**
 * Aggiorna automaticamente la sezione origine quando si seleziona una ricetta - VERSIONE CORRETTA
 */
private void aggiornaSezioneOrigine() {
    VoceMenu ricettaSelezionata = comboRicetteDaSpostare.getValue();
    if (ricettaSelezionata != null && menuSelezionato != null) {
        // Trova in quale sezione si trova questa ricetta
        for (SezioniMenu sezione : menuSelezionato.getSezioni()) {
            for (VoceMenu voce : sezione.getVoci()) {
                if (voce.getId().equals(ricettaSelezionata.getId())) {
                    comboSezioniOrigine.setValue(sezione.getTitolo());
                    return;
                }
            }
        }
    }
}

/**
 * Aggiorna tutte le combo box delle sezioni - VERSIONE CORRETTA
 */
private void aggiornaComboBoxSezioni(Menu menu) {
    // Pulisce tutte le combo box delle sezioni
    comboSezioni.getItems().clear();
    comboSezioniOrigine.getItems().clear();
    comboSezioniDestinazione.getItems().clear();
    
    if (menu != null) {
        // Usa il controller per ottenere i titoli delle sezioni
        ObservableList<String> titoli = controller.getTitoliSezioniMenuSelezionato();
        
        comboSezioni.getItems().addAll(titoli);
        comboSezioniOrigine.getItems().addAll(titoli);
        comboSezioniDestinazione.getItems().addAll(titoli);
    }
}
    
/**
 * Aggiorna tutte le combo box delle ricette - VERSIONE CORRETTA
 */
private void aggiornaComboBoxRicette(Menu menu) {
    // Pulisce le combo box delle ricette
    comboRicetteDaSpostare.getItems().clear();
    comboRicetteDaRimuovere.getItems().clear();
    
    if (menu != null) {
        // Usa il controller per ottenere tutte le voci del menu
        ObservableList<VoceMenu> voci = controller.getVociMenuSelezionato();
        
        comboRicetteDaSpostare.getItems().addAll(voci);
        comboRicetteDaRimuovere.getItems().addAll(voci);
    }
}

    /**
     * Mostra un messaggio di errore
     */
    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
    
    /**
     * Chiede conferma per una azione
     */
    private boolean confermaAzione(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        return alert.showAndWait().get() == ButtonType.OK;
    }
    
    /**
     * Restituisce il nodo principale della vista
     */
    public Node getView() {
        return layoutPrincipale;
    }
    
    /**
     * Aggiorna il messaggio di stato
     */
    public void aggiornaStato(String messaggio) {
        if (labelStato != null) {
            labelStato.setText(messaggio);
        }
    }
}