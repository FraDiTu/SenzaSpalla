package com.catring.controller;

import com.catring.model.Evento;
import com.catring.utils.EventoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class EventoController implements Initializable {
    
    @FXML private ListView<Evento> eventiListView;
    @FXML private TextArea dettagliEventoArea;
    
    private EventoService eventoService;
    private ObservableList<Evento> eventiList;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventoService = EventoService.getInstance();
        eventiList = FXCollections.observableArrayList();
        
        eventiListView.setItems(eventiList);
        
        // Listener per selezione evento
        eventiListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostraDettagliEvento(newSelection);
            }
        });
        
        caricaEventi();
    }
    
    @FXML
    private void handleConsultaEvento() {
        caricaEventi();
    }
    
    private void caricaEventi() {
        eventiList.clear();
        eventiList.addAll(eventoService.getEventi());
    }
    
    private void mostraDettagliEvento(Evento evento) {
        StringBuilder dettagli = new StringBuilder();
        dettagli.append("ID: ").append(evento.getId()).append("\n");
        dettagli.append("Luogo: ").append(evento.getLuogo()).append("\n");
        dettagli.append("Data Inizio: ").append(evento.getDataInizio()).append("\n");
        dettagli.append("Data Fine: ").append(evento.getDataFine()).append("\n");
        dettagli.append("Tipo: ").append(evento.getTipo()).append("\n");
        dettagli.append("Note: ").append(evento.getNote()).append("\n");
        
        dettagliEventoArea.setText(dettagli.toString());
    }
}