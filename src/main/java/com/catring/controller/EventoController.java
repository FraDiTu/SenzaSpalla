package com.catring.controller;

import com.catring.model.Evento;
import com.catring.singleton.MenuService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

/**
 * PATTERN GRASP: CONTROLLER
 * Controller per la gestione degli eventi con numero persone.
 */
public class EventoController {
    
    private MenuService menuService;
    private ListView<Evento> listaEventi;
    private TextArea areaDettagli;
    private Label labelStato;
    private ObservableList<Evento> eventiList;
    
    public EventoController() {
        this.menuService = MenuService.getInstance();
        this.eventiList = FXCollections.observableArrayList();
    }
    
    public void setListaEventi(ListView<Evento> listaEventi) {
        this.listaEventi = listaEventi;
        this.listaEventi.setItems(eventiList);
    }
    
    public void setAreaDettagli(TextArea areaDettagli) {
        this.areaDettagli = areaDettagli;
    }
    
    public void setLabelStato(Label labelStato) {
        this.labelStato = labelStato;
    }
    
    public void handleAggiornaEventi() {
        try {
            eventiList.clear();
            eventiList.addAll(menuService.consultaEventi());
            
            aggiornaStato("Trovati " + eventiList.size() + " eventi assegnati");
            mostraMessaggio("Lista eventi aggiornata", 
                           "Sono stati caricati " + eventiList.size() + " eventi");
            
        } catch (Exception e) {
            aggiornaStato("Errore nel caricamento degli eventi");
            mostraErrore("Errore", "Impossibile caricare gli eventi: " + e.getMessage());
        }
    }
    
    public void handleSelezionaEvento(Evento evento) {
        if (evento != null && areaDettagli != null) {
            String dettagli = creaDettagliEvento(evento);
            areaDettagli.setText(dettagli);
            aggiornaStato("Evento selezionato: " + evento.getLuogo());
        }
    }
    
    private String creaDettagliEvento(Evento evento) {
        StringBuilder dettagli = new StringBuilder();
        
        dettagli.append("EVENTO: ").append(evento.getLuogo()).append("\n\n");
        dettagli.append("Date: dal ").append(evento.getDataInizio())
               .append(" al ").append(evento.getDataFine()).append("\n");
        dettagli.append("Luogo: ").append(evento.getLuogo()).append("\n");
        dettagli.append("Tipo: ").append(evento.getTipo()).append("\n");
        dettagli.append("Numero persone: ").append(evento.getNumeroPersone()).append("\n");
        dettagli.append("Note: ").append(evento.getNote() != null ? evento.getNote() : "Nessuna nota").append("\n");
        
        if (evento.getCliente() != null) {
            dettagli.append("\nCLIENTE:\n");
            dettagli.append("Nome: ").append(evento.getCliente().getNome()).append("\n");
            dettagli.append("Tipo: ").append(evento.getCliente().getTipo()).append("\n");
            dettagli.append("Contatti: ").append(evento.getCliente().getContatti()).append("\n");
        }
        
        if (evento.getServizi() != null && !evento.getServizi().isEmpty()) {
            dettagli.append("\nSERVIZI PREVISTI:\n");
            for (int i = 0; i < evento.getServizi().size(); i++) {
                var servizio = evento.getServizi().get(i);
                dettagli.append("- ").append(servizio.getTipo())
                       .append(" (").append(servizio.getFasciaOraria()).append(")\n");
            }
        }
        
        return dettagli.toString();
    }
    
    private void aggiornaStato(String messaggio) {
        if (labelStato != null) {
            labelStato.setText(messaggio);
        }
    }
    
    private void mostraMessaggio(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
    
    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
    
    public ObservableList<Evento> getEventiList() {
        return eventiList;
    }
    
    public void caricaDatiIniziali() {
        handleAggiornaEventi();
    }
}