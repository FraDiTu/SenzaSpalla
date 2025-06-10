package it.catering.catring.controller;

import it.catering.catring.model.entities.User;
import it.catering.catring.model.factories.UserFactory;
import java.util.*;

public class AuthController {
    private static AuthController instance;
    private Map<String, User> users;
    private User currentUser;
    
    private AuthController() {
        this.users = new HashMap<>();
        initializeDefaultUsers();
    }
    
    public static synchronized AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }
    
    private void initializeDefaultUsers() {
        // Utenti di default per testing
        User chef1 = UserFactory.createUser("chef", "chef1", "password", "Mario", "Rossi", "chef@catering.it", "Cucina Italiana");
        User cuoco1 = UserFactory.createUser("cuoco", "cuoco1", "password", "Giuseppe", "Verdi", "cuoco@catering.it", "5");
        User organizzatore1 = UserFactory.createUser("organizzatore", "org1", "password", "Anna", "Bianchi", "org@catering.it", null);
        
        users.put(chef1.getUsername(), chef1);
        users.put(cuoco1.getUsername(), cuoco1);
        users.put(organizzatore1.getUsername(), organizzatore1);
    }
    
    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean registerUser(String tipo, String username, String password, 
                               String nome, String cognome, String email, String extra) {
        if (users.containsKey(username)) {
            return false; // Username già esistente
        }
        
        try {
            User newUser = UserFactory.createUser(tipo, username, password, nome, cognome, email, extra);
            users.put(username, newUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}