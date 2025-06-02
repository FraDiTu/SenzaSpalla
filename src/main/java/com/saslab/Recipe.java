package com.saslab;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta una ricetta nel sistema Cat &amp; Ring
 * Implementa pattern Information Expert per gestire i dati della ricetta
 */
public class Recipe {
    
    public enum RecipeState {
        DRAFT, PUBLISHED, ARCHIVED
    }
    
    private String id;
    private String name;
    private String description;
    private String ownerId;  // proprietario
    private String authorId; // autore originale
    private int preparationTime; // in minuti
    private RecipeState state;
    private List<Ingredient> ingredients;
    private List<String> tags;
    private List<String> advanceInstructions; // preparazioni in anticipo
    private List<String> lastMinuteInstructions; // preparazioni dell'ultimo minuto
    private int basePortions; // numero di porzioni di base
    
    // Constructor - implementa pattern Creator
    public Recipe(String id, String name, String ownerId, int preparationTime) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.name = Objects.requireNonNull(name, "Il nome non può essere null");
        this.ownerId = Objects.requireNonNull(ownerId, "L'owner ID non può essere null");
        this.preparationTime = preparationTime;
        this.state = RecipeState.DRAFT;
        this.ingredients = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.advanceInstructions = new ArrayList<>();
        this.lastMinuteInstructions = new ArrayList<>();
        this.basePortions = 4; // default
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getOwnerId() { return ownerId; }
    public String getAuthorId() { return authorId; }
    public int getPreparationTime() { return preparationTime; }
    public RecipeState getState() { return state; }
    public List<Ingredient> getIngredients() { return new ArrayList<>(ingredients); }
    public List<String> getTags() { return new ArrayList<>(tags); }
    public List<String> getAdvanceInstructions() { return new ArrayList<>(advanceInstructions); }
    public List<String> getLastMinuteInstructions() { return new ArrayList<>(lastMinuteInstructions); }
    public int getBasePortions() { return basePortions; }
    
    // Setters con validazione
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    
    public void setBasePortions(int basePortions) {
        if (basePortions <= 0) {
            throw new IllegalArgumentException("Le porzioni base devono essere positive");
        }
        this.basePortions = basePortions;
    }
    
    // Business methods - implementa Information Expert
    public void addIngredient(String name, double quantity, String unit) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        ingredients.add(new Ingredient(name, quantity, unit));
    }
    
    public void addTag(String tag) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void addAdvanceInstruction(String instruction) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        advanceInstructions.add(instruction);
    }
    
    public void addLastMinuteInstruction(String instruction) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        lastMinuteInstructions.add(instruction);
    }
    
    public void publish() {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Solo le ricette in bozza possono essere pubblicate");
        }
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("Una ricetta deve avere almeno un ingrediente");
        }
        state = RecipeState.PUBLISHED;
    }
    
    public void unpublish() {
        if (state != RecipeState.PUBLISHED) {
            throw new IllegalStateException("Solo le ricette pubblicate possono essere ritirate");
        }
        state = RecipeState.DRAFT;
    }
    
    public boolean canBeModified(String userId) {
        return ownerId.equals(userId) && state == RecipeState.DRAFT;
    }
    
    public boolean isPublished() {
        return state == RecipeState.PUBLISHED;
    }
    
    public Recipe createCopy(String newId, String newOwnerId) {
        Recipe copy = new Recipe(newId, this.name + " (Copia)", newOwnerId, this.preparationTime);
        copy.setDescription(this.description);
        copy.setAuthorId(this.authorId != null ? this.authorId : this.ownerId);
        copy.setBasePortions(this.basePortions);
        
        // Copia ingredienti
        for (Ingredient ingredient : this.ingredients) {
            copy.addIngredient(ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit());
        }
        
        // Copia tags
        for (String tag : this.tags) {
            copy.addTag(tag);
        }
        
        // Copia istruzioni
        for (String instruction : this.advanceInstructions) {
            copy.addAdvanceInstruction(instruction);
        }
        
        for (String instruction : this.lastMinuteInstructions) {
            copy.addLastMinuteInstruction(instruction);
        }
        
        return copy;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recipe recipe = (Recipe) obj;
        return Objects.equals(id, recipe.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Recipe{id='%s', name='%s', state=%s, owner='%s'}", 
                           id, name, state, ownerId);
    }
    
    // Inner class per gli ingredienti
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