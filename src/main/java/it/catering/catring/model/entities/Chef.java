// File: src/main/java/it/catering/catring/model/entities/Chef.java
package it.catering.catring.model.entities;

public class Chef extends User {
    private String specializzazione;
    
    // Costruttore di default per Jackson
    public Chef() {
        super();
    }
    
    public Chef(String username, String password, String nome, String cognome, String email, String specializzazione) {
        super(username, password, nome, cognome, email);
        this.specializzazione = specializzazione;
    }
    
    public String getSpecializzazione() { return specializzazione; }
    public void setSpecializzazione(String specializzazione) { this.specializzazione = specializzazione; }
    
    @Override
    public String toString() {
        return "Chef: " + getNomeCompleto();
    }
}