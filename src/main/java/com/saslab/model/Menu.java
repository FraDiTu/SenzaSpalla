package com.saslab.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Classe che rappresenta un Menù nel sistema Cat & Ring
 * Implementa pattern Composite per gestire le sezioni e le voci
 * CORRETTA: Risolve problemi di copia incompleta e validazione
 */
public class Menu {
    
    public enum MenuState {
        DRAFT("Bozza"),
        PUBLISHED("Pubblicato"),
        IN_USE("In Uso"),
        ARCHIVED("Archiviato");
        
        private final String displayName;
        
        MenuState(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String id;
    private String name;
    private String description;
    private String chefId; // creatore del menù
    private MenuState state;
    private List<MenuSection> sections;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    
    // Caratteristiche del menù
    private boolean requiresChefPresence;
    private boolean hasOnlyHotDishes;
    private boolean hasOnlyColdDishes;
    private boolean requiresKitchenOnSite;
    private boolean suitableForBuffet;
    private boolean fingerFoodOnly;
    
    // Constructor
    public Menu(String id, String name, String chefId) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.name = Objects.requireNonNull(name, "Il nome non può essere null");
        this.chefId = Objects.requireNonNull(chefId, "L'ID dello chef non può essere null");
        
        validateParameters();
        
        this.state = MenuState.DRAFT;
        this.sections = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        
        // Default values
        this.requiresChefPresence = false;
        this.hasOnlyHotDishes = false;
        this.hasOnlyColdDishes = false;
        this.requiresKitchenOnSite = false;
        this.suitableForBuffet = true;
        this.fingerFoodOnly = false;
    }
    
    private void validateParameters() {
        if (id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID non può essere vuoto");
        }
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }
        if (chefId.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID dello chef non può essere vuoto");
        }
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getChefId() { return chefId; }
    public MenuState getState() { return state; }
    public List<MenuSection> getSections() { return new ArrayList<>(sections); }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastModified() { return lastModified; }
    
    // Characteristic getters
    public boolean isRequiresChefPresence() { return requiresChefPresence; }
    public boolean isHasOnlyHotDishes() { return hasOnlyHotDishes; }
    public boolean isHasOnlyColdDishes() { return hasOnlyColdDishes; }
    public boolean isRequiresKitchenOnSite() { return requiresKitchenOnSite; }
    public boolean isSuitableForBuffet() { return suitableForBuffet; }
    public boolean isFingerFoodOnly() { return fingerFoodOnly; }
    
    // Setters con validazione migliorata
    public void setName(String name) {
        Objects.requireNonNull(name, "Il nome non può essere null");
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile modificare un menù in uso");
        }
        this.name = name.trim();
        updateLastModified();
    }
    
