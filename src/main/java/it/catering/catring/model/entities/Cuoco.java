// File: src/main/java/it/catering/catring/model/entities/Cuoco.java
package it.catering.catring.model.entities;

public class Cuoco extends User {
    private int anniEsperienza;
    
    // Costruttore di default per Jackson
    public Cuoco() {
        super();
    }
    
    public Cuoco(String username, String password, String nome, String cognome, String email, int anniEsperienza) {
        super(username, password, nome, cognome, email);
        this.anniEsperienza = anniEsperienza;
    }
    
    public int getAnniEsperienza() { return anniEsperienza; }
    public void setAnniEsperienza(int anniEsperienza) { this.anniEsperienza = anniEsperienza; }
    
    @Override
    public String toString() {
        return "Cuoco: " + getNomeCompleto();
    }
}