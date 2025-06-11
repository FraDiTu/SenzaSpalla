package com.catring.information_expert;

import com.catring.model.Menu;
import com.catring.model.Ricetta;

/**
 * PATTERN GRASP: INFORMATION EXPERT
 * Questa classe è l'esperto per la validazione di menu e ricette.
 * Ha tutta la conoscenza necessaria per validare correttamente.
 */
public class MenuValidator {
    
    /**
     * Valida un menu completo
     */
    public boolean isValidMenu(Menu menu) {
        if (menu == null) {
            return false;
        }
        
        return isValidMenuName(menu.getNome()) &&
               isValidMenuDescription(menu.getDescrizione()) &&
               hasValidSections(menu);
    }
    
    /**
     * Valida il nome di un menu
     */
    public boolean isValidMenuName(String nome) {
        return nome != null && !nome.trim().isEmpty() && nome.length() >= 3;
    }
    
    /**
     * Valida la descrizione di un menu
     */
    public boolean isValidMenuDescription(String descrizione) {
        return descrizione != null && !descrizione.trim().isEmpty();
    }
    
    /**
     * Valida che il menu abbia sezioni valide
     */
    public boolean hasValidSections(Menu menu) {
        return menu.getSezioni() != null && !menu.getSezioni().isEmpty();
    }
    
    /**
     * Valida una ricetta
     */
    public boolean isValidRicetta(Ricetta ricetta) {
        if (ricetta == null) {
            return false;
        }
        
        return isValidRicettaName(ricetta.getNome()) &&
               isValidRicettaDescription(ricetta.getDescrizione()) &&
               isValidTempoPreparazione(ricetta.getTempoPreparazione()) &&
               isValidStato(ricetta.getStato());
    }
    
    /**
     * Valida il nome di una ricetta
     */
    public boolean isValidRicettaName(String nome) {
        return nome != null && !nome.trim().isEmpty() && nome.length() >= 2;
    }
    
    /**
     * Valida la descrizione di una ricetta
     */
    public boolean isValidRicettaDescription(String descrizione) {
        return descrizione != null && !descrizione.trim().isEmpty();
    }
    
    /**
     * Valida il tempo di preparazione
     */
    public boolean isValidTempoPreparazione(int tempo) {
        return tempo > 0 && tempo <= 600; // max 10 ore
    }
    
    /**
     * Valida lo stato di una ricetta
     */
    public boolean isValidStato(String stato) {
        return "bozza".equals(stato) || "pubblicata".equals(stato);
    }
    
    /**
     * Valida un email
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.contains("@") && email.contains(".") && email.length() >= 5;
    }
}