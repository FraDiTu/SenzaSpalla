package com.saslab.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta una preparazione nel sistema Cat &amp; Ring
 * Le preparazioni sono ingredienti intermedi utilizzati in altre ricette
 */
public class Preparation {
    
    public enum PreparationState {
        DRAFT, PUBLISHED, ARCHIVED
    }
    
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private String authorId;
    private int executionTime; // tempo di esecuzione in minuti
    private PreparationState state;
    private List<Ingredient> ingredients;
    private List<String> tags;
    private List<String> instructions;
    private String notes;
    private double yieldQuantity; // quantità prodotta
    private String yieldUnit; // unità della quantità prodotta
    
    public Preparation(String id, String name, String ownerId, int executionTime) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.name = Objects.requireNonNull(name, "Il nome non può essere null");
        this.ownerId = Objects.requireNonNull(ownerId, "L'owner ID non può essere null");
        this.executionTime = executionTime;
        this.state = PreparationState.DRAFT;
        this.ingredients = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.yieldQuantity = 1.0;
        this.yieldUnit = "porzione";
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getOwnerId() { return ownerId; }
    public String getAuthorId() { return authorId; }
    public int getExecutionTime() { return executionTime; }
    public PreparationState getState() { return state; }
    public List<Ingredient> getIngredients() { return new ArrayList<>(ingredients); }
    public List<String> getTags() { return new ArrayList<>(tags); }
    public List<String> getInstructions() { return new ArrayList<>(instructions); }
    public String getNotes() { return notes; }
    public double getYieldQuantity() { return yieldQuantity; }
    public String getYieldUnit() { return yieldUnit; }
    
    // Setters
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public void setYield(double quantity, String unit) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantità deve essere positiva");
        }
        this.yieldQuantity = quantity;
        this.yieldUnit = Objects.requireNonNull(unit, "L'unità non può essere null");
    }
    
    // Business methods
    public void addIngredient(String name, double quantity, String unit) {
        if (state != PreparationState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una preparazione pubblicata");
        }
        ingredients.add(new Ingredient(name, quantity, unit));
    }
    
    public void addTag(String tag) {
        if (state != PreparationState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una preparazione pubblicata");
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void addInstruction(String instruction) {
        if (state != PreparationState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una preparazione pubblicata");
        }
        instructions.add(instruction);
    }
    
    public void publish() {
        if (state != PreparationState.DRAFT) {
            throw new IllegalStateException("Solo le preparazioni in bozza possono essere pubblicate");
        }
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("Una preparazione deve avere almeno un ingrediente");
        }
        if (instructions.isEmpty()) {
            throw new IllegalStateException("Una preparazione deve avere almeno un'istruzione");
        }
        state = PreparationState.PUBLISHED;
    }
    
    public void unpublish() {
        if (state != PreparationState.PUBLISHED) {
            throw new IllegalStateException("Solo le preparazioni pubblicate possono essere ritirate");
        }
        state = PreparationState.DRAFT;
    }
    
    public boolean canBeModified(String userId) {
        return ownerId.equals(userId) && state == PreparationState.DRAFT;
    }
    
    public boolean isPublished() {
        return state == PreparationState.PUBLISHED;
    }
    
    public Preparation createCopy(String newId, String newOwnerId) {
        Preparation copy = new Preparation(newId, this.name + " (Copia)", newOwnerId, this.executionTime);
        copy.setDescription(this.description);
        copy.setAuthorId(this.authorId != null ? this.authorId : this.ownerId);
        copy.setNotes(this.notes);
        copy.setYield(this.yieldQuantity, this.yieldUnit);
        
        // Copia ingredienti
        for (Ingredient ingredient : this.ingredients) {
            copy.addIngredient(ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit());
        }
        
        // Copia tags
        for (String tag : this.tags) {
            copy.addTag(tag);
        }
        
        // Copia istruzioni
        for (String instruction : this.instructions) {
            copy.addInstruction(instruction);
        }
        
        return copy;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Preparation that = (Preparation) obj;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Preparation{id='%s', name='%s', state=%s, owner='%s'}", 
                           id, name, state, ownerId);
    }
    
    // Inner class per gli ingredienti (riutilizza la stessa logica delle ricette)
    public static class Ingredient {
        private final String name;
        private final double quantity;
        private final String unit;
        
        public Ingredient(String name, double quantity, String unit) {
            this.name = Objects.requireNonNull(name, "Il nome dell'ingrediente non può essere null");
            this.quantity = quantity;
            this.unit = Objects.requireNonNull(unit, "L'unità non può essere null");
            
            if (quantity <= 0) {
                throw new IllegalArgumentException("La quantità deve essere positiva");
            }
        }
        
        public String getName() { return name; }
        public double getQuantity() { return quantity; }
        public String getUnit() { return unit; }
        
        @Override
        public String toString() {
            return String.format("%s: %.2f %s", name, quantity, unit);
        }
    }
}