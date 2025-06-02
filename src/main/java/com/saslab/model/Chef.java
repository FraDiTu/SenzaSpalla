package com.saslab.model;

import java.util.Arrays;
import java.util.List;

/**
 * Classe che rappresenta uno Chef nel sistema Cat &amp; Ring
 * Lo chef può creare ricette, gestire menù e assegnare compiti ai cuochi
 */
public class Chef extends User {
    
    private String specialization;
    private int experienceYears;
    
    // Actions che uno chef può compiere
    private static final List<String> CHEF_ACTIONS = Arrays.asList(
        "CREATE_RECIPE",
        "EDIT_OWN_RECIPE", 
        "PUBLISH_RECIPE",
        "CREATE_MENU",
        "EDIT_MENU",
        "ASSIGN_TASKS",
        "SUPERVISE_KITCHEN",
        "APPROVE_MENU",
        "VIEW_RECIPE_BOOK",
        "CREATE_PREPARATION"
    );
    
    public Chef(String id, String name, String email) {
        super(id, name, email);
        this.experienceYears = 0;
    }
    
    public Chef(String id, String name, String email, String specialization, int experienceYears) {
        super(id, name, email);
        this.specialization = specialization;
        this.experienceYears = experienceYears;
    }
    
    // Getters e Setters specifici
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
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
    
    @Override
    public UserRole getRole() {
        return UserRole.CHEF;
    }
    
    @Override
    public boolean canPerformAction(String action) {
        return CHEF_ACTIONS.contains(action);
    }
    
    @Override
    protected boolean performRoleSpecificValidation() {
        // Gli chef devono essere attivi per poter lavorare
        return isActive();
    }
    
    // Metodi business specifici dello chef
    public boolean canApproveMenu() {
        return isActive();
    }
    
    public boolean canAssignTasks() {
        return isActive();
    }
    
    public boolean canCreateRecipe() {
        return isActive();
    }
    
    public boolean canSuperviseKitchen() {
        return isActive();
    }
    
    // Pattern Information Expert - lo chef sa se può gestire un certo tipo di cucina
    public boolean canHandleCuisineType(String cuisineType) {
        if (specialization == null || cuisineType == null) {
            return true; // Chef generico
        }
        
        // Logic per determinare se lo chef può gestire un tipo specifico di cucina
        return specialization.toLowerCase().contains(cuisineType.toLowerCase()) ||
               specialization.equalsIgnoreCase("generale") ||
               experienceYears >= 10; // Chef molto esperti possono gestire qualsiasi cucina
    }
    
    // Metodo per calcolare il livello di competenza
    public String getCompetenceLevel() {
        if (experienceYears < 2) {
            return "Junior";
        } else if (experienceYears < 5) {
            return "Intermediate";
        } else if (experienceYears < 10) {
            return "Senior";
        } else {
            return "Expert";
        }
    }
    
    @Override
    public String toString() {
        return String.format("Chef{id='%s', name='%s', specialization='%s', experience=%d years, level='%s'}", 
                           id, name, specialization, experienceYears, getCompetenceLevel());
    }
}