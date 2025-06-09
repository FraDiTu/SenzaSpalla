package it.catering.catring.model.entities;

public class Organizzatore extends User {
    
    public Organizzatore(String username, String password, String nome, String cognome, String email) {
        super(username, password, nome, cognome, email);
    }
    
    @Override
    public String toString() {
        return "Organizzatore: " + getNomeCompleto();
    }
}
