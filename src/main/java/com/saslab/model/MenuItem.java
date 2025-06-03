package com.saslab.model;

import java.util.Objects;

/**
 * Classe che rappresenta una voce di menù
 * Implementa pattern Composite come foglia
 */
public class MenuItem {
    
    private String id;
    private String recipeId;
    private String displayName; // nome visualizzato nel menù (può essere diverso dal nome della ricetta)
    private String modifications; // eventuali modifiche al testo
    private double price; // prezzo eventuale
    private boolean available; // disponibilità
    
    public MenuItem(String id, String recipeId, String displayName) {
        this.id = Objects.requireNonNull(id, "L'ID non può essere null");
        this.recipeId = Objects.requireNonNull(recipeId, "L'ID della ricetta non può essere null");
        this.displayName = Objects.requireNonNull(displayName, "Il nome di visualizzazione non può essere null");
        this.available = true;
        this.price = 0.0;
    }
    
    // Getters
    public String getId() { return id; }
    public String getRecipeId() { return recipeId; }
    public String getDisplayName() { return displayName; }
    public String getModifications() { return modifications; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    
    // Setters
    public void setDisplayName(String displayName) {
        this.displayName = Objects.requireNonNull(displayName, "Il nome di visualizzazione non può essere null");
    }
    
    public void setModifications(String modifications) {
        this.modifications = modifications;
    }
    
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Il prezzo non può essere negativo");
        }
        this.price = price;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    // Business methods
    public boolean hasModifications() {
        return modifications != null && !modifications.trim().isEmpty();
    }
    
    public boolean hasPrice() {
        return price > 0;
    }
    
    public String getFullDisplayText() {
        StringBuilder text = new StringBuilder(displayName);
        if (hasModifications()) {
            text.append(" (").append(modifications).append(")");
        }
        if (hasPrice()) {
            text.append(" - €").append(String.format("%.2f", price));
        }
        if (!available) {
            text.append(" [Non Disponibile]");
        }
        return text.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MenuItem menuItem = (MenuItem) obj;
        return Objects.equals(id, menuItem.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("MenuItem{id='%s', recipeId='%s', displayName='%s', available=%s}", 
                           id, recipeId, displayName, available);
    }

    /**
 * Accetta un visitor per implementare il pattern Visitor
 */
public void accept(com.saslab.visitor.MenuVisitor visitor) {
    visitor.visitMenuItem(this);
}
}