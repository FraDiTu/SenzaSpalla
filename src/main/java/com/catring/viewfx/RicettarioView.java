package com.catring.viewfx;

import com.catring.controller.MenuController;
import com.catring.model.Ricetta;
import com.catring.model.Ingrediente;
import com.catring.model.Dose;
import com.catring.model.Tag;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RicettarioView {
    
    private MenuController controller;
    private VBox layoutPrincipale;

    private TextField campoNomeRicetta;
    private TextField campoDescrizioneRicetta;
    private TextField campoTempoPreparazione;
    private TextField campoAutoreRicetta;
    private ComboBox<String> comboStatoRicetta;
    private Button bottoneInserisciRicetta;
    private Button bottoneConsultaRicettario;
    private Button bottoneEliminaRicetta;

    private TextField campoNomeIngrediente;
    private TextField campoQuantita;
    private ComboBox<String> comboUnitaMisura;
    private Button bottoneAggiungiIngrediente;
    private ListView<String> listaIngredienti;

    private TextField campoTag;
    private Button bottoneAggiungiTag;
    private ListView<String> listaTags;

    private ListView<Ricetta> listaRicette;

    private Ricetta ricettaCorrente;
    
    public RicettarioView(MenuController controller) {
        this.controller = controller;
        this.ricettaCorrente = null;
        creaInterfaccia();
        collegaController();
    }

    private void creaInterfaccia() {
        layoutPrincipale = new VBox();
        layoutPrincipale.setSpacing(20);
        layoutPrincipale.setStyle("-fx-padding: 20px;");
        layoutPrincipale.setMinWidth(1300);
        layoutPrincipale.setPrefWidth(1500);

        Label titolo = new Label("Gestione Ricette");
        titolo.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2c3e50;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(700);
        
        VBox contenutoCompleto = creaContenutoCompleto();
        scrollPane.setContent(contenutoCompleto);
        
        layoutPrincipale.getChildren().addAll(titolo, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }

    private VBox creaContenutoCompleto() {
        VBox contenuto = new VBox();
        contenuto.setSpacing(25);
        contenuto.setMinWidth(1250);

        HBox pannelloAzioni = creaPannelloAzioni();

        HBox contenutoPrincipale = creaContenutoPrincipale();
        
        contenuto.getChildren().addAll(pannelloAzioni, contenutoPrincipale);
        return contenuto;
    }

    private HBox creaPannelloAzioni() {
        HBox pannello = new HBox();
        pannello.setSpacing(15);
        pannello.setStyle("-fx-padding: 10px 0;");
        
        bottoneConsultaRicettario = new Button("Aggiorna Ricettario");
        bottoneConsultaRicettario.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-padding: 12px 20px; -fx-font-size: 14px;");
        bottoneConsultaRicettario.setPrefWidth(180);
        
        Region spazio = new Region();
        HBox.setHgrow(spazio, Priority.ALWAYS);
        
        Label info = new Label("Crea ricette complete con ingredienti, dosi e tag");
        info.setStyle("-fx-text-fill: #666; -fx-font-size: 12px; -fx-font-style: italic;");
        
        pannello.getChildren().addAll(bottoneConsultaRicettario, spazio, info);
        return pannello;
    }

    private HBox creaContenutoPrincipale() {
        HBox contenuto = new HBox();
        contenuto.setSpacing(25);
        contenuto.setMinHeight(600);

        VBox pannelloForm = creaPannelloFormRicetta();

        VBox pannelloLista = creaPannelloListaRicette();
        
        contenuto.getChildren().addAll(pannelloForm, pannelloLista);
        return contenuto;
    }

    private VBox creaPannelloFormRicetta() {
        VBox pannello = new VBox();
        pannello.setSpacing(20);
        pannello.setPrefWidth(700);
        pannello.setMinWidth(650);
        
        Label etichetta = new Label("Crea/Modifica Ricetta");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-font-size: 16px;");

        TitledPane sezioneBase = new TitledPane("Dati Base", creaSezioneBase());
        sezioneBase.setExpanded(true);
        sezioneBase.setStyle("-fx-font-size: 14px;");

        TitledPane sezioneIngredienti = new TitledPane("Ingredienti", creaSezioneIngredienti());
        sezioneIngredienti.setExpanded(true);
        sezioneIngredienti.setStyle("-fx-font-size: 14px;");

        TitledPane sezioneTags = new TitledPane("Tag", creaSezioneTags());
        sezioneTags.setExpanded(false);
        sezioneTags.setStyle("-fx-font-size: 14px;");

        HBox pannelloPulsanti = creaPannelloPulsantiRicetta();
        
        pannello.getChildren().addAll(etichetta, sezioneBase, sezioneIngredienti, sezioneTags, pannelloPulsanti);
        return pannello;
    }

    private VBox creaSezioneBase() {
        VBox sezione = new VBox();
        sezione.setSpacing(15);
        sezione.setStyle("-fx-padding: 15px;");
        
        GridPane griglia = new GridPane();
        griglia.setHgap(15);
        griglia.setVgap(15);

        Label labelNome = new Label("Nome ricetta:");
        labelNome.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        campoNomeRicetta = new TextField();
        campoNomeRicetta.setPromptText("Es: Pasta al pomodoro");
        campoNomeRicetta.setPrefWidth(300);

        Label labelDescrizione = new Label("Descrizione:");
        labelDescrizione.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        campoDescrizioneRicetta = new TextField();
        campoDescrizioneRicetta.setPromptText("Breve descrizione");
        campoDescrizioneRicetta.setPrefWidth(300);

        Label labelTempo = new Label("Tempo (minuti):");
        labelTempo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        campoTempoPreparazione = new TextField();
        campoTempoPreparazione.setPromptText("30");
        campoTempoPreparazione.setPrefWidth(100);

        Label labelAutore = new Label("Chef responsabile:");
        labelAutore.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        campoAutoreRicetta = new TextField();
        campoAutoreRicetta.setPromptText("Nome del chef");
        campoAutoreRicetta.setPrefWidth(200);

        Label labelStato = new Label("Stato:");
        labelStato.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        comboStatoRicetta = new ComboBox<>();
        comboStatoRicetta.getItems().addAll("bozza", "pubblicata");
        comboStatoRicetta.setValue("bozza");
        comboStatoRicetta.setPrefWidth(120);
        
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

    private VBox creaSezioneIngredienti() {
        VBox sezione = new VBox();
        sezione.setSpacing(15);
        sezione.setStyle("-fx-padding: 15px;");
        
        Label info = new Label("Aggiungi ingredienti con le relative quantita:");
        info.setStyle("-fx-font-size: 13px; -fx-text-fill: #666; -fx-font-weight: bold;");
        
        GridPane grigliaNuovo = new GridPane();
        grigliaNuovo.setHgap(15);
        grigliaNuovo.setVgap(10);
        
        Label labelIngrediente = new Label("Ingrediente:");
        labelIngrediente.setStyle("-fx-font-size: 12px;");
        campoNomeIngrediente = new TextField();
        campoNomeIngrediente.setPromptText("Es: Pomodoro");
        campoNomeIngrediente.setPrefWidth(150);
        
        Label labelQuantita = new Label("Quantita:");
        labelQuantita.setStyle("-fx-font-size: 12px;");
        campoQuantita = new TextField();
        campoQuantita.setPromptText("500");
        campoQuantita.setPrefWidth(80);
        
        Label labelUnita = new Label("Unita:");
        labelUnita.setStyle("-fx-font-size: 12px;");
        comboUnitaMisura = new ComboBox<>();
        comboUnitaMisura.getItems().addAll("grammi", "kg", "litri", "ml", "cucchiai", "cucchiaini", "pezzi", "spicchi");
        comboUnitaMisura.setValue("grammi");
        comboUnitaMisura.setPrefWidth(100);
        
        bottoneAggiungiIngrediente = new Button("Aggiungi");
        bottoneAggiungiIngrediente.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8px 15px;");
        bottoneAggiungiIngrediente.setPrefWidth(90);
        
        grigliaNuovo.add(labelIngrediente, 0, 0);
        grigliaNuovo.add(campoNomeIngrediente, 0, 1);
        grigliaNuovo.add(labelQuantita, 1, 0);
        grigliaNuovo.add(campoQuantita, 1, 1);
        grigliaNuovo.add(labelUnita, 2, 0);
        grigliaNuovo.add(comboUnitaMisura, 2, 1);
        grigliaNuovo.add(bottoneAggiungiIngrediente, 3, 1);
        
        Label labelLista = new Label("Ingredienti aggiunti:");
        labelLista.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        listaIngredienti = new ListView<>();
        listaIngredienti.setPrefHeight(150);
        listaIngredienti.setMinHeight(120);
        listaIngredienti.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5px;");
        
        Button bottoneRimuoviIngrediente = new Button("Rimuovi Selezionato");
        bottoneRimuoviIngrediente.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8px 15px;");
        bottoneRimuoviIngrediente.setOnAction(e -> rimuoviIngredienteSelezionato());
        
        sezione.getChildren().addAll(info, grigliaNuovo, labelLista, listaIngredienti, bottoneRimuoviIngrediente);
        return sezione;
    }

    private VBox creaSezioneTags() {
        VBox sezione = new VBox();
        sezione.setSpacing(15);
        sezione.setStyle("-fx-padding: 15px;");
        
        HBox pannelloNuovoTag = new HBox();
        pannelloNuovoTag.setSpacing(15);
        
        Label labelTag = new Label("Nuovo tag:");
        labelTag.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        campoTag = new TextField();
        campoTag.setPromptText("Es: vegetariano, veloce, dessert");
        campoTag.setPrefWidth(250);
        
        bottoneAggiungiTag = new Button("Aggiungi");
        bottoneAggiungiTag.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 8px 15px;");
        
        pannelloNuovoTag.getChildren().addAll(labelTag, campoTag, bottoneAggiungiTag);
        
        Label labelListaTags = new Label("Tag aggiunti:");
        labelListaTags.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        listaTags = new ListView<>();
        listaTags.setPrefHeight(100);
        listaTags.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5px;");
        
        Button bottoneRimuoviTag = new Button("Rimuovi Tag Selezionato");
        bottoneRimuoviTag.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8px 15px;");
        bottoneRimuoviTag.setOnAction(e -> rimuoviTagSelezionato());
        
        sezione.getChildren().addAll(pannelloNuovoTag, labelListaTags, listaTags, bottoneRimuoviTag);
        return sezione;
    }

    private HBox creaPannelloPulsantiRicetta() {
        HBox pannello = new HBox();
        pannello.setSpacing(15);
        pannello.setStyle("-fx-padding: 20px 0;");
        
        bottoneInserisciRicetta = new Button("Salva Ricetta");
        bottoneInserisciRicetta.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-padding: 12px 25px; -fx-font-size: 14px; -fx-font-weight: bold;");
        bottoneInserisciRicetta.setPrefWidth(150);
        
        Button bottoneAnnulla = new Button("Pulisci Campi");
        bottoneAnnulla.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 12px 25px; -fx-font-size: 14px;");
        bottoneAnnulla.setPrefWidth(150);
        bottoneAnnulla.setOnAction(e -> annullaModifica());
        
        pannello.getChildren().addAll(bottoneInserisciRicetta, bottoneAnnulla);
        return pannello;
    }

    private void verificaRicettaCorrente() {
        if (ricettaCorrente == null) {
            ricettaCorrente = new Ricetta();
            ricettaCorrente.setId("");
        }
    }

    private VBox creaPannelloListaRicette() {
        VBox pannello = new VBox();
        pannello.setSpacing(20);
        HBox.setHgrow(pannello, Priority.ALWAYS);
        pannello.setMinWidth(450);
        
        Label etichetta = new Label("Ricette Disponibili");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-font-size: 16px;");
        
        listaRicette = new ListView<>();
        listaRicette.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 8px; -fx-border-width: 2px;");
        VBox.setVgrow(listaRicette, Priority.ALWAYS);
        listaRicette.setPrefHeight(400);

        listaRicette.setCellFactory(listView -> new ListCell<Ricetta>() {
            @Override
            protected void updateItem(Ricetta ricetta, boolean empty) {
                super.updateItem(ricetta, empty);
                if (empty || ricetta == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(ricetta.getNome() + "\n" + ricetta.getAutore() + 
                           " | " + ricetta.getTempoPreparazione() + " min | " + 
                           ricetta.getIngredienti().size() + " ingredienti | " +
                           "Stato: " + ricetta.getStato());
                    setStyle("-fx-padding: 10px; -fx-font-size: 12px;");
                    setPrefHeight(60);
                }
            }
        });

        HBox pannelloPulsanti = new HBox();
        pannelloPulsanti.setSpacing(15);
        
        Button bottoneModifica = new Button("Modifica Ricetta");
        bottoneModifica.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px 18px; -fx-font-size: 13px;");
        bottoneModifica.setPrefWidth(150);
        bottoneModifica.setOnAction(e -> modificaRicettaSelezionata());
        
        bottoneEliminaRicetta = new Button("Elimina Ricetta");
        bottoneEliminaRicetta.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px 18px; -fx-font-size: 13px;");
        bottoneEliminaRicetta.setPrefWidth(150);
        
        pannelloPulsanti.getChildren().addAll(bottoneModifica, bottoneEliminaRicetta);
        
        Label testoInfo = new Label("Seleziona una ricetta per modificarla o eliminarla");
        testoInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-font-style: italic;");

        Label statsRicette = new Label("Ricette totali: 0 | Pubblicate: 0 | Bozze: 0");
        statsRicette.setId("statsRicette");
        statsRicette.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60;");
        
        pannello.getChildren().addAll(etichetta, listaRicette, pannelloPulsanti, testoInfo, statsRicette);
        return pannello;
    }

    public Node getView() {
        return layoutPrincipale;
    }

    private void modificaRicettaSelezionata() {
        Ricetta selezionata = listaRicette.getSelectionModel().getSelectedItem();
        if (selezionata == null) {
            mostraErrore("Nessuna ricetta selezionata", "Seleziona una ricetta da modificare");
            return;
        }
        
        ricettaCorrente = selezionata;
        caricaDatiRicetta(selezionata);
    }

    private void caricaDatiRicetta(Ricetta ricetta) {
        campoNomeRicetta.setText(ricetta.getNome());
        campoDescrizioneRicetta.setText(ricetta.getDescrizione());
        campoTempoPreparazione.setText(String.valueOf(ricetta.getTempoPreparazione()));
        campoAutoreRicetta.setText(ricetta.getAutore());
        comboStatoRicetta.setValue(ricetta.getStato());
        
        aggiornaListaIngredienti();
        aggiornaListaTags();
    }

    private void aggiungiIngrediente() {
        verificaRicettaCorrente();
        
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

    private void rimuoviIngredienteSelezionato() {
        if (ricettaCorrente == null) return;
        
        String selezionato = listaIngredienti.getSelectionModel().getSelectedItem();
        if (selezionato == null) {
            mostraErrore("Nessun ingrediente selezionato", "Seleziona un ingrediente da rimuovere");
            return;
        }

        for (Ingrediente ingrediente : ricettaCorrente.getIngredienti()) {
            if (selezionato.contains(ingrediente.getNome())) {
                ricettaCorrente.rimuoviIngrediente(ingrediente);
                break;
            }
        }
        
        aggiornaListaIngredienti();
    }

    private void aggiungiTag() {
        verificaRicettaCorrente();
        
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

    private void annullaModifica() {
        ricettaCorrente = null;
        pulisciTuttiICampi();
        aggiornaListaIngredienti();
        aggiornaListaTags();
    }

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

    private void aggiornaListaTags() {
        listaTags.getItems().clear();
        if (ricettaCorrente != null) {
            for (Tag tag : ricettaCorrente.getTags()) {
                listaTags.getItems().add(tag.getNome());
            }
        }
    }

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

    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    private void collegaController() {

        controller.setComponentiRicettario(
            campoNomeRicetta, campoDescrizioneRicetta, campoTempoPreparazione,
            campoAutoreRicetta, comboStatoRicetta, listaRicette
        );

        bottoneConsultaRicettario.setOnAction(e -> {
            controller.handleConsultaRicettario();
            aggiornaStatistiche();
        });
        bottoneInserisciRicetta.setOnAction(e -> salvaRicettaCorrente());
        bottoneEliminaRicetta.setOnAction(e -> controller.handleEliminaRicettaDalRicettario());
        bottoneAggiungiIngrediente.setOnAction(e -> aggiungiIngrediente());
        bottoneAggiungiTag.setOnAction(e -> aggiungiTag());
    }

    private void salvaRicettaCorrente() {

        if (ricettaCorrente == null) {
            ricettaCorrente = new Ricetta();
            ricettaCorrente.setId("");
        }

        String nome = campoNomeRicetta.getText().trim();
        String descrizione = campoDescrizioneRicetta.getText().trim();
        String tempoStr = campoTempoPreparazione.getText().trim();
        String autore = campoAutoreRicetta.getText().trim();
        String stato = comboStatoRicetta.getValue();
        
        if (nome.isEmpty()) {
            mostraErrore("Nome ricetta mancante", "Inserisci il nome della ricetta");
            return;
        }

        if (controller.ricettaEsiste(nome) && 
            (ricettaCorrente.getId() == null || ricettaCorrente.getId().isEmpty())) {
            mostraErrore("Ricetta esistente", "Una ricetta con questo nome esiste già");
            return;
        }
        
        try {
            int tempo = Integer.parseInt(tempoStr.isEmpty() ? "0" : tempoStr);
            
            ricettaCorrente.setNome(nome);
            ricettaCorrente.setDescrizione(descrizione);
            ricettaCorrente.setTempoPreparazione(tempo);
            ricettaCorrente.setAutore(autore);
            ricettaCorrente.setStato(stato);

            if (ricettaCorrente.getId() == null || ricettaCorrente.getId().isEmpty()) {
                controller.handleInserisciRicettaCompleta(ricettaCorrente);
            } else {
                controller.handleAggiornaRicetta(ricettaCorrente);
            }
            
            annullaModifica();
            aggiornaStatistiche();
            
        } catch (NumberFormatException e) {
            mostraErrore("Dati non validi", "Inserisci valori numerici validi per il tempo");
        }
    }

    private void aggiornaStatistiche() {
        if (controller.getTutteRicetteList() != null) {
            long totali = controller.getTutteRicetteList().size();
            long pubblicate = controller.getTutteRicetteList().stream()
                    .filter(r -> "pubblicata".equals(r.getStato()))
                    .count();
            long bozze = totali - pubblicate;
            
            String stats = String.format("Ricette totali: %d | Pubblicate: %d | Bozze: %d", 
                                        totali, pubblicate, bozze);

        }
    }
}