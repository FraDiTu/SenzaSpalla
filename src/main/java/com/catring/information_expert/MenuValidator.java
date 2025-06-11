package com.catring.information_expert;

import com.catring.model.Menu;
import com.catring.model.Ricetta;

public class MenuValidator {

    public boolean isValidMenu(Menu menu) {
        if (menu == null) {
            return false;
        }
        
        return isValidMenuName(menu.getNome()) &&
               isValidMenuDescription(menu.getDescrizione()) &&
               hasValidSections(menu);
    }

    public boolean isValidMenuName(String nome) {
        return nome != null && !nome.trim().isEmpty() && nome.length() >= 3;
    }

    public boolean isValidMenuDescription(String descrizione) {
        return descrizione != null && !descrizione.trim().isEmpty();
    }

    public boolean hasValidSections(Menu menu) {
        return menu.getSezioni() != null && !menu.getSezioni().isEmpty();
    }

    public boolean isValidRicetta(Ricetta ricetta) {
        if (ricetta == null) {
            return false;
        }
        
        return isValidRicettaName(ricetta.getNome()) &&
               isValidRicettaDescription(ricetta.getDescrizione()) &&
               isValidTempoPreparazione(ricetta.getTempoPreparazione()) &&
               isValidStato(ricetta.getStato());
    }

    public boolean isValidRicettaName(String nome) {
        return nome != null && !nome.trim().isEmpty() && nome.length() >= 2;
    }

    public boolean isValidRicettaDescription(String descrizione) {
        return descrizione != null && !descrizione.trim().isEmpty();
    }

    public boolean isValidTempoPreparazione(int tempo) {
        return tempo > 0 && tempo <= 600;
    }

    public boolean isValidStato(String stato) {
        return "bozza".equals(stato) || "pubblicata".equals(stato);
    }

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.contains("@") && email.contains(".") && email.length() >= 5;
    }
}