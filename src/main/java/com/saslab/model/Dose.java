package com.saslab.model;

import java.util.Objects;

/**
 * Classe che rappresenta una Dose di un ingrediente
 * Specifica la quantità e l'unità di misura per una ricetta o preparazione
 * Implementa pattern Value Object (GoF)
 */
public class Dose {
    
    private String ingredienteId;
    private double quantitativo;
    private String unitaMisura;
    
    public Dose(String ingredienteId, double quantitativo, String unitaMisura) {
        this.ingredienteId = Objects.requireNonNull(ingredienteId, "L'ID dell'ingrediente non può essere null");
        this.unitaMisura = Objects.requireNonNull(unitaMisura, "L'unità di misura non può essere null");
        
        if (quantitativo <= 0) {
            throw new IllegalArgumentException("Il quantitativo deve essere positivo");
        }
        
        this.quantitativo = quantitativo;
    }
    
    // Getters
    public String getIngredienteId() { return ingredienteId; }
    public double getQuantitativo() { return quantitativo; }
    public String getUnitaMisura() { return unitaMisura; }
    
    // Business methods
    public Dose scalaPorzioni(int porzioniBase, int porzioniTarget) {
        if (porzioniBase <= 0 || porzioniTarget <= 0) {
            throw new IllegalArgumentException("Le porzioni devono essere positive");
        }
        
        double nuovoQuantitativo = quantitativo * ((double) porzioniTarget / porzioniBase);
        return new Dose(ingredienteId, nuovoQuantitativo, unitaMisura);
    }
    
    public boolean isCompatibile(String unitaMisuraRichiesta) {
        return unitaMisura.equals(unitaMisuraRichiesta) || 
               sonoPessoEquivalenti(unitaMisura, unitaMisuraRichiesta);
    }
    
    private boolean sonoPessoEquivalenti(String unita1, String unita2) {
        // Logica semplificata per unità equivalenti
        return (unita1.equals("g") && unita2.equals("kg")) ||
               (unita1.equals("kg") && unita2.equals("g")) ||
               (unita1.equals("ml") && unita2.equals("l")) ||
               (unita1.equals("l") && unita2.equals("ml"));
    }
    
    public String getDisplayText() {
        return String.format("%.2f %s", quantitativo, unitaMisura);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Dose dose = (Dose) obj;
        return Double.compare(dose.quantitativo, quantitativo) == 0 &&
               Objects.equals(ingredienteId, dose.ingredienteId) &&
               Objects.equals(unitaMisura, dose.unitaMisura);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(ingredienteId, quantitativo, unitaMisura);
    }
    
    @Override
    public String toString() {
        return String.format("Dose{ingrediente='%s', quantitativo=%.2f, unita='%s'}", 
                           ingredienteId, quantitativo, unitaMisura);
    }
}