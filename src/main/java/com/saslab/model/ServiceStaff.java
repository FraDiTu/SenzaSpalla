package com.saslab.model;

import java.util.Arrays;
import java.util.List;

/**
 * Classe che rappresenta il Personale di Servizio nel sistema Cat &amp; Ring
 * Il personale di servizio gestisce il servizio durante gli eventi
 */
public class ServiceStaff extends User {
    
    private String serviceRole; // cameriere, sommelier, lavapiatti, etc.
    private int experienceYears;
    private boolean availableForShifts;
    
    // Actions che il personale di servizio può compiere
    private static final List<String> SERVICE_STAFF_ACTIONS = Arrays.asList(
        "SET_AVAILABILITY",
        "VIEW_SHIFTS",
        "VIEW_ASSIGNED_SERVICE",
        "REPORT_SERVICE_ISSUE",
        "CONFIRM_SHIFT_ATTENDANCE",
        "VIEW_EVENT_DETAILS"
    );
    
    public ServiceStaff(String id, String name, String email, String serviceRole) {
        super(id, name, email);
        this.serviceRole = serviceRole;
        this.experienceYears = 0;
        this.availableForShifts = true;
    }
    
    public ServiceStaff(String id, String name, String email, String serviceRole, int experienceYears) {
        super(id, name, email);
        this.serviceRole = serviceRole;
        this.experienceYears = experienceYears;
        this.availableForShifts = true;
    }
    
    // Getters e Setters specifici
    public String getServiceRole() {
        return serviceRole;
    }
    
    public void setServiceRole(String serviceRole) {
        this.serviceRole = serviceRole;
    }
    
    public int getExperienceYears() {
        return experienceYears;
    }
    
    public void setExperienceYears(int experienceYears) {
        if (experienceYears < 0) {
            throw new IllegalArgumentException("Gli anni di esperienza non possono essere negativi");
        }
        this.experienceYears = experienceYears;
    }
    
    public boolean isAvailableForShifts() {
        return availableForShifts;
    }
    
    public void setAvailableForShifts(boolean availableForShifts) {
        this.availableForShifts = availableForShifts;
    }
    
    @Override
    public UserRole getRole() {
        return UserRole.SERVICE_STAFF;
    }
    
    @Override
    public boolean canPerformAction(String action) {
        return SERVICE_STAFF_ACTIONS.contains(action);
    }
    
    @Override
    protected boolean performRoleSpecificValidation() {
        // Il personale di servizio deve essere attivo per poter lavorare
        return isActive();
    }
    
    // Metodi business specifici del personale di servizio
    public boolean canTakeShifts() {
        return isActive() && availableForShifts;
    }
    
    public boolean canHandleComplexService() {
        return experienceYears >= 2 && isActive();
    }
    
    // Pattern Information Expert - il personale sa se può gestire un certo tipo di servizio
    public boolean canHandleServiceType(String serviceType) {
        if (!isActive() || !availableForShifts) {
            return false;
        }
        
        if (serviceRole == null || serviceType == null) {
            return true; // Personale generico
        }
        
        // Logic per determinare compatibilità ruolo-servizio
        switch (serviceRole.toLowerCase()) {
            case "cameriere":
                return serviceType.toLowerCase().contains("sala") ||
                       serviceType.toLowerCase().contains("tavolo") ||
                       serviceType.toLowerCase().contains("servizio");
            case "sommelier":
                return serviceType.toLowerCase().contains("vino") ||
                       serviceType.toLowerCase().contains("bevande") ||
                       serviceType.toLowerCase().contains("cocktail");
            case "lavapiatti":
                return serviceType.toLowerCase().contains("cucina") ||
                       serviceType.toLowerCase().contains("pulizia");
            case "barista":
                return serviceType.toLowerCase().contains("bar") ||
                       serviceType.toLowerCase().contains("caffè") ||
                       serviceType.toLowerCase().contains("bevande");
            default:
                return true; // Altri ruoli possono fare servizio generico
        }
    }
    
    // Metodo per calcolare il livello di esperienza
    public String getExperienceLevel() {
        if (experienceYears < 1) {
            return "Entry Level";
        } else if (experienceYears < 3) {
            return "Junior";
        } else if (experienceYears < 6) {
            return "Intermediate";
        } else if (experienceYears < 10) {
            return "Senior";
        } else {
            return "Expert";
        }
    }
    
    // Metodo per verificare se può supervisionare altri membri del personale
    public boolean canSuperviseOthers() {
        return experienceYears >= 5 && isActive() && 
               (serviceRole.equalsIgnoreCase("capo sala") || experienceYears >= 8);
    }
    
    // Metodo per verificare se può gestire eventi di alto livello
    public boolean canHandleVipEvents() {
        return experienceYears >= 3 && isActive() &&
               !serviceRole.equalsIgnoreCase("lavapiatti");
    }
    
    // Metodo per calcolare la capacità di gestire coperti
    public int getMaxCoverageCapacity() {
        switch (serviceRole.toLowerCase()) {
            case "cameriere":
                return experienceYears < 2 ? 20 : (experienceYears < 5 ? 30 : 40);
            case "sommelier":
                return experienceYears < 2 ? 30 : (experienceYears < 5 ? 50 : 70);
            case "capo sala":
                return 100; // Gestisce l'intera sala
            default:
                return 25; // Capacità standard
        }
    }
    
    @Override
    public String toString() {
        return String.format("ServiceStaff{id='%s', name='%s', role='%s', experience=%d years, level='%s', available=%s}", 
                           id, name, serviceRole, experienceYears, getExperienceLevel(), availableForShifts);
    }
}