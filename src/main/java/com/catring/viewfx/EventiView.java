package com.catring.viewfx;

import com.catring.controller.EventoController;
import com.catring.model.Evento;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * VISTA PER LA GESTIONE DEGLI EVENTI
 * Mostra la lista degli eventi assegnati e i loro dettagli
 */
public class EventiView {
    
    private EventoController controller;
    private VBox layoutPrincipale;
    
    // Componenti dell'interfaccia
    private ListView<Evento> listaEventi;
    private TextArea areaDettagli;
    private Button bottoneCaricaEventi;
    private Label labelStato;
    
    public EventiView(EventoController controller) {
        this.controller = controller;
        creaInterfaccia();
        collegaController();
    }
    
    /**
     * Crea l'interfaccia per la gestione eventi
     */
    private void creaInterfaccia() {
        layoutPrincipale = new VBox();
        layoutPrincipale.setSpacing(15);
        layoutPrincipale.setStyle("-fx-padding: 15px;");
        
        // Intestazione
        Label titolo = new Label("Eventi Assegnati");
        titolo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        
        Label descrizione = new Label("Visualizza e gestisci gli eventi che ti sono stati assegnati");
        descrizione.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        
        // Pulsanti di azione
        HBox pannelloAzioni = creaPannelloAzioni();
        
        // Contenuto principale
        HBox contenutoPrincipale = creaContenutoPrincipale();
        
        // Area di stato
        labelStato = new Label("Sistema pronto. Seleziona 'Carica Eventi' per visualizzare gli eventi assegnati");
        labelStato.setStyle("-fx-text-fill: #27ae60; -fx-padding: 10px; -fx-background-color: #f8f9fa;");
        
        layoutPrincipale.getChildren().addAll(titolo, descrizione, pannelloAzioni, contenutoPrincipale, labelStato);
    }
    
    /**
     * Crea il pannello con i pulsanti di azione
     */
    private HBox creaPannelloAzioni() {
        HBox pannello = new HBox();
        pannello.setSpacing(10);
        
        bottoneCaricaEventi = new Button("Carica Eventi Assegnati");
        bottoneCaricaEventi.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8px 16px;");
        
        // Aggiunto solo il pulsante per caricare eventi, rimosso "Nuovo Evento"
        pannello.getChildren().add(bottoneCaricaEventi);
        return pannello;
    }
    
    /**
     * Crea il contenuto principale con lista eventi e dettagli
     */
    private HBox creaContenutoPrincipale() {
        HBox contenuto = new HBox();
        contenuto.setSpacing(15);
        
        // Pannello sinistro - Lista eventi
        VBox pannelloLista = creaPannelloListaEventi();
        
        // Pannello destro - Dettagli evento
        VBox pannelloDettagli = creaPannelloDettagli();
        
        contenuto.getChildren().addAll(pannelloLista, pannelloDettagli);
        return contenuto;
    }
    
    /**
     * Crea il pannello con la lista degli eventi
     */
    private VBox creaPannelloListaEventi() {
        VBox pannello = new VBox();
        pannello.setSpacing(8);
        pannello.setPrefWidth(350);
        
        Label etichetta = new Label("Lista Eventi Assegnati");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        listaEventi = new ListView<>();
        listaEventi.setPrefHeight(300);
        listaEventi.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5px;");
        
        // Personalizza la visualizzazione degli eventi
        listaEventi.setCellFactory(listView -> new ListCell<Evento>() {
            @Override
            protected void updateItem(Evento evento, boolean empty) {
                super.updateItem(evento, empty);
                if (empty || evento == null) {
                    setText(null);
                } else {
                    setText(evento.getLuogo() + " - " + evento.getDataInizio());
                }
            }
        });
        
        Label infoEventi = new Label("Gli eventi vengono assegnati dal sistema di gestione");
        infoEventi.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        pannello.getChildren().addAll(etichetta, listaEventi, infoEventi);
        return pannello;
    }
    
    /**
     * Crea il pannello con i dettagli dell'evento selezionato
     */
    private VBox creaPannelloDettagli() {
        VBox pannello = new VBox();
        pannello.setSpacing(8);
        HBox.setHgrow(pannello, Priority.ALWAYS);
        
        Label etichetta = new Label("Dettagli Evento Selezionato");
        etichetta.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        areaDettagli = new TextArea();
        areaDettagli.setPrefHeight(300);
        areaDettagli.setEditable(false);
        areaDettagli.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5px;");
        areaDettagli.setText("Seleziona un evento dalla lista per vedere i dettagli completi");
        
        Label infoDettagli = new Label("I dettagli includono cliente, servizi previsti e note speciali");
        infoDettagli.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        pannello.getChildren().addAll(etichetta, areaDettagli, infoDettagli);
        return pannello;
    }
    
    /**
     * Collega i componenti dell'interfaccia al controller
     */
    private void collegaController() {
        // Imposta la lista eventi nel controller
        controller.setListaEventi(listaEventi);
        controller.setAreaDettagli(areaDettagli);
        controller.setLabelStato(labelStato);
        
        // Collega il pulsante al controller
        bottoneCaricaEventi.setOnAction(e -> controller.handleAggiornaEventi());
        
        // Listener per la selezione degli eventi
        listaEventi.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                controller.handleSelezionaEvento(newSelection);
            }
        });
    }
    
    /**
     * Restituisce il nodo principale della vista
     */
    public Node getView() {
        return layoutPrincipale;
    }
    
    /**
     * Restituisce la lista degli eventi (per il controller)
     */
    public ListView<Evento> getListaEventi() {
        return listaEventi;
    }
    
    /**
     * Restituisce l'area dei dettagli (per il controller)
     */
    public TextArea getAreaDettagli() {
        return areaDettagli;
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