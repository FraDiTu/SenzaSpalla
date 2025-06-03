package com.saslab;

import com.saslab.model.Dose;
import com.saslab.model.Tag;
import com.saslab.model.Preparation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Classe che rappresenta una ricetta nel sistema Cat & Ring
 * Implementa pattern Information Expert per gestire i dati della ricetta
 * CORRETTA: Risolve problemi di relazioni Recipe-Preparation e gestione corretta di Dose/Tag
 */
public class Recipe {
    
    public enum RecipeState {
        DRAFT("Bozza"),
        PUBLISHED("Pubblicata"),
        ARCHIVED("Archiviata");
        
        private final String displayName;
        
        RecipeState(String displayName) {
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
    private int preparationTime; 
    private RecipeState state;
    private List<Dose> dosi; 
    private List<Tag> tags; 
    private List<String> advanceInstructions; 
    private List<String> lastMinuteInstructions; 
    private int basePortions; 
    private List<String> preparazioniRichieste; // CORRETTO: Lista degli ID delle preparazioni necessarie
    
    // Constructor
    public Recipe(String id, String name, String ownerId, int preparationTime) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.name = Objects.requireNonNull(name, "Il nome non può essere null");
        this.ownerId = Objects.requireNonNull(ownerId, "L'owner ID non può essere null");
        this.preparationTime = preparationTime;
        this.state = RecipeState.DRAFT;
        this.dosi = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.advanceInstructions = new ArrayList<>();
        this.lastMinuteInstructions = new ArrayList<>();
        this.preparazioniRichieste = new ArrayList<>();
        this.basePortions = 4; 
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getOwnerId() { return ownerId; }
    public String getAuthorId() { return authorId; }
    public int getPreparationTime() { return preparationTime; }
    public RecipeState getState() { return state; }
    public List<Dose> getDosi() { return new ArrayList<>(dosi); }
    public List<Tag> getTags() { return new ArrayList<>(tags); }
    public List<String> getAdvanceInstructions() { return new ArrayList<>(advanceInstructions); }
    public List<String> getLastMinuteInstructions() { return new ArrayList<>(lastMinuteInstructions); }
    public int getBasePortions() { return basePortions; }
    public List<String> getPreparazioniRichieste() { return new ArrayList<>(preparazioniRichieste); }
    
    // NUOVO: Metodo per ottenere i nomi dei tag (compatibilità con codice esistente)
    public List<String> getTagNames() {
        return tags.stream()
                .map(Tag::getNome)
                .collect(Collectors.toList());
    }
    
    // NUOVO: Metodo di compatibilità per RecipeListController
    public List<String> getTags() {
        return getTagNames();
    }
    
    // NUOVO: Metodo per ottenere gli ID delle preparazioni utilizzate
    public List<String> getUsedPreparationIds() {
        return new ArrayList<>(preparazioniRichieste);
    }
    
    // Metodi di compatibilità con il codice esistente
    public List<Ingredient> getIngredients() {
        return dosi.stream()
                .map(dose -> new Ingredient(dose.getIngredienteId(), dose.getQuantitativo(), dose.getUnitaMisura()))
                .collect(Collectors.toList());
    }
    
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
    
    // Business methods
    public void addDose(String ingredienteId, double quantitativo, String unitaMisura) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        dosi.add(new Dose(ingredienteId, quantitativo, unitaMisura));
    }
    
    public void addIngredient(String name, double quantity, String unit) {
        addDose(name, quantity, unit);
    }
    
    // CORRETTO: Gestione corretta dei tag come oggetti Tag
    public void addTag(String tagName) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        Tag newTag = new Tag(tagName);
        if (!tags.contains(newTag)) {
            tags.add(newTag);
        }
    }
    
    public void addTag(Tag tag) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    // CORRETTO: Gestione corretta delle preparazioni richieste
    public void addPreparazioneRichiesta(String preparazioneId) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        if (!preparazioniRichieste.contains(preparazioneId)) {
            preparazioniRichieste.add(preparazioneId);
        }
    }
    
    public void removePreparazioneRichiesta(String preparazioneId) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        preparazioniRichieste.remove(preparazioneId);
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
        if (dosi.isEmpty()) {
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
    
    public boolean richiedePreparazioni() {
        return !preparazioniRichieste.isEmpty();
    }
    
    public List<Dose> getDosiScalate(int nuovePortions) {
        return dosi.stream()
                .map(dose -> dose.scalaPorzioni(basePortions, nuovePortions))
                .collect(Collectors.toList());
    }
    
    // CORRETTO: Metodo per verificare se ha un tag specifico
    public boolean hasTag(String tagName) {
        return tags.stream().anyMatch(tag -> tag.getNome().equals(tagName.toLowerCase()));
    }
    
    public Recipe createCopy(String newId, String newOwnerId) {
        Recipe copy = new Recipe(newId, this.name + " (Copia)", newOwnerId, this.preparationTime);
        copy.setDescription(this.description);
        copy.setAuthorId(this.authorId != null ? this.authorId : this.ownerId);
        copy.setBasePortions(this.basePortions);
        
        // Copia dosi
        for (Dose dose : this.dosi) {
            copy.addDose(dose.getIngredienteId(), dose.getQuantitativo(), dose.getUnitaMisura());
        }
        
        // Copia tags
        for (Tag tag : this.tags) {
            copy.addTag(tag);
        }
        
        // Copia preparazioni richieste
        for (String prepId : this.preparazioniRichieste) {
            copy.addPreparazioneRichiesta(prepId);
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