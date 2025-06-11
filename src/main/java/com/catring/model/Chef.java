package com.catring.model;

public class Chef extends Utente {
    private String cognome;
    private String specializzazione;
    
    public Chef() {}
    
    public Chef(String id, String nome, String email, String password, String cognome, String specializzazione) {
        super(id, nome, email, password);
        this.cognome = cognome;
        this.specializzazione = specializzazione;
    }
    
    // Getters e Setters
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    
    public String getSpecializzazione() { return specializzazione; }
    public void setSpecializzazione(String specializzazione) { this.specializzazione = specializzazione; }
}