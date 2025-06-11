package com.catring.viewfx;

import com.catring.controller.EventoController;
import com.catring.model.Evento;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class EventiView {
    
    private EventoController controller;
    private VBox layoutPrincipale;

    private ListView<Evento> listaEventi;
    private TextArea areaDettagli;
    private Button bottoneCaricaEventi;
    private Label labelStato;
    
    public EventiView(EventoController controller) {
        this.controller = controller;
        creaInterfaccia();
        collegaController();
    }

    private void creaInterfaccia() {
        layoutPrincipale = new VBox();
        layoutPrincipale.setSpacing(20);
        layoutPrincipale.setStyle("-fx-padding: 20px;");
        layoutPrincipale.setMinWidth(1200);
        layoutPrincipale.setPrefWidth(1400);

        Label titolo = new Label("Eventi Assegnati");
        titolo.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2c3e50;");
        
        Label descrizione = new Label("Visualizza e gestisci gli eventi che ti sono stati assegnati");
        descrizione.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");

        HBox pannelloAzioni = creaPannelloAzioni();

        HBox contenutoPrincipale = creaContenutoPrincipale();

        labelStato = new Label("Sistema pronto. Seleziona 'Carica Eventi' per visualizzare gli eventi assegnati");
        labelStato.setStyle("-fx-text-fill: #27ae60; -fx-padding: 15px; -fx-background-color: #f8f9fa; -fx-font-size: 14px;");
        labelStato.setMinHeight(50);
        
        layoutPrincipale.getChildren().addAll(titolo, descrizione, pannelloAzioni, contenutoPrincipale, labelStato);
    }

    private HBox creaPannelloAzioni() {
        HBox pannello = new HBox();
        pannello.setSpacing(15);
        pannello.setStyle("-fx-padding: 10px 0;");
        
        bottoneCaricaEventi = new Button("Carica Eventi Assegnati");
        bottoneCaricaEventi.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 12px 20px; -fx-font-size: 14px;");
        bottoneCaricaEventi.setPrefWidth(200);

        Region spazio = new Region();
        HBox.setHgrow(spazio, Priority.ALWAYS);
        
        pannello.getChildren().addAll(bottoneCaricaEventi, spazio);
        return pannello;
    }

    private HBox creaContenutoPrincipale() {
        HBox contenuto = new HBox();
        contenuto.setSpacing(25);
        contenuto.setMinHeight(500);
        contenuto.setPrefHeight(600);

        VBox pannelloLista = creaPannelloListaEventi();

        VBox pannelloDettagli = creaPannelloDettagli();
        
        contenuto.getChildren().addAll(pannelloLista, pannelloDettagli);
        return contenuto;
    }

    private VBox creaPannelloListaEventi() {
        VBox pannello = new VBox();
        pannello.setSpacing(15);
        pannello.setPrefWidth(450);
        pannello.setMinWidth(400);
        
        Label etichetta = new Label("Lista Eventi Assegnati");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-font-size: 16px;");
        
        listaEventi = new ListView<>();
        listaEventi.setPrefHeight(400);
        listaEventi.setMinHeight(350);
        listaEventi.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 8px; -fx-border-width: 2px;");

        listaEventi.setCellFactory(listView -> new ListCell<Evento>() {
            @Override
            protected void updateItem(Evento evento, boolean empty) {
                super.updateItem(evento, empty);
                if (empty || evento == null) {
                    setText(null);
                    setStyle("");
                } else {

                    setText(evento.getLuogo() + "\n" + 
                           evento.getDataInizio() + " → " + evento.getDataFine() + "\n" +
                           "Tipo: " + evento.getTipo() + " | Persone: " + evento.getNumeroPersone());

                    setStyle("-fx-padding: 10px; -fx-font-size: 12px;");
                    setPrefHeight(80);
                }
            }
        });
        
                
        pannello.getChildren().addAll(etichetta, listaEventi);
        return pannello;
    }

    private VBox creaPannelloDettagli() {
        VBox pannello = new VBox();
        pannello.setSpacing(15);
        HBox.setHgrow(pannello, Priority.ALWAYS);
        pannello.setMinWidth(500);
        
        Label etichetta = new Label("Dettagli Evento Selezionato");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-font-size: 16px;");
        
        areaDettagli = new TextArea();
        areaDettagli.setPrefHeight(400);
        areaDettagli.setMinHeight(350);
        areaDettagli.setEditable(false);
        areaDettagli.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 8px; -fx-border-width: 2px; -fx-font-size: 13px; -fx-font-family: 'Courier New', monospace;");
        areaDettagli.setWrapText(true);

        VBox pannelloInfo = new VBox();
        pannelloInfo.setSpacing(10);
        pannelloInfo.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15px; -fx-border-radius: 8px;");
                
        pannello.getChildren().addAll(etichetta, areaDettagli, pannelloInfo);
        return pannello;
    }

    private void collegaController() {

        controller.setListaEventi(listaEventi);
        controller.setAreaDettagli(areaDettagli);
        controller.setLabelStato(labelStato);

        bottoneCaricaEventi.setOnAction(e -> {
            controller.handleAggiornaEventi();
            aggiornaStatistiche();
        });

        listaEventi.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                controller.handleSelezionaEvento(newSelection);
                evidenziaEventoSelezionato();
            } else {

                if (areaDettagli != null) {
                    areaDettagli.setText("Nessun evento selezionato\n\nSeleziona un evento dalla lista per vedere i dettagli");
                }
            }
        });
    }

    private void evidenziaEventoSelezionato() {
        Evento eventoSelezionato = listaEventi.getSelectionModel().getSelectedItem();
        if (eventoSelezionato != null) {

            String titoloDettagli = "Dettagli: " + eventoSelezionato.getLuogo() + " (" + eventoSelezionato.getTipo() + ")";

            aggiornaStato("Evento selezionato: " + eventoSelezionato.getLuogo());
        }
    }

    private void aggiornaStatistiche() {
        if (controller.getEventiList() != null) {
            int totaleEventi = controller.getEventiList().size();

            long eventiInCorso = controller.getEventiList().stream()
                    .filter(e -> e.getDataInizio().isAfter(java.time.LocalDate.now()) || 
                               e.getDataInizio().isEqual(java.time.LocalDate.now()))
                    .count();
            
            long eventiCompletati = totaleEventi - eventiInCorso;

            String statsText = String.format("Eventi caricati: %d | In corso: %d | Completati: %d", 
                                            totaleEventi, eventiInCorso, eventiCompletati);

            aggiornaStato("Statistiche aggiornate - " + statsText);
        }
    }

    public Node getView() {
        return layoutPrincipale;
    }

    public ListView<Evento> getListaEventi() {
        return listaEventi;
    }

    public TextArea getAreaDettagli() {
        return areaDettagli;
    }

    public void aggiornaStato(String messaggio) {
        if (labelStato != null) {
            labelStato.setText(messaggio);
        }
    }

    public void attivaVista() {

        if (controller != null) {
            controller.caricaDatiIniziali();
            aggiornaStatistiche();
        }
    }

    public void pulisciSelezione() {
        if (listaEventi != null) {
            listaEventi.getSelectionModel().clearSelection();
        }
        if (areaDettagli != null) {
            areaDettagli.setText("Seleziona un evento dalla lista per vedere i dettagli completi");
        }
    }
}