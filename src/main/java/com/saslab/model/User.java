package com.saslab.model;

import java.util.Objects;

/**
 * Classe base astratta per tutti gli utenti del sistema Cat &amp; Ring
 * Implementa pattern Template Method per il comportamento comune degli utenti
 */
public abstract class User {
    
    protected String id;
    protected String name;
    protected String email;
    protected String password;
    protected boolean active;
    
    public User(String id, String name, String email) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.name = Objects.requireNonNull(name, "Il nome non può essere null");
        this.email = Objects.requireNonNull(email, "L'email non può essere null");
        this.active = true;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public boolean isActive() { return active; }
    
    // Setters
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Il nome non può essere null");
    }
    
    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "L'email non può essere null");
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void activate() {
        this.active = true;
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    // Template method - definisce il flusso di autenticazione
    public final boolean authenticate(String email, String password) {
        if (!isActive()) {
            return false;
        }
        
        if (!this.email.equals(email)) {
            return false;
        }
        
        return validatePassword(password) && performRoleSpecificValidation();
    }
    
    // Hook methods da implementare nelle sottoclassi
    protected boolean validatePassword(String password) {
        return this.password != null && this.password.equals(password);
    }
    
    // Hook method per validazioni specifiche del ruolo
    protected abstract boolean performRoleSpecificValidation();
    
    // Abstract method - ogni tipo di utente deve definire i suoi permessi
    public abstract UserRole getRole();
    
    // Abstract method - ogni tipo di utente può avere azioni specifiche
    public abstract boolean canPerformAction(String action);
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("%s{id='%s', name='%s', email='%s', active=%s}", 
                           getClass().getSimpleName(), id, name, email, active);
    }
    
    // Enum per i ruoli utente
    public enum UserRole {
        ORGANIZER("Organizzatore"),
        CHEF("Chef"),
        COOK("Cuoco"),
        SERVICE_STAFF("Personale di Servizio");
        
        private final String displayName;
        
        UserRole(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}