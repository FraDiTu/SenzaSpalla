package com.catring.viewfx;

import com.catring.controller.MenuController;
import com.catring.model.Ricetta;
import com.catring.model.Ingrediente;
import com.catring.model.Dose;
import com.catring.model.Tag;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * VISTA SEMPLIFICATA PER LA GESTIONE DEL RICETTARIO
 * Gestisce l'interfaccia per creare, visualizzare ed eliminare ricette con ingredienti
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
    
    // Componenti per ingredienti
    private TextField campoNomeIngrediente;
    private TextField campoQuantita;
    private ComboBox<String> comboUnitaMisura;
    private Button bottoneAggiungiIngrediente;
    private ListView<String> listaIngredienti;
    
    // Componenti per tag
    private TextField campoTag;
    private Button bottoneAggiungiTag;
    private ListView<String> listaTags;
    
    // Lista ricette esistenti
    private ListView<Ricetta> listaRicette;
    
    // Ricetta corrente in modifica
    private Ricetta ricettaCorrente;
    
    public RicettarioView(MenuController controller) {
        this.controller = controller;
        this.ricettaCorrente = null;
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
        
        // ScrollPane per contenere tutto
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        
        VBox contenutoCompleto = creaContenutoCompleto();
        scrollPane.setContent(contenutoCompleto);
        
        layoutPrincipale.getChildren().addAll(titolo, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }
    
    /**
     * Crea tutto il contenuto della vista
     */
    private VBox creaContenutoCompleto() {
        VBox contenuto = new VBox();
        contenuto.setSpacing(20);
        
        // Pulsante per aggiornare
        HBox pannelloAzioni = creaPannelloAzioni();
        
        // Contenuto principale
        HBox contenutoPrincipale = creaContenutoPrincipale();
        
        contenuto.getChildren().addAll(pannelloAzioni, contenutoPrincipale);
        return contenuto;
    }
    
    /**
     * Crea il pannello con le azioni principali
     */
    private HBox creaPannelloAzioni() {
        HBox pannello = new HBox();
        pannello.setSpacing(10);
        
        bottoneConsultaRicettario = new Button("Aggiorna Ricettario");
        bottoneConsultaRicettario.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-padding: 8px 16px;");
        
        // RIMOSSO il bottone "Nuova Ricetta" come richiesto
        
        pannello.getChildren().add(bottoneConsultaRicettario);
        return pannello;
    }
    
    /**
     * Crea il contenuto principale
     */
    private HBox creaContenutoPrincipale() {
        HBox contenuto = new HBox();
        contenuto.setSpacing(15);
        
        // Pannello sinistro - Form ricetta
        VBox pannelloForm = creaPannelloFormRicetta();
        
        // Pannello destro - Lista ricette
        VBox pannelloLista = creaPannelloListaRicette();
        
        contenuto.getChildren().addAll(pannelloForm, pannelloLista);
        return contenuto;
    }
    
    /**
     * Crea il pannello del form per le ricette
     */
    private VBox creaPannelloFormRicetta() {
        VBox pannello = new VBox();
        pannello.setSpacing(15);
        pannello.setPrefWidth(600);
        
        Label etichetta = new Label("Crea/Modifica Ricetta");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        // Sezione dati base
        TitledPane sezioneBase = new TitledPane("Dati Base", creaSezioneBase());
        sezioneBase.setExpanded(true);
        
        // Sezione ingredienti
        TitledPane sezioneIngredienti = new TitledPane("Ingredienti", creaSezioneIngredienti());
        sezioneIngredienti.setExpanded(true);
        
        // Sezione tag
        TitledPane sezioneTags = new TitledPane("Tag", creaSezioneTags());
        sezioneTags.setExpanded(false);
        
        // Pulsanti azioni
        HBox pannelloPulsanti = creaPannelloPulsantiRicetta();
        
        pannello.getChildren().addAll(etichetta, sezioneBase, sezioneIngredienti, sezioneTags, pannelloPulsanti);
        return pannello;
    }
    
    /**
     * Crea la sezione con i dati base della ricetta - SENZA numero porte
     */
    private VBox creaSezioneBase() {
        VBox sezione = new VBox();
        sezione.setSpacing(10);
        
        GridPane griglia = new GridPane();
        griglia.setHgap(10);
        griglia.setVgap(10);
        
        // Nome ricetta
        Label labelNome = new Label("Nome ricetta:");
        campoNomeRicetta = new TextField();
        campoNomeRicetta.setPromptText("Es: Pasta al pomodoro");
        
        // Descrizione
        Label labelDescrizione = new Label("Descrizione:");
        campoDescrizioneRicetta = new TextField();
        campoDescrizioneRicetta.setPromptText("Breve descrizione");
        
        // Tempo preparazione
        Label labelTempo = new Label("Tempo (minuti):");
        campoTempoPreparazione = new TextField();
        campoTempoPreparazione.setPromptText("30");
        
        // Autore
        Label labelAutore = new Label("Chef responsabile:");
        campoAutoreRicetta = new TextField();
        campoAutoreRicetta.setPromptText("Nome del chef");
        
        // Stato
        Label labelStato = new Label("Stato:");
        comboStatoRicetta = new ComboBox<>();
        comboStatoRicetta.getItems().addAll("bozza", "pubblicata");
        comboStatoRicetta.setValue("bozza");
        
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
        
        sezione.getChildren().add(griglia);
        return sezione;
    }
    
    /**
     * Crea la sezione per gestire gli ingredienti
     */
    private VBox creaSezioneIngredienti() {
        VBox sezione = new VBox();
        sezione.setSpacing(10);
        
        Label info = new Label("Aggiungi ingredienti con le relative quantita:");
        info.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        GridPane grigliaNuovo = new GridPane();
        grigliaNuovo.setHgap(10);
        grigliaNuovo.setVgap(10);
        
        Label labelIngrediente = new Label("Ingrediente:");
        campoNomeIngrediente = new TextField();
        campoNomeIngrediente.setPromptText("Es: Pomodoro");
        
        Label labelQuantita = new Label("Quantita:");
        campoQuantita = new TextField();
        campoQuantita.setPromptText("500");
        
        Label labelUnita = new Label("Unita:");
        comboUnitaMisura = new ComboBox<>();
        comboUnitaMisura.getItems().addAll("grammi", "kg", "litri", "ml", "cucchiai", "cucchiaini", "pezzi", "spicchi");
        comboUnitaMisura.setValue("grammi");
        
        bottoneAggiungiIngrediente = new Button("Aggiungi");
        bottoneAggiungiIngrediente.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        
        grigliaNuovo.add(labelIngrediente, 0, 0);
        grigliaNuovo.add(campoNomeIngrediente, 1, 0);
        grigliaNuovo.add(labelQuantita, 2, 0);
        grigliaNuovo.add(campoQuantita, 3, 0);
        grigliaNuovo.add(labelUnita, 4, 0);
        grigliaNuovo.add(comboUnitaMisura, 5, 0);
        grigliaNuovo.add(bottoneAggiungiIngrediente, 6, 0);
        
        Label labelLista = new Label("Ingredienti aggiunti:");
        listaIngredienti = new ListView<>();
        listaIngredienti.setPrefHeight(120);
        
        Button bottoneRimuoviIngrediente = new Button("Rimuovi Selezionato");
        bottoneRimuoviIngrediente.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        bottoneRimuoviIngrediente.setOnAction(e -> rimuoviIngredienteSelezionato());
        
        sezione.getChildren().addAll(info, grigliaNuovo, labelLista, listaIngredienti, bottoneRimuoviIngrediente);
        return sezione;
    }
    
    /**
     * Crea la sezione per gestire i tag
     */
    private VBox creaSezioneTags() {
        VBox sezione = new VBox();
        sezione.setSpacing(10);
        
        HBox pannelloNuovoTag = new HBox();
        pannelloNuovoTag.setSpacing(10);
        
        Label labelTag = new Label("Nuovo tag:");
        campoTag = new TextField();
        campoTag.setPromptText("Es: vegetariano, veloce, dessert");
        
        bottoneAggiungiTag = new Button("Aggiungi");
        bottoneAggiungiTag.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        
        pannelloNuovoTag.getChildren().addAll(labelTag, campoTag, bottoneAggiungiTag);
        
        Label labelListaTags = new Label("Tag aggiunti:");
        listaTags = new ListView<>();
        listaTags.setPrefHeight(80);
        
        Button bottoneRimuoviTag = new Button("Rimuovi Tag Selezionato");
        bottoneRimuoviTag.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        bottoneRimuoviTag.setOnAction(e -> rimuoviTagSelezionato());
        
        sezione.getChildren().addAll(pannelloNuovoTag, labelListaTags, listaTags, bottoneRimuoviTag);
        return sezione;
    }
    
    /**
     * Crea il pannello con i pulsanti per le azioni sulla ricetta
     */
    private HBox creaPannelloPulsantiRicetta() {
        HBox pannello = new HBox();
        pannello.setSpacing(10);
        
        bottoneInserisciRicetta = new Button("Salva Ricetta");
        bottoneInserisciRicetta.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-padding: 10px 20px;");
        
        Button bottoneNuovaRicetta = new Button("Nuova Ricetta");
        bottoneNuovaRicetta.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px 20px;");
        bottoneNuovaRicetta.setOnAction(e -> iniziaNuovaRicetta());
        
        Button bottoneAnnulla = new Button("Annulla");
        bottoneAnnulla.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10px 20px;");
        bottoneAnnulla.setOnAction(e -> annullaModifica());
        
        pannello.getChildren().addAll(bottoneInserisciRicetta, bottoneNuovaRicetta, bottoneAnnulla);
        return pannello;
    }
    
    /**
     * Crea il pannello con la lista delle ricette
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
        
        // Personalizza la visualizzazione delle ricette - SENZA numero porte
        listaRicette.setCellFactory(listView -> new ListCell<Ricetta>() {
            @Override
            protected void updateItem(Ricetta ricetta, boolean empty) {
                super.updateItem(ricetta, empty);
                if (empty || ricetta == null) {
                    setText(null);
                } else {
                    setText(ricetta.getNome() + " - " + ricetta.getAutore() + 
                           " (" + ricetta.getTempoPreparazione() + " min, " + 
                           ricetta.getIngredienti().size() + " ingredienti)");
                }
            }
        });
        
        // Pannello pulsanti per gestione ricette
        HBox pannelloPulsanti = new HBox();
        pannelloPulsanti.setSpacing(10);
        
        Button bottoneModifica = new Button("Modifica Ricetta");
        bottoneModifica.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8px 16px;");
        bottoneModifica.setOnAction(e -> modificaRicettaSelezionata());
        
        bottoneEliminaRicetta = new Button("Elimina Ricetta");
        bottoneEliminaRicetta.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8px 16px;");
        
        pannelloPulsanti.getChildren().addAll(bottoneModifica, bottoneEliminaRicetta);
        
        Label testoInfo = new Label("Seleziona una ricetta per modificarla o eliminarla");
        testoInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        pannello.getChildren().addAll(etichetta, listaRicette, pannelloPulsanti, testoInfo);
        return pannello;
    }
    
    /**
     * Inizia la creazione di una nuova ricetta
     */
    private void iniziaNuovaRicetta() {
        ricettaCorrente = new Ricetta();
        pulisciTuttiICampi();
        aggiornaListaIngredienti();
        aggiornaListaTags();
    }
    
    /**
     * Modifica la ricetta selezionata
     */
    private void modificaRicettaSelezionata() {
        Ricetta selezionata = listaRicette.getSelectionModel().getSelectedItem();
        if (selezionata == null) {
            mostraErrore("Nessuna ricetta selezionata", "Seleziona una ricetta da modificare");
            return;
        }
        
        ricettaCorrente = selezionata;
        caricaDatiRicetta(selezionata);
    }
    
    /**
     * Carica i dati di una ricetta nel form - SENZA numero porte
     */
    private void caricaDatiRicetta(Ricetta ricetta) {
        campoNomeRicetta.setText(ricetta.getNome());
        campoDescrizioneRicetta.setText(ricetta.getDescrizione());
        campoTempoPreparazione.setText(String.valueOf(ricetta.getTempoPreparazione()));
        campoAutoreRicetta.setText(ricetta.getAutore());
        comboStatoRicetta.setValue(ricetta.getStato());
        
        aggiornaListaIngredienti();
        aggiornaListaTags();
    }
    
    /**
     * Aggiunge un ingrediente alla ricetta corrente
     */
    private void aggiungiIngrediente() {
        if (ricettaCorrente == null) {
            mostraErrore("Nessuna ricetta", "Inizia prima una nuova ricetta");
            return;
        }
        
        String nomeIngrediente = campoNomeIngrediente.getText().trim();
        String quantitaStr = campoQuantita.getText().trim();
        String unita = comboUnitaMisura.getValue();
        
        if (nomeIngrediente.isEmpty() || quantitaStr.isEmpty()) {
            mostraErrore("Dati incompleti", "Inserisci nome ingrediente e quantita");
            return;
        }
        
        try {
            double quantita = Double.parseDouble(quantitaStr);
            
            Ingrediente ingrediente = new Ingrediente("I" + System.currentTimeMillis(), nomeIngrediente, "base", unita);
            Dose dose = new Dose(quantita, unita);
            
            ricettaCorrente.aggiungiIngrediente(ingrediente, dose);
            
            campoNomeIngrediente.clear();
            campoQuantita.clear();
            aggiornaListaIngredienti();
            
        } catch (NumberFormatException e) {
            mostraErrore("Quantita non valida", "Inserisci un numero valido per la quantita");
        }
    }
    
    /**
     * Rimuove l'ingrediente selezionato
     */
    private void rimuoviIngredienteSelezionato() {
        if (ricettaCorrente == null) return;
        
        String selezionato = listaIngredienti.getSelectionModel().getSelectedItem();
        if (selezionato == null) {
            mostraErrore("Nessun ingrediente selezionato", "Seleziona un ingrediente da rimuovere");
            return;
        }
        
        // Trova l'ingrediente corrispondente
        for (Ingrediente ingrediente : ricettaCorrente.getIngredienti()) {
            if (selezionato.contains(ingrediente.getNome())) {
                ricettaCorrente.rimuoviIngrediente(ingrediente);
                break;
            }
        }
        
        aggiornaListaIngredienti();
    }
    
    /**
     * Aggiunge un tag alla ricetta corrente
     */
    private void aggiungiTag() {
        if (ricettaCorrente == null) {
            mostraErrore("Nessuna ricetta", "Inizia prima una nuova ricetta");
            return;
        }
        
        String nomeTag = campoTag.getText().trim();
        if (nomeTag.isEmpty()) {
            mostraErrore("Tag vuoto", "Inserisci il nome del tag");
            return;
        }
        
        Tag tag = new Tag(nomeTag);
        ricettaCorrente.getTags().add(tag);
        
        campoTag.clear();
        aggiornaListaTags();
    }
    
    /**
     * Rimuove il tag selezionato
     */
    private void rimuoviTagSelezionato() {
        if (ricettaCorrente == null) return;
        
        String selezionato = listaTags.getSelectionModel().getSelectedItem();
        if (selezionato == null) {
            mostraErrore("Nessun tag selezionato", "Seleziona un tag da rimuovere");
            return;
        }
        
        ricettaCorrente.getTags().removeIf(tag -> tag.getNome().equals(selezionato));
        aggiornaListaTags();
    }
    
    /**
     * Annulla la modifica corrente
     */
    private void annullaModifica() {
        ricettaCorrente = null;
        pulisciTuttiICampi();
        aggiornaListaIngredienti();
        aggiornaListaTags();
    }
    
    /**
     * Aggiorna la visualizzazione degli ingredienti
     */
    private void aggiornaListaIngredienti() {
        listaIngredienti.getItems().clear();
        if (ricettaCorrente != null) {
            for (int i = 0; i < ricettaCorrente.getIngredienti().size(); i++) {
                Ingrediente ingrediente = ricettaCorrente.getIngredienti().get(i);
                Dose dose = null;
                if (i < ricettaCorrente.getDosi().size()) {
                    dose = ricettaCorrente.getDosi().get(i);
                }
                
                String item = ingrediente.getNome();
                if (dose != null) {
                    item += " - " + dose.getQuantitativo() + " " + dose.getUnitaMisura();
                }
                listaIngredienti.getItems().add(item);
            }
        }
    }
    
    /**
     * Aggiorna la visualizzazione dei tag
     */
    private void aggiornaListaTags() {
        listaTags.getItems().clear();
        if (ricettaCorrente != null) {
            for (Tag tag : ricettaCorrente.getTags()) {
                listaTags.getItems().add(tag.getNome());
            }
        }
    }
    
    /**
     * Pulisce tutti i campi del form - SENZA numero porte
     */
    private void pulisciTuttiICampi() {
        campoNomeRicetta.clear();
        campoDescrizioneRicetta.clear();
        campoTempoPreparazione.clear();
        campoAutoreRicetta.clear();
        comboStatoRicetta.setValue("bozza");
        campoNomeIngrediente.clear();
        campoQuantita.clear();
        comboUnitaMisura.setValue("grammi");
        campoTag.clear();
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
     * Collega i componenti dell'interfaccia al controller
     */
    private void collegaController() {
        // Imposta i componenti nel controller
        controller.setComponentiRicettario(
            campoNomeRicetta, campoDescrizioneRicetta, campoTempoPreparazione,
            campoAutoreRicetta, comboStatoRicetta, listaRicette
        );
        
        // Collega i pulsanti agli eventi
        bottoneConsultaRicettario.setOnAction(e -> controller.handleConsultaRicettario());
        bottoneInserisciRicetta.setOnAction(e -> salvaRicettaCorrente());
        bottoneEliminaRicetta.setOnAction(e -> controller.handleEliminaRicettaDalRicettario());
        bottoneAggiungiIngrediente.setOnAction(e -> aggiungiIngrediente());
        bottoneAggiungiTag.setOnAction(e -> aggiungiTag());
    }
    
    /**
     * Salva la ricetta corrente - SENZA numero porte
     */
    private void salvaRicettaCorrente() {
        if (ricettaCorrente == null) {
            mostraErrore("Nessuna ricetta", "Inizia prima una nuova ricetta");
            return;
        }
        
        // Aggiorna i dati della ricetta corrente
        String nome = campoNomeRicetta.getText().trim();
        String descrizione = campoDescrizioneRicetta.getText().trim();
        String tempoStr = campoTempoPreparazione.getText().trim();
        String autore = campoAutoreRicetta.getText().trim();
        String stato = comboStatoRicetta.getValue();
        
        if (nome.isEmpty()) {
            mostraErrore("Nome ricetta mancante", "Inserisci il nome della ricetta");
            return;
        }
        
        try {
            int tempo = Integer.parseInt(tempoStr);
            
            ricettaCorrente.setNome(nome);
            ricettaCorrente.setDescrizione(descrizione);
            ricettaCorrente.setTempoPreparazione(tempo);
            ricettaCorrente.setAutore(autore);
            ricettaCorrente.setStato(stato);
            
            // Se è una nuova ricetta, chiamiamo il controller per inserirla
            if (ricettaCorrente.getId() == null || ricettaCorrente.getId().isEmpty()) {
                controller.handleInserisciRicettaCompleta(ricettaCorrente);
            } else {
                controller.handleAggiornaRicetta(ricettaCorrente);
            }
            
            annullaModifica();
            
        } catch (NumberFormatException e) {
            mostraErrore("Dati non validi", "Inserisci valori numerici validi per il tempo");
        }
    }
    
    /**
     * Restituisce il nodo principale della vista
     */
    public Node getView() {
        return layoutPrincipale;
    }
}