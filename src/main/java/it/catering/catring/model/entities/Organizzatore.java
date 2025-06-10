// File: src/main/java/it/catering/catring/model/entities/Organizzatore.java
package it.catering.catring.model.entities;

public class Organizzatore extends User {
    
    // Costruttore di default per Jackson
    public Organizzatore() {
        super();
    }
    
    public Organizzatore(String username, String password, String nome, String cognome, String email) {
        super(username, password, nome, cognome, email);
    }
    
    @Override
    public String toString() {
        return "Organizzatore: " + getNomeCompleto();
    }
}