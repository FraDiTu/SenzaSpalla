package com.saslab.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Classe che rappresenta un Menù nel sistema Cat &amp; Ring
 * Implementa pattern Composite per gestire le sezioni e le voci
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
    
    // Setters con validazione
    public void setName(String name) {
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile modificare un menù in uso");
        }
        this.name = Objects.requireNonNull(name, "Il nome non può essere null");
        updateLastModified();
    }
    
    public void setDescription(String description) {
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile modificare un menù in uso");
        }
        this.description = description;
        updateLastModified();
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        updateLastModified();
    }
    
    // Characteristic setters
    public void setRequiresChefPresence(boolean requiresChefPresence) {
        this.requiresChefPresence = requiresChefPresence;
        updateLastModified();
    }
    
    public void setHasOnlyHotDishes(boolean hasOnlyHotDishes) {
        this.hasOnlyHotDishes = hasOnlyHotDishes;
        if (hasOnlyHotDishes) {
            this.hasOnlyColdDishes = false;
        }
        updateLastModified();
    }
    
    public void setHasOnlyColdDishes(boolean hasOnlyColdDishes) {
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
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile modificare le sezioni di un menù in uso");
        }
        
        Objects.requireNonNull(sectionNames, "La lista delle sezioni non può essere null");
        if (sectionNames.isEmpty()) {
            throw new IllegalArgumentException("Deve essere specificata almeno una sezione");
        }
        
        // Verifica nomi unici
        Set<String> uniqueNames = new HashSet<>(sectionNames);
        if (uniqueNames.size() != sectionNames.size()) {
            throw new IllegalArgumentException("I nomi delle sezioni devono essere unici");
        }
        
        sections.clear();
        for (int i = 0; i < sectionNames.size(); i++) {
            String sectionId = id + "_SEC_" + (i + 1);
            sections.add(new MenuSection(sectionId, sectionNames.get(i), i));
        }
        
        updateLastModified();
    }
    
    public void addRecipeToSection(String recipeId, String sectionName) {
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile modificare un menù in uso");
        }
        
        MenuSection section = findSectionByName(sectionName);
        if (section == null) {
            throw new IllegalArgumentException("Sezione non trovata: " + sectionName);
        }
        
        section.addMenuItem(new MenuItem(generateMenuItemId(), recipeId, recipeId));
        updateLastModified();
    }
    
    public void moveRecipesBetweenSection(String recipeId, String sourceSectionName, String destinationSectionName) {
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile modificare un menù in uso");
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
        
        destSection.addMenuItem(item);
        updateLastModified();
    }
    
    public void removeRecipeFromSection(String recipeId, String sectionName) {
        if (state == MenuState.IN_USE) {
            throw new IllegalStateException("Non è possibile modificare un menù in uso");
        }
        
        MenuSection section = findSectionByName(sectionName);
        if (section == null) {
            throw new IllegalArgumentException("Sezione non trovata: " + sectionName);
        }
        
        section.removeMenuItemByRecipeId(recipeId);
        updateLastModified();
    }
    
    // State management methods
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
        
        state = MenuState.PUBLISHED;
        updateLastModified();
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
        return sections.stream()
                .filter(section -> section.getTitle().equals(sectionName))
                .findFirst()
                .orElse(null);
    }
    
    public MenuItem findMenuItemByRecipeId(String recipeId) {
        for (MenuSection section : sections) {
            MenuItem item = section.findMenuItemByRecipeId(recipeId);
            if (item != null) {
                return item;
            }
        }
        return null;
    }
    
    // Factory method per creare una copia
    public Menu createCopy(String newId, String newChefId) {
        Menu copy = new Menu(newId, this.name + " (Copia)", newChefId);
        copy.setDescription(this.description);
        copy.setNotes(this.notes);
        
        // Copia caratteristiche
        copy.setRequiresChefPresence(this.requiresChefPresence);
        copy.setHasOnlyHotDishes(this.hasOnlyHotDishes);
        copy.setHasOnlyColdDishes(this.hasOnlyColdDishes);
        copy.setRequiresKitchenOnSite(this.requiresKitchenOnSite);
        copy.setSuitableForBuffet(this.suitableForBuffet);
        copy.setFingerFoodOnly(this.fingerFoodOnly);
        
        // Copia sezioni e voci
        List<String> sectionNames = sections.stream()
                .map(MenuSection::getTitle)
                .collect(java.util.stream.Collectors.toList());
        copy.defineMenuSections(sectionNames);
        
        for (MenuSection section : sections) {
            for (MenuItem item : section.getMenuItems()) {
                copy.addRecipeToSection(item.getRecipeId(), section.getTitle());
            }
        }
        
        return copy;
    }
    
    private void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }
    
    private String generateMenuItemId() {
        return id + "_ITEM_" + UUID.randomUUID().toString().substring(0, 8);
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
        return String.format("Menu{id='%s', name='%s', state=%s, sections=%d, items=%d}", 
                           id, name, state, sections.size(), getTotalMenuItems());
    }
    /**
 * Accetta un visitor per implementare il pattern Visitor
 * Permette di aggiungere nuove operazioni senza modificare la classe
 */
public void accept(com.saslab.visitor.MenuVisitor visitor) {
    visitor.visitMenu(this);
    for (MenuSection section : sections) {
        section.accept(visitor);
    }
}
}