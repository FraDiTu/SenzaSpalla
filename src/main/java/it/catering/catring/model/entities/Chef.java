package it.catering.catring.model.entities;

public class Chef extends User {
    private String specializzazione;
    
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