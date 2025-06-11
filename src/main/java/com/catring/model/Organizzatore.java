package com.catring.model;

public class Organizzatore extends Utente {
    private String cognome;
    private String telefono;
    
    public Organizzatore() {}
    
    public Organizzatore(String id, String nome, String email, String password, String cognome, String telefono) {
        super(id, nome, email, password);
        this.cognome = cognome;
        this.telefono = telefono;
    }
    
    // Getters e Setters
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