    public void setDescription(String description) {
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile modificare un menù in uso");
        }
        this.description = description != null ? description.trim() : null;
        updateLastModified();
    }
    
    public void setNotes(String notes) {
        this.notes = notes != null ? notes.trim() : null;
        updateLastModified();
    }
    
    // Characteristic setters con validazione logica
    public void setRequiresChefPresence(boolean requiresChefPresence) {
        this.requiresChefPresence = requiresChefPresence;
        updateLastModified();
    }
    
    public void setHasOnlyHotDishes(boolean hasOnlyHotDishes) {
        if (hasOnlyHotDishes && hasOnlyColdDishes) {
            throw new IllegalStateException("Un menù non può avere solo piatti caldi E solo piatti freddi");
        }
        this.hasOnlyHotDishes = hasOnlyHotDishes;
        if (hasOnlyHotDishes) {
            this.hasOnlyColdDishes = false;
        }
        updateLastModified();
    }
    
    public void setHasOnlyColdDishes(boolean hasOnlyColdDishes) {
        if (hasOnlyColdDishes && hasOnlyHotDishes) {
            throw new IllegalStateException("Un menù non può avere solo piatti freddi E solo piatti caldi");
        }
        this.hasOnlyColdDishes = hasOnlyColdDishes;
        if (hasOnlyColdDishes) {
            this.hasOnlyHotDishes = false;
        }
        updateLastModified();
    }
    
    public void setRequiresKitchenOnSite(boolean requiresKitchenOnSite) {
        this.requiresKitchenOnSite = requiresKitchenOnSite;
        updateLastModified();
    }
    
    public void setSuitableForBuffet(boolean suitableForBuffet) {
        this.suitableForBuffet = suitableForBuffet;
        updateLastModified();
    }
    
    public void setFingerFoodOnly(boolean fingerFoodOnly) {
        this.fingerFoodOnly = fingerFoodOnly;
        updateLastModified();
    }
    
    // Section management methods - implementa pattern Composite
    public void defineMenuSections(List<String> sectionNames) {
        Objects.requireNonNull(sectionNames, "La lista delle sezioni non può essere null");
        
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile modificare le sezioni di un menù in uso");
        }
        
        if (sectionNames.isEmpty()) {
            throw new IllegalArgumentException("Deve essere specificata almeno una sezione");
        }
        
        // Verifica nomi unici e non vuoti
        Set<String> uniqueNames = new HashSet<>();
        for (String name : sectionNames) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("I nomi delle sezioni non possono essere vuoti");
            }
            String trimmedName = name.trim();
            if (!uniqueNames.add(trimmedName)) {
                throw new IllegalArgumentException("I nomi delle sezioni devono essere unici: " + trimmedName);
            }
        }
        
        sections.clear();
        for (int i = 0; i < sectionNames.size(); i++) {
            String sectionId = id + "_SEC_" + (i + 1);
            sections.add(new MenuSection(sectionId, sectionNames.get(i).trim(), i));
        }
        
        updateLastModified();
    }
    
    public void addRecipeToSection(String recipeId, String sectionName) {
        Objects.requireNonNull(recipeId, "L'ID della ricetta non può essere null");
        Objects.requireNonNull(sectionName, "Il nome della sezione non può essere null");
        
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile modificare un menù in uso");
        }
        
        MenuSection section = findSectionByName(sectionName);
        if (section == null) {
            throw new IllegalArgumentException("Sezione non trovata: " + sectionName);
        }
        
        // Verifica che la ricetta non sia già presente nella sezione
        if (section.findMenuItemByRecipeId(recipeId) != null) {
            throw new IllegalArgumentException("La ricetta è già presente nella sezione");
        }
        
        section.addMenuItem(new MenuItem(generateMenuItemId(), recipeId, recipeId));
        updateLastModified();
    }
    
    public void moveRecipesBetweenSection(String recipeId, String sourceSectionName, String destinationSectionName) {
        Objects.requireNonNull(recipeId, "L'ID della ricetta non può essere null");
        Objects.requireNonNull(sourceSectionName, "Il nome della sezione di origine non può essere null");
        Objects.requireNonNull(destinationSectionName, "Il nome della sezione di destinazione non può essere null");
        
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile modificare un menù in uso");
        }
        
        if (sourceSectionName.equals(destinationSectionName)) {
            throw new IllegalArgumentException("Le sezioni di origine e destinazione non possono essere uguali");
        }
        
        MenuSection sourceSection = findSectionByName(sourceSectionName);
        MenuSection destSection = findSectionByName(destinationSectionName);
        
        if (sourceSection == null) {
            throw new IllegalArgumentException("Sezione di origine non trovata: " + sourceSectionName);
        }
        if (destSection == null) {
            throw new IllegalArgumentException("Sezione di destinazione non trovata: " + destinationSectionName);
        }
        
        MenuItem item = sourceSection.removeMenuItemByRecipeId(recipeId);
        if (item == null) {
            throw new IllegalArgumentException("Ricetta non trovata nella sezione di origine");
        }
        
        // Verifica che non esista già nella destinazione
        if (destSection.findMenuItemByRecipeId(recipeId) != null) {
            // Ripristina nella sezione originale se fallisce
            sourceSection.addMenuItem(item);
            throw new IllegalArgumentException("La ricetta è già presente nella sezione di destinazione");
        }
        
        destSection.addMenuItem(item);
        updateLastModified();
    }
    
    public void removeRecipeFromSection(String recipeId, String sectionName) {
        Objects.requireNonNull(recipeId, "L'ID della ricetta non può essere null");
        Objects.requireNonNull(sectionName, "Il nome della sezione non può essere null");
        
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile modificare un menù in uso");
        }
        
        MenuSection section = findSectionByName(sectionName);
        if (section == null) {
            throw new IllegalArgumentException("Sezione non trovata: " + sectionName);
        }
        
        MenuItem removed = section.removeMenuItemByRecipeId(recipeId);
        if (removed == null) {
            throw new IllegalArgumentException("Ricetta non trovata nella sezione specificata");
        }
        
        updateLastModified();
    }
    
    // State management methods con validazioni migliorate
    public void publish() {
        if (state != MenuState.DRAFT) {
            throw new IllegalStateException("Solo i menù in bozza possono essere pubblicati");
        }
        if (sections.isEmpty()) {
            throw new IllegalStateException("Il menù deve avere almeno una sezione");
        }
        if (getTotalMenuItems() == 0) {
            throw new IllegalStateException("Il menù deve contenere almeno una voce");
        }
        
        // Verifica consistenza caratteristiche
        validateMenuCharacteristics();
        
        state = MenuState.PUBLISHED;
        updateLastModified();
    }
    
    private void validateMenuCharacteristics() {
        if (hasOnlyHotDishes && hasOnlyColdDishes) {
            throw new IllegalStateException("Configurazione invalida: un menù non può avere solo piatti caldi E solo piatti freddi");
        }
        
        // Se finger food only, dovrebbe essere suitable for buffet
        if (fingerFoodOnly && !suitableForBuffet) {
            // Warning ma non blocca la pubblicazione
            System.out.println("Warning: Finger food è generalmente adatto per buffet");
        }
    }
    
    public void markAsInUse() {
        if (state != MenuState.PUBLISHED) {
            throw new IllegalStateException("Solo i menù pubblicati possono essere messi in uso");
        }
        state = MenuState.IN_USE;
        updateLastModified();
    }
    
    public void archive() {
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile archiviare un menù in uso");
        }
        state = MenuState.ARCHIVED;
        updateLastModified();
    }
    
    // Information Expert methods
    public boolean canBeModified() {
        return state == MenuState.DRAFT || state == MenuState.PUBLISHED;
    }
    
    public boolean canBeDeleted() {
        return state == MenuState.DRAFT;
    }
    
    public boolean isInUse() {
        return state == MenuState.IN_USE;
    }
    
    public int getTotalMenuItems() {
        return sections.stream()
                .mapToInt(section -> section.getMenuItems().size())
                .sum();
    }
    
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> allItems = new ArrayList<>();
        for (MenuSection section : sections) {
            allItems.addAll(section.getMenuItems());
        }
        return allItems;
    }
    
    public MenuSection findSectionByName(String sectionName) {
        if (sectionName == null) return null;
        
        return sections.stream()
                .filter(section -> section.getTitle().equals(sectionName))
                .findFirst()
                .orElse(null);
    }
    
    public MenuItem findMenuItemByRecipeId(String recipeId) {
        if (recipeId == null) return null;
        
        for (MenuSection section : sections) {
            MenuItem item = section.findMenuItemByRecipeId(recipeId);
            if (item != null) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * CORRETTO: Factory method per creare una copia completa
     * Risolve il problema della copia incompleta delle caratteristiche booleane
     */
    public Menu createCopy(String newId, String newChefId) {
        Objects.requireNonNull(newId, "Il nuovo ID non può essere null");
        Objects.requireNonNull(newChefId, "Il nuovo chef ID non può essere null");
        
        if (newId.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nuovo ID non può essere vuoto");
        }
        if (newChefId.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nuovo chef ID non può essere vuoto");
        }
        
        Menu copy = new Menu(newId, this.name + " (Copia)", newChefId);
        
        // Copia descrizione e note
        copy.setDescription(this.description);
        copy.setNotes(this.notes);
        
        // CORRETTO: Copia TUTTE le caratteristiche booleane (era mancante nel codice originale)
        copy.setRequiresChefPresence(this.requiresChefPresence);
        copy.setHasOnlyHotDishes(this.hasOnlyHotDishes);
        copy.setHasOnlyColdDishes(this.hasOnlyColdDishes);
        copy.setRequiresKitchenOnSite(this.requiresKitchenOnSite);
        copy.setSuitableForBuffet(this.suitableForBuffet);
        copy.setFingerFoodOnly(this.fingerFoodOnly);
        
        // Copia sezioni e voci solo se esistono
        if (!sections.isEmpty()) {
            List<String> sectionNames = sections.stream()
                    .map(MenuSection::getTitle)
                    .collect(java.util.stream.Collectors.toList());
            copy.defineMenuSections(sectionNames);
            
            // Copia le voci di ogni sezione
            for (MenuSection section : sections) {
                for (MenuItem item : section.getMenuItems()) {
                    try {
                        copy.addRecipeToSection(item.getRecipeId(), section.getTitle());
                    } catch (Exception e) {
                        // Log l'errore ma continua la copia
                        System.err.println("Errore nella copia della voce menu: " + e.getMessage());
                    }
                }
            }
        }
        
        return copy;
    }
    
    /**
     * Verifica se il menù contiene una ricetta specifica
     */
    public boolean containsRecipe(String recipeId) {
        return findMenuItemByRecipeId(recipeId) != null;
    }
    
    /**
     * Ottiene il numero di sezioni
     */
    public int getSectionCount() {
        return sections.size();
    }
    
    /**
     * Verifica se il menù è vuoto (senza voci)
     */
    public boolean isEmpty() {
        return getTotalMenuItems() == 0;
    }
    
    /**
     * Ottiene tutte le sezioni non vuote
     */
    public List<MenuSection> getNonEmptySections() {
        return sections.stream()
                .filter(MenuSection::hasMenuItems)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Calcola una stima del tempo di preparazione totale
     */
    public int getEstimatedPreparationTime() {
        // Implementazione semplificata - dovrebbe consultare le ricette
        return getTotalMenuItems() * 30; // 30 minuti per voce (stima)
    }
    
    /**
     * Verifica la compatibilità con un tipo di evento
     */
    public boolean isCompatibleWithEventType(String eventType) {
        if (eventType == null) return true;
        
        String lowerEventType = eventType.toLowerCase();
        
        // Logica di compatibilità
        if (lowerEventType.contains("buffet")) {
            return suitableForBuffet;
        }
        
        if (lowerEventType.contains("cocktail") || lowerEventType.contains("aperitivo")) {
            return fingerFoodOnly || suitableForBuffet;
        }
        
        if (lowerEventType.contains("formale") || lowerEventType.contains("gala")) {
            return !fingerFoodOnly && requiresChefPresence;
        }
        
        // Per altri tipi di evento, il menù è generalmente compatibile
        return true;
    }
    
    private void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }
    
    private String generateMenuItemId() {
        return id + "_ITEM_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Menu menu = (Menu) obj;
        return Objects.equals(id, menu.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Menu{id='%s', name='%s', state=%s, sections=%d, items=%d, chef='%s'}", 
                           id, name, state, sections.size(), getTotalMenuItems(), chefId);
    }
    
    /**
     * Accetta un visitor per implementare il pattern Visitor
     * Permette di aggiungere nuove operazioni senza modificare la classe
     */
    public void accept(com.saslab.visitor.MenuVisitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("Il visitor non può essere null");
        }
        
        visitor.visitMenu(this);
        for (MenuSection section : sections) {
            section.accept(visitor);
        }
    }
    
    /**
     * Fornisce una rappresentazione dettagliata del menù per debug
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== MENU: ").append(name).append(" ===\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Chef: ").append(chefId).append("\n");
        sb.append("Stato: ").append(state.getDisplayName()).append("\n");
        sb.append("Creato: ").append(createdAt).append("\n");
        sb.append("Modificato: ").append(lastModified).append("\n");
        
        if (description != null) {
            sb.append("Descrizione: ").append(description).append("\n");
        }
        
        sb.append("\nCaratteristiche:\n");
        sb.append("- Richiede chef presente: ").append(requiresChefPresence ? "Sì" : "No").append("\n");
        sb.append("- Solo piatti caldi: ").append(hasOnlyHotDishes ? "Sì" : "No").append("\n");
        sb.append("- Solo piatti freddi: ").append(hasOnlyColdDishes ? "Sì" : "No").append("\n");
        sb.append("- Richiede cucina sul posto: ").append(requiresKitchenOnSite ? "Sì" : "No").append("\n");
        sb.append("- Adatto per buffet: ").append(suitableForBuffet ? "Sì" : "No").append("\n");
        sb.append("- Solo finger food: ").append(fingerFoodOnly ? "Sì" : "No").append("\n");
        
        sb.append("\nSezioni (").append(sections.size()).append("):\n");
        for (MenuSection section : sections) {
            sb.append("- ").append(section.getTitle())
              .append(" (").append(section.getMenuItemCount()).append(" voci)\n");
        }
        
        if (notes != null) {
            sb.append("\nNote: ").append(notes).append("\n");
        }
        
        return sb.toString();
    }