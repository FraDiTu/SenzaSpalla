package com.catring.creator;

import com.catring.model.*;
import com.catring.information_expert.IdGenerator;

/**
 * PATTERN GRASP: CREATOR
 * Questa classe è responsabile della creazione di oggetti del dominio menu.
 * Implementa il pattern Creator decidendo chi deve creare le istanze.
 */
public class MenuCreator {
    
    private IdGenerator idGenerator;
    
    public MenuCreator() {
        this.idGenerator = new IdGenerator();
    }
    
    /**
     * Crea un nuovo Menu (Creator pattern)
     * MenuCreator è responsabile perché gestisce la logica di creazione
     */
    public Menu creaMenu(String nome, String descrizione, String note) {
        String id = idGenerator.generateMenuId();
        return new Menu(id, nome, descrizione, note);
    }
    
    /**
     * Crea una nuova Sezione del Menu (Creator pattern)
     * MenuCreator è responsabile perché le sezioni fanno parte del menu
     */
    public SezioniMenu creaSezione(String titolo, int ordine) {
        String id = idGenerator.generateSezioneId();
        return new SezioniMenu(id, titolo, ordine);
    }
    
    /**
     * Crea una nuova Voce del Menu (Creator pattern)
     * MenuCreator è responsabile perché le voci fanno parte del menu
     */
    public VoceMenu creaVoceMenu(Ricetta ricetta) {
        String id = idGenerator.generateVoceId();
        VoceMenu voce = new VoceMenu(id, ricetta.getNome(), ricetta.getId(), "");
        voce.setRicetta(ricetta);
        return voce;
    }
    
    /**
     * Crea una nuova Ricetta (Creator pattern)
     * MenuCreator può creare ricette perché sono parte del sistema menu
     */
    public Ricetta creaRicetta(String nome, String descrizione, int tempoPreparazione, String stato, String autore) {
        String id = idGenerator.generateRicettaId();
        return new Ricetta(id, nome, descrizione, tempoPreparazione, stato, autore);
    }
    
    /**
     * Crea un nuovo Cliente (Creator pattern)
     */
    public Cliente creaCliente(String nome, String tipo, String contatti) {
        String id = idGenerator.generateClienteId();
        return new Cliente(id, nome, tipo, contatti);
    }
    
    /**
     * Crea un nuovo Evento (Creator pattern)
     */
    public Evento creaEvento(java.time.LocalDate dataInizio, java.time.LocalDate dataFine, 
                            String luogo, String tipo, String note) {
        String id = idGenerator.generateEventoId();
        return new Evento(id, dataInizio, dataFine, luogo, tipo, note);
    }
}