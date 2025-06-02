package com.saslab.controller;

import com.saslab.factory.UserFactory;
import com.saslab.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Controller per la gestione degli utenti del sistema
 * Implementa pattern Controller (GRASP) per coordinare le operazioni sugli utenti
 */
public class UserController {
    
    private final Map<String, User> users;
    private final UserFactory userFactory;
    
    public UserController() {
        this.users = new ConcurrentHashMap<>();
        this.userFactory = UserFactory.getInstance();
        initializeDefaultUsers();
    }

    /**
     * Inizializza alcuni utenti di default per il sistema
     */
    private void initializeDefaultUsers() {
        // Crea un organizzatore di default
        Organizer defaultOrganizer = userFactory.createOrganizer("Admin", "admin@catring.com", "Amministrazione");
        defaultOrganizer.setPassword("admin123");
        users.put(defaultOrganizer.getId(), defaultOrganizer);
        
        // Crea uno chef di default
        Chef defaultChef = userFactory.createChef("Mario Rossi", "mario@catring.com", "Cucina Italiana", 10);
        defaultChef.setPassword("chef123");
        users.put(defaultChef.getId(), defaultChef);
        
        // Crea un cuoco di default
        Cook defaultCook = userFactory.createCook("Giuseppe Verdi", "giuseppe@catring.com", 5);
        defaultCook.setPassword("cook123");
        users.put(defaultCook.getId(), defaultCook);
        
        // Crea personale di servizio di default
        ServiceStaff defaultService = userFactory.createServiceStaff("Anna Bianchi", "anna@catring.com", "Cameriere", 3);
        defaultService.setPassword("service123");
        users.put(defaultService.getId(), defaultService);
    }
    
    /**
     * Crea un nuovo utente nel sistema
     */
    public String createUser(String userType, String name, String email, String password) {
        validateUserCreationParameters(name, email, password);
        
        if (emailExists(email)) {
            throw new IllegalArgumentException("Email già esistente nel sistema");
        }
        
        User user = userFactory.createUser(userType, name, email);
        user.setPassword(password);
        
        users.put(user.getId(), user);
        return user.getId();
    }
    
    /**
     * Crea uno chef con informazioni specifiche
     */
    public String createChef(String name, String email, String password, String specialization, int experienceYears) {
        validateUserCreationParameters(name, email, password);
        
        if (emailExists(email)) {
            throw new IllegalArgumentException("Email già esistente nel sistema");
        }
        
        Chef chef = userFactory.createChef(name, email, specialization, experienceYears);
        chef.setPassword(password);
        
        users.put(chef.getId(), chef);
        return chef.getId();
    }
    
    /**
     * Crea un cuoco con informazioni specifiche
     */
    public String createCook(String name, String email, String password, int experienceYears) {
        validateUserCreationParameters(name, email, password);
        
        if (emailExists(email)) {
            throw new IllegalArgumentException("Email già esistente nel sistema");
        }
        
        Cook cook = userFactory.createCook(name, email, experienceYears);
        cook.setPassword(password);
        
        users.put(cook.getId(), cook);
        return cook.getId();
    }
    
    /**
     * Crea un organizzatore con informazioni specifiche
     */
    public String createOrganizer(String name, String email, String password, String department) {
        validateUserCreationParameters(name, email, password);
        
        if (emailExists(email)) {
            throw new IllegalArgumentException("Email già esistente nel sistema");
        }
        
        Organizer organizer = userFactory.createOrganizer(name, email, department);
        organizer.setPassword(password);
        
        users.put(organizer.getId(), organizer);
        return organizer.getId();
    }
    
    /**
     * Crea personale di servizio con informazioni specifiche
     */
    public String createServiceStaff(String name, String email, String password, String serviceRole, int experienceYears) {
        validateUserCreationParameters(name, email, password);
        
        if (emailExists(email)) {
            throw new IllegalArgumentException("Email già esistente nel sistema");
        }
        
        ServiceStaff staff = userFactory.createServiceStaff(name, email, serviceRole, experienceYears);
        staff.setPassword(password);
        
        users.put(staff.getId(), staff);
        return staff.getId();
    }
    
    /**
     * Autentica un utente nel sistema
     */
    public User authenticateUser(String email, String password) {
        User user = users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
        
        if (user != null && user.authenticate(email, password)) {
            return user;
        }
        
        return null;
    }
    
    /**
     * Ottiene un utente per ID
     */
    public User getUser(String userId) {
        return users.get(userId);
    }
    
