package com.saslab.model;

import java.util.Objects;

/**
 * Classe che rappresenta un ingrediente nel sistema Cat & Ring
 * Utilizzata sia per ricette che per preparazioni
 * CORRETTA: Risolve il problema di import/reference nell'originale
 */
public class Ingredient {
    
    private String name;
    private double quantity;
    private String unit;
    private String preparationId; // ID preparazione se è una preparazione intermedia
    private boolean isBasicIngredient; // true se ingrediente base, false se preparazione
    
    // Constructor per ingrediente base
    public Ingredient(String name, double quantity, String unit) {
        this.name = Objects.requireNonNull(name, "Il nome dell'ingrediente non può essere null");
        this.quantity = quantity;
        this.unit = Objects.requireNonNull(unit, "L'unità non può essere null");
        this.isBasicIngredient = true;
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantità deve essere positiva");
        }
    }
    
    // Constructor per preparazione intermedia
    public Ingredient(String name, double quantity, String unit, String preparationId) {
        this(name, quantity, unit);
        this.preparationId = preparationId;
        this.isBasicIngredient = false;
    }
    
    // Getters
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getPreparationId() { return preparationId; }
    public boolean isBasicIngredient() { return isBasicIngredient; }
    public boolean isPreparation() { return !isBasicIngredient; }
    
    // Setters con validazione
    public void setQuantity(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantità deve essere positiva");
        }
        this.quantity = quantity;
    }
    
    public void setUnit(String unit) {
        this.unit = Objects.requireNonNull(unit, "L'unità non può essere null");
    }
    
    // Business methods
    public boolean isCompatibleUnit(String otherUnit) {
        if (unit.equals(otherUnit)) return true;
        
        // Logica conversioni unità compatibili
        switch (unit.toLowerCase()) {
            case "g":
                return otherUnit.toLowerCase().equals("kg");
            case "kg":
                return otherUnit.toLowerCase().equals("g");
            case "ml":
                return otherUnit.toLowerCase().equals("l");
            case "l":
                return otherUnit.toLowerCase().equals("ml");
            default:
                return false;
        }
    }
    
    public double convertToUnit(String targetUnit) {
        if (unit.equals(targetUnit)) return quantity;
        
        switch (unit.toLowerCase() + "_to_" + targetUnit.toLowerCase()) {
            case "g_to_kg":
                return quantity / 1000.0;
            case "kg_to_g":
                return quantity * 1000.0;
            case "ml_to_l":
                return quantity / 1000.0;
            case "l_to_ml":
                return quantity * 1000.0;
            default:
                throw new IllegalArgumentException("Conversione non supportata: " + unit + " -> " + targetUnit);
        }
    }
    
    // Factory methods
    public static Ingredient createBasicIngredient(String name, double quantity, String unit) {
        return new Ingredient(name, quantity, unit);
    }
    
    public static Ingredient createFromPreparation(String name, double quantity, String unit, String preparationId) {
        return new Ingredient(name, quantity, unit, preparationId);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ingredient that = (Ingredient) obj;
        return Double.compare(that.quantity, quantity) == 0 &&
               isBasicIngredient == that.isBasicIngredient &&
               Objects.equals(name, that.name) &&
               Objects.equals(unit, that.unit) &&
               Objects.equals(preparationId, that.preparationId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, quantity, unit, preparationId, isBasicIngredient);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s: %.2f %s", name, quantity, unit));
        if (isPreparation()) {
            sb.append(" (preparazione: ").append(preparationId).append(")");
        }
        return sb.toString();
    }
}