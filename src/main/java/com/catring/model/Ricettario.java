package com.catring.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ricettario {
    private String id;
    private String versione;
    private LocalDate dataUltimoAggiornamento;
    private List<Ricetta> ricette;
    
    public Ricettario() {
        this.ricette = new ArrayList<>();
    }
    
    public Ricettario(String id, String versione, LocalDate dataUltimoAggiornamento) {
        this();
        this.id = id;
        this.versione = versione;
        this.dataUltimoAggiornamento = dataUltimoAggiornamento;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getVersione() { return versione; }
    public void setVersione(String versione) { this.versione = versione; }
    
    public LocalDate getDataUltimoAggiornamento() { return dataUltimoAggiornamento; }
    public void setDataUltimoAggiornamento(LocalDate dataUltimoAggiornamento) { this.dataUltimoAggiornamento = dataUltimoAggiornamento; }
    
    public List<Ricetta> getRicette() { return ricette; }
    public void setRicette(List<Ricetta> ricette) { this.ricette = ricette; }
}