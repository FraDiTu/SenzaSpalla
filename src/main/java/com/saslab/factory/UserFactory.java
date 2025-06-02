package com.saslab.factory;

import com.saslab.model.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory per la creazione degli utenti del sistema Cat &amp; Ring
 * Implementa pattern Singleton + Abstract Factory
 * Implementa pattern Creator per centralizzare la creazione degli utenti
 */
public class UserFactory {
    
    private static UserFactory instance;
    private final AtomicInteger userIdCounter;
    
    private UserFactory() {
        this.userIdCounter = new AtomicInteger(1000);
    }
    
    public static synchronized UserFactory getInstance() {
        if (instance == null) {
            instance = new UserFactory();
        }
        return instance;
    }
    
    /**
     * Crea un utente del tipo specificato
     * @param userType tipo di utente (chef, organizer, cook, service_staff)
     * @param name nome dell'utente
     * @param email email dell'utente
     * @return istanza dell'utente creato
     */
    public User createUser(String userType, String name, String email) {
        String id = generateUserId(userType);
        
        switch (userType.toLowerCase()) {
            case "chef":
                return new Chef(id, name, email);
            case "organizer":
                return new Organizer(id, name, email);
            case "cook":
                return new Cook(id, name, email);
            case "service_staff":
            case "service":
                return new ServiceStaff(id, name, email, "Generico");
            default:
                throw new IllegalArgumentException("Tipo di utente non supportato: " + userType);
        }
    }
    
    /**
     * Crea uno chef con specializzazione
     */
    public Chef createChef(String name, String email, String specialization, int experienceYears) {
        String id = generateUserId("chef");
        return new Chef(id, name, email, specialization, experienceYears);
    }
    
    /**
     * Crea un organizzatore con dipartimento
     */
    public Organizer createOrganizer(String name, String email, String department) {
        String id = generateUserId("organizer");
        return new Organizer(id, name, email, department);
    }
    
    /**
     * Crea un cuoco con esperienza
     */
    public Cook createCook(String name, String email, int experienceYears) {
        String id = generateUserId("cook");
        return new Cook(id, name, email, experienceYears);
    }
    
    /**
     * Crea personale di servizio con ruolo specifico
     */
    public ServiceStaff createServiceStaff(String name, String email, String serviceRole, int experienceYears) {
        String id = generateUserId("service");
        return new ServiceStaff(id, name, email, serviceRole, experienceYears);
    }
    
    /**
     * Genera un ID univoco per l'utente
     */
    private String generateUserId(String userType) {
        String prefix = getUserTypePrefix(userType);
        int id = userIdCounter.incrementAndGet();
        return prefix + String.format("%04d", id);
    }
    
    /**
     * Ottiene il prefisso per il tipo di utente
     */
    private String getUserTypePrefix(String userType) {
        switch (userType.toLowerCase()) {
            case "chef":
                return "CHF";
            case "organizer":
                return "ORG";
            case "cook":
                return "COK";
            case "service_staff":
            case "service":
                return "SRV";
            default:
                return "USR";
        }
    }
    
    /**
     * Crea un utente da dati esistenti (per il caricamento da database)
     */
    public User createUserFromData(String id, String userType, String name, String email) {
        switch (userType.toLowerCase()) {
            case "chef":
                return new Chef(id, name, email);
            case "organizer":
                return new Organizer(id, name, email);
            case "cook":
                return new Cook(id, name, email);
            case "service_staff":
            case "service":
                return new ServiceStaff(id, name, email, "Generico");
            default:
                throw new IllegalArgumentException("Tipo di utente non supportato: " + userType);
        }
    }
    
    /**
     * Valida i dati dell'utente prima della creazione
     */
    public boolean validateUserData(String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Validazione email semplice
        return email.contains("@") && email.contains(".");
    }
    
    /**
     * Determina il tipo di utente da una stringa di ruolo
     */
    public String determineUserType(User.UserRole role) {
        switch (role) {
            case CHEF:
                return "chef";
            case ORGANIZER:
                return "organizer";
            case COOK:
                return "cook";
            case SERVICE_STAFF:
                return "service_staff";
            default:
                throw new IllegalArgumentException("Ruolo non supportato: " + role);
        }
    }
    
    /**
     * Reset del counter (per test)
     */
    public void resetCounter() {
        userIdCounter.set(1000);
    }
}