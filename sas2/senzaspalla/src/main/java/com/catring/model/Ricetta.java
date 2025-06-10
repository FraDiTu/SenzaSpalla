package com.catring.model;

import java.util.ArrayList;
import java.util.List;

public class Ricetta {
    private String id;
    private String nome;
    private String descrizione;
    private int tempoPreparazione;
    private String stato;
    private String autore;
    private List<Ingrediente> ingredienti;
    private List<Preparazione> preparazioni;
    private List<Tag> tags;
    
    public Ricetta() {
        this.ingredienti = new ArrayList<>();
        this.preparazioni = new ArrayList<>();
        this.tags = new ArrayList<>();
    }
    
    public Ricetta(String id, String nome, String descrizione, int tempoPreparazione, String stato, String autore) {
        this();
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.tempoPreparazione = tempoPreparazione;
        this.stato = stato;
        this.autore = autore;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    
    public int getTempoPreparazione() { return tempoPreparazione; }
    public void setTempoPreparazione(int tempoPreparazione) { this.tempoPreparazione = tempoPreparazione; }
    
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    
    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }
    
    public List<Ingrediente> getIngredienti() { return ingredienti; }
    public void setIngredienti(List<Ingrediente> ingredienti) { this.ingredienti = ingredienti; }
    
    public List<Preparazione> getPreparazioni() { return preparazioni; }
    public void setPreparazioni(List<Preparazione> preparazioni) { this.preparazioni = preparazioni; }
    
    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }
}