    /**
     * Ottiene un utente per email
     */
    public User getUserByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Ottiene tutti gli utenti del sistema
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    
    /**
     * Ottiene utenti per ruolo
     */
    public List<User> getUsersByRole(User.UserRole role) {
        return users.values().stream()
                .filter(user -> user.getRole() == role)
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene tutti gli chef
     */
    public List<Chef> getAllChefs() {
        return users.values().stream()
                .filter(user -> user instanceof Chef)
                .map(user -> (Chef) user)
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene tutti i cuochi
     */
    public List<Cook> getAllCooks() {
        return users.values().stream()
                .filter(user -> user instanceof Cook)
                .map(user -> (Cook) user)
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene tutti gli organizzatori
     */
    public List<Organizer> getAllOrganizers() {
        return users.values().stream()
                .filter(user -> user instanceof Organizer)
                .map(user -> (Organizer) user)
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene tutto il personale di servizio
     */
    public List<ServiceStaff> getAllServiceStaff() {
        return users.values().stream()
                .filter(user -> user instanceof ServiceStaff)
                .map(user -> (ServiceStaff) user)
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene utenti attivi
     */
    public List<User> getActiveUsers() {
        return users.values().stream()
                .filter(User::isActive)
                .collect(Collectors.toList());
    }
    
    /**
     * Aggiorna le informazioni di un utente
     */
    public void updateUser(String userId, String name, String email) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }
        
        if (!user.getEmail().equals(email) && emailExists(email)) {
            throw new IllegalArgumentException("Email già esistente nel sistema");
        }
        
        user.setName(name);
        user.setEmail(email);
    }
    
    /**
     * Cambia la password di un utente
     */
    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }
        
        if (!user.authenticate(user.getEmail(), oldPassword)) {
            throw new SecurityException("Password attuale non corretta");
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("La nuova password deve essere di almeno 6 caratteri");
        }
        
        user.setPassword(newPassword);
    }
    
    /**
     * Attiva un utente
     */
    public void activateUser(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }
        
        user.activate();
    }
    
    /**
     * Disattiva un utente
     */
    public void deactivateUser(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }
        
        user.deactivate();
    }
    
    /**
     * Elimina un utente dal sistema
     */
    public boolean deleteUser(String userId) {
        User user = users.get(userId);
        if (user == null) {
            return false;
        }
        
        // Verifica che l'utente non sia associato ad eventi in corso
        // Questa verifica dovrebbe essere implementata con controlli sugli eventi
        
        users.remove(userId);
        return true;
    }
    
    /**
     * Cerca utenti per nome
     */
    public List<User> searchUsersByName(String namePattern) {
        String pattern = namePattern.toLowerCase();
        return users.values().stream()
                .filter(user -> user.getName().toLowerCase().contains(pattern))
                .collect(Collectors.toList());
    }
    
    /**
     * Verifica se un utente può eseguire un'azione specifica
     */
    public boolean canUserPerformAction(String userId, String action) {
        User user = users.get(userId);
        return user != null && user.canPerformAction(action);
    }
    
    /**
     * Ottiene statistiche sugli utenti
     */
    public Map<String, Integer> getUserStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        long totalUsers = users.size();
        long activeUsers = users.values().stream().filter(User::isActive).count();
        long chefs = users.values().stream().filter(u -> u instanceof Chef).count();
        long cooks = users.values().stream().filter(u -> u instanceof Cook).count();
        long organizers = users.values().stream().filter(u -> u instanceof Organizer).count();
        long serviceStaff = users.values().stream().filter(u -> u instanceof ServiceStaff).count();
        
        stats.put("total", (int) totalUsers);
        stats.put("active", (int) activeUsers);
        stats.put("inactive", (int) (totalUsers - activeUsers));
        stats.put("chefs", (int) chefs);
        stats.put("cooks", (int) cooks);
        stats.put("organizers", (int) organizers);
        stats.put("serviceStaff", (int) serviceStaff);
        
        return stats;
    }
    
    /**
     * Ottiene cuochi disponibili per i turni
     */
    public List<Cook> getAvailableCooks() {
        return users.values().stream()
                .filter(user -> user instanceof Cook)
                .map(user -> (Cook) user)
                .filter(Cook::canTakeShifts)
                .collect(Collectors.toList());
    }
    
    /**
     * Ottiene personale di servizio disponibile per i turni
     */
    public List<ServiceStaff> getAvailableServiceStaff() {
        return users.values().stream()
                .filter(user -> user instanceof ServiceStaff)
                .map(user -> (ServiceStaff) user)
                .filter(ServiceStaff::canTakeShifts)
                .collect(Collectors.toList());
    }
    
    // Metodi di utilità privati
    private void validateUserCreationParameters(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome è obbligatorio");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email è obbligatoria");
        }
        if (!userFactory.validateUserData(name, email)) {
            throw new IllegalArgumentException("Dati utente non validi");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La password deve essere di almeno 6 caratteri");
        }
    }
    
    private boolean emailExists(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
    
    /**
     * Ottiene il numero totale di utenti
     */
    public int getTotalUserCount() {
        return users.size();
    }
    
    /**
     * Reset del sistema (per test)
     */
    public void resetSystem() {
        users.clear();
        userFactory.resetCounter();
        initializeDefaultUsers();
    }
}