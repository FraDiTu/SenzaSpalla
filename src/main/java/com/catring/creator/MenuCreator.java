package com.catring.creator;

import com.catring.model.*;
import com.catring.information_expert.IdGenerator;

public class MenuCreator {
    
    private IdGenerator idGenerator;
    
    public MenuCreator() {
        this.idGenerator = new IdGenerator();
    }
    
    public Menu creaMenu(String nome, String descrizione, String note) {
        String id = idGenerator.generateMenuId();
        return new Menu(id, nome, descrizione, note);
    }
    
    public SezioniMenu creaSezione(String titolo, int ordine) {
        String id = idGenerator.generateSezioneId();
        return new SezioniMenu(id, titolo, ordine);
    }
    
    public VoceMenu creaVoceMenu(Ricetta ricetta) {
        String id = idGenerator.generateVoceId();
        VoceMenu voce = new VoceMenu(id, ricetta.getNome(), ricetta.getId(), "");
        voce.setRicetta(ricetta);
        return voce;
    }
    
    public Ricetta creaRicetta(String nome, String descrizione, int tempoPreparazione, String stato, String autore) {
        String id = idGenerator.generateRicettaId();
        return new Ricetta(id, nome, descrizione, tempoPreparazione, stato, autore);
    }
    
    public Cliente creaCliente(String nome, String tipo, String contatti) {
        String id = idGenerator.generateClienteId();
        return new Cliente(id, nome, tipo, contatti);
    }
    
    public Evento creaEvento(java.time.LocalDate dataInizio, java.time.LocalDate dataFine, 
                            String luogo, String tipo, String note) {
        String id = idGenerator.generateEventoId();
        return new Evento(id, dataInizio, dataFine, luogo, tipo, note);
    }
}