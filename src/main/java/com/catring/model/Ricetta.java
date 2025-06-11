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
    private List<Dose> dosi;
    private List<Preparazione> preparazioni;
    private List<Tag> tags;
    private int numeroPorte;
    
    public Ricetta() {
        this.ingredienti = new ArrayList<>();
        this.dosi = new ArrayList<>();
        this.preparazioni = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.numeroPorte = 4; // default
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
    
    /**
     * Aggiunge un ingrediente con la relativa dose
     */
    public void aggiungiIngrediente(Ingrediente ingrediente, Dose dose) {
        this.ingredienti.add(ingrediente);
        this.dosi.add(dose);
    }
    
    /**
     * Rimuove un ingrediente e la sua dose
     */
    public void rimuoviIngrediente(Ingrediente ingrediente) {
        int index = this.ingredienti.indexOf(ingrediente);
        if (index != -1) {
            this.ingredienti.remove(index);
            if (index < this.dosi.size()) {
                this.dosi.remove(index);
            }
        }
    }
    
    /**
     * Restituisce la dose per un ingrediente specifico
     */
    public Dose getDosePerIngrediente(Ingrediente ingrediente) {
        int index = this.ingredienti.indexOf(ingrediente);
        if (index != -1 && index < this.dosi.size()) {
            return this.dosi.get(index);
        }
        return null;
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
    
    public List<Dose> getDosi() { return dosi; }
    public void setDosi(List<Dose> dosi) { this.dosi = dosi; }
    
    public List<Preparazione> getPreparazioni() { return preparazioni; }
    public void setPreparazioni(List<Preparazione> preparazioni) { this.preparazioni = preparazioni; }
    
    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }
    
    public int getNumeroPorte() { return numeroPorte; }
    public void setNumeroPorte(int numeroPorte) { this.numeroPorte = numeroPorte; }
}