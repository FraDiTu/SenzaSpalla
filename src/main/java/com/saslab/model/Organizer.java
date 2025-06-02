package com.saslab.model;

import java.util.Arrays;
import java.util.List;

/**
 * Classe che rappresenta un Organizzatore nel sistema Cat &amp; Ring
 * L'organizzatore gestisce eventi, personale e turni di lavoro
 */
public class Organizer extends User {
    
    private String department;
    private int managedEventsCount;
    
    // Actions che un organizzatore può compiere
    private static final List<String> ORGANIZER_ACTIONS = Arrays.asList(
        "CREATE_EVENT",
        "EDIT_EVENT", 
        "DELETE_EVENT",
        "ASSIGN_CHEF_TO_EVENT",
        "MANAGE_STAFF",
        "CREATE_SHIFTS",
        "EDIT_SHIFTS",
        "APPROVE_MENU",
        "REQUEST_MENU_CHANGES",
        "SUPERVISE_KITCHEN",
        "MANAGE_SERVICE_STAFF",
        "VIEW_EVENT_PROGRESS",
        "CLOSE_EVENT"
    );
    
    public Organizer(String id, String name, String email) {
        super(id, name, email);
        this.managedEventsCount = 0;
    }
    
    public Organizer(String id, String name, String email, String department) {
        super(id, name, email);
        this.department = department;
        this.managedEventsCount = 0;
    }
    
    // Getters e Setters specifici
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public int getManagedEventsCount() {
        return managedEventsCount;
    }
    
    public void incrementManagedEvents() {
        this.managedEventsCount++;
    }
    
    @Override
    public UserRole getRole() {
        return UserRole.ORGANIZER;
    }
    
    @Override
    public boolean canPerformAction(String action) {
        return ORGANIZER_ACTIONS.contains(action);
    }
    
    @Override
    protected boolean performRoleSpecificValidation() {
        // Gli organizzatori devono essere attivi per poter lavorare
        return isActive();
    }
    
    // Metodi business specifici dell'organizzatore
    public boolean canCreateEvent() {
        return isActive();
    }
    
    public boolean canManageStaff() {
        return isActive();
    }
    
    public boolean canApproveMenu() {
        return isActive();
    }
    
    public boolean canManageShifts() {
        return isActive();
    }
    
    // Pattern Information Expert - l'organizzatore sa se può gestire un evento
    public boolean canHandleEventType(String eventType) {
        if (!isActive()) {
            return false;
        }
        
        // Logic per determinare se l'organizzatore può gestire un tipo specifico di evento
        if (department == null) {
            return true; // Organizzatore generico
        }
        
        switch (department.toLowerCase()) {
            case "corporate":
                return eventType.toLowerCase().contains("aziendale") || 
                       eventType.toLowerCase().contains("corporate") ||
                       eventType.toLowerCase().contains("business");
            case "wedding":
                return eventType.toLowerCase().contains("matrimonio") ||
                       eventType.toLowerCase().contains("wedding");
            case "private":
                return eventType.toLowerCase().contains("privato") ||
                       eventType.toLowerCase().contains("private") ||
                       eventType.toLowerCase().contains("family");
            default:
                return true; // Altri dipartimenti possono gestire qualsiasi evento
        }
    }
    
    // Metodo per calcolare il livello di esperienza
    public String getExperienceLevel() {
        if (managedEventsCount < 5) {
            return "Beginner";
        } else if (managedEventsCount < 20) {
            return "Intermediate";
        } else if (managedEventsCount < 50) {
            return "Advanced";
        } else {
            return "Expert";
        }
    }
    
    // Metodo per verificare se può gestire eventi complessi
    public boolean canHandleComplexEvents() {
        return managedEventsCount >= 10 && isActive();
    }
    
    // Metodo per verificare se può supervisionare altri organizzatori
    public boolean canSuperviseOthers() {
        return managedEventsCount >= 30 && isActive();
    }
    
    @Override
    public String toString() {
        return String.format("Organizer{id='%s', name='%s', department='%s', managedEvents=%d, level='%s'}", 
                           id, name, department, managedEventsCount, getExperienceLevel());
    }
}