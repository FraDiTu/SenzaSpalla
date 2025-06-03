package com.saslab.model;

import java.util.Arrays;
import java.util.List;

/**
 * Classe che rappresenta un Cuoco nel sistema Cat &amp; Ring
 * I cuochi eseguono le preparazioni assegnate dallo chef
 */
public class Cook extends User {
    
    private int experienceYears;
    private String specialization;
    private boolean availableForShifts;
    
    // Actions che un cuoco può compiere
    private static final List<String> COOK_ACTIONS = Arrays.asList(
        "CREATE_RECIPE",
        "EDIT_OWN_RECIPE",
        "VIEW_RECIPE_BOOK",
        "VIEW_ASSIGNED_TASKS",
        "MARK_TASK_COMPLETED",
        "REPORT_TASK_ISSUE",
        "SET_AVAILABILITY",
        "VIEW_SHIFTS",
        "CREATE_PREPARATION"
    );
    
    public Cook(String id, String name, String email) {
        super(id, name, email);
        this.experienceYears = 0;
        this.availableForShifts = true;
    }
    
    public Cook(String id, String name, String email, int experienceYears) {
        super(id, name, email);
        this.experienceYears = experienceYears;
        this.availableForShifts = true;
    }
    
    // Getters e Setters specifici
    public int getExperienceYears() {
        return experienceYears;
    }
    
    public void setExperienceYears(int experienceYears) {
        if (experienceYears < 0) {
            throw new IllegalArgumentException("Gli anni di esperienza non possono essere negativi");
        }
        this.experienceYears = experienceYears;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public boolean isAvailableForShifts() {
        return availableForShifts;
    }
    
    public void setAvailableForShifts(boolean availableForShifts) {
        this.availableForShifts = availableForShifts;
    }
    
    @Override
    public UserRole getRole() {
        return UserRole.COOK;
    }
    
    @Override
    public boolean canPerformAction(String action) {
        return COOK_ACTIONS.contains(action);
    }
    
    @Override
    protected boolean performRoleSpecificValidation() {
        // I cuochi devono essere attivi per poter lavorare
        return isActive();
    }
    
    // Metodi business specifici del cuoco
    public boolean canCreateRecipe() {
        return isActive();
    }
    
    public boolean canTakeShifts() {
        return isActive() && availableForShifts;
    }
    
    public boolean canHandleComplexPreparations() {
        return experienceYears >= 3 && isActive();
    }
    
    // Pattern Information Expert - il cuoco sa se può gestire un certo tipo di preparazione
    public boolean canHandlePreparationType(String preparationType) {
        if (!isActive() || !availableForShifts) {
            return false;
        }
        
        if (specialization == null || preparationType == null) {
            return experienceYears >= 1; // Cuochi con almeno 1 anno possono fare preparazioni base
        }
        
        // Logic per determinare se il cuoco può gestire un tipo specifico di preparazione
        if (specialization.toLowerCase().contains(preparationType.toLowerCase())) {
            return true;
        }
        
        // Cuochi molto esperti possono gestire qualsiasi preparazione
        return experienceYears >= 5;
    }
    
    // Metodo per calcolare il livello di competenza
    public String getSkillLevel() {
        if (experienceYears < 1) {
            return "Apprentice";
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
    
    // Metodo per calcolare la capacità di lavoro (ore che può lavorare consecutivamente)
    public int getMaxWorkingHours() {
        if (experienceYears < 1) {
            return 6; // Apprendisti: massimo 6 ore
        } else if (experienceYears < 3) {
            return 8; // Junior: 8 ore standard
        } else {
            return 10; // Esperti: fino a 10 ore se necessario
        }
    }
    
    // Metodo per verificare se può supervisionare altri cuochi
    public boolean canSuperviseOthers() {
        return experienceYears >= 5 && isActive();
    }
    
    // Metodo per verificare se può lavorare in servizio (oltre che in preparazione)
    public boolean canWorkInService() {
        return experienceYears >= 2 && isActive();
    }
    
    @Override
    public String toString() {
        return String.format("Cook{id='%s', name='%s', experience=%d years, skill='%s', specialization='%s', available=%s}", 
                           id, name, experienceYears, getSkillLevel(), specialization, availableForShifts);
    }
}