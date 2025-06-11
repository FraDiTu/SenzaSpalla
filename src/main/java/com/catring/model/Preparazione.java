package com.catring.model;

public class Preparazione {
    private String id;
    private String nome;
    private String descrizione;
    private int tempoEsecuzione;
    private String note;
    private String stato;
    private String autore;
    
    public Preparazione() {}
    
    public Preparazione(String id, String nome, String descrizione, int tempoEsecuzione, String note, String stato, String autore) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.tempoEsecuzione = tempoEsecuzione;
        this.note = note;
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
    
    public int getTempoEsecuzione() { return tempoEsecuzione; }
    public void setTempoEsecuzione(int tempoEsecuzione) { this.tempoEsecuzione = tempoEsecuzione; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    
    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }
}