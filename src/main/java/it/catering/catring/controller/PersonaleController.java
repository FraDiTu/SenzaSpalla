package it.catering.catring.controller;

import it.catering.catring.model.entities.*;
import it.catering.catring.model.factories.UserFactory;
import java.util.List;
import java.util.stream.Collectors;

public class PersonaleController {
    private AuthController authController;
    private User currentUser;
    
    public PersonaleController() {
        this.authController = AuthController.getInstance();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User createPersonale(String tipo, String username, String password, 
                               String nome, String cognome, String email, String extra) {
        if (!(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono creare personale");
        }
        
        return authController.registerUser(tipo, username, password, nome, cognome, email, extra) 
            ? UserFactory.createUser(tipo, username, password, nome, cognome, email, extra)
            : null;
    }
    
    public List<User> getAllPersonale() {
        if (!(currentUser instanceof Organizzatore)) {
            throw new IllegalStateException("Solo gli organizzatori possono visualizzare tutto il personale");
        }
        
        return authController.getAllUsers();
    }
    
    public List<Cuoco> getAllCuochi() {
        return authController.getAllUsers().stream()
            .filter(u -> u instanceof Cuoco)
            .map(u -> (Cuoco) u)
            .collect(Collectors.toList());
    }
    
    public List<PersonaleServizio> getAllPersonaleServizio() {
        return authController.getAllUsers().stream()
            .filter(u -> u instanceof PersonaleServizio)
            .map(u -> (PersonaleServizio) u)
            .collect(Collectors.toList());
    }
    
    public List<Chef> getAllChef() {
        return authController.getAllUsers().stream()
            .filter(u -> u instanceof Chef)
            .map(u -> (Chef) u)
            .collect(Collectors.toList());
    }
    
    public List<Organizzatore> getAllOrganizzatori() {
        return authController.getAllUsers().stream()
            .filter(u -> u instanceof Organizzatore)
            .map(u -> (Organizzatore) u)
            .collect(Collectors.toList());
    }
}