package com.catring.controller;

import com.catring.model.Evento;
import com.catring.singleton.MenuService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

/**
 * PATTERN GRASP: CONTROLLER
 * Controller per la gestione degli eventi.
 * Coordina le interazioni tra EventiView e il modello degli eventi.
 */
public class EventoController {
    
    // Servizio per accedere ai dati (Pattern Singleton)
    private MenuService menuService;
    
    // Componenti dell'interfaccia collegati dalla view
    private ListView<Evento> listaEventi;
    private TextArea areaDettagli;
    private Label labelStato;
    
    // Lista observable per gli eventi
    private ObservableList<Evento> eventiList;
    
    public EventoController() {
        this.menuService = MenuService.getInstance();
        this.eventiList = FXCollections.observableArrayList();
    }
    
    /**
     * Imposta i componenti dell'interfaccia dalla view
     */
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
    
    /**
     * Gestisce l'aggiornamento della lista eventi
     */
    public void handleAggiornaEventi() {
        try {
            // Carica gli eventi dal servizio
            eventiList.clear();
            eventiList.addAll(menuService.consultaEventi());
            
            // Aggiorna il messaggio di stato
            aggiornaStato("Trovati " + eventiList.size() + " eventi assegnati");
            
            // Mostra messaggio di successo
            mostraMessaggio("Lista eventi aggiornata", 
                           "Sono stati caricati " + eventiList.size() + " eventi");
            
        } catch (Exception e) {
            aggiornaStato("Errore nel caricamento degli eventi");
            mostraErrore("Errore", "Impossibile caricare gli eventi: " + e.getMessage());
        }
    }
    
    /**
     * Gestisce la selezione di un evento
     */
    public void handleSelezionaEvento(Evento evento) {
        if (evento != null && areaDettagli != null) {
            String dettagli = creaDettagliEvento(evento);
            areaDettagli.setText(dettagli);
            aggiornaStato("Evento selezionato: " + evento.getLuogo());
        }
    }
    
    /**
     * Crea il testo dei dettagli per un evento
     */
    private String creaDettagliEvento(Evento evento) {
        StringBuilder dettagli = new StringBuilder();
        
        dettagli.append("EVENTO: ").append(evento.getLuogo()).append("\n\n");
        dettagli.append("Date: dal ").append(evento.getDataInizio())
               .append(" al ").append(evento.getDataFine()).append("\n");
        dettagli.append("Luogo: ").append(evento.getLuogo()).append("\n");
        dettagli.append("Tipo: ").append(evento.getTipo()).append("\n\n");
        dettagli.append("Note: ").append(evento.getNote() != null ? evento.getNote() : "Nessuna nota").append("\n");
        
        // Aggiunge informazioni del cliente se disponibili
        if (evento.getCliente() != null) {
            dettagli.append("\nCLIENTE:\n");
            dettagli.append("Nome: ").append(evento.getCliente().getNome()).append("\n");
            dettagli.append("Tipo: ").append(evento.getCliente().getTipo()).append("\n");
            dettagli.append("Contatti: ").append(evento.getCliente().getContatti()).append("\n");
        }
        
        // Aggiunge informazioni sui servizi se disponibili
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
    
    /**
     * Aggiorna il messaggio di stato
     */
    private void aggiornaStato(String messaggio) {
        if (labelStato != null) {
            labelStato.setText(messaggio);
        }
    }
    
    /**
     * Mostra un messaggio informativo
     */
    private void mostraMessaggio(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
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
     * Restituisce la lista degli eventi (per test o altre funzionalità)
     */
    public ObservableList<Evento> getEventiList() {
        return eventiList;
    }
    
    /**
     * Carica i dati iniziali
     */
    public void caricaDatiIniziali() {
        handleAggiornaEventi();
    }
}