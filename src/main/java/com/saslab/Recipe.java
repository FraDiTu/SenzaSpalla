package com.saslab;

import com.saslab.model.Dose;
import com.saslab.model.Tag;
import com.saslab.model.Preparation;
import com.saslab.model.Instruction;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Classe che rappresenta una ricetta nel sistema Cat & Ring
 * CORRETTA: Gestione completa di preparazioni intermedie e istruzioni strutturate
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
    private List<Instruction> advanceInstructions; // CORRETTO: Oggetti Instruction strutturati
    private List<Instruction> lastMinuteInstructions; // CORRETTO: Oggetti Instruction strutturati
    private int basePortions; 
    private List<PreparationUsage> preparazioniRichieste; // CORRETTO: Classe dedicata con attributi
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    
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
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
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
    public List<Instruction> getAdvanceInstructions() { return new ArrayList<>(advanceInstructions); }
    public List<Instruction> getLastMinuteInstructions() { return new ArrayList<>(lastMinuteInstructions); }
    public int getBasePortions() { return basePortions; }
    public List<PreparationUsage> getPreparazioniRichieste() { return new ArrayList<>(preparazioniRichieste); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastModified() { return lastModified; }
    
    // NUOVO: Metodi per compatibilità con UI esistente
    public List<String> getTagNames() {
        return tags.stream().map(Tag::getNome).collect(Collectors.toList());
    }
    
    public List<String> getTags() {
        return getTagNames();
    }
    
    public List<String> getAdvanceInstructionTexts() {
        return advanceInstructions.stream().map(Instruction::getText).collect(Collectors.toList());
    }
    
    public List<String> getLastMinuteInstructionTexts() {
        return lastMinuteInstructions.stream().map(Instruction::getText).collect(Collectors.toList());
    }
    
    // Setters con validazione
    public void setDescription(String description) {
        this.description = description;
        updateLastModified();
    }
    
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
        updateLastModified();
    }
    
    public void setBasePortions(int basePortions) {
        if (basePortions <= 0) {
            throw new IllegalArgumentException("Le porzioni base devono essere positive");
        }
        this.basePortions = basePortions;
        updateLastModified();
    }
    
    // NUOVO: Gestione avanzata delle preparazioni intermedie
    public void addPreparazioneRichiesta(String preparazioneId, double quantita, String unitaMisura, 
                                        int tempoEsecuzione, String note) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        
        PreparationUsage usage = new PreparationUsage(preparazioneId, quantita, unitaMisura, 
                                                     tempoEsecuzione, note);
        
        // Controlla se esiste già
        boolean exists = preparazioniRichieste.stream()
                .anyMatch(p -> p.getPreparationId().equals(preparazioneId));
        
        if (!exists) {
            preparazioniRichieste.add(usage);
            updateLastModified();
        }
    }
    
    public void removePreparazioneRichiesta(String preparazioneId) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        
        boolean removed = preparazioniRichieste.removeIf(p -> p.getPreparationId().equals(preparazioneId));
        if (removed) {
            updateLastModified();
        }
    }
    
    // NUOVO: Gestione avanzata delle istruzioni
    public void addAdvanceInstruction(String text, int ordine, int tempoEstimato, String note) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        
        Instruction instruction = new Instruction(text, ordine, tempoEstimato, note);
        advanceInstructions.add(instruction);
        sortInstructionsByOrder(advanceInstructions);
        updateLastModified();
    }
    
    public void addLastMinuteInstruction(String text, int ordine, int tempoEstimato, String note) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        
        Instruction instruction = new Instruction(text, ordine, tempoEstimato, note);
        lastMinuteInstructions.add(instruction);
        sortInstructionsByOrder(lastMinuteInstructions);
        updateLastModified();
    }
    
    // Compatibilità con versione precedente
    public void addAdvanceInstruction(String instruction) {
        addAdvanceInstruction(instruction, advanceInstructions.size() + 1, 5, null);
    }
    
    public void addLastMinuteInstruction(String instruction) {
        addLastMinuteInstruction(instruction, lastMinuteInstructions.size() + 1, 5, null);
    }
    
    // NUOVO: Spostamento istruzioni
    public void moveInstructionUp(Instruction instruction, boolean isAdvance) {
        List<Instruction> list = isAdvance ? advanceInstructions : lastMinuteInstructions;
        int index = list.indexOf(instruction);
        
        if (index > 0) {
            Instruction current = list.get(index);
            Instruction previous = list.get(index - 1);
            
            // Scambia gli ordini
            int tempOrder = current.getOrdine();
            current.setOrdine(previous.getOrdine());
            previous.setOrdine(tempOrder);
            
            sortInstructionsByOrder(list);
            updateLastModified();
        }
    }
    
    public void moveInstructionDown(Instruction instruction, boolean isAdvance) {
        List<Instruction> list = isAdvance ? advanceInstructions : lastMinuteInstructions;
        int index = list.indexOf(instruction);
        
        if (index < list.size() - 1) {
            Instruction current = list.get(index);
            Instruction next = list.get(index + 1);
            
            // Scambia gli ordini
            int tempOrder = current.getOrdine();
            current.setOrdine(next.getOrdine());
            next.setOrdine(tempOrder);
            
            sortInstructionsByOrder(list);
            updateLastModified();
        }
    }
    
    private void sortInstructionsByOrder(List<Instruction> instructions) {
        instructions.sort((i1, i2) -> Integer.compare(i1.getOrdine(), i2.getOrdine()));
    }
    
    // Business methods esistenti
    public void addDose(String ingredienteId, double quantitativo, String unitaMisura) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        dosi.add(new Dose(ingredienteId, quantitativo, unitaMisura));
        updateLastModified();
    }
    
    public void addIngredient(String name, double quantity, String unit) {
        addDose(name, quantity, unit);
    }
    
    public void addTag(String tagName) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        Tag newTag = new Tag(tagName);
        if (!tags.contains(newTag)) {
            tags.add(newTag);
            updateLastModified();
        }
    }
    
    public void addTag(Tag tag) {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Non è possibile modificare una ricetta pubblicata");
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
            updateLastModified();
        }
    }
    
    public void publish() {
        if (state != RecipeState.DRAFT) {
            throw new IllegalStateException("Solo le ricette in bozza possono essere pubblicate");
        }
        if (dosi.isEmpty()) {
            throw new IllegalStateException("Una ricetta deve avere almeno un ingrediente");
        }
        if (advanceInstructions.isEmpty() && lastMinuteInstructions.isEmpty()) {
            throw new IllegalStateException("Una ricetta deve avere almeno un'istruzione");
        }
        
        state = RecipeState.PUBLISHED;
        updateLastModified();
    }
    
    public void unpublish() {
        if (state != RecipeState.PUBLISHED) {
            throw new IllegalStateException("Solo le ricette pubblicate possono essere ritirate");
        }
        state = RecipeState.DRAFT;
        updateLastModified();
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
    
    public boolean hasTag(String tagName) {
        return tags.stream().anyMatch(tag -> tag.getNome().equals(tagName.toLowerCase()));
    }
    
    // NUOVO: Calcola tempo totale di preparazione
    public int getTotalExecutionTime() {
        int advanceTime = advanceInstructions.stream()
                .mapToInt(Instruction::getTempoEstimato)
                .sum();
        
        int lastMinuteTime = lastMinuteInstructions.stream()
                .mapToInt(Instruction::getTempoEstimato)
                .sum();
        
        int preparationTime = preparazioniRichieste.stream()
                .mapToInt(PreparationUsage::getTempoEsecuzione)
                .sum();
        
        return preparationTime + advanceTime + lastMinuteTime;
    }
    
    private void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }
    
    // Metodi compatibilità con il codice esistente
    public List<Ingredient> getIngredients() {
        return dosi.stream()
                .map(dose -> new Ingredient(dose.getIngredienteId(), dose.getQuantitativo(), dose.getUnitaMisura()))
                .collect(Collectors.toList());
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
        for (PreparationUsage prep : this.preparazioniRichieste) {
            copy.addPreparazioneRichiesta(prep.getPreparationId(), prep.getQuantita(), 
                                        prep.getUnitaMisura(), prep.getTempoEsecuzione(), prep.getNote());
        }
        
        // Copia istruzioni
        for (Instruction instruction : this.advanceInstructions) {
            copy.addAdvanceInstruction(instruction.getText(), instruction.getOrdine(), 
                                     instruction.getTempoEstimato(), instruction.getNote());
        }
        
        for (Instruction instruction : this.lastMinuteInstructions) {
            copy.addLastMinuteInstruction(instruction.getText(), instruction.getOrdine(), 
                                        instruction.getTempoEstimato(), instruction.getNote());
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
    
    // NUOVO: Classe per gestire l'uso di preparazioni intermedie
    public static class PreparationUsage {
        private String preparationId;
        private double quantita;
        private String unitaMisura;
        private int tempoEsecuzione;
        private String note;
        
        public PreparationUsage(String preparationId, double quantita, String unitaMisura, 
                              int tempoEsecuzione, String note) {
            this.preparationId = Objects.requireNonNull(preparationId);
            this.quantita = quantita;
            this.unitaMisura = Objects.requireNonNull(unitaMisura);
            this.tempoEsecuzione = tempoEsecuzione;
            this.note = note;
        }
        
        // Getters
        public String getPreparationId() { return preparationId; }
        public double getQuantita() { return quantita; }
        public String getUnitaMisura() { return unitaMisura; }
        public int getTempoEsecuzione() { return tempoEsecuzione; }
        public String getNote() { return note; }
        
        // Setters
        public void setQuantita(double quantita) { this.quantita = quantita; }
        public void setUnitaMisura(String unitaMisura) { this.unitaMisura = unitaMisura; }
        public void setTempoEsecuzione(int tempoEsecuzione) { this.tempoEsecuzione = tempoEsecuzione; }
        public void setNote(String note) { this.note = note; }
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