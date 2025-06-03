package com.saslab.model;

import com.saslab.model.Dose;
import com.saslab.model.Tag;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Classe che rappresenta una preparazione nel sistema Cat & Ring
 * Le preparazioni sono ingredienti intermedi utilizzati in altre ricette
 * CORRETTA: Aggiunge tracking di utilizzo nelle ricette e gestione corretta relazioni
 */
public class Preparation {
    
    public enum PreparationState {
        DRAFT("Bozza"),
        PUBLISHED("Pubblicata"),
        ARCHIVED("Archiviata");
        
        private final String displayName;
        
        PreparationState(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private String authorId;
    private int executionTime; 
    private PreparationState state;
    private List<Dose> dosi; 
    private List<Tag> tags; 
    private List<String> instructions;
    private String notes;
    private double yieldQuantity; 
    private String yieldUnit; 
    
    // NUOVO: Tracking di utilizzo nelle ricette
    private Set<String> usedInRecipes; // Set di recipe IDs che utilizzano questa preparazione
    
    public Preparation(String id, String name, String ownerId, int executionTime) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.name = Objects.requireNonNull(name, "Il nome non può essere null");
        this.ownerId = Objects.requireNonNull(ownerId, "L'owner ID non può essere null");
        this.executionTime = executionTime;
        this.state = PreparationState.DRAFT;
        this.dosi = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.usedInRecipes = new HashSet<>(); // NUOVO
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
    public List<Dose> getDosi() { return new ArrayList<>(dosi); }
    public List<Tag> getTags() { return new ArrayList<>(tags); }
    public List<String> getInstructions() { return new ArrayList<>(instructions); }
    public String getNotes() { return notes; }
    public double getYieldQuantity() { return yieldQuantity; }
    public String getYieldUnit() { return yieldUnit; }
    
    // NUOVO: Metodi per gestire utilizzo nelle ricette
    public Set<String> getUsedInRecipes() { return new HashSet<>(usedInRecipes); }
    
    public void addUsageInRecipe(String recipeId) {
        usedInRecipes.add(recipeId);
    }
    
    public void removeUsageInRecipe(String recipeId) {
        usedInRecipes.remove(recipeId);
    }
    
    public boolean isUsedInRecipe(String recipeId) {
        return usedInRecipes.contains(recipeId);
    }
    
    public boolean hasActiveUsages() {
        return !usedInRecipes.isEmpty();
    }
    
    // NUOVO: Metodi di compatibilità per tag
    public List<String> getTagNames() {
        return tags.stream()
                .map(Tag::getNome)
                .collect(Collectors.toList());
    }
    
    // Metodi di compatibilità con il codice esistente
    public List<Ingredient> getIngredients() {
        return dosi.stream()
                .map(dose -> new Ingredient(dose.getIngredienteId(), dose.getQuantitativo(), dose.getUnitaMisura()))
                .collect(Collectors.toList());
    }
    
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
    public void addDose(String ingredienteId, double quantitativo, String unitaMisura) {
        if (state != PreparationState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una preparazione pubblicata");
        }
        dosi.add(new Dose(ingredienteId, quantitativo, unitaMisura));
    }
    
    public void addIngredient(String name, double quantity, String unit) {
        addDose(name, quantity, unit);
    }
    
    public void addTag(String tagName) {
        if (state != PreparationState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una preparazione pubblicata");
        }
        Tag newTag = new Tag(tagName);
        if (!tags.contains(newTag)) {
            tags.add(newTag);
        }
    }
    
    public void addTag(Tag tag) {
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
        if (dosi.isEmpty()) {
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
        
        // NUOVO: Controllo se è utilizzata in ricette attive
        if (hasActiveUsages()) {
            throw new IllegalStateException("Non è possibile ritirare una preparazione utilizzata in ricette");
        }
        
        state = PreparationState.DRAFT;
    }
    
    public boolean canBeModified(String userId) {
        return ownerId.equals(userId) && state == PreparationState.DRAFT;
    }
    
    public boolean isPublished() {
        return state == PreparationState.PUBLISHED;
    }
    
    // NUOVO: Metodo per verificare se può essere eliminata
    public boolean canBeDeleted() {
        return state == PreparationState.DRAFT && !hasActiveUsages();
    }
    
    public List<Dose> getDosiScalate(double fattoreScala) {
        return dosi.stream()
                .map(dose -> new Dose(dose.getIngredienteId(), 
                                    dose.getQuantitativo() * fattoreScala, 
                                    dose.getUnitaMisura()))
                .collect(Collectors.toList());
    }
    
    public boolean hasTag(String tagName) {
        return tags.stream().anyMatch(tag -> tag.getNome().equals(tagName.toLowerCase()));
    }
    
    public Preparation createCopy(String newId, String newOwnerId) {
        Preparation copy = new Preparation(newId, this.name + " (Copia)", newOwnerId, this.executionTime);
        copy.setDescription(this.description);
        copy.setAuthorId(this.authorId != null ? this.authorId : this.ownerId);
        copy.setNotes(this.notes);
        copy.setYield(this.yieldQuantity, this.yieldUnit);
        
        // Copia dosi
        for (Dose dose : this.dosi) {
            copy.addDose(dose.getIngredienteId(), dose.getQuantitativo(), dose.getUnitaMisura());
        }
        
        // Copia tags
        for (Tag tag : this.tags) {
            copy.addTag(tag);
        }
        
        // Copia istruzioni
        for (String instruction : this.instructions) {
            copy.addInstruction(instruction);
        }
        
        // NON copiare usedInRecipes - la copia è una nuova preparazione
        
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
        return String.format("Preparation{id='%s', name='%s', state=%s, owner='%s', usedInRecipes=%d}", 
                           id, name, state, ownerId, usedInRecipes.size());
    }
    
    // Inner class per compatibilità con il codice esistente
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