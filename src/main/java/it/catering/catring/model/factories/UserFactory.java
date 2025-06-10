package it.catering.catring.model.factories;

import it.catering.catring.model.entities.*;

public abstract class UserFactory {
    
    public static User createUser(String tipo, String username, String password, 
                                String nome, String cognome, String email, String extra) {
        return switch (tipo.toLowerCase()) {
            case "chef" -> new Chef(username, password, nome, cognome, email, extra);
            case "cuoco" -> new Cuoco(username, password, nome, cognome, email, 
                                    extra != null ? Integer.parseInt(extra) : 0);
            case "organizzatore" -> new Organizzatore(username, password, nome, cognome, email);
            default -> throw new IllegalArgumentException("Tipo utente non valido: " + tipo);
        };
    }
